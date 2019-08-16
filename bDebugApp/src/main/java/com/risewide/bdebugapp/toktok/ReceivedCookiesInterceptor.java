package com.risewide.bdebugapp.toktok;

import com.risewide.bdebugapp.util.BLog;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            BLog.w("ReceivedCookiesInterceptor put: "+cookies);
            SharedPreferenceBase.put(Const.SHARED_PREFERENCE_NAME_COOKIE, cookies);
        }

        return originalResponse;
    }
}
