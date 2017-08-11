package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.SmsMmsMsg;

/**
 * Created by birdea on 2017-08-09.
 */

public class MmsSmsReadProject {

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
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public SmsMmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}
}
