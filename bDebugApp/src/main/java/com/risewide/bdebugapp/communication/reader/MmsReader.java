package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.MmsReadProject;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader {
	public List<SmsMmsMsg> read(Context context) {
		ReadProjector<SmsMmsMsg> rp = new MmsReadProject.All();
		List<SmsMmsMsg> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), rp.getSelection(), null, Telephony.Mms.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.moveToFirst()) {
			rp.storeColumnIndex(cursor);
			do {
				SmsMmsMsg item = rp.read(context, cursor);
				dataList.add(item);
			}while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return dataList;
	}
}
