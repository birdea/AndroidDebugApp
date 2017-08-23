package com.risewide.bdebugapp.communication.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.QueryMmsProject;
import com.risewide.bdebugapp.util.SVLog;

import android.content.Context;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader extends AbsMsgReader {

	private CanonicalAddressReader canonicalAddressReader;

	public MmsReader(Context context, QueryConfig config) {
		super(context, config);
		initProjection(config);
		canonicalAddressReader = new CanonicalAddressReader(context, config);
	}

	@Override
	public void setQueryConfig(QueryConfig config) {
		super.setQueryConfig(config);
		initProjection(config);
	}

	private void initProjection(QueryConfig config) {
		switch (config.getTableType()) {
			case All:
				project = new QueryMmsProject.All();
				break;
			case Inbox:
				project = new QueryMmsProject.Inbox();
				break;
			case Sent:
				//project = new QueryMmsProject.Sent();
				break;
		}
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		project.setConfigSortOrder(getConfigSortOrder());
		project.setSelection(" thread_id=="+queryConfig.getThreadId()+" ");
		//- execute to readAll
		List<CommMsgData> mmsList = project.readAll(context);
		//- get canonical address data map (id, address)
		/*if (queryConfig.isExtraLoadAddressData()) {
			canonicalAddressReader.setQueryConfig(queryConfig);
			Map<Long, String> map = getAddresses(context);
			//- assign address with _id;
			for (CommMsgData data : mmsList) {
				long key = data.thread_id;
				//
				if (!TextUtils.isEmpty(data.address)) {
					SVLog.d("already has address:"+data.address+", _id:"+data._id);
					continue;
				}
				if (map.containsKey(key)) {
					String address = map.get(key);
					data.address = address;
					SVLog.d("assign ["+key+"] address:"+address);
				} else {
					SVLog.d("no assign ["+key+"], body:"+data.getBodyMessage());
				}
			}
		}*/
		return mmsList;
	}

	private Map<Long, String> getAddresses(Context context) {
		canonicalAddressReader.setQueryConfig(queryConfig);
		List<CommMsgData> addressList = canonicalAddressReader.read(context);
		Map<Long, String> map = new HashMap<>();
		for(CommMsgData data : addressList) {
			long key = data._id;
			if (map.containsKey(key)) {
				SVLog.d("build map - key["+key+"] is contained..");
			} else {
				SVLog.d("build map - key["+key+"] address:"+data.address);
				map.put(key, data.address);
			}
		}
		return map;
	}
}
