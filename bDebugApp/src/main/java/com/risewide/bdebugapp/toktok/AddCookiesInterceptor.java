package com.risewide.bdebugapp.toktok;

import com.risewide.bdebugapp.util.BLog;

import java.io.IOException;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        Set<String> preferences = SharedPreferenceBase.get(Const.SHARED_PREFERENCE_NAME_COOKIE);

        if (preferences != null) {
            for (String cookie : preferences) {
                BLog.w("AddCookiesInterceptor addHeader: "+cookie);
                builder.addHeader("Cookie", cookie);
            }
        }
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");

        return chain.proceed(builder.build());
    }
}
