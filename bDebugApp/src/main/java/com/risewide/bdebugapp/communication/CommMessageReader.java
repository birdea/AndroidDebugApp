package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.risewide.bdebugapp.communication.data.MessageItem;
import com.risewide.bdebugapp.util.SVLog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by birdea on 2017-05-12.
 */

public class CommMessageReader {

	public enum Type{
		SMS,
		MMS,
	}

	public CommMessageReader() {
	}

	public interface OnTextMessageListener {
		public void onComplete(List<MessageItem> list);
	}

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

	public void readMessage(final Context context, final OnTextMessageListener listener) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				/*
				CommReader commReader = new CommReader();
				listener.onComplete(commReader.read(context));
				*/
				long startTime = System.currentTimeMillis();
				// 1st get sms
				SmsReader smsReader = new SmsReader();
				List<MessageItem> smsList = smsReader.read(context);
				SVLog.i("timechecker", "delayed(1):"+ (System.currentTimeMillis() - startTime));
				// 2nd get mms
				//MmsReader mmsReader = new MmsReader();
				//List<MessageItem> mmsList = mmsReader.read(context);
				SVLog.i("timechecker", "delayed(2):"+ (System.currentTimeMillis() - startTime));
				// 3rd unify msgs
				List<MessageItem> allList = new ArrayList<>();
				allList.addAll(smsList);
				//allList.addAll(mmsList);
				SVLog.i("timechecker", "delayed(3):"+ (System.currentTimeMillis() - startTime));
				// 4th sort msgs
				Collections.sort(allList);
				SVLog.i("timechecker", "delayed(4):"+ (System.currentTimeMillis() - startTime));
				// 5th notify data
				listener.onComplete(allList);
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void readSmsMessages(final Context context, final OnTextMessageListener listener) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				SmsReader smsReader = new SmsReader();
				listener.onComplete(smsReader.read(context));
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void readMmsMessages(final Context context, final OnTextMessageListener listener) {
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
}
