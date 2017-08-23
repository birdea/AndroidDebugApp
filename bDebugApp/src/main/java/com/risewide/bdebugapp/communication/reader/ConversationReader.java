package com.risewide.bdebugapp.communication.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.QueryConversationProject;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-03.
 */

public class ConversationReader extends AbsMsgReader {

	private CanonicalAddressReader canonicalAddressReader;

	public ConversationReader(Context context, QueryConfig config) {
		super(context, config);
		init(context, config);
	}

	private void init(Context context, QueryConfig config) {
		project = QueryConversationProject.getProject(context);
		canonicalAddressReader = new CanonicalAddressReader(context, config);
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		project.setConfigSortOrder(getConfigSortOrder());
		//- execute to readAll
		List<CommMsgData> conversations = project.readAll(context);
		//- get canonical address data map (id, address)
		if (queryConfig.isExtraLoadAddressData()) {
			canonicalAddressReader.setQueryConfig(queryConfig);
			Map<Long, String> map = getAddresses(context);
			//- assign address with _id;
			for (CommMsgData data : conversations) {
				long key;
				if (data.isSamsungProjection) {
					key = Long.parseLong(data.recipient_ids);
				} else {
					key = data.thread_id;
				}
				if (!TextUtils.isEmpty(data.address)) {
					//SVLog.d("already has address:"+data.address+", _id:"+data._id);
					continue;
				}
				if (map.containsKey(key)) {
					String address = map.get(key);
					data.address = address;
					//SVLog.d("assign ["+key+"] address:"+address);
				} else {
					//SVLog.d("no assign ["+key+"]");
				}
			}
		}
		return conversations;
	}

	private Map<Long, String> getAddresses(Context context) {
		canonicalAddressReader.setQueryConfig(queryConfig);
		List<CommMsgData> addressList = canonicalAddressReader.read(context);
		Map<Long, String> map = new HashMap<>();
		for(CommMsgData data : addressList) {
			long key = data._id;
			if (map.containsKey(key)) {
				//SVLog.d("build map - key["+key+"] is contained..");
			} else {
				//SVLog.d("build map - key["+key+"] address:"+data.address);
				map.put(key, data.address);
			}
		}
		return map;
	}
}
