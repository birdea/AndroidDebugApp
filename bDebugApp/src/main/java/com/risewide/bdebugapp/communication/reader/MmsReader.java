package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.projection.MmsReadProject;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader {

	public List<MessageItem> read(Context context) {

		ReadProjector<MessageItem> rp = new MmsReadProject.All();

		List<MessageItem> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;//"read!=1";
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			MessageItem item = rp.read(context, cursor);
			dataList.add(item);
		}
		cursor.close();
		// get address, text message
		MmsReaderSub mmsReaderSub = new MmsReaderSub();
		for(final MessageItem item : dataList) {
			item.listAddress = mmsReaderSub.getAddressNumber(resolver, (int) item.id);
			//item.body = mmsReaderSub.getTextMessage(resolver, String.valueOf(item.id));
			/*mmsReaderSub.getAddressNumberAsync(context, (int) item.id, new MmsReaderSub.OnReadListener() {
				@Override
				public void onRead(Object data) {
					item.listAddress = (List<String>) data;
				}
			});
			mmsReaderSub.getMessageOfMmsAsync(context, String.valueOf(item.id), new MmsReaderSub.OnReadListener() {
				@Override
				public void onRead(Object data) {
					item.body = (String) data;
				}
			});*/
		}
		//
		return dataList;
	}
}
