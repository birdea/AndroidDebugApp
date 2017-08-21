package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.HandyThreadTask;
import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.model.CommMsgReadType;
import com.risewide.bdebugapp.communication.reader.MmsReader;
import com.risewide.bdebugapp.communication.reader.ConversationReader;
import com.risewide.bdebugapp.communication.reader.SmsReader;
import com.risewide.bdebugapp.util.SVLog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by birdea on 2017-08-08.
 */

public class CommUnifyMessageReader extends AbsMessageReader{

	private CommMsgReadType smsProtocolReadType;
	private QueryConfig queryConfig = new QueryConfig();

	public CommUnifyMessageReader() {
		smsProtocolReadType = CommMsgReadType.SMS;
	}

	public CommMsgReadType getSmsProtocolReadType() {
		return smsProtocolReadType;
	}

	public void setSmsProtocolReadType(CommMsgReadType type) {
		smsProtocolReadType = type;
	}

	public void setQueryConfig(QueryConfig config) {
		this.queryConfig = config;
	}

	public QueryConfig getQueryConfig() {
		return queryConfig;
	}

	@Override
	public void read(Context context, OnReadTextMessageListener listener) {
		switch (smsProtocolReadType) {
			case ALL_SEQUENTIAL:
				readAllMessageOnSequence(context, listener);
				break;
			case CONVERSATION:
				readMmsSmsConversationMessage(context, listener);
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

	private void readAllMessageOnSequence(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				/*
				ConversationReader commReader = new ConversationReader();
				listener.onComplete(commReader.read(context));
				*/
				long startTime = System.currentTimeMillis();
				// 1st get sms
				SmsReader smsReader = new SmsReader(context, queryConfig);
				List<CommMsgData> smsList = smsReader.read(context);
				SVLog.i("timechecker", "delayed(1):"+ (System.currentTimeMillis() - startTime));
				// 2nd get mms
				MmsReader mmsReader = new MmsReader(context, queryConfig);
				List<CommMsgData> mmsList = mmsReader.read(context);
				SVLog.i("timechecker", "delayed(2):"+ (System.currentTimeMillis() - startTime));
				// 3rd unify msgs
				List<CommMsgData> allList = new ArrayList<>();
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
				SmsReader smsReader = new SmsReader(context, queryConfig);
				listener.onComplete(smsReader.read(context));
			}
		});
	}

	private void readMmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				MmsReader mmsReader = new MmsReader(context, queryConfig);
				listener.onComplete(mmsReader.read(context));
			}
		});
	}

	private void readMmsSmsConversationMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				ConversationReader mmsReader = new ConversationReader(context, queryConfig);
				listener.onComplete(mmsReader.read(context));
			}
		});
	}

}
