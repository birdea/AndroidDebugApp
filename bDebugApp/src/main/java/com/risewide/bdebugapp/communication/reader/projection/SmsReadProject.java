package com.risewide.bdebugapp.communication.reader.projection;

import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.helper.CursorHelper;
import com.risewide.bdebugapp.communication.model.MessageItem;

/**
 * Created by birdea on 2017-08-08.
 */

public class SmsReadProject {

	public static abstract class ReadProjector {
		public abstract String[] getProjection();
		public abstract Uri getUri();
		public abstract MessageItem read(Cursor cursor);
	}

	public static class All extends ReadProjector {
		private static final String[] PROJECTION_SMS = {
				Telephony.Sms._ID,
				Telephony.Sms.CREATOR,
				Telephony.Sms.ADDRESS,
				Telephony.Sms.PERSON,
				Telephony.Sms.DATE,
				Telephony.Sms.DATE_SENT,
				Telephony.Sms.PROTOCOL,
				Telephony.Sms.ERROR_CODE,
				Telephony.Sms.READ,
				Telephony.Sms.STATUS,
				Telephony.Sms.TYPE,
				Telephony.Sms.SUBJECT,
				Telephony.Sms.BODY,
				Telephony.Sms.SERVICE_CENTER,
				Telephony.Sms.LOCKED,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION_SMS;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.CONTENT_URI;
		}

		@Override
		public MessageItem read(Cursor cursor) {
			MessageItem item = new MessageItem();
			item.id = CursorHelper.getLong(cursor,Telephony.Sms._ID);
			item.creator = CursorHelper.getString(cursor,Telephony.Sms.CREATOR);
			item.address = CursorHelper.getString(cursor,Telephony.Sms.ADDRESS);
			item.person = CursorHelper.getInt(cursor,Telephony.Sms.PERSON);
			item.date = CursorHelper.getLong(cursor,Telephony.Sms.DATE);
			item.dateSent = CursorHelper.getLong(cursor,Telephony.Sms.DATE_SENT);
			item.protocol = CursorHelper.getInt(cursor,Telephony.Sms.PROTOCOL);
			item.errorCode = CursorHelper.getInt(cursor,Telephony.Sms.ERROR_CODE);
			item.read = CursorHelper.getInt(cursor,Telephony.Sms.READ);
			item.status = CursorHelper.getInt(cursor,Telephony.Sms.STATUS);
			item.type = CursorHelper.getInt(cursor,Telephony.Sms.TYPE);
			item.subject = CursorHelper.getString(cursor,Telephony.Sms.SUBJECT);
			item.body = CursorHelper.getString(cursor,Telephony.Sms.BODY);
			item.serviceCenter = CursorHelper.getString(cursor,Telephony.Sms.SERVICE_CENTER);
			item.locked = CursorHelper.getInt(cursor,Telephony.Sms.LOCKED);
			return item;
		}
	}

	public static class Inbox extends ReadProjector {
		private static final String[] PROJECTION_SMS_INBOX = {
				Telephony.Sms.Inbox._ID,
				Telephony.Sms.Inbox.THREAD_ID,
				Telephony.Sms.Inbox.ADDRESS,
				Telephony.Sms.Inbox.DATE,
				Telephony.Sms.Inbox.DATE_SENT,
				Telephony.Sms.Inbox.STATUS,
				Telephony.Sms.Inbox.TYPE,
				Telephony.Sms.Inbox.BODY,
				Telephony.Sms.Inbox.READ,
				Telephony.Sms.Inbox.SEEN,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION_SMS_INBOX;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Inbox.CONTENT_URI;
		}

		@Override
		public MessageItem read(Cursor cursor) {
			MessageItem item = new MessageItem();
			item.id = CursorHelper.getLong(cursor, Telephony.Sms.Inbox._ID);
			//item.creator = CursorHelper.getString(cursor, Telephony.Sms.Inbox.CREATOR);
			item.address = CursorHelper.getString(cursor, Telephony.Sms.Inbox.ADDRESS);
			//item.person = CursorHelper.getInt(cursor, Telephony.Sms.Inbox.PERSON);
			item.date = CursorHelper.getLong(cursor,Telephony.Sms.Inbox.DATE);
			item.dateSent = CursorHelper.getLong(cursor,Telephony.Sms.Inbox.DATE_SENT);
			//item.protocol = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.PROTOCOL);
			//item.errorCode = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.ERROR_CODE);
			item.read = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.READ);
			item.status = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.STATUS);
			item.type = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.TYPE);
			//item.subject = CursorHelper.getString(cursor,Telephony.Sms.Inbox.SUBJECT);
			item.body = CursorHelper.getString(cursor,Telephony.Sms.Inbox.BODY);
			//item.serviceCenter = CursorHelper.getString(cursor,Telephony.Sms.Inbox.SERVICE_CENTER);
			//item.locked = CursorHelper.getInt(cursor,Telephony.Sms.Inbox.LOCKED);
			return item;
		}
	}

	public static class Sent extends ReadProjector {
		private static final String[] PROJECTION_SMS_SENT = {
				Telephony.Sms.Sent._ID,
				Telephony.Sms.Sent.THREAD_ID,
				Telephony.Sms.Sent.ADDRESS,
				Telephony.Sms.Sent.DATE,
				Telephony.Sms.Sent.DATE_SENT,
				Telephony.Sms.Sent.STATUS,
				Telephony.Sms.Sent.TYPE,
				Telephony.Sms.Sent.BODY,
				Telephony.Sms.Sent.READ,
				Telephony.Sms.Sent.SEEN,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION_SMS_SENT;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Sent.CONTENT_URI;
		}

		@Override
		public MessageItem read(Cursor cursor) {
			MessageItem item = new MessageItem();
			item.id = CursorHelper.getLong(cursor, Telephony.Sms.Sent._ID);
			//item.creator = CursorHelper.getString(cursor, Telephony.Sms.Sent.CREATOR);
			item.address = CursorHelper.getString(cursor, Telephony.Sms.Sent.ADDRESS);
			//item.person = CursorHelper.getInt(cursor, Telephony.Sms.Sent.PERSON);
			item.date = CursorHelper.getLong(cursor,Telephony.Sms.Sent.DATE);
			item.dateSent = CursorHelper.getLong(cursor,Telephony.Sms.Sent.DATE_SENT);
			//item.protocol = CursorHelper.getInt(cursor,Telephony.Sms.Sent.PROTOCOL);
			//item.errorCode = CursorHelper.getInt(cursor,Telephony.Sms.Sent.ERROR_CODE);
			item.read = CursorHelper.getInt(cursor,Telephony.Sms.Sent.READ);
			item.status = CursorHelper.getInt(cursor,Telephony.Sms.Sent.STATUS);
			item.type = CursorHelper.getInt(cursor,Telephony.Sms.Sent.TYPE);
			//item.subject = CursorHelper.getString(cursor,Telephony.Sms.Sent.SUBJECT);
			item.body = CursorHelper.getString(cursor,Telephony.Sms.Sent.BODY);
			//item.serviceCenter = CursorHelper.getString(cursor,Telephony.Sms.Sent.SERVICE_CENTER);
			//item.locked = CursorHelper.getInt(cursor,Telephony.Sms.Sent.LOCKED);
			return item;
		}
	}
}
