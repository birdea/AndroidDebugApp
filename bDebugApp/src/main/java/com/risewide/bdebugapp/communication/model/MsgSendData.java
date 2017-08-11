package com.risewide.bdebugapp.communication.model;

import android.net.Uri;

/**
 * Created by birdea on 2017-08-08.
 */

public class MsgSendData {
	public String phoneNumberSender;
	public String phoneNumberReceiver;
	public String nameSender;
	public String nameReceiver;
	public String textMessage;
	public Uri imageDataUri;

	public void setNameSender(String name) {
		nameSender = name;
	}
	public void setNameReceiver(String name) {
		nameReceiver = name;
	}
	public void setPhoneNumberSender(String phoneNumber) {
		phoneNumberSender = phoneNumber;
	}
	public void setPhoneNumberReceiver(String phoneNumber) {
		phoneNumberReceiver = phoneNumber;
	}
	public void setTextMessage(String message) {
		textMessage = message;
	}
	public void setImageUri(Uri imageUri) {
		imageDataUri = imageUri;
	}
}
