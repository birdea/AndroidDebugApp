package com.risewide.bdebugapp.communication.util;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.MediaStore;

/**
 * Created by birdea on 2017-08-04.
 */

public class IntentActionHelper {

	private static final int REQCODE_CONTACT_ACTION_PICK = 0x01;
	private static final int REQCODE_GALERY_ACTION_PICK = 0x02;
	private Activity activity;
	private OnActivityResultDispatcher onActivityResultDispatcher;

	public interface OnActivityResultDispatcher {
		void dispatcher(int resultCode, Intent data);
	}

	public void selectReceiverPhoneNumber(Activity act, OnActivityResultDispatcher dispatcher) {
		//
		activity = act;
		onActivityResultDispatcher = dispatcher;
		//
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		activity.startActivityForResult(intent, REQCODE_CONTACT_ACTION_PICK);
	}

	public void selectGaleryImage(Activity act, OnActivityResultDispatcher dispatcher) {
		activity = act;
		onActivityResultDispatcher = dispatcher;
		//
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		activity.startActivityForResult(intent, REQCODE_GALERY_ACTION_PICK);
	}

	public boolean onActivityResultDispatcher(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQCODE_CONTACT_ACTION_PICK:
			case REQCODE_GALERY_ACTION_PICK:
			{
				onActivityResultDispatcher.dispatcher(resultCode, data);
				return true;
			}

		}
		return false;
	}
}
