package com.xueh.comm_core.net;

import android.os.Build.VERSION_CODES;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.JsonUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/8/9 13:29
 * 备注：
 */
public class LoggingHttpInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public interface Logger {
        void log(String message);
//        Logger DEFAULT = message -> Platform.get().log(INFO, message, null);
    }

    public LoggingHttpInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Set<String> headersToRedact = Collections.emptySet();

    private volatile Level level = Level.BODY;

    public void setJson(boolean json) {
        isJson = json;
    }

    private volatile boolean isJson = false;

    public LoggingHttpInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    public Response intercept(Chain chain) throws IOException {
        synchronized (this) {
            Level level = this.level;

            Request request = chain.request();
            if (level == Level.NONE) {
                return chain.proceed(request);
            }

            boolean logBody = level == Level.BODY;
            boolean logHeaders = logBody || level == Level.HEADERS;

            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;

            Connection connection = chain.connection();
            String requestStartMessage = "--> "
                    + request.method()
                    + ' ' + request.url()
                    + (connection != null ? " " + connection.protocol() : "");
            if (!logHeaders && hasRequestBody) {
                requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
            }
            logger.log("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.log("┃ " + requestStartMessage);

            if (logHeaders) {
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        logger.log("┃ Content-Type: " + requestBody.contentType());
                    }
                    if (requestBody.contentLength() != -1) {
                        logger.log("┃ Content-Length: " + requestBody.contentLength());
                    }
                }

                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        logHeader(headers, i);
                    }
                }

                if (!logBody || !hasRequestBody) {
                    logger.log("┃ --> END " + request.method());
                } else if (bodyHasUnknownEncoding(request.headers())) {
                    logger.log("┃ --> END " + request.method() + " (encoded body omitted)");
                } else {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    logger.log("┃");
                    if (isPlaintext(buffer)) {
                        logger.log("┃ " + buffer.readString(charset));
                        logger.log("┃ --> END " + request.method()
                                + " (" + requestBody.contentLength() + "-byte body)");
                    } else {
                        logger.log("┃ --> END " + request.method() + " (binary "
                                + requestBody.contentLength() + "-byte body omitted)");
                    }
                }
            }

            long startNs = System.nanoTime();
            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                logger.log("┃ <-- HTTP FAILED: " + e);
                throw e;
            }
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
            logger.log("┃");
            logger.log("┃ <-- "
                    + response.code()
                    + (response.message().isEmpty() ? "" : ' ' + response.message())
                    + ' ' + response.request().url()
                    + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

            if (logHeaders) {
                Headers headers = response.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    logHeader(headers, i);
                }

                if (!logBody || !HttpHeaders.promisesBody(response)) {
                    logger.log("┃ <-- END HTTP");
                } else if (bodyHasUnknownEncoding(response.headers())) {
                    logger.log("┃ <-- END HTTP (encoded body omitted)");
                } else {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();

                    Long gzippedLength = null;
                    if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                        gzippedLength = buffer.size();
                        try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                            buffer = new Buffer();
                            buffer.writeAll(gzippedResponseBody);
                        }
                    }

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    if (!isPlaintext(buffer)) {
                        logger.log("┃");
                        logger.log("┃ <-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                        return response;
                    }

                    if (contentLength != 0) {
                        logger.log("┃");
                        if (isJson) {
                            logger.log("┃ " + JsonUtils.formatJson(buffer.clone().readString(charset)));

                        } else {
                            logger.log("┃ " + buffer.clone().readString(charset));
                        }
                    }

                    if (gzippedLength != null) {
                        logger.log("┃ <-- END HTTP (" + buffer.size() + "-byte, "
                                + gzippedLength + "-gzipped-byte body)");
                    } else {
                        logger.log("┃ <-- END HTTP (" + buffer.size() + "-byte body)");
                    }
                }
            }
            logger.log("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return response;
        }
    }

    private void logHeader(Headers headers, int i) {
        String value = headersToRedact.contains(headers.name(i)) ? "██" : headers.value(i);
        logger.log("┃ " + headers.name(i) + ": " + value);
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private static boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }
}

