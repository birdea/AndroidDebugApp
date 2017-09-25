package com.risewide.bdebugapp.util;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by birdea on 2016-11-16.
 * #References
 * 1. https://developer.android.com/training/managing-audio/audio-focus.html
 * 2. http://202psj.tistory.com/505
 */
public class AudioFocusManager {

	private AudioFocusManager() {
	}

	private static AudioFocusManager instance;

	public static AudioFocusManager getInstance() {
		if(instance == null) {
			synchronized (AudioFocusManager.class) {
				if(instance == null) {
					instance = new AudioFocusManager();
				}
			}
		}
		return instance;
	}

	private static final int STREAM_TYPE = AudioManager.STREAM_SYSTEM; // Use the music stream.
	/**
	 1. AUDIOFOCUS_GAIN : 음악이나 Video 처럼 얼마나 오랫동안 재생을 해야하는지 모르는 경우 사용하도록 권장.
	 2. AUDIOFOCUS_GAIN_TRANSIENT : 잠시 동안 Audio Focus 를 얻어야 하는 경우 사용하도록 권장.
	 3. AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK : Audio Focus 를 얻더라도, 그 뒤에 소리들이 볼륨만 낮게 된 상태에서 내가 낼려고 하는 소리와 같이 나와야 하는 경우 사용하도록 권장.
	 */
	private static final int FOCUS_MODE = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT; // to indicate a temporary gain or request of audio focus

	private AudioManager getAudioManager(Context context){
		if (null == context) {
			return null;
		}
		Context applicationContext = context.getApplicationContext();
		return (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
	}

	public void init(Context context, AudioManager.OnAudioFocusChangeListener listener) {
		AudioManager audioManager = getAudioManager(context);
		if (null == audioManager) {
			return;
		}
		int result = audioManager.requestAudioFocus(listener, STREAM_TYPE, FOCUS_MODE);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			SLog.i("init - result [SUCCESS] AUDIOFOCUS_REQUEST_GRANTED ! = " + result);
		} else {
			SLog.i("init - result [FAIL] AUDIOFOCUS_REQUEST_NOT_GRANTED ! = " + result);
		}
	}

	public void release(Context context, AudioManager.OnAudioFocusChangeListener listener) {
		AudioManager audioManager = getAudioManager(context);
		if (null == audioManager) {
			return;
		}
		int result = audioManager.abandonAudioFocus(listener);
		SLog.i("release - abandonAudioFocus > result : " + result);
		switch (result) {
			case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
				SLog.i("release - FAIL (AUDIOFOCUS_REQUEST_FAILED): " + result);
				break;
			case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
				SLog.i("release - SUCCESS (AUDIOFOCUS_REQUEST_GRANTED): " + result);
				break;
			default:
				break;
		}
	}

	public void takeInFocus(Context context) {
		AudioManager audioManager = getAudioManager(context);
		if (null == audioManager) {
			return;
		}
		// Request audio focus for playback
		int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, STREAM_TYPE, FOCUS_MODE);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			// audioManager.registerMediaButtonEventReceiver(RemoteControlReceiver);
			// Start playback.
			SLog.i("takeInFocus - result [SUCCESS] AUDIOFOCUS_REQUEST_GRANTED ! = " + result);
		} else {
			SLog.i("takeInFocus - result [FAIL] AUDIOFOCUS_REQUEST_NOT_GRANTED ! = " + result);
		}
	}

	public void takeOutFocus(Context context) {
		AudioManager audioManager = getAudioManager(context);
		if (null == audioManager) {
			return;
		}
		int result = audioManager.abandonAudioFocus(onAudioFocusChangeListener);
		switch (result) {
			case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
				SLog.i("takeOutFocus - FAIL (AUDIOFOCUS_REQUEST_FAILED): " + result);
				break;
			case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
				SLog.i("takeOutFocus - SUCCESS (AUDIOFOCUS_REQUEST_GRANTED): " + result);
				break;
			default:
				break;
		}
	}

	public static String getAudioFocusStatus(int focusModeValue) {
		switch(focusModeValue) {
			case AudioManager.AUDIOFOCUS_GAIN:
				return "AUDIOFOCUS_GAIN";
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
				return "AUDIOFOCUS_GAIN_TRANSIENT";
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
				return "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
				return "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
			case AudioManager.AUDIOFOCUS_LOSS:
				return "AUDIOFOCUS_LOSS";
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				return "AUDIOFOCUS_LOSS_TRANSIENT";
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				return "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
		}
		return "Unknown";
	}


	private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			switch(focusChange) {
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					/**
					 * [Pause playback]
					 * Lost focus for a short time, but we have to stop playback.
					 * We don't release the media player because playback is likely to resume
					 * if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
					 */
					SLog.i("onAudioFocusChange - AUDIOFOCUS_LOSS_TRANSIENT : " + focusChange);
					break;
				case AudioManager.AUDIOFOCUS_GAIN:
					/**
					 * [Resume playback]
					 * if (mMediaPlayer == null) initMediaPlayer();
					 * else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
					 * mMediaPlayer.setVolume(1.0f, 1.0f);
					 */
					SLog.i("onAudioFocusChange - AUDIOFOCUS_GAIN : " + focusChange);
					break;
				case AudioManager.AUDIOFOCUS_LOSS:
					/**
					 * [Release player]
					 * Lost focus for an unbounded amount of time: stop playback and release media player
					 * if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
					 * mMediaPlayer.release();
					 * mMediaPlayer = null;
					 */
					SLog.i("onAudioFocusChange - AUDIOFOCUS_LOSS : " + focusChange);
					//am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
					// Stop playback
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
					/**
					 * Lost focus for a short time, but it's ok to keep playing at an attenuated level
					 * if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
					 */
					SLog.i("onAudioFocusChange - AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK : " + focusChange);
					break;
				default:
					SLog.i("onAudioFocusChange - unknown focusChange : " + focusChange);
					break;
			}
		}
	};
}
