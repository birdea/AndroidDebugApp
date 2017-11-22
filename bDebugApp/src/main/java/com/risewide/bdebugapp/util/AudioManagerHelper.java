package com.risewide.bdebugapp.util;

import android.content.Context;
import android.media.AudioManager;


/**
 * @author hyunho.mo
 *
 * @since 2017.10.16
 */
public class AudioManagerHelper {
    private static final String TAG = AudioManagerHelper.class.getSimpleName();


    /**
     * @param ringerMode
     * @return
     */
    public static String getRingerModeLabel(int ringerMode) {
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_SILENT:
                return "RINGER_MODE_SILENT";
            case AudioManager.RINGER_MODE_VIBRATE:
                return "RINGER_MODE_VIBRATE";
            case AudioManager.RINGER_MODE_NORMAL:
                return "RINGER_MODE_NORMAL";
        }
        return "";
    }

    /**
     * @param context
     * @param ringerMode
     */
    public static void setRingerMode(Context context, int ringerMode) {
        AudioManager audioManager = SystemServiceHelper.getAudioManager(context);
        audioManager.setRingerMode(ringerMode);
    }

    /**
     * @param context
     * @param on
     * @return
     */
    public static boolean setSpeakerphoneOn(Context context, boolean on) {
        AudioManager audioManager = SystemServiceHelper.getAudioManager(context);

        boolean result = audioManager.isSpeakerphoneOn();
        if (result != on) {
            audioManager.setSpeakerphoneOn(on);

            result = audioManager.isSpeakerphoneOn();
            SLog.i(TAG, String.format("setSpeakerphoneOn() : on(%s), result(%s)", on, result));
        } else {
            SLog.i(TAG, String.format("setSpeakerphoneOn() : Already %s is set", on));
        }
        return result;
    }

    /**
     * @param context
     * @return
     */
    public static boolean isSpeakerphoneOn(Context context) {
        AudioManager audioManager = SystemServiceHelper.getAudioManager(context);
        return audioManager.isSpeakerphoneOn();
    }
}
