package com.risewide.bdebugapp.communication.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.util.HandyThreadTask;
import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.util.SVLog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Telephony;

/**
 * @link http://stackoverflow.com/questions/3012287/how-to-read-mms-data-in-android
 * Created by birdea on 2017-08-09.
 */

public class MmsReaderSub {

	public interface OnReadListener {
		void onRead(Object data);
	}

	public void getAddressNumberAsync(final ContentResolver resolver, final int id, final OnReadListener listener){
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				List<String> list = getAddressNumber(resolver, id);
				listener.onRead(list);
			}
		});
	}

	public void getMessageOfMmsAsync(final ContentResolver resolver, final String mmsId, final OnReadListener listener) {
		HandyThreadTask.execute(new Runnable() {
			@Override
			public void run() {
				String data = getTextMessage(resolver, mmsId);
				listener.onRead(data);
			}
		});
	}

	public List<String> getAddressNumber(ContentResolver resolver, int id) {
		String[] projection = new String[] { "*" };
		String selection = null;//new String("msg_id=" + id); //?: optiional vs necessary(mandatory)
		Uri uri = Telephony.Mms.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).appendPath("addr").build();
		Cursor cursor = resolver.query(uri, projection, selection, null, null);

		List<String> list = new ArrayList<>();
		if (cursor!=null && cursor.moveToFirst()) {
			// don't be lazy to assign all indexes
			int idx_address = cursor.getColumnIndex(Telephony.Mms.Addr.ADDRESS);
			//
			int rowCount = 0;
			do {
				String address = cursor.getString(idx_address);
				SVLog.i("rowCount:"+(rowCount++)+", idx_address:"+idx_address+", address:"+address);
				list.add(address);
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return list;
	}

	public String getTextMessage(ContentResolver resolver, String mid) {
		String selection = Telephony.Mms.Part.MSG_ID + "=" + mid;
		Uri uri = Telephony.Mms.CONTENT_URI.buildUpon().appendPath("part").build();
		//Uri uri = Uri.parse("content://mms/part");
		Cursor cursor = resolver.query(uri, null, selection, null, null);
		//
		StringBuilder sb = new StringBuilder();
		if (cursor != null && cursor.moveToFirst()) {
			//
			int idx_id = cursor.getColumnIndex(Telephony.Mms.Part._ID);
			int idx_ct = cursor.getColumnIndex(Telephony.Mms.Part.CONTENT_TYPE);
			int idx_data = cursor.getColumnIndex(Telephony.Mms.Part._DATA);
			int idx_text = cursor.getColumnIndex(Telephony.Mms.Part.TEXT);
			//
			do {
				String partId = cursor.getString(idx_id);
				String type = cursor.getString(idx_ct);
				if ("text/plain".equals(type)) {
					String data = cursor.getString(idx_data);
					if (data != null) {
						sb.append("\n").append(getMmsText(resolver, partId));
					} else {
						sb.append("\n").append(cursor.getString(idx_text));
					}
				}
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return sb.toString();
	}

	public String getMmsText(ContentResolver resolver, String id) {
		Uri uri = Telephony.Mms.CONTENT_URI.buildUpon().appendPath("part").appendPath(id).build();
		//Uri uri = Uri.parse("content://mms/part/" + _id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = resolver.openInputStream(uri);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String temp = br.readLine();
				while (temp != null) {
					sb.append(temp);
					// if (sb.length() > 100) break;
					temp = br.readLine();
				}
				IOCloser.close(br);
				IOCloser.close(isr);
			}
		} catch (IOException e) {
		} finally {
			IOCloser.close(is);
		}
		return sb.toString();
	}

	public Bitmap getImageData(ContentResolver resolver, String _id) {
		Uri uri = Telephony.Mms.CONTENT_URI.buildUpon().appendPath("part").appendPath(_id).build();
		//Uri uri = Uri.parse("content://mms/part/" + _id);
		InputStream is = null;
		try {
			is = resolver.openInputStream(uri);
			return BitmapFactory.decodeStream(is);
		} catch (IOException e) {}
		finally {
			IOCloser.close(is);
		}
		return null;
	}

	public String getRecipientAddress(ContentResolver resolver, long recipientId) {
		String number = null;
		String[] projection = new String[] { "*" };
		Cursor cursor = resolver.query(ContentUris.withAppendedId(Uri.parse("content://mms-sms/canonical-address"), recipientId),
				projection, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				number = cursor.getString(0); // same as cursor.getString(cursor.getColumnIndex("address"))
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return number;
	}

	public String getMessageId(ContentResolver resolver, long thread_id, String m_id) {
		String id = null;
		String[] projection = new String[] { "*" };
		String selection = new StringBuilder()
				.append(Telephony.Mms.Inbox.MESSAGE_ID).append("=?")
				.append(" AND ")
				.append(Telephony.Mms.Inbox.THREAD_ID).append("=?")
				.toString();
		String[] selectionArgs = new String[] { m_id, String.valueOf(thread_id) };
		String sortOrder = Telephony.Mms.Inbox.DEFAULT_SORT_ORDER;
		Uri uri = Telephony.Mms.Inbox.CONTENT_URI;
		Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				id = cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox._ID)); // same as cursor.getString(cursor.getColumnIndex("address"))
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return id;
	}
}
