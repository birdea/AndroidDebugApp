package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgSendType;
import com.risewide.bdebugapp.communication.model.MsgSendData;
import com.risewide.bdebugapp.communication.util.OnHandyEventListener;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.util.SLog;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;

/**
 * <p>*Send message*
 * <p>Supported for SMS, LMS
 * <p>Not-Supported for MMS
 * <p>Android API : android.telephony.{@link SmsManager}
 *
 * {@See https://developer.android.com/reference/android/telephony/SmsManager.html}
 *
 * <p>*Description*
 * <br>{@link #setCommMsgSendType(CommMsgSendType type)} 메시지 전송 타입 설정
 * <br>{@link CommMsgSendType#AUTO_ADJUST} 로 설정시 메시지 길이에 따라 자동 문자 전송 (SMS or LMS)
 * <br>{@link #send(Context, OnSendTextMessageListener)} 메시지 전송 (main) 메소드
 * <br>{@link MessageSendListener} 메시지 발신의 성공 여부 확인을 위한 브로드캐스트 리시버
 * <br>{@link MessageDeliveryListener} 발신한 메시지의 수신 여부 확인을 위한 브로드캐스트 리시버
 *
 * <p>Created by birdea on 2017-08-02.
 */

public class CommUnifyMessageSender extends AbsMessageSender {

	public static final boolean IS_SUPPORT_MMS = false;

	private CommMsgSendType mCommMsgSendType;
	private MsgSendData mMsgSendData;

	private OnHandyEventListener mOnHandyEventListener;

	public CommUnifyMessageSender() {
		mMsgSendData = new MsgSendData();
		mCommMsgSendType = CommMsgSendType.AUTO_ADJUST;
	}

	public void setOnHandyEventListener(OnHandyEventListener listener) {
		mOnHandyEventListener = listener;
	}
	private void notifyOnEventListener(String msg) {
		SLog.i("CommUnifyMessageSender", msg);
		if(mOnHandyEventListener ==null) {
			return;
		}
		mOnHandyEventListener.onEvent(msg);
	}

	private static final int MY_PERMISSION_REQUEST = 0x01;
	public boolean hasPermission(Context context) {
		// check permission on runtime of execution
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
				if (context instanceof Activity) {
					Activity activity = (Activity) context;
					ActivityCompat.requestPermissions(activity,
							new String[]{Manifest.permission.SEND_SMS},
							MY_PERMISSION_REQUEST);
					activity.finish();
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public void send(Context context, OnSendTextMessageListener listener) {
		// check for data to be ready to send
		if (!isValidData(context, mMsgSendData)) {
			notifyOnEventListener("validation fails..");
			TToast.show(context, "invalid data, check again plz..");
			return;
		}
		notifyOnEventListener("validation is ok.. send on protocol:"+ mCommMsgSendType);
		// switch the delivery way of automatic or manual
		if (CommMsgSendType.AUTO_ADJUST.equals(mCommMsgSendType)) {
			TToast.show(context, "send on automatic-protocol");
			sendOnAutomaticProtocol(context, listener);
		} else {
			TToast.show(context, "send on manual-protocol");
			sendOnManualProtocol(context, listener);
		}
	}

	// setter
	public void setCommMsgSendType(CommMsgSendType type) {
		mCommMsgSendType = type;
	}
	public CommMsgSendType getCommMsgSendType(){
		return mCommMsgSendType;
	}

	public MsgSendData getMsgSendData() {
		return mMsgSendData;
	}
	public void setMsgSendData(MsgSendData data) {
		mMsgSendData = data;
	}

	private void sendOnAutomaticProtocol(Context context, OnSendTextMessageListener listener) {
		SmsManager smsManager = SmsManager.getDefault();
		// MMS
		if (IS_SUPPORT_MMS && mMsgSendData.imageDataUri != null) {
			sendOnMMS(context, listener);
			return;
		}
		// LMS OR SMS
		List<String> msgList = smsManager.divideMessage(mMsgSendData.textMessage);
		int msgLength = msgList.size();
		if (msgLength > 1) {
			sendOnLMS(context, listener);
		} else {
			sendOnSMS(context, listener);
		}
	}

	private void sendOnManualProtocol(Context context, OnSendTextMessageListener listener) {
		switch(mCommMsgSendType) {
			case SMS:
				sendOnSMS(context, listener);
				break;
			case LMS:
				sendOnLMS(context, listener);
				break;
			case MMS:
				sendOnMMS(context, listener);
				break;
			default:
				break;
		}
	}

	private void sendOnLMS(Context context, OnSendTextMessageListener listener) {
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, scAddress, text;
		ArrayList<PendingIntent> sentIntentList = new ArrayList<>();
		ArrayList<PendingIntent> deliveryIntentList = new ArrayList<>();
		IntentFilter filterMsgSend = new IntentFilter(ACTION_SMS_SEND);
		IntentFilter filterMsgDelivery = new IntentFilter(ACTION_SMS_DELIVERY);
		//
		destinationAddress = mMsgSendData.phoneNumberReceiver;
		scAddress = null;
		text = mMsgSendData.textMessage;
		//
		ArrayList<String> msgList = smsManager.divideMessage(text);
		notifyOnEventListener("sendOnLMS [size]:"+msgList.size());
		for(int i=0;i<msgList.size();i++) {
			String actionSend = String.format("%s", ACTION_SMS_SEND);
			String actionDelivery = String.format("%s", ACTION_SMS_DELIVERY);
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(actionSend), PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(actionDelivery), PendingIntent.FLAG_UPDATE_CURRENT);
			//- set ArrayList<PendingIntent>
			sentIntentList.add(sentIntent);
			deliveryIntentList.add(deliveryIntent);
			//- set IntentFilter
			filterMsgSend.addAction(actionSend);
			filterMsgDelivery.addAction(actionDelivery);
		}
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, msgList.size(), listener), filterMsgSend);
		context.registerReceiver(new MessageDeliveryListener(context, msgList.size(), listener), filterMsgDelivery);
		//
		notifyOnEventListener("sendOn[LMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendMultipartTextMessage(destinationAddress, scAddress, msgList, sentIntentList, deliveryIntentList);
	}

