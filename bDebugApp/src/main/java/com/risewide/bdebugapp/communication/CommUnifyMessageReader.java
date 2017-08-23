package com.risewide.bdebugapp.communication;

import com.risewide.bdebugapp.communication.model.CommMsgReadType;
import com.risewide.bdebugapp.communication.reader.AbsMsgReader;
import com.risewide.bdebugapp.communication.reader.ConversationReader;
import com.risewide.bdebugapp.communication.reader.ConversationThreadReader;
import com.risewide.bdebugapp.communication.reader.MmsReader;
import com.risewide.bdebugapp.communication.reader.SmsReader;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.HandyThreadTask;

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

	private CommMsgReadType readProtocolType;
	private SmsReader smsReader;
	private MmsReader mmsReader;
	private ConversationReader conversationReader;
	private ConversationThreadReader conversationThreadReader;

	private QueryConfig queryConfig = new QueryConfig();

	public CommUnifyMessageReader() {
		readProtocolType = CommMsgReadType.SMS;
	}

	public CommMsgReadType getReadProtocolType() {
		return readProtocolType;
	}

	public void setReadProtocolType(CommMsgReadType type) {
		readProtocolType = type;
	}

	public void setQueryConfig(QueryConfig config) {
		this.queryConfig = config;
	}

	public QueryConfig getQueryConfig() {
		return queryConfig;
	}

	@Override
	public void read(Context context, OnReadTextMessageListener listener) {
		switch (readProtocolType) {
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
				readThreadIdMessage(context, listener);
				break;
		}
	}

	private SmsReader getSmsReader(Context context) {
		if(smsReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(smsReader == null) {
					smsReader = new SmsReader(context, queryConfig);
				}
			}
		}
		return smsReader;
	}

	private MmsReader getMmsReader(Context context) {
		if(mmsReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(mmsReader == null) {
					mmsReader = new MmsReader(context, queryConfig);
				}
			}
		}
		return mmsReader;
	}

	private ConversationReader getConversationReader(Context context) {
		if(conversationReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(conversationReader == null) {
					conversationReader = new ConversationReader(context, queryConfig);
				}
			}
		}
		return conversationReader;
	}

	private ConversationThreadReader getConversationThreadReader(Context context) {
		if(conversationThreadReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(conversationThreadReader == null) {
					conversationThreadReader = new ConversationThreadReader(context, queryConfig);
				}
			}
		}
		return conversationThreadReader;
	}


	@Override
	public void registerContentObserver(Context context, boolean notifyForDescendents, AbsMsgReader.OnContentObserver observer) {
		//getSmsReader(context).registerContentObserver(context, notifyForDescendents, observer);
		//getMmsReader(context).registerContentObserver(context, notifyForDescendents, observer);
		getConversationReader(context).registerContentObserver(context, notifyForDescendents, observer);
	}

	@Override
	public void unregisterContentObserver(Context context, AbsMsgReader.OnContentObserver observer) {
		//smsReader.unregisterContentObserver(context, observer);
		//mmsReader.unregisterContentObserver(context, observer);
		getConversationReader(context).unregisterContentObserver(context, observer);
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

	private void readSmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				AbsMsgReader reader = getSmsReader(context);
				reader.setQueryConfig(queryConfig);
				listener.onComplete(reader.read(context));
			}
		});
	}

	private void readMmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				AbsMsgReader reader = getMmsReader(context);
				reader.setQueryConfig(queryConfig);
				listener.onComplete(reader.read(context));
			}
		});
	}

	private void readMmsSmsConversationMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				AbsMsgReader reader = getConversationReader(context);
				reader.setQueryConfig(queryConfig);
				listener.onComplete(reader.read(context));
			}
		});
	}

	private void readThreadIdMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				AbsMsgReader reader = getConversationThreadReader(context);
				reader.setQueryConfig(queryConfig);
				listener.onComplete(reader.read(context));
			}
		});
	}

}
