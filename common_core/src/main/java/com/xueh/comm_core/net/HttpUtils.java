package com.xueh.comm_core.net;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 创建日期: 2017/7/6 10:23
 * 备注：HTTP网络工具类
 */
public class HttpUtils {

    private static final int TIME_OUT_CONNECT = 60;
    private static final int TIME_OUT_READ = 60;
    private static final int TIME_OUT_WRITE = 60;

    public static HashMap<String, String> getHashMap() {
        return mHashMap;
    }

    public static HashMap<String, String> mHashMap = new HashMap<>();

    public static OkHttpClient getOkHttp() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS);


        BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder()
//                .addQueryParamsMap(mHashMap)
                .addHeaderParamsMap(mHashMap) //添加公共参数到 post 请求体
//                .addParamsMap(mHashMap) // 可以添加 Map 格式的参数
                .build();

        builder.addInterceptor(basicParamsInterceptor);


        LoggingHttpInterceptor loggingHttpInterceptor = new LoggingHttpInterceptor(new LoggingHttpInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.i("HTTP", "" + message);
            }
        });
        loggingHttpInterceptor.setLevel(LoggingHttpInterceptor.Level.BODY);
        builder.addInterceptor(loggingHttpInterceptor);


//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new Logger() {
//            @Override
//            public void log(String message) {
//                com.sunlands.comm_core.net.Logger.i("HTTP", "" + message);
//            }
//        });
//        logging.setLevel(Level.BODY);
//        builder.addInterceptor(logging);


        return builder.build();
    }

}
