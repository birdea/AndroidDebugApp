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

import com.risewide.bdebugapp.communication.model.MsgSendData;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.communication.model.SmsMmsMsgSendType;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-02.
 */

public class SmsUnifyMessageSender extends AbsMessageSender {

	public static final boolean IS_SUPPORT_MMS = false;

	public enum CallMethodType {
		DirectCall,
		Intent,
	}

	private SmsMmsMsgSendType protocolType;
	private CallMethodType callMethodType;
	private MsgSendData messageData;

	public SmsUnifyMessageSender() {
		messageData = new MsgSendData();
		protocolType = SmsMmsMsgSendType.SMS;
		callMethodType = CallMethodType.DirectCall;
	}

	public void setOnHandyEventListener(OnHandyEventListener listener) {
		onHandyEventListener = listener;
	}
	private OnHandyEventListener onHandyEventListener;
	private void notifyOnEventListener(String msg) {
		SVLog.i("SmsUnifyMessageSender", msg);
		if(onHandyEventListener ==null) {
			return;
		}
		onHandyEventListener.onEvent(msg);
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
	public void send(Context context) {
		// check for data to be ready to send
		if (!isValidData(context, messageData)) {
			notifyOnEventListener("validation fails..");
			TToast.show(context, "invalid data, check again plz..");
			return;
		}
		notifyOnEventListener("validation is ok.. send on protocol:"+protocolType);
		// switch the delivery way of automatic or manual
		if (SmsMmsMsgSendType.AUTO_ADJUST.equals(protocolType)) {
			TToast.show(context, "send on automatic-protocol");
			sendOnAutomaticProtocol(context);
		} else {
			TToast.show(context, "send on manual-protocol");
			sendOnManualProtocol(context);
		}
	}

	// setter
	public void setProtocolType(SmsMmsMsgSendType type) {
		protocolType = type;
	}
	public void setCallMethodType(CallMethodType type) {
		callMethodType = type;
	}
	public SmsMmsMsgSendType getProtocolType(){
		return protocolType;
	}
	public CallMethodType getCallMethodType(){
		return callMethodType;
	}

	public MsgSendData getMessageData() {
		return messageData;
	}
	public void setMessageData(MsgSendData data) {
		messageData = data;
	}

	private void sendOnAutomaticProtocol(Context context) {
		SmsManager smsManager = SmsManager.getDefault();
		// MMS
		if (IS_SUPPORT_MMS && messageData.imageDataUri != null) {
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
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress, scAddress, text;
		ArrayList<PendingIntent> sentIntentList = new ArrayList<>();
		ArrayList<PendingIntent> deliveryIntentList = new ArrayList<>();
		IntentFilter filterMsgSend = new IntentFilter(ACTION_SMS_SEND);
		IntentFilter filterMsgDelivery = new IntentFilter(ACTION_SMS_DELIVERY);
		//
		destinationAddress = messageData.phoneNumberReceiver;
		scAddress = null;
		text = messageData.textMessage;
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
		context.registerReceiver(new MessageSendListener(context, msgList.size()), filterMsgSend);
		context.registerReceiver(new MessageDeliveryListener(context, msgList.size()), filterMsgDelivery);
		//
		notifyOnEventListener("sendOn[LMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendMultipartTextMessage(destinationAddress, scAddress, msgList, sentIntentList, deliveryIntentList);
	}

	private void sendOnSMS(Context context) {
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
		notifyOnEventListener("sendOn[SMS] addr:"+destinationAddress+",text:"+text);
		smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
	}

	private void sendOnMMS(Context context) {
		notifyOnEventListener("sendOn[MMS] IS_SUPPORT_MMS:"+IS_SUPPORT_MMS);
		if(IS_SUPPORT_MMS==false) {
			return;
		}
		SmsManager smsManager = SmsManager.getDefault();
		//
		String destinationAddress;
		PendingIntent sentIntent;
		Uri contentUri = messageData.imageDataUri;
		//
		destinationAddress = messageData.phoneNumberReceiver;
		Bundle configOverrides = smsManager.getCarrierConfigValues();
		//
		sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SEND), PendingIntent.FLAG_UPDATE_CURRENT);
		// deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERY), PendingIntent.FLAG_UPDATE_CURRENT);
		// register send & delivery listener
		context.registerReceiver(new MessageSendListener(context, 1), new IntentFilter(ACTION_SMS_SEND));
		context.registerReceiver(new MessageDeliveryListener(context, 1), new IntentFilter(ACTION_SMS_DELIVERY));
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
			notifyOnEventListener("MessageSendListener.onReceive - resultCode:"+resultCode+",action:"+intent.getAction()+", msgSize:"+msgSize+",count:"+count);
			switch(resultCode){
				case Activity.RESULT_OK:
					TToast.show(context, "SMS send, RESULT_OK");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					TToast.show(context, "SMS send, RESULT_ERROR_GENERIC_FAILURE");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					TToast.show(context, "SMS send, RESULT_ERROR_NO_SERVICE");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					TToast.show(context, "SMS send, RESULT_ERROR_RADIO_OFF");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					TToast.show(context, "SMS send, RESULT_ERROR_NULL_PDU");
					break;
			}
			//
			if (msgSize <= ++count) {
				this.context.unregisterReceiver(this);
				notifyOnEventListener("MessageSendListener unregisterReceiver! size:"+msgSize+",count:"+count);
			}
		}
	}
	private class MessageDeliveryListener extends BroadcastReceiver {
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
			notifyOnEventListener("MessageDeliveryListener.onReceive - resultCode:"+resultCode+",action:"+intent.getAction()+", msgSize:"+msgSize+",count:"+count);
			switch (resultCode){
				case Activity.RESULT_OK:
					TToast.show(context, "SMS delivery RESULT_OK");
					break;
				case Activity.RESULT_CANCELED:
					TToast.show(context, "SMS delivery RESULT_CANCELED");
					break;
			}
			//
			if (msgSize <= ++count) {
				this.context.unregisterReceiver(this);
				notifyOnEventListener("MessageDeliveryListener unregisterReceiver! size:"+msgSize+",count:"+count);
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
