package com.xueh.comm_core.net;


import com.xueh.comm_core.helper.DevelopHelper;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Map<String, Retrofit> mServiceMap = new HashMap();

    private static final String DOMAIN_BASE = DevelopHelper.getBaseUrl();

    private ServiceGenerator() {
    }

    public static <T> T getService(Class<T> serviceClass) {
        return getCustomService(DOMAIN_BASE, serviceClass);
    }

    /**
     * @param domain Retrofit的BaseUrl
     */
    public static <T> T getCustomService(String domain, Class<T> serviceClass) {
        synchronized (ServiceGenerator.class) {
            Retrofit retrofit = mServiceMap.get(domain);
            if (retrofit == null) {
                retrofit = getRetrofit(domain);
                //只缓存最常用的
                if (DOMAIN_BASE.equals(domain)) {
                    mServiceMap.put(domain, retrofit);
                }
            }
            return createServiceFrom(retrofit, serviceClass);
        }
    }


    private static <T> T createServiceFrom(Retrofit retrofit, Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private static Retrofit getRetrofit(String base_url) {
        return new Retrofit.Builder()
                .baseUrl(base_url)
                .client(HttpUtils.getOkHttp())
             //   .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())         //返回内容的转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //请求Call的转换器
                .build();
    }

    public static void reset() {
        mServiceMap.clear();
    }
}
