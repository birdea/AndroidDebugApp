package com.risewide.bdebugapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;

import com.risewide.bdebugapp.communication.util.TToast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 1. 8.
 */
public class MainAssistActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_assist);
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btnSetAssistApp: {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS), 0);
                break;
            }
            case R.id.btnGetAssistApp: {
                showCurrentAssistInfo();
                break;
            }
        }
    }

    private void showCurrentAssistInfo() {
        Context context = getApplicationContext();
        ComponentName cpName;
        cpName = MainAssistActivity.getCurrentAssist(context);
        String out1 = (cpName!=null)?cpName.flattenToShortString():"null";
        cpName = MainAssistActivity.getCurrentAssistWithReflection(context);
        String out2 = (cpName!=null)?cpName.flattenToShortString():"null";
        TToast.show(context, "currentAssist:"+out1+","+out2);
    }


    public static ComponentName getCurrentAssistWithReflection(Context context) {
        try {
            Method myUserIdMethod = UserHandle.class.getDeclaredMethod("myUserId");
            myUserIdMethod.setAccessible(true);
            Integer userId = (Integer) myUserIdMethod.invoke(null);
            if (userId != null) {
                Constructor constructor = Class.forName("com.android.internal.app.AssistUtils").getConstructor(Context.class);
                Object assistUtils = constructor.newInstance(context);
                Method getAssistComponentForUserMethod = assistUtils.getClass().getDeclaredMethod("getAssistComponentForUser", int.class);
                getAssistComponentForUserMethod.setAccessible(true);
                return (ComponentName) getAssistComponentForUserMethod.invoke(assistUtils, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ComponentName getCurrentAssist(Context context) {
        final String setting = Settings.Secure.getString(context.getContentResolver(), "assistant");
        if (setting != null) {
            return ComponentName.unflattenFromString(setting);
        }
        return null;
    }
}
