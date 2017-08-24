package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import android.content.Context;

/**
 * <p>특정 thread_id 에 속하는 모든 메시지(SMS,MMS)를 획득하기 위한 Reader class
 * <p>thread_id는 conversations table 에서 획득할 수 있다. (또는 SMS, MMS table 에서도 가능)
 * <p>thread_id column 값은 단말기 별로 다를 수 있다. (삼성단말기:_id, 그외단말기:thread_id)
 *
 * Created by birdea on 2017-08-03.
 */

public class ConversationThreadReader extends AbsMsgReader {

	private MmsReader mMmsReader;
	private SmsReader mSmsReader;

	public ConversationThreadReader(Context context, QueryConfig config) {
		super(context, config);
		mMmsReader = new MmsReader(context, config);
		mSmsReader = new SmsReader(context, config);
	}

	@Override
	public void setQueryConfig(QueryConfig config) {
		super.setQueryConfig(config);
		if (mMmsReader !=null)
			mMmsReader.setQueryConfig(config);
		if (mSmsReader !=null)
			mSmsReader.setQueryConfig(config);
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//-step.1 make empty list to fill up later
		List<CommMsgData> conversations = new ArrayList<>();

		//-step.2 get each protocol msg on same-thread or multi-thread (if it needs)
		List<CommMsgData> mmsList = mMmsReader.read(context);
		List<CommMsgData> smsList = mSmsReader.read(context);

		//-step.3 fill each result list on empty list
		conversations.addAll(mmsList);
		conversations.addAll(smsList);

		//-step.4 sort (desc or asc)
		Collections.sort(conversations, getComparator(mQueryConfig.getSortOrder()));

		//-step.5 make sub list for limit
		int limit = mQueryConfig.getLimitSize();
		int length = conversations.size();
		if (limit > 0 && length > limit) {
			conversations = conversations.subList(0, limit);
		}
		return conversations;
	}

	private Comparator<CommMsgData> getComparator(QueryConfig.Order order) {
		switch (order) {
			case DESC:
				return new CompareDateDesc();
			case ASC:
				return new CompareDateAsc();
		}
		return null;
	}


	static class CompareDateDesc implements Comparator<CommMsgData> {
		@Override
		public int compare(CommMsgData d1, CommMsgData d2) {
			return d1.getDate() > d2.getDate() ? -1 : d1.getDate() < d2.getDate() ? 1 : 0;
		}
	}

	static class CompareDateAsc implements Comparator<CommMsgData> {
		@Override
		public int compare(CommMsgData d1, CommMsgData d2) {
			return d1.getDate() < d2.getDate() ? -1 : d1.getDate() > d2.getDate() ? 1 : 0;
		}
	}

}