	private void sendOnSMS(Context context, OnSendTextMessageListener listener) {
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, scAddress, text;
		PendingIntent sentIntent, deliveryIntent;
		//
		destinationAddress = mMsgSendData.phoneNumberReceiver;
		scAddress = null;
		text = mMsgSendData.textMessage;
		//
		sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SEND), PendingIntent.FLAG_UPDATE_CURRENT);
		deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERY), PendingIntent.FLAG_UPDATE_CURRENT);
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, 1, listener), new IntentFilter(ACTION_SMS_SEND));
		context.registerReceiver(new MessageDeliveryListener(context, 1, listener), new IntentFilter(ACTION_SMS_DELIVERY));
		//
		notifyOnEventListener("sendOn[SMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
	}

	private void sendOnMMS(Context context, OnSendTextMessageListener listener) {
		notifyOnEventListener("sendOn[MMS] IS_SUPPORT_MMS:"+IS_SUPPORT_MMS);
		if(IS_SUPPORT_MMS==false) {
			return;
		}
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress;
		PendingIntent sentIntent;
		Uri contentUri = mMsgSendData.imageDataUri;
		//
		destinationAddress = mMsgSendData.phoneNumberReceiver;
		Bundle configOverrides = smsManager.getCarrierConfigValues();
		//
		sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SEND), PendingIntent.FLAG_UPDATE_CURRENT);
		// deliveryIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_SMS_DELIVERY), PendingIntent.FLAG_UPDATE_CURRENT);
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, 1, listener), new IntentFilter(ACTION_SMS_SEND));
		context.registerReceiver(new MessageDeliveryListener(context, 1, listener), new IntentFilter(ACTION_SMS_DELIVERY));
		//
		notifyOnEventListener("sendOn[MMS] addr:"+destinationAddress+",contentUri:"+contentUri);
		smsManager.sendMultimediaMessage(context, contentUri, destinationAddress, configOverrides, sentIntent);
	}

	private boolean isValidData(Context context, MsgSendData messageData) {

		if (TextUtils.isEmpty(messageData.phoneNumberReceiver)) {
			notifyOnEventListener("Invalid [destinationAddress]");
			TToast.show(context, "Invalid [destinationAddress]");
			return false;
		}
		if (TextUtils.isEmpty(messageData.textMessage)) {
			notifyOnEventListener("Invalid [message is empty]");
			TToast.show(context, "Invalid [message  is empty]");
			return false;
		}
		return true;
	}

	private static final String ACTION_SMS_SEND = "ACTION_SMS_SEND";
	private static final String ACTION_SMS_DELIVERY = "ACTION_SMS_DELIVERY";

	private class MessageSendListener extends BroadcastReceiver {
		private Context mContext;
		private final int mMsgSize;
		private int mCntSuccess;
		private int mCntFail;
		private OnSendTextMessageListener mOnSendTextMessageListener;

		MessageSendListener(Context cont, int size, OnSendTextMessageListener listener){
			mContext = cont;
			mMsgSize = size;
			mOnSendTextMessageListener = listener;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			int resCode = getResultCode();
			notifyOnEventListener("MessageSendListener.onReceive - resCode:"+resCode+",mMsgSize:"+ mMsgSize +",mCntSuccess:"+ mCntSuccess +",mCntFail:"+ mCntFail);
			switch(resCode){
				case Activity.RESULT_OK:
					mCntSuccess++;
					SLog.d("SMS send, RESULT_OK");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_GENERIC_FAILURE");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_NO_SERVICE");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_RADIO_OFF");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_NULL_PDU");
					break;
				case 5:/*SmsManager.RESULT_ERROR_LIMIT_EXCEEDED*/
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_LIMIT_EXCEEDED");
					break;
				case 6:/*SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE*/
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_FDN_CHECK_FAILURE");
					break;
				default:
					mCntFail++;
					SLog.d("SMS send, RESULT_ERROR_UNKNOWN");
					break;
			}
			notifyCompleteEvent();
		}

		private void notifyCompleteEvent() {
			int cntTotal = mCntSuccess + mCntFail;
			//
			if (mMsgSize <= cntTotal) {
				SLog.d("SMS send complete!!!");
				mContext.unregisterReceiver(this);
				notifyOnEventListener("MessageSendListener.complete! size:"+ mMsgSize +",cntTotal:"+cntTotal+",mCntSuccess:"+ mCntSuccess +",mCntFail:"+ mCntFail);
				if (mOnSendTextMessageListener != null) {
					mOnSendTextMessageListener.onSent(cntTotal== mCntSuccess);
				}
			}
		}
	}
	private class MessageDeliveryListener extends BroadcastReceiver {
		private Context mContext;
		private final int mMsgSize;
		private int mCntSuccess;
		private int mCntFail;
		private OnSendTextMessageListener mOnSendTextMessageListener;

		MessageDeliveryListener(Context cont, int size, OnSendTextMessageListener listener){
			mContext = cont;
			mMsgSize = size;
			mOnSendTextMessageListener = listener;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			int resCode = getResultCode();
			notifyOnEventListener("MessageDeliveryListener.onReceive - resCode:"+resCode+",mMsgSize:"+ mMsgSize +",mCntSuccess:"+ mCntSuccess +",mCntFail:"+ mCntFail);
			switch (resCode){
				case Activity.RESULT_OK:
					mCntSuccess++;
					SLog.d("SMS delivery, RESULT_OK");
					break;
				case Activity.RESULT_CANCELED:
					mCntFail++;
					SLog.d("SMS delivery, RESULT_CANCELED");
					break;
				default:
					mCntFail++;
					SLog.d("SMS delivery, RESULT_ERROR_UNKNOWN");
					break;
			}
			notifyCompleteEvent();
		}

		private void notifyCompleteEvent() {
			int cntTotal = mCntSuccess + mCntFail;
			//
			if (mMsgSize <= cntTotal) {
				SLog.d("SMS delivery complete!!!");
				mContext.unregisterReceiver(this);
				notifyOnEventListener("MessageDeliveryListener.complete! size:"+ mMsgSize +",cntTotal:"+cntTotal+",mCntSuccess:"+ mCntSuccess +",mCntFail:"+ mCntFail);
				if (mOnSendTextMessageListener != null) {
					mOnSendTextMessageListener.onReceived(cntTotal== mCntSuccess);
				}
			}
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////

	public static int getCountOfDivideMessage(String text) {
		if (TextUtils.isEmpty(text)) {
			return 0;
		}
		SmsManager smsManager = SmsManager.getDefault();
		List<String> list = smsManager.divideMessage(text);
		if (list == null) {
			return 0;
		}
		return list.size();
	}
}
