package com.risewide.bdebugapp.communication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.risewide.bdebugapp.communication.helper.HandyThreadTask;
import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.model.SmsProtocolReadType;
import com.risewide.bdebugapp.communication.reader.MmsSmsReader;
import com.risewide.bdebugapp.communication.reader.MmsReader;
import com.risewide.bdebugapp.communication.reader.SmsReader;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by birdea on 2017-08-08.
 */

public class SmsUnifyMessageReader extends AbsMessageReader{

	private SmsProtocolReadType smsProtocolReadType;

	public SmsUnifyMessageReader() {
		smsProtocolReadType = SmsProtocolReadType.SMS;
	}

	public void setSmsProtocolReadType(SmsProtocolReadType type) {
		smsProtocolReadType = type;
	}

	@Override
	public void read(Context context, OnReadTextMessageListener listener) {
		switch (smsProtocolReadType) {
			case ALL_SEQUENTIAL:
				readAllMessageOnSequence(context, listener);
				break;
			case MMS_SMS:
				readMmsSmsMessage(context, listener);
				break;
			case SMS:
				readSmsMessage(context, listener);
				break;
			case MMS:
				readMmsMessage(context, listener);
				break;
			default:
				break;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	private static final int MY_PERMISSION_REQUEST = 0x01;
	public boolean hasPermission(Context context) {
		// check permission on runtime of execution
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
				if (context instanceof Activity) {
					Activity activity = (Activity) context;
					ActivityCompat.requestPermissions(activity,
							new String[]{Manifest.permission.READ_SMS},
							MY_PERMISSION_REQUEST);
					activity.finish();
				}
				return false;
			}
		}
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface OnReadTextMessageListener {
		void onComplete(List<MessageItem> list);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private void readAllMessageOnSequence(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				/*
				MmsSmsReader commReader = new MmsSmsReader();
				listener.onComplete(commReader.read(context));
				*/
				long startTime = System.currentTimeMillis();
				// 1st get sms
				SmsReader smsReader = new SmsReader();
				List<MessageItem> smsList = smsReader.read(context);
				SVLog.i("timechecker", "delayed(1):"+ (System.currentTimeMillis() - startTime));
				// 2nd get mms
				MmsReader mmsReader = new MmsReader();
				List<MessageItem> mmsList = mmsReader.read(context);
				SVLog.i("timechecker", "delayed(2):"+ (System.currentTimeMillis() - startTime));
				// 3rd unify msgs
				List<MessageItem> allList = new ArrayList<>();
				allList.addAll(smsList);
				allList.addAll(mmsList);
				SVLog.i("timechecker", "delayed(3):"+ (System.currentTimeMillis() - startTime));
				// 4th sort msgs
				Collections.sort(allList);
				SVLog.i("timechecker", "delayed(4):"+ (System.currentTimeMillis() - startTime));
				// 5th notify data
				listener.onComplete(allList);
			}
		});
	}

	private void readSmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				SmsReader smsReader = new SmsReader();
				listener.onComplete(smsReader.read(context));
			}
		});
	}

	private void readMmsMessage(final Context context, final OnReadTextMessageListener listener) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				MmsReader mmsReader = new MmsReader();
				listener.onComplete(mmsReader.read(context));
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	private void readMmsSmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				MmsSmsReader mmsReader = new MmsSmsReader();
				listener.onComplete(mmsReader.read(context));
			}
		});
	}

}
