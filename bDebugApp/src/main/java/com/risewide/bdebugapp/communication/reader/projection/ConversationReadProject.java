package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;

/**
 * Created by birdea on 2017-08-09.
 */

public class ConversationReadProject {

	public static class All extends ReadProjector<SmsMmsMsg> {

		private static final String[] PROJECTION = {
				"*"
		};

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			return "read!=1";
		}

		@Override
		public Uri getUri() {
			return Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		private MmsReaderSub mmsReaderSub = new MmsReaderSub();

		@Override
		public SmsMmsMsg read(Context context, Cursor cursor) {
			SmsMmsMsg item = new SmsMmsMsg(SmsMmsMsg.Type.CONVERSATION);
			item._id = cursor.getLong(cursor.getColumnIndex(Telephony.MmsSms._ID));
			item.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			item.body = cursor.getString(cursor.getColumnIndex("snippet"));
			item.read = cursor.getInt(cursor.getColumnIndex("read"));
			item.type = cursor.getInt(cursor.getColumnIndex("type"));
			//
			String recipient_ids = cursor.getString(cursor.getColumnIndex("recipient_ids"));
			item.address = mmsReaderSub.getRecipientAddress(context.getContentResolver(), Long.parseLong(recipient_ids));
			return item;
		}
	}
}
