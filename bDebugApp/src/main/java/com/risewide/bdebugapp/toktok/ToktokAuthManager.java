package com.risewide.bdebugapp.toktok;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.view.menu.ActionMenuItem;
import android.telephony.TelephonyManager;

import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.util.BLog;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToktokAuthManager {

    private static final String TAG = "ToktokAuthManager";

    private Context context;
    private static final String TOKTOK_AUTH_APP = "com.skt.pe.provider";

    public void init(Context context) {
        this.context = context;
    }

    public void checkStoreInfo() {
        // 인증 App. 설치가 되어 있는가?
        BLog.d("checkStoreInfo() installed? "+checkInstallationOf(context, TOKTOK_AUTH_APP));
    }

    public void resetCookieSession() {
        SharedPreferenceBase.put(Const.SHARED_PREFERENCE_NAME_COOKIE, null);
    }

    public static boolean checkInstallationOf(Context context, String packagename) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void checkAuthInfo(final Activity activity) {
        try {
            Map<String, String> map = getAuthKeyCompanyCDEncPwdMDN(context);

            String companyCd = map.get("COMPANY_CD");
            String encPwd = map.get("ENC_PWD");
            String osName = "Android"; // Android 로 고정값
            String groupCd ="SK"; // SK 로 고정값
            String osVersion = String.valueOf(Build.VERSION.SDK_INT); // Android OS 버전
            String authKey = map.get("AUTHKEY");
            String mdn = map.get("MDN");
            String appId = getTTMAppID(); // 발급받은 toktok Mobile APP ID
            String appVer = "1.0.0"; // App.의 버전
            String lang = Locale.getDefault().getLanguage(); // 언어 코드
            //
            String primitive = "COMMON_COMMON_EMPINFO";//"COMMON_COMMON_EMPINFO";//"COMMON_COMMON_USERINFO";
            //
            String cookie = null;//
            /*Set<String> stringSet = SharedPreferenceBase.get(Const.SHARED_PREFERENCE_NAME_COOKIE);
            if (stringSet != null) {
                for (String str : stringSet) {
                    BLog.w(">> cookie str:"+str);
                    if (str != null && !str.isEmpty()) {
                        cookie = str;
                        BLog.w(">> cookie:"+cookie);
                        break;
                    }
                }
                BLog.w(">> cookie has: "+cookie);
            } else {
                BLog.w(">> cookie null: "+cookie);
            }*/
            //
            Call<LoginVo> call;
            /*if (cookie != null)
            {
                call = ToktokApiClient.getService().getAuthenticateCookie(
                        cookie,
                        primitive,
                        companyCd,
                        appId,
                        appVer,
                        encPwd,
                        osName,
                        groupCd,
                        lang,
                        authKey,
                        osVersion,
                        mdn
                );
            }
            else */
                {
                call = ToktokApiClient.getService().getAuthenticatePost(
                        primitive,
                        companyCd,
                        appId,
                        appVer,
                        encPwd,
                        osName,
                        groupCd,
                        lang,
                        authKey,
                        osVersion,
                        mdn
                );
            }
            call.enqueue(new Callback<LoginVo>() {
                @Override
                public void onResponse(Call<LoginVo> call, Response<LoginVo> response) {
                    BLog.i(TAG, "onResponse()");

                    launchAuthApp(activity);

                    try {
                        String body = response.body().toString();
                        TToast.show(context, "body:" +body);
                        BLog.i(TAG, "body?: " +body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<LoginVo> call, Throwable t) {
                    BLog.i(TAG, "onFailure()");
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String AUTH_ACTION_NAME = "com.sk.pe.group.auth.GMP_LOGIN";

    private void launchAuthApp(Activity activity) {
        Intent intent = new Intent(AUTH_ACTION_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, 1007);
    }

    private Map<String, String> getAuthKeyCompanyCDEncPwdMDN(Context context) throws Exception {
        BLog.d("getAuthKeyCompanyCDEncPwdMDN()");
        // Android Phone일 경우
        {
            Map<String, String> map = new HashMap<String, String>();
            ContentValues values = new ContentValues();
            values.put("MDN", getMDN(context));
            values.put("APPID", getTTMAppID());
            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Uri.parse("content://com.skt.pe.auth/GMP_AUTH_PWD"), values);
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);
            BLog.d("returnId: "+returnId);
            if ("E001".equals(returnId) || "E002".equals(returnId) || "E007".equals(returnId) || "E008".equals(returnId)) {
                throw new Exception("Auth Exception");
            }
            String buffer = authValues.get(2);
            BLog.d("buffer: "+buffer);
            int b_offset = 0;
            int offset = buffer.indexOf("|");
            if (offset != -1) {
                map.put("AUTHKEY", buffer.substring(0, offset));
                b_offset = offset;
                offset = buffer.indexOf("|", offset + 1);
                if (offset != -1) {
                    map.put("COMPANY_CD", buffer.substring(b_offset + 1, offset));
                    map.put("ENC_PWD", buffer.substring(offset + 1));
                    //map.put("ENC_PWD", URLEncoder.encode(buffer.substring(offset + 1), "UTF-8"));
                    map.put("MDN", getMDN(context));
                    return map;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private String getMDN(Context context) {
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String line1Number = tMgr.getLine1Number();
        BLog.d("getMDN: "+line1Number);
        return line1Number;
    }

    private String getTTMAppID() {
        return "A000ST0045";
    }

}
