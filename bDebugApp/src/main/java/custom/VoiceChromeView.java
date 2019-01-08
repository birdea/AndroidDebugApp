package custom;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by minsoo on 2017. 9. 3..
 */

public class VoiceChromeView extends LinearLayout {

    private static final String TAG = "VoiceChromeView";

    public VoiceChromeView(Context context) {
        super(context);
    }


    /*public static void onASRStateChanged(AsrState asrstate, ChromeBarView chromeBarView) {
        BLog.d(TAG, "onASRStateChanged " + asrstate);
        int visibility = -1;

        ChromeBarView mChromeBarView = chromeBarView;

        switch (asrstate) {
            case READY:
                visibility = View.VISIBLE;
                mChromeBarView.setBarType(ChromeBarType.CBTReady);
                break;
            case SPEECH_START:
                //startLevelThread(mRunnableReadSpeechLevel);
                mChromeBarView.setBarType(ChromeBarType.CBTSpeaking);
                break;
            case SPEECH_END:
                //mChromeBarView.setBarTypeAfterDelay(ChromeBarType.CBTThinking);
                mChromeBarView.setBarType(ChromeBarType.CBTThinking);
                visibility = View.VISIBLE;
                //interruptLevelThread();
                break;
            case ERROR:
                //interruptLevelThread();
                mChromeBarView.setBarType(ChromeBarType.CBTError);
                break;
            case CANCELED:
                //interruptLevelThread();
                visibility = View.GONE;
                break;
            //case ERROR_WITH_TTS:
            //case SPEAKING:
                //mChromeBarView.setBarType(ChromeBarType.CBTSpeaking);
                //startLevelThread(mRunnableReadSpeakingLevel);
                //visibility = View.VISIBLE;
                //break;
            default:
        }

        //render(asrstate, visibility);
    }*/

