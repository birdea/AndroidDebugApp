package com.risewide.bdebugapp.external;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-02-14.
 */

public class SpeechDemoGoogleActivity extends BaseActivity {

	private static final long PARAM_STT_WAITING_TIME = 3000L;
	private static final int PARAM_STT_MAX_RESULT = 15;

	private SpeechRecognizer speechRecognizer;
	//
	private EditText et_speech;
	private ListView lv_status;
	private HandyListAdapter handyListAdapter;

	private SttQualityTestRecorder recorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sttdemo);
		//
		findView();
		init();
		//
		recorder = new SttQualityTestRecorder("google");
		//
		addListParam("onCreate()","let's figure out!");
	}

	private void findView() {
		et_speech = (EditText) findViewById(R.id.et_speech);
		//
		handyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);
		//
		lv_status = (ListView) findViewById(R.id.lv_status);
		lv_status.setAdapter(handyListAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//
		release();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//
		release();
	}

	private void init() {
		if(speechRecognizer == null) {
			addListParam("init()","speechRecognizer.createSpeechRecognizer");
			speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
			speechRecognizer.setRecognitionListener(listener);
		}
	}
	private void release() {
		if(speechRecognizer != null) {
			addListParam("release()","speechRecognizer.destroy");
			speechRecognizer.destroy();
			speechRecognizer = null;
		}
	}

	public void onClickExecute(View view) {
		switch(view.getId()) {
			case R.id.btn_stt_google: {
				addListParam("onClick()","Mode.Google");
				release();
				triggerStt(Mode.Google);
				break;
			}
			case R.id.btn_stt_custom: {
				addListParam("onClick()","Mode.Custom");
				startRecognize(false);
				break;
			}
			case R.id.btn_stt_custom_loop: {
				recorder.init();
				addListParam("onClick()","Mode.Custom(LOOP)");
				startRecognize(true);
				break;
			}
			case R.id.btn_stt_custom_stop: {
				if(speechRecognizer != null) {
					speechRecognizer.cancel();
				}
				recorder.setActivate(false);
				recorder.release();
				break;
			}
			default:
				break;
		}
	}

	private void startRecognize(boolean loop) {
		recorder.setActivate(loop);
		init();
		triggerStt(Mode.Custom);
	}

	private void loopRecognize() {
		if(recorder.isActivate() == false) {

			MediaController.MediaPlayerControl c;
			return;
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				init();
				triggerStt(Mode.Custom);
			}
		}, 1000);
	}

	private enum Mode{
		Google {
			@Override
			public void trigger(Activity activity, String packageName) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
				i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName);    //호출한 패키지
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());                            //음성인식 언어 설정
				i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");                     //사용자에게 보여 줄 글자
				try {
					activity.startActivityForResult(i, REQ_CODE_STT_GOOGLE);
				} catch( ActivityNotFoundException e){
					e.printStackTrace();
				}
			}

			@Override
			public List<String> receive(Intent data) {
				String key = RecognizerIntent.EXTRA_RESULTS;
				List list = data.getStringArrayListExtra(key);        //인식된 데이터 list 받아옴.
				//String[] result = new String[mResult.size()];            //배열생성. 다이얼로그에서 출력하기 위해
				//mResult.toArray(result);                                    //    list 배열로 변환
				return list;
			}
		},
		Custom {
			@Override
			public void trigger(Activity activity, String packageName) {
//				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //음성인식 intent생성
//				i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName);    //데이터 설정
//				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
//				speechRecognizer.startListening(i);
			}

			@Override
			public List<String> receive(Intent data) {
				return null;
			}
		},
		;

		public abstract void trigger(Activity activity, String packageName);
		public abstract List<String> receive(Intent data);
	}

	private void triggerStt(Mode mode) {
		if(null == mode) {
			return;
		}
		if(Mode.Google.equals(mode)) {
			mode.trigger(this, getPackageName());
			SVLog.i("Google.start!");
		} else {

			if(false == SpeechRecognizer.isRecognitionAvailable(this)) {
				SVLog.i("SpeechRecognizer.can not run!");
				//return;
			}
			speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //음성인식 intent생성
			speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //데이터 설정
			speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());                            //음성인식 언어 설정
			speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
			speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, PARAM_STT_WAITING_TIME);
			speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, PARAM_STT_MAX_RESULT);
			speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");
			speechRecognizer.startListening(speechIntent);
			SVLog.i("SpeechRecognizer.startListening");
		}
	}

	private Intent speechIntent;

	final static int REQ_CODE_STT_GOOGLE = 1;
	final static int REQ_CODE_STT_CUSTOM = 2;


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
			case REQ_CODE_STT_GOOGLE: {
				if(resultCode == RESULT_OK && data != null) {
					List<String> list = Mode.Google.receive(data);
					SVLog.i("# speech text list size : "+list.size());
					for(String result : list) {
						addListParam("onActivityResult() > result", result);
					}
					String text = list.get(0);
					if(text != null) {
						addListParam("onActivityResult() > setText", text);
						et_speech.setText(text);
					}
				}
				break;
			}
			case REQ_CODE_STT_CUSTOM: {
				break;
			}
		}
	}

	private void addListParam(String head, Object body) {
		HandyListAdapter.Param param = new HandyListAdapter.Param();
		param.msgHead = head;
		param.msgBody = String.valueOf(body);
		handyListAdapter.add(param);
		handyListAdapter.notifyDataSetChanged();
		//
	}


	private RecognitionListener listener = new RecognitionListener() {
		@Override
		public void onReadyForSpeech(Bundle params) {
			SVLog.i("#onReadyForSpeech:"+params);
			addListParam("RecognitionListener().onReadyForSpeech", params);
		}

		@Override
		public void onBeginningOfSpeech() {
			SVLog.i("#onBeginningOfSpeech");
			addListParam("RecognitionListener().onBeginningOfSpeech", "n/a");
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			SVLog.i("#onRmsChanged:"+rmsdB);
			addListParam("RecognitionListener().onRmsChanged", rmsdB);
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			String rececived = (buffer!=null)?buffer.toString():null;
			SVLog.i("#onBufferReceived:"+rececived);
			addListParam("RecognitionListener().onBufferReceived", rececived);
		}

		@Override
		public void onEndOfSpeech() {
			SVLog.i("#onEndOfSpeech");
			addListParam("RecognitionListener().onEndOfSpeech", "n/a");
		}

		@Override
		public void onError(int error) {
			SVLog.i("#onError:" + error);
			switch(error) {
				case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				case SpeechRecognizer.ERROR_NO_MATCH:
					SVLog.i("#onEvent error:" + error + ">> retry.");
					addListParam("RecognitionListener().onEvent", "#error: "+error+">> retry.");
					if(speechRecognizer != null && speechIntent != null) {
						speechRecognizer.startListening(speechIntent);
					}
					break;
				default:
					SVLog.i("#onEvent error:" + error + ">> do nothing.");
					addListParam("RecognitionListener().onEvent", "#error: "+error + ">> do nothing.");
					break;
			}
		}

		@Override
		public void onResults(Bundle results) {
			SVLog.i("#onResults:"+results);
			String key = SpeechRecognizer.RESULTS_RECOGNITION;
			List<String> listResult = results.getStringArrayList(key);        //인식된 데이터 list 받아옴.
			StringBuilder sb = new StringBuilder();
			for(String result : listResult) {
				SVLog.i("#onResults result:"+ result);
				addListParam("RecognitionListener().onResults", "#candidates: "+result);
				sb.append(result);
				et_speech.setText(result);
			}
			String text = listResult.get(0);//sb.toString();
			SVLog.i("#onResults text:"+ text +" /listResult:"+listResult);
			if(text != null) {
				addListParam("RecognitionListener().onResults", "#text: "+text);
				et_speech.setText(text);
				recorder.write(text);
			}
			loopRecognize();
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			SVLog.i("#onPartialResults:"+partialResults);
			String key = SpeechRecognizer.RESULTS_RECOGNITION;
			List<String> listResult = partialResults.getStringArrayList(key);        //인식된 데이터 list 받아옴.
			StringBuilder sb = new StringBuilder();
			for(String result : listResult) {
				SVLog.i("#onPartialResults result:"+ result);
				addListParam("RecognitionListener().onPartialResults", "#result: "+result);
				sb.append(result);
				et_speech.setText(result);
			}
			String text = listResult.get(0);//sb.toString();
			SVLog.i("#onPartialResults text:"+ text +" /listResult:"+listResult);
			if(text != null) {
				addListParam("RecognitionListener().onPartialResults", "#text: "+text);
				et_speech.setText(text);
			}
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			SVLog.i("#onEvent eventType:"+eventType + " /Bundle:"+params);
			addListParam("RecognitionListener().onEvent", "#eventType: "+eventType);
		}
	};
}