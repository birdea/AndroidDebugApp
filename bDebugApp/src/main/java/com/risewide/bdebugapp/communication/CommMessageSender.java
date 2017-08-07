package com.risewide.bdebugapp.communication;

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

import com.risewide.bdebugapp.communication.helper.TToast;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-02.
 */

public class CommMessageSender {

	public enum ProtocolType {
		AUTO,
		SMS,
		LMS,
		MMS,
	}

	public enum CallMethodType {
		DirectCall,
		Intent,
	}

	public class MessageData {
		public String phoneNumberSender;
		public String phoneNumberReceiver;
		public String nameSender;
		public String nameReceiver;
		public String textMessage;
		public Uri imageDataUri;
	}

	private ProtocolType protocolType;// = ProtocolType.SMS;
	private CallMethodType callMethodType;// = CallMethodType.DirectCall;
	private MessageData messageData;

	public CommMessageSender() {
		messageData = new MessageData();
	}

	public interface OnProcessListener {
		void onEvent(String msg);
	}
	public void setOnProcessListener(OnProcessListener listener) {
		onProcessListener = listener;
	}
	private OnProcessListener onProcessListener;
	private void notifyOnProcessListener(String msg) {
		SVLog.i("CommMessageSender", msg);
		if(onProcessListener ==null) {
			return;
		}
		onProcessListener.onEvent(msg);
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

	// setter
	public void setProtocolType(ProtocolType type) {
		protocolType = type;
	}
	public ProtocolType getProtocolType(){
		return protocolType;
	}
	public void setCallMethodType(CallMethodType type) {
		callMethodType = type;
	}
	public CallMethodType getCallMethodType(){
		return callMethodType;
	}
	public void setNameSender(String name) {
		messageData.nameSender = name;
	}
	public void setNameReceiver(String name) {
		messageData.nameReceiver = name;
	}
	public void setPhoneNumberSender(String phoneNumber) {
		messageData.phoneNumberSender = phoneNumber;
	}
	public void setPhoneNumberReceiver(String phoneNumber) {
		messageData.phoneNumberReceiver = phoneNumber;
	}
	public void setTextMessage(String message) {
		messageData.textMessage = message;
	}
	public void setImageUri(Uri imageUri) {
		messageData.imageDataUri = imageUri;
	}

	public MessageData getMessageData() {
		return messageData;
	}
	// trigger
	public void send(Context context) {
		// check for data to be ready to send
		if (!isValidData(context, messageData)) {
			notifyOnProcessListener("validation fails..");
			TToast.show(context, "invalid data, check again plz..");
			return;
		}
		notifyOnProcessListener("validation is ok.. send on protocol:"+protocolType);
		// switch the delivery way of automatic or manual
		if (ProtocolType.AUTO.equals(protocolType)) {
			TToast.show(context, "send on automatic-protocol");
			sendOnAutomaticProtocol(context);
		} else {
			TToast.show(context, "send on manual-protocol");
			sendOnManualProtocol(context);
		}
	}

	private void sendOnAutomaticProtocol(Context context) {
		SmsManager smsManager = SmsManager.getDefault();
		// MMS
		if (messageData.imageDataUri != null) {
			sendOnMMS(context);
			return;
		}
		// LMS OR SMS
		List<String> msgList = smsManager.divideMessage(messageData.textMessage);
		int msgLength = msgList.size();
		if (msgLength > 1) {
			sendOnLMS(context);
		} else {
			sendOnSMS(context);
		}
	}

	private void sendOnManualProtocol(Context context) {
		switch(protocolType) {
			case SMS:
				sendOnSMS(context);
				break;
			case LMS:
				sendOnLMS(context);
				break;
			case MMS:
				sendOnMMS(context);
				break;
			default:
				break;
		}
	}

	private void sendOnLMS(Context context) {
		notifyOnProcessListener("sendOnLMS");
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, scAddress, text;
		ArrayList<PendingIntent> sentIntentList = new ArrayList<>();
		ArrayList<PendingIntent> deliveryIntentList = new ArrayList<>();
		IntentFilter filterMsgSend = new IntentFilter();
		IntentFilter filterMsgDelivery = new IntentFilter(ACTION_SMS_DELIVERY);
		//
		destinationAddress = messageData.phoneNumberReceiver;
		scAddress = null;
		text = messageData.textMessage;
		//
		ArrayList<String> msgList = smsManager.divideMessage(text);
		notifyOnProcessListener("sendOnLMS [size]:"+msgList.size());
		for(int i=0;i<msgList.size();i++) {
			String action = ACTION_SMS_SEND+"_"+i;
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(action), 0);
			sentIntentList.add(sentIntent);
			filterMsgSend.addAction(action);
		}
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, msgList.size()), filterMsgSend);
		context.registerReceiver(new MessageDeliveryListener(context, msgList.size()), filterMsgDelivery);
		//
		TToast.show(context, "sendOn[LMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendMultipartTextMessage(destinationAddress, scAddress, msgList, sentIntentList, deliveryIntentList);
	}

	private void sendOnSMS(Context context) {
		notifyOnProcessListener("sendOnSMS");
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, scAddress, text;
		PendingIntent sentIntent, deliveryIntent;
		//
		destinationAddress = messageData.phoneNumberReceiver;
		scAddress = null;
		text = messageData.textMessage;
		//
		sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SEND), PendingIntent.FLAG_UPDATE_CURRENT);
		deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERY), PendingIntent.FLAG_UPDATE_CURRENT);
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, 1), new IntentFilter(ACTION_SMS_SEND));
		context.registerReceiver(new MessageDeliveryListener(context, 1), new IntentFilter(ACTION_SMS_DELIVERY));
		//
		TToast.show(context, "sendOn[SMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
	}

	private void sendOnMMS(Context context) {
		notifyOnProcessListener("sendOnMMS");
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, text;
		PendingIntent sentIntent;
		Uri contentUri = messageData.imageDataUri;
		//
		destinationAddress = messageData.phoneNumberReceiver;
		text = messageData.textMessage;
		Bundle configOverrides = new Bundle();
		//
		sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SEND), PendingIntent.FLAG_UPDATE_CURRENT);
		// deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERY), PendingIntent.FLAG_UPDATE_CURRENT);
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, 1), new IntentFilter(ACTION_SMS_SEND));
		context.registerReceiver(new MessageDeliveryListener(context, 1), new IntentFilter(ACTION_SMS_DELIVERY));
		//
		TToast.show(context, "sendOn[MMS] addr:"+destinationAddress+",contentUri:"+contentUri);
		smsManager.sendMultimediaMessage(context, contentUri, destinationAddress, configOverrides, sentIntent);
	}

	private boolean isValidData(Context context, MessageData messageData) {

		if (TextUtils.isEmpty(messageData.phoneNumberReceiver)) {
			notifyOnProcessListener("Invalid [destinationAddress]");
			TToast.show(context, "Invalid [destinationAddress]");
			return false;
		}
		if (TextUtils.isEmpty(messageData.textMessage)) {
			notifyOnProcessListener("Invalid [message is empty]");
			TToast.show(context, "Invalid [message  is empty]");
			return false;
		}
		return true;
	}

	private static final String ACTION_SMS_SEND = "ACTION_SMS_SEND";
	private static final String ACTION_SMS_DELIVERY = "ACTION_SMS_DELIVERY";

	public class MessageSendListener extends BroadcastReceiver {
		private Context context;
		private final int msgSize;
		private int count;
		MessageSendListener(Context cont, int size){
			this.context = cont;
			this.msgSize = size;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultCode = getResultCode();
			notifyOnProcessListener("MessageSendListener.onReceive - resultCode:"+resultCode+",action:"+intent.getAction()+", msgSize:"+msgSize+",count:"+count);
			switch(resultCode){
				case Activity.RESULT_OK:
					TToast.show(context, "SMS 전송 완료");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					TToast.show(context, "SMS 전송 실패");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					TToast.show(context, "서비스 지역이 아닙니다");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					TToast.show(context, "무선(Radio)가 꺼져있습니다");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					TToast.show(context, "PDU Null");
					break;
			}
			//
			if (msgSize <= ++count) {
				this.context.unregisterReceiver(this);
				notifyOnProcessListener("MessageSendListener unregisterReceiver! size:"+msgSize+",count:"+count);
			}
		}
	}
	public class MessageDeliveryListener extends BroadcastReceiver {
		private Context context;
		private final int msgSize;
		private int count;
		MessageDeliveryListener(Context cont, int size){
			this.context = cont;
			this.msgSize = size;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultCode = getResultCode();
			notifyOnProcessListener("MessageDeliveryListener.onReceive - resultCode:"+resultCode+",action:"+intent.getAction()+", msgSize:"+msgSize+",count:"+count);
			switch (resultCode){
				case Activity.RESULT_OK:
					TToast.show(context, "SMS 도착 완료");
					break;
				case Activity.RESULT_CANCELED:
					TToast.show(context, "SMS 도착 실패");
					break;
			}
			//
			if (msgSize <= ++count) {
				this.context.unregisterReceiver(this);
				notifyOnProcessListener("MessageDeliveryListener unregisterReceiver! size:"+msgSize+",count:"+count);
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
