package com.risewide.bdebugapp.external;

import java.lang.ref.WeakReference;
import java.util.List;

import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.external.naver.NaverRecognizer;
import com.risewide.bdebugapp.external.naver.NaverTTS;
import com.risewide.bdebugapp.external.naver.config.NaverApiConfig;
import com.risewide.bdebugapp.util.SLog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by birdea on 2017-02-14.
 */

public class SpeechDemoNaverActivity extends Activity {

	private static final String TAG = SpeechDemoNaverActivity.class.getSimpleName();

	private SpeechDemoNaverActivity.RecognitionHandler handler;
	private NaverRecognizer naverRecognizer;

	private TextView txtResult;
	private Button btnStart;
	private String mResult;

	//private AudioWriterPCM writer;

	private String lastWord;
	//
	private SttQualityTestRecorder recorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo_naver);

		txtResult = (TextView) findViewById(R.id.txt_result);
		btnStart = (Button) findViewById(R.id.btn_start);

		handler = new SpeechDemoNaverActivity.RecognitionHandler(this);
		naverRecognizer = new NaverRecognizer(this, handler, NaverApiConfig.CLIENT_ID);

		btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				recorder.setActivate(false);
				if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
					// Start button is pushed when SpeechRecognizer's state is inactive.
					// Run SpeechRecongizer by calling recognize().
					startRecognize(false);
				} else {
					Log.d(TAG, "stop and wait Final Result");
					btnStart.setEnabled(false);
					naverRecognizer.getSpeechRecognizer().stop();
				}
			}
		});
		//
		recorder = new SttQualityTestRecorder("naver");
	}

	public void onClickButton(View view) {
		switch (view.getId()) {
			case R.id.btn_get_tts_mp3: {
				NaverTTS.getMp3File(SpeechDemoNaverActivity.this, lastWord);
				break;
			}
			case R.id.btn_start_loop: {
				recorder.init();
				startRecognize(true);
				break;
			}
			case R.id.btn_stop: {
				recorder.setActivate(false);
				recorder.release();
				naverRecognizer.getSpeechRecognizer().stop();
				btnStart.setEnabled(true);
				btnStart.setText(R.string.str_start);
				break;
			}
			default:
				break;
		}
	}

	private void startRecognize(boolean loop) {
		recorder.setActivate(loop);
		mResult = "";
		txtResult.setText("Connecting...");
		btnStart.setText(R.string.str_stop);
		naverRecognizer.recognize();
	}

	private void loopRecognize() {
		if(recorder.isActivate() == false) {
			return;
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				naverRecognizer.recognize();
			}
		}, 1000);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// NOTE : initialize() must be called on start time.
		naverRecognizer.getSpeechRecognizer().initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mResult = "";
		txtResult.setText("");
		btnStart.setText(R.string.str_start);
		btnStart.setEnabled(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// NOTE : release() must be called on stop time.
		naverRecognizer.getSpeechRecognizer().release();
	}

	// Declare handler for handling SpeechRecognizer thread's Messages.
	static class RecognitionHandler extends Handler {
		private final WeakReference<SpeechDemoNaverActivity> mActivity;

		RecognitionHandler(SpeechDemoNaverActivity activity) {
			mActivity = new WeakReference<SpeechDemoNaverActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SpeechDemoNaverActivity activity = mActivity.get();
			if (activity != null) {
				activity.handleMessage(msg);
			}
		}
	}

	// Handle speech recognition Messages.
	private void handleMessage(Message msg) {
		switch (msg.what) {
			case R.id.clientReady:
				// Now an user can speak.
				SLog.d(TAG, "NaverSTT: clientReady");
				txtResult.setText("Connected");
				//writer = new AudioWriterPCM(
				//		Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
				//writer.open("Test");
				//recorder.write("clientReady");
				break;

			case R.id.audioRecording:
				SLog.d(TAG, "NaverSTT: audioRecording");
				//writer.write((short[]) msg.obj);
				break;

			case R.id.partialResult:
				SLog.d(TAG, "NaverSTT: partialResult");
				// Extract obj property typed with String.
				mResult = (String) (msg.obj);
				txtResult.setText(mResult);
				break;

			case R.id.finalResult:
				SLog.d(TAG, "NaverSTT: finalResult");
				// Extract obj property typed with String array.
				// The first element is recognition result for speech.
				lastWord = null;
				SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
				List<String> results = speechRecognitionResult.getResults();
				StringBuilder strBuf = new StringBuilder();
				for(String result : results) {
					if(lastWord == null) {
						lastWord = result;
					}
					strBuf.append(result);
					strBuf.append("\n");
				}
				mResult = strBuf.toString();
				txtResult.setText(mResult);
				recorder.write(lastWord);
				break;

			case R.id.recognitionError:
				SLog.d(TAG, "NaverSTT: recognitionError");
				//if (writer != null) {
				//	writer.close();
				//}

				mResult = "Error code : " + msg.obj.toString();
				txtResult.setText(mResult);
				btnStart.setText(R.string.str_start);
				btnStart.setEnabled(true);
				//recorder.write("recognitionError");
				loopRecognize();
				break;

			case R.id.clientInactive:
				SLog.d(TAG, "NaverSTT: clientInactive");
				//if (writer != null) {
				//	writer.close();
				//}

				btnStart.setText(R.string.str_start);
				btnStart.setEnabled(true);
				//recorder.write("clientInactive");
				loopRecognize();
				break;
		}
	}
}
