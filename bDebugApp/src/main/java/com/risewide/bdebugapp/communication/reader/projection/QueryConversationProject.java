package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.helper.MmsReaderHelper;
import com.risewide.bdebugapp.communication.reader.helper.SmsReaderHelper;
import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public class QueryConversationProject {

	/**
	 * 삼성 단말기에서 Telephony.MmsSms.CONTENT_CONVERSATIONS_URI 로 접근이 불가하기 때문에 Project 정보 구분
	 *
	 * @param context
	 * @param queryConfig
	 * @param sortOrder
	 * @return
	 */
	public static AbsQueryProject<CommMsgData> getProject(Context context, QueryConfig queryConfig, String sortOrder) {
		AbsQueryProject<CommMsgData> project = new CommonProject();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		project.setConfigSortOrder(sortOrder);
		//
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor;
		try {
			cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
			SVLog.i("** Conversation - getProject - Common(LG) URI");
		} catch (Exception ignorable) {
			ignorable.printStackTrace();
			AbsQueryProject<CommMsgData> projectSamsung = new SamsungProject();
			projectSamsung.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
			projectSamsung.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
			projectSamsung.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
			projectSamsung.setConfigSortOrder(sortOrder);
			cursor = resolver.query(projectSamsung.getUri(), projectSamsung.getProjection(), projectSamsung.getSelection(), projectSamsung.getSelectionArgs(), sortOrder);
			project = projectSamsung;
			SVLog.i("** Conversation - getProject - Samsung URI");
		}
		project.setQueriedCursor(cursor);
		CursorUtil.printOutCursorInfo(cursor);
		return project;
	}

	public static class CommonProject extends AbsQueryProject<CommMsgData> {

		private SmsReaderHelper smsReaderSub = new SmsReaderHelper();
		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

		private static final String[] PROJECTION = {
				Telephony.MmsSms._ID,
				"date",
				"read",
				"type",
				"address",
				Telephony.Mms.THREAD_ID,
				Telephony.Mms.MESSAGE_ID,
				"body",
		};

		private int idx_id, idx_date, idx_read, idx_type, idx_address, idx_threadId, idx_m_id, idx_body;
		@Override
		public void storeProjectColumnIndex(Cursor cursor) {
			idx_id = cursor.getColumnIndex(Telephony.MmsSms._ID);
			idx_date = cursor.getColumnIndex("date");
			idx_read = cursor.getColumnIndex("read");
			idx_type = cursor.getColumnIndex("type");

			idx_address = cursor.getColumnIndex("address");
			idx_threadId = cursor.getColumnIndex(Telephony.Mms.THREAD_ID);
			idx_m_id = cursor.getColumnIndex(Telephony.Mms.MESSAGE_ID);
			idx_body = cursor.getColumnIndex("body");
		}

		@Override
		public CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.CONVERSATION);
			item._id = CursorUtil.getLong(cursor, idx_id);
			item.setDate(CursorUtil.getLong(cursor, idx_date));
			item.read = CursorUtil.getInt(cursor, idx_read);
			item.type = CursorUtil.getInt(cursor, idx_type);
			item.address = CursorUtil.getString(cursor, idx_address);
			item.thread_id = CursorUtil.getLong(cursor, idx_threadId);
			item.m_id = CursorUtil.getString(cursor, idx_m_id);
			// do extra task for fill empty slot
			String m_id = item.m_id;
			String address = item.address;
			ContentResolver resolver = context.getContentResolver();
			//
			if (TextUtils.isEmpty(address) || "null".equals(address)) {
				if (isExtraLoadAddressData) {
					item.listAddress = mmsReaderSub.getAddressNumber(resolver, (int) item._id);
				}
			}
			if (TextUtils.isEmpty(m_id) || "null".equals(m_id)) {
				item.body = CursorUtil.getString(cursor, idx_body);
			} else {
				if (isExtraLoadMessageData) {
					// read a text message on MMS
					String mid = mmsReaderSub.getMessageIdOnCommonUri(resolver, item.thread_id, item.m_id);
					String mms = mmsReaderSub.getTextMessage(resolver, mid);
					item.body = mms;
				}
			}
			return item;
		}


		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		/**
		 * content provider query param 형태를 selection, selectionArgs[] 별개로 할당시 WHERE clause 적용 안되는 경우 발생
		 * So that, bypass to use hard code like below > "read!=1"
		 * 오류 발생 단말기 : G5 LG-F700S Android 7.0
		 * 정상 동작 단말기 : GalaxyS7 SM-G930S 7.0 using {@link SamsungProject}
		 * @return
		 */
		@Override
		public String getSelection() {
			if (isLoadOnlyUnreadData) {
				return "read!=1";//Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			//if (isLoadOnlyUnreadData) {
			//	return new String[]{"1"};
			//}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
		}

		@Override
		public List<CommMsgData> readAll(Context context) {
			Cursor cursor = getQueriedCursor();
			List<CommMsgData> list = new ArrayList<>();
			if (cursor != null && cursor.moveToFirst()) {
				storeProjectColumnIndex(cursor);
				do {
					CommMsgData item = read(context, cursor);
					list.add(item);
				} while (cursor.moveToNext());
			}
			IOCloser.close(cursor);
			return list;
		}
	}

	public static class SamsungProject extends AbsQueryProject<CommMsgData> {

		private SmsReaderHelper smsReaderSub = new SmsReaderHelper();
		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

		private static final String[] PROJECTION = {
				Telephony.MmsSms._ID,
				"date",
				"recipient_ids",
				"snippet",
				"snippet_cs",
				//"snippet_type",
				"read",
		};

		private int idxId, idxDate, idxRecipientIds, idxSnippet, idxSnippetCs, idxRead;//, idxSnippetType;
		@Override
		public void storeProjectColumnIndex(Cursor cursor) {
			idxId = cursor.getColumnIndex(Telephony.MmsSms._ID);
			idxDate = cursor.getColumnIndex("date");
			idxRecipientIds = cursor.getColumnIndex("recipient_ids");
			idxSnippet = cursor.getColumnIndex("snippet");
			idxSnippetCs = cursor.getColumnIndex("snippet_cs");
			//idxSnippetType = cursor.getColumnIndex("snippet_type");
			idxRead = cursor.getColumnIndex("read");
		}

		@Override
		public CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.CONVERSATION);
			item._id = CursorUtil.getLong(cursor, idxId);
			item.setDate(CursorUtil.getLong(cursor, idxDate));
			item.recipient_ids = CursorUtil.getString(cursor, idxRecipientIds);
			item.snippet = CursorUtil.getString(cursor, idxSnippet);
			item.snippet_cs = CursorUtil.getInt(cursor, idxSnippetCs);
			//item.snippet_type = CursorUtil.getInt(cursor, idxSnippetType);
			item.read = CursorUtil.getInt(cursor, idxRead);
			//
			if (isExtraLoadAddressData) {
				item.address = mmsReaderSub.getRecipientAddress(context.getContentResolver(), Long.parseLong(item.recipient_ids));
			}
			if (isExtraLoadMessageData) {
				ContentResolver resolver = context.getContentResolver();
				// read a text message on MMS
				String mid = mmsReaderSub.getMessageIdOnSamsungUri(resolver, item._id, item.getDate());
				item.body = mmsReaderSub.getTextMessage(resolver, mid);
				// read a text message on SMS
				CommMsgData itemSms = smsReaderSub.getTextMessage(resolver, item._id, item.getDate(), CommMsgData.Type.CONVERSATION);
				CommMsgData selected = CommMsgData.getLastestMsgWithExistBody(item, itemSms);
				item.body = selected.body;
			}
			return item;
		}

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isLoadOnlyUnreadData) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isLoadOnlyUnreadData) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Uri.parse("content://mms-sms/conversations?simple=true");
		}

		@Override
		public List<CommMsgData> readAll(Context context) {
			Cursor cursor = getQueriedCursor();

			List<CommMsgData> list = new ArrayList<>();
			if (cursor != null && cursor.moveToFirst()) {
				storeProjectColumnIndex(cursor);
				do {
					CommMsgData item = read(context, cursor);
					list.add(item);
				} while (cursor.moveToNext());
			}
			IOCloser.close(cursor);
			return list;
		}
	}
}