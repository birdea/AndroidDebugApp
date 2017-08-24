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

	private CanonicalAddressReader mCanonicalAddressReader;

	public MmsReader(Context context, QueryConfig config) {
		super(context, config);
		initProjection(config);
		mCanonicalAddressReader = new CanonicalAddressReader(context, config);
	}

	@Override
	public void setQueryConfig(QueryConfig config) {
		super.setQueryConfig(config);
		initProjection(config);
	}

	private void initProjection(QueryConfig config) {
		switch (config.getTableType()) {
			case All:
				mQueryProject = new QueryMmsProject.All();
				break;
			case Inbox:
				mQueryProject = new QueryMmsProject.Inbox();
				break;
			case Sent:
				//mQueryProject = new QueryMmsProject.Sent();
				break;
		}
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		mQueryProject.setExtraLoadMessageData(mQueryConfig.isExtraLoadMessageData());
		mQueryProject.setExtraLoadAddressData(mQueryConfig.isExtraLoadAddressData());
		mQueryProject.setLoadOnlyUnreadData(mQueryConfig.isSelectLoadOnlyUnread());
		mQueryProject.setConfigSortOrder(getConfigSortOrder());
		mQueryProject.setSelection(" thread_id=="+ mQueryConfig.getThreadId()+" ");
		//- execute to readAll
		List<CommMsgData> mmsList = mQueryProject.readAll(context);
		//- get canonical address data map (id, address)
		/*if (mQueryConfig.isExtraLoadAddressData()) {
			mCanonicalAddressReader.setQueryConfig(mQueryConfig);
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
		mCanonicalAddressReader.setQueryConfig(mQueryConfig);
		List<CommMsgData> addressList = mCanonicalAddressReader.read(context);
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