    /*static final String TAG = "VoiceChromeView";
    private static final long READ_LEVEL_INTERVAL = 50L;
    @SuppressWarnings("unused")
    private AccelerateInterpolator decInterpolator;
    private ChromeBarView mChromeBarView;
    private FrameLayout mVoiceChromeViewFrame;
    private TextView mWatermark;
    private TextView mWatermarkASR;
    private boolean mIsBasicActionTTS;

    private Thread mThreadLevel;
    private final Runnable mRunnableReadSpeakingLevel = new Runnable() {
        @Override
        public void run() {
            int speakingLevel = 0;
            Thread.currentThread().setName("THRD_READ_SPEAKING");
            BLog.w(TAG, "Read Speaking Level Started");
            while (!Thread.currentThread().isInterrupted()) {
                int updown = speakingLevel / 10;
                int level = speakingLevel % 10;
                int realLevel;
                if (updown == 0 || updown % 2 == 0) {
                    realLevel = level;
                } else {
                    realLevel = 9 - level;
                }
                //BLog.v(TAG, "speakingLevel: " + realLevel);
                setSpeakingLevel(realLevel);
                speakingLevel++;
                try {
                    Thread.sleep(READ_LEVEL_INTERVAL);
                } catch (InterruptedException e) {
                    BLog.w(TAG, "Interrupted Read Speaking Level THRD " + e);
                    break;
                }
            }
        }
    };

    private final Runnable mRunnableReadSpeechLevel = new Runnable() {
        @Override
        public void run() {
            Thread.currentThread().setName("THRD_READ_SPEECH");
            BLog.w(TAG, "Read Speech Level Started");
            while (!Thread.currentThread().isInterrupted()) {
                *//*final int level = NuguAICloudManager.getInstance().getSpeechLevel();
                //BLog.v(TAG, "speechLevel: " + level);
                setListeningLevel(level);
                try {
                    Thread.sleep(READ_LEVEL_INTERVAL);
                } catch (InterruptedException e) {
                    BLog.w(TAG, "Interrupted Read Speech Level THRD " + e);
                    break;
                }*//*
            }
        }
    };

    public VoiceChromeView(Context context) {
        super(context);
        inflateView(context);
    }

    public VoiceChromeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public VoiceChromeView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        inflateView(context);
    }

    @Deprecated
    public void clearView() {
        setSTT("");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        interruptLevelThread();
    }

    *//**
     * layout inflate
     *//*
    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(R.layout.voice_chrome_view, null);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(view, params);
            initView();
            //decInterpolator = new AccelerateInterpolator(1.f);
        }
    }

    private void initView() {
        mVoiceChromeViewFrame = (FrameLayout) findViewById(R.id.voicechrome_view_frame);
        mChromeBarView = (ChromeBarView) findViewById(R.id.chrome_bar_view);
        mWatermark = (TextView) findViewById(R.id.watermark);
        mWatermarkASR = (TextView) findViewById(R.id.watermark_asr);

        //if (BuildConfig.SHOW_VERSION_WATERMARK || NuguReleaseManager.RELEASE_TARGET != NuguReleaseManager.ReleaseMode.REAL) {
            findViewById(R.id.watermark_layout).setVisibility(VISIBLE);
            mWatermark.setText(getWaterMarkString());
        //} else {
        //    findViewById(R.id.watermark_layout).setVisibility(GONE);
        //}
        BLog.d(TAG, getWaterMarkString());

        //onDuxVisibilityChanged();
    }

    private String getWaterMarkString() {
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo stbPackageInfo = SystemServiceManager.getInstance().getPackageManager()
                    .getPackageInfo("com.skt.aidev.nugujuniorphone", 0);
            if (stbPackageInfo != null) {
                sb.append("mini Ver: ")
                        .append(stbPackageInfo.versionName)
                        .append(", VC: ")
                        .append(stbPackageInfo.versionCode);
            }
        } catch (PackageManager.NameNotFoundException ignored) {}
        sb.append(", Fm: ").append(NuguPreferenceManager.getInstance().getFirmwareVersion()).append('\n');

        switch (NuguReleaseManager.RELEASE_TARGET) {
            case DEV:
                sb.append("DEV");
                break;
            case DTG:
                sb.append("DTG");
                break;
            case STG:
                sb.append("STG");
                break;
            case QA01:
                sb.append("QA01");
                break;
            case QA02:
                sb.append("QA02");
                break;
            case RTG:
                sb.append("RTG");
                break;
            case REAL:
                sb.append("REAL");
                break;
            default:
        }

        // TODO: 서버에 올려줄 수 있는 형태로 변경.
        sb.append(", 내장 W_UP: ").append(NuguDeviceTypeManager.getInstance().isUseInternalizedWakeup());
//        sb.append(" ").append(NuguPreferenceManager.getInstance().getAppVersionName()).append(" ").append(BuildConfig.ENV);
        return sb.toString();
    }

    @Deprecated
    private void onDuxVisibilityChanged() {
        if (isDuxVisible()) {
            mVoiceChromeViewFrame.setBackgroundColor(Color.parseColor("#D9000000"));
        } else {
            mVoiceChromeViewFrame.setBackgroundResource(R.drawable.nugu_general_vc_bg);
        }
    }

    public boolean isDuxVisible() {
//        return TDuxControlManager.getInstance().isDuxShown();
        //TODO(SANGWOOK): Temporary commented
        return true;
    }

    @Deprecated
    public void setSTT(String stt) {

    }

    public void onTTSState(TTSConfig.TTSState ttsState) {
        switch (ttsState) {
            case START:
                if (!mIsBasicActionTTS) { //If Speaking, leave it
                    onASRStateChanged(AsrState.CANCELED); //show
                }
                break;
            default:
                onASRStateChanged(AsrState.CANCELED); //hide
                break;
        }
        BLog.d(TAG, "onTTSState " + ttsState + " mIsBasicActionTTS: " + mIsBasicActionTTS);
    }

    @Deprecated
    public void onBasicActionTTSStateChanged(TTSConfig.TTSState ttsState) {
        switch (ttsState) {
            case START:
                mIsBasicActionTTS = true;
                onASRStateChanged(AsrState.SPEAKING); //hide
                break;
            default:
                mIsBasicActionTTS = false;
                break;
        }
        BLog.d(TAG, "onBasicActionTTSStateChanged " + ttsState + " mIsBasicActionTTS: " + mIsBasicActionTTS);
    }

    @Deprecated
    public void onNuguStateCommand(NuguStateCommand state) {
        switch (state) {
            case MEDIA_PLAY:
                mIsBasicActionTTS = true;
                onASRStateChanged(AsrState.SPEAKING); //hide
                break;
            case MEDIA_STOP:
                onASRStateChanged(AsrState.CANCELED); //hide
            default:
                mIsBasicActionTTS = false;
                break;
        }
        BLog.d(TAG, "onNuguStateCommand " + state + " mIsBasicActionTTS: " + mIsBasicActionTTS);
    }

    public void onASRStateChanged(AsrState asrstate) {
        BLog.d(TAG, "onASRStateChanged " + asrstate);
        int visibility = -1;

        switch (asrstate) {
            case READY:
                visibility = View.VISIBLE;
                mChromeBarView.setBarType(ChromeBarType.CBTReady);
                break;
            case SPEECH_START:
                //startLevelThread(mRunnableReadSpeechLevel);
                mChromeBarView.setBarType(ChromeBarType.CBTSpeaking);
                break;
            case SPEECH_END:
                //mChromeBarView.setBarTypeAfterDelay(ChromeBarType.CBTThinking);
                visibility = View.VISIBLE;
                //interruptLevelThread();
                break;
            case ERROR:
                //interruptLevelThread();
                mChromeBarView.setBarType(ChromeBarType.CBTError);
                break;
            case CANCELED:
                //interruptLevelThread();
                visibility = View.GONE;
                break;
            case ERROR_WITH_TTS:
            case SPEAKING:
                mChromeBarView.setBarType(ChromeBarType.CBTSpeaking);
                //startLevelThread(mRunnableReadSpeakingLevel);
                visibility = View.VISIBLE;
                break;
            default:
        }

        render(asrstate, visibility);
    }

    private void render(final AsrState asrstate, final int visibility) {
        *//*ApplicationDelegate.getInstance().getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.SHOW_VERSION_WATERMARK && mWatermarkASR != null) {
                    mWatermarkASR.setText("ASRState : " + asrstate);
                }

                if (visibility != -1) { //visibility has to change
                    VoiceChromeView.this.setVisibility(visibility);
                }
            }
        });*//*
    }

    private synchronized void interruptLevelThread() {
        if (mThreadLevel != null) {
            if (mThreadLevel.isAlive() && !mThreadLevel.isInterrupted()) {
                mThreadLevel.interrupt();
            }
        }
    }

    private synchronized void startLevelThread(Runnable runnable) {
        interruptLevelThread();
        mThreadLevel = new Thread(runnable);
        mThreadLevel.start();
    }
//    public int convertedLevel(int level) {
//        float ratio = 1.f * (level - 200000) / 600000;
//        if (ratio < 0) ratio = 0;
//        if (ratio > 1) ratio = 1;
//        int val = (int)(decInterpolator.getInterpolation(ratio) * 20);
//
//        return val;
//    }

    public void setListeningLevel(int level) {
        if (mChromeBarView.getBarType() == ChromeBarType.CBTListening) {
//            int convLevel = convertedLevel(level);
            int convLevel = convertToVisualValue(20, level, 1.f);
            mChromeBarView.setLevel(convLevel);
        }
    }

    public void setSpeakingLevel(int level) {
        if (mChromeBarView.getBarType() == ChromeBarType.CBTSpeaking) {
            int level0 = convertToVisualValue(20, level, 1.0f);
            mChromeBarView.setLevel(level0);
        }
    }

    private int convertToVisualValue(final int stepSize, final int voiceLevel, float gainValue) {
        int levelOffset = 0;
        int convertLevel = voiceLevel * 2;//실제 볼륨 셋팅은 1~10 인데 이미지는 20장임으로  *2를 수행한다
        Random random = new Random();
        float randomFloat = random.nextFloat();

        if (voiceLevel < 4) {
            // 소리가 작을때는 파동이 거의 없는 것이 좋을듯
        } else {
            if (randomFloat < 0.2f) {
                levelOffset = -2;
            } else if (randomFloat < 0.4f) {
                levelOffset = -1;
            } else if (randomFloat > 0.8f) {
                levelOffset = +2;
            } else if (randomFloat > 0.6f) {
                levelOffset = +1;
            } else {
                levelOffset = 0;
            }
            convertLevel = convertLevel + levelOffset;

            if (convertLevel > stepSize - 1) {
                convertLevel = stepSize - 1;
            } else if (convertLevel < 0) {
                convertLevel = 0;

            }
        }

        return (int) (convertLevel * gainValue);
    }*/
}
