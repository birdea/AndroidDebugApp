package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.QueryConversationProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryMmsProject;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-03.
 */

public class ConversationThreadReader extends AbsMsgReader {

	private MmsReader mmsReader;
	private SmsReader smsReader;

	public ConversationThreadReader(Context context, QueryConfig config) {
		super(context, config);
		mmsReader = new MmsReader(context, config);
		smsReader = new SmsReader(context, config);
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		//project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		//project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		//project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		//project.setConfigSortOrder(getConfigSortOrder());
		//- execute to readAll
		List<CommMsgData> conversations = new ArrayList<>();//project.readAll(context);

		List<CommMsgData> mmsList = mmsReader.read(context);
		List<CommMsgData> smsList = smsReader.read(context);

		conversations.addAll(mmsList);
		conversations.addAll(smsList);

		Collections.sort(conversations);
		//- get canonical address data map (id, address)
		return conversations;
	}
}
