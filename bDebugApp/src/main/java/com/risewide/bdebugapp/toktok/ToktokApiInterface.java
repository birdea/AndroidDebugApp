package com.risewide.bdebugapp.toktok;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ToktokApiInterface {

    String REAL_URL = "https://m.toktok.sk.com:9443/";
    String DEV_URL = "https://devgmp.sktelecom.com:9443/";

    @GET("service.pe")
    Call<LoginVo> getAuthenticate(
            @Query("primitive") String primitive,
            @Query("companyCd") String companyCd,
            @Query("appId") String appId,
            @Query("appVer") String appVer,
            @Query("encPwd") String encPwd,
            @Query("osName") String osName,
            @Query("groupCd") String groupCd,
            @Query("lang") String lang,
            @Query("authKey") String authKey,
            @Query("osVersion") String osVersion,
            @Query("mdn") String mdn
            );

    @GET("service.pe")
    Call<LoginVo> getAuthenticateCookie(
            @Header("Cookie") String cookie,
            @Query("primitive") String primitive,
            @Query("companyCd") String companyCd,
            @Query("appId") String appId,
            @Query("appVer") String appVer,
            @Query("encPwd") String encPwd,
            @Query("osName") String osName,
            @Query("groupCd") String groupCd,
            @Query("lang") String lang,
            @Query("authKey") String authKey,
            @Query("osVersion") String osVersion,
            @Query("mdn") String mdn
    );

    @FormUrlEncoded
    @POST("service.pe")
    Call<LoginVo> getAuthenticatePost(
            @Field("primitive") String primitive,
            @Field("companyCd") String companyCd,
            @Field("appId") String appId,
            @Field("appVer") String appVer,
            @Field("encPwd") String encPwd,
            @Field("osName") String osName,
            @Field("groupCd") String groupCd,
            @Field("lang") String lang,
            @Field("authKey") String authKey,
            @Field("osVersion") String osVersion,
            @Field("mdn") String mdn
    );


}
