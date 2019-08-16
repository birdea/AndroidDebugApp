package com.risewide.bdebugapp.toktok;

import android.content.Context;

import com.risewide.bdebugapp.util.BLog;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ToktokApiClient {

    private static Retrofit retrofit = null;

    public static void init(Context context) {
        BLog.w("init() context:"+context);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                //.addInterceptor(new AddCookiesInterceptor())
                //.addInterceptor(new ReceivedCookiesInterceptor())
                //.connectTimeout(30, TimeUnit.SECONDS)
                //.writeTimeout(30, TimeUnit.SECONDS)
                //.readTimeout(30, TimeUnit.SECONDS)
                //.cookieJar(new JavaNetCookieJar(cookieManager))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ToktokApiInterface.DEV_URL)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
    }

    public static ToktokApiInterface getService() {
        return retrofit.create(ToktokApiInterface.class);
    }
}
