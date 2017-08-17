package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;
import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-08-09.
 */

public class ConversationReadProject {

	/**
	 * 삼성 단말기에서 Telephony.MmsSms.CONTENT_CONVERSATIONS_URI 로 접근이 불가하기 때문에 Project 정보 구분
	 *
	 * @param context
	 * @param queryConfig
	 * @param sortOrder
	 * @return
	 */
	public static ReadProjector<MmsSmsMsg> getProject(Context context, QueryConfig queryConfig, String sortOrder) {
		ReadProjector<MmsSmsMsg> project = new CommonProject();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());
		//
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor;
		try {
			cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
			SVLog.i("** Conversation - getProject - Common(LG) URI");
		} catch (Exception ignorable) {
			ReadProjector<MmsSmsMsg> projectSamsung = new SamsungProject();
			projectSamsung.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
			projectSamsung.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
			projectSamsung.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());
			cursor = resolver.query(projectSamsung.getUri(), projectSamsung.getProjection(), projectSamsung.getSelection(), projectSamsung.getSelectionArgs(), sortOrder);
			project = projectSamsung;
			SVLog.i("** Conversation - getProject - Samsung URI");
		}
		project.setQueriedCursor(cursor);
		CursorUtil.printOutCursorInfo(cursor);
		return project;
	}

	public static class CommonProject extends ReadProjector<MmsSmsMsg> {

		private static final String[] PROJECTION = {
				"*",
				/*"_id",
				"date",
				"read",
				"type",
				"address",
				Telephony.Mms.THREAD_ID,
				Telephony.Mms.MESSAGE_ID,
				"body",*/
		};

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
			if (isSelectLoadOnlyUnread) {
				return "read!=1";//Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			//if (isSelectLoadOnlyUnread) {
			//	return new String[]{"1"};
			//}
			return null;
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
		public MmsSmsMsg read(Context context, Cursor cursor) {
			MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.CONVERSATION);
			item._id = cursor.getLong(cursor.getColumnIndex(Telephony.MmsSms._ID));
			item.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			item.read = cursor.getInt(cursor.getColumnIndex("read"));
			item.type = cursor.getInt(cursor.getColumnIndex("type"));
			item.address = cursor.getString(cursor.getColumnIndex("address"));
			item.thread_id = cursor.getLong(cursor.getColumnIndex(Telephony.Mms.THREAD_ID));
			item.m_id = cursor.getString(cursor.getColumnIndex(Telephony.Mms.MESSAGE_ID));
			// do extra task for fill empty slot
			String m_id = item.m_id;
			String address = item.address;
			ContentResolver cr = context.getContentResolver();
			//
			if (TextUtils.isEmpty(address) || "null".equals(address)) {
				if (isExtraLoadAddressData) {
					item.listAddress = mmsReaderSub.getAddressNumber(cr, (int) item._id);
				}
			}
			if (TextUtils.isEmpty(m_id) || "null".equals(m_id)) {
				item.body = cursor.getString(cursor.getColumnIndex("body"));
			} else {
				if (isExtraLoadMessageData) {
					String mid = mmsReaderSub.getMessageId(context.getContentResolver(), item.thread_id, item.m_id);
					item.body = mmsReaderSub.getTextMessage(context.getContentResolver(), mid);
				}
			}
			return item;
		}
	}

	public static class SamsungProject extends ReadProjector<MmsSmsMsg> {

		private static final String[] PROJECTION = {
				"*"
		};

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isSelectLoadOnlyUnread) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isSelectLoadOnlyUnread) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Uri.parse("content://mms-sms/conversations?simple=true");
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {

		}

		private MmsReaderSub mmsReaderSub = new MmsReaderSub();

		@Override
		public MmsSmsMsg read(Context context, Cursor cursor) {
			MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.CONVERSATION);
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
