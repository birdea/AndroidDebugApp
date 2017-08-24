package com.risewide.bdebugapp.communication;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
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
 * <p>*Read Message*
 * <p>SMS, MMS, Conversations
 * <p>Content Provider(*CP) uri를 통해서 각 타입별 메시지를 읽어올 수 있다.
 * <p>각 메시지 타입별 CP uri
 * <br>	1. SMS 수신메시지 : {@link android.provider.Telephony.Sms.Inbox}
 * <br>	2. MSM 수신메시지 : {@link android.provider.Telephony.Mms.Inbox}
 * <br>	3. Conversations 통합메시지
 * <br>		3-1. Samsung	: "content://mms-sms/conversations?simple=true"
 * <br>		3-2. etc		: "content://mms-sms/conversations"
 * <p>각 메시지 타입별 쿼리 전/후처리 상세화, 공통화 요소 관리를 위해 {@link AbsMsgReader} 클래스로 추상화.
 * <p>각 메시지 타입별 쿼리 프로젝션 처리 상세화, 공통화 요소 관리를 위해 {@link com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject} 클래스로 추상화.
 * <p>Created by birdea on 2017-08-08.
 */

public class CommUnifyMessageReader extends AbsMessageReader{

	private CommMsgReadType mCommMsgReadType;
	private SmsReader mSmsReader;
	private MmsReader mMmsReader;
	private ConversationReader mConversationReader;
	private ConversationThreadReader mConversationThreadReader;
	private QueryConfig mQueryConfig = new QueryConfig();
	private List<CommMsgData> mReadMsgList;

	public CommUnifyMessageReader() {
		mCommMsgReadType = CommMsgReadType.CONVERSATION;
	}

	public CommMsgReadType getReadProtocolType() {
		return mCommMsgReadType;
	}

	public void setReadProtocolType(CommMsgReadType type) {
		mCommMsgReadType = type;
	}

	public void setQueryConfig(QueryConfig config) {
		mQueryConfig = config;
	}

	public QueryConfig getQueryConfig() {
		return mQueryConfig;
	}

	public List<CommMsgData> getReadMsgList() {
		return mReadMsgList;
	}

	@Override
	public void read(Context context, OnReadTextMessageListener listener) {
		switch (mCommMsgReadType) {
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
		if(mSmsReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(mSmsReader == null) {
					mSmsReader = new SmsReader(context, mQueryConfig);
				}
			}
		}
		return mSmsReader;
	}

	private MmsReader getMmsReader(Context context) {
		if(mMmsReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(mMmsReader == null) {
					mMmsReader = new MmsReader(context, mQueryConfig);
				}
			}
		}
		return mMmsReader;
	}

	private ConversationReader getConversationReader(Context context) {
		if(mConversationReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(mConversationReader == null) {
					mConversationReader = new ConversationReader(context, mQueryConfig);
				}
			}
		}
		return mConversationReader;
	}

	private ConversationThreadReader getConversationThreadReader(Context context) {
		if(mConversationThreadReader == null) {
			synchronized (CommUnifyMessageReader.class) {
				if(mConversationThreadReader == null) {
					mConversationThreadReader = new ConversationThreadReader(context, mQueryConfig);
				}
			}
		}
		return mConversationThreadReader;
	}


	@Override
	public void registerContentObserver(Context context, boolean notifyForDescendents, AbsMsgReader.OnContentObserver observer) {
		//getSmsReader(context).registerContentObserver(context, notifyForDescendents, observer);
		//getMmsReader(context).registerContentObserver(context, notifyForDescendents, observer);
		getConversationReader(context).registerContentObserver(context, notifyForDescendents, observer);
	}

	@Override
	public void unregisterContentObserver(Context context, AbsMsgReader.OnContentObserver observer) {
		//mSmsReader.unregisterContentObserver(context, observer);
		//mMmsReader.unregisterContentObserver(context, observer);
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
				try {
					AbsMsgReader reader = getSmsReader(context);
					reader.setQueryConfig(mQueryConfig);
					mReadMsgList = reader.read(context);
					listener.onComplete(mReadMsgList);
				} catch (Throwable e) {
					listener.onError(e);
				}
			}
		});
	}

	private void readMmsMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AbsMsgReader reader = getMmsReader(context);
					reader.setQueryConfig(mQueryConfig);
					mReadMsgList = reader.read(context);
					listener.onComplete(mReadMsgList);
				} catch (Throwable e) {
					listener.onError(e);
				}
			}
		});
	}

	private void readMmsSmsConversationMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AbsMsgReader reader = getConversationReader(context);
					reader.setQueryConfig(mQueryConfig);
					mReadMsgList = reader.read(context);
					listener.onComplete(mReadMsgList);
				} catch (Throwable e) {
					listener.onError(e);
				}
			}
		});
	}

	private void readThreadIdMessage(final Context context, final OnReadTextMessageListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AbsMsgReader reader = getConversationThreadReader(context);
					reader.setQueryConfig(mQueryConfig);
					mReadMsgList = reader.read(context);
					listener.onComplete(mReadMsgList);
				} catch (Throwable e) {
					listener.onError(e);
				}
			}
		});
	}

}
