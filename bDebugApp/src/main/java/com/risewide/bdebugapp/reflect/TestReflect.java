package com.risewide.bdebugapp.reflect;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.android.phone.PhoneUtils;
import com.risewide.bdebugapp.MainActivity;
import com.risewide.bdebugapp.util.SLog;
import com.samsung.bluetoothle.BluetoothLEClientProfile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author seungtae.hwang(birdea @ sk.com)
 * @since 2018. 8. 14. created by seungtae.hwang
 */
public class TestReflect {

    private static final String TAG = "TestReflect";

    private static final String SYSTEM_SET_CALL_NOISE_REDUCTION = "call_noise_reduction";
    //https://github.com/ryun/MOD_Note2_call_recording/blob/master/com/android/phone/PhoneApp.smali

    public static void changeByDummyClass(Context context) {

//        boolean flagPre = PhoneUtilEx.isNoiseSuppressionOn(context);
//        if (flagPre) {
//            PhoneUtilEx.turnOnNoiseSuppression(context, false, false);
//        }
//        boolean flagPost = PhoneUtilEx.isNoiseSuppressionOn(context);
//        SLog.d(TAG, String.format("isNoiseSuppressionOn: pre(%s)->post(%s)", flagPre, flagPost));
    }

    //-> 시스템 권한 문제로 사용 불가 (get 가능, put 불가)
    public static void changeBySettings(Context context) {
        int valPre = -1, valPost = -1;
        ContentResolver resolver = context.getContentResolver();
        try {
            // pre value
            valPre = Settings.Global.getInt(resolver, SYSTEM_SET_CALL_NOISE_REDUCTION);
            // put
            Settings.Global.putInt(resolver, SYSTEM_SET_CALL_NOISE_REDUCTION, (valPre==0)?1:0);
            // post value
            valPost = Settings.System.getInt(resolver, SYSTEM_SET_CALL_NOISE_REDUCTION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        SLog.d(TAG, String.format("setValue() pre(%s)->post(%s)", valPre, valPost));
    }

    public static void changeByReflection(Context context) {

        boolean flag = false;
        String exName = MainActivity.class.getName();
        SLog.d(TAG, String.format("setSpeakerphoneOn(%s) packageName:%s", flag, exName));
//PhoneUtilsCommon
        try {
            Class<?> cls = Class.forName(BluetoothLEClientProfile.class.getName());//, true, null);//context.getClassLoader().loadClass(packageName);
            if (cls == null) {
                SLog.d(TAG, "invoke() : loadClass is null.");
                return;
            }
            printClassInfo(cls);

        } catch (Exception e) {
            SLog.w(TAG, e);
        }
    }

    private static void printClassInfo(Class<?> cls) {
        // 가지고 있는 멤버 변수를 출력해보자. public 멤버 변수만 가져온다.
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            SLog.i(TAG, field.getType().getName() + " " + field.getName());
        }
        SLog.i(TAG, "--------------------------------------------");

        // 가지고 있는 메소드의 이름을 출력 해보자. public 메소드만 가져온다.
        Method[] methods = cls.getMethods();
        StringBuffer sb = new StringBuffer();
        for (Method method : methods) {
            sb.append(method.getName());

            // 메소드 인자가 있다면 출력하자.
            Class<?>[] argTypes = method.getParameterTypes();
            sb.append("(");
            int size = argTypes.length;
            for (Class<?> argType : argTypes) {
                String argName = argType.getName();
                sb.append(argName + " val");
                if (--size != 0) {
                    sb.append(", ");
                }
            }
            sb.append(")");

            // 리턴 인자를 출력하자.
            Class<?> returnType = method.getReturnType();
            sb.append(" : " + returnType.getName());

            SLog.i(TAG, sb.toString());
            sb.setLength(0);
        }
        SLog.i(TAG, "--------------------------------------------");
    }
}
