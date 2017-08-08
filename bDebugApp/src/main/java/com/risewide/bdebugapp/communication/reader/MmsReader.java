package com.risewide.bdebugapp.communication.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.model.MessageItem;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader {

	private static final String[] PROJECTION_MMS_MESSAGE = {
			Telephony.Mms._ID,
			Telephony.Mms.CREATOR,
			//Telephony.Mms.Addr.ADDRESS,//Telephony.Mms.ADDRESS,
			//Telephony.Mms.PERSON,
			Telephony.Mms.DATE,
			Telephony.Mms.DATE_SENT,
			//Telephony.Mms.PROTOCOL,
			//Telephony.Mms.ERROR_CODE,
			Telephony.Mms.READ,
			Telephony.Mms.STATUS,
			//Telephony.Mms.TYPE,
			Telephony.Mms.SUBJECT,
			//Telephony.Mms.Inbox.BODY,
			//Telephony.Mms.SERVICE_CENTER,
			Telephony.Mms.LOCKED,
	};

	public List<MessageItem> read(Context context) {
		List<MessageItem> contactInfoList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;//") GROUP BY ("+Telephony.Sms.CONTACT_ID;
		Cursor msg = resolver.query(Telephony.Mms.CONTENT_URI, PROJECTION_MMS_MESSAGE, selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		MmsReader mmsReader = new MmsReader();
		while (msg.moveToNext())
		{
			MessageItem item = new MessageItem();

			item.id = msg.getLong(msg.getColumnIndex(Telephony.Mms._ID));
			item.creator = msg.getString(msg.getColumnIndex(Telephony.Mms.CREATOR));
			item.address = mmsReader.getAddressNumber(resolver, (int)item.id);
			//item.person = msg.getInt(msg.getColumnIndex(Telephony.Mms.PERSON));
			item.date = msg.getLong(msg.getColumnIndex(Telephony.Mms.DATE)) * 1000;
			item.dateSent = msg.getLong(msg.getColumnIndex(Telephony.Mms.DATE_SENT));
			//item.protocol = msg.getInt(msg.getColumnIndex(Telephony.Mms.PROTOCOL));
			//item.errorCode = msg.getInt(msg.getColumnIndex(Telephony.Mms.ERROR_CODE));
			item.read = msg.getInt(msg.getColumnIndex(Telephony.Mms.READ));
			item.status = msg.getInt(msg.getColumnIndex(Telephony.Mms.STATUS));
			//item.type = msg.getInt(msg.getColumnIndex(Telephony.Mms.TYPE));
			item.subject = msg.getString(msg.getColumnIndex(Telephony.Mms.SUBJECT));
			//item.body = mmsReader.messageFromMms(resolver, String.valueOf(item.id));
			//item.serviceCenter = msg.getString(msg.getColumnIndex(Telephony.Mms.SERVICE_CENTER));
			item.locked = msg.getInt(msg.getColumnIndex(Telephony.Mms.LOCKED));

			contactInfoList.add(item);
		}
		msg.close();
		//
		return contactInfoList;
	}

	class MmsPart {

	}

	public MmsPart getMmsPartInfo(ContentResolver resolver, String id) {
		MmsPart mmsPart = new MmsPart();

		return mmsPart;
	}

	public String getMmsText(ContentResolver resolver, String id) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = resolver.openInputStream(partURI);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null) {
					sb.append(temp);
					// if (sb.length() > 100) break;
					temp = reader.readLine();
				}
			}
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString().trim();
	}

	public String messageFromMms(ContentResolver resolver, String mmsId) {
		String selectionPart = "mid=" + mmsId;
		Uri uriPart = Uri.parse("content://mms/part");
		Cursor cursorPart = resolver.query(uriPart, null, selectionPart, null, null);

		String messageBody = "";
		if (cursorPart.moveToFirst()) {

			do {
				String partId = cursorPart.getString(cursorPart.getColumnIndex("_id"));
				String type = cursorPart.getString(cursorPart.getColumnIndex("ct"));

				if ("text/plain".equals(type)) {
					String data = cursorPart.getString(cursorPart.getColumnIndex("_data"));
					if (data != null) {
						messageBody += "\n" + getMmsText(resolver, partId);
					} else {
						messageBody += "\n" + cursorPart.getString(cursorPart.getColumnIndex("text"));
					}
				}
			} while (cursorPart.moveToNext());

			cursorPart.close();
		}
		if (!TextUtils.isEmpty(messageBody))
			messageBody = messageBody.substring(1);
		return messageBody;
	}

	// 참고 : android MMS 모니터링 http://devroid.com/80181708954
	// mms 전화번호 가져오는 정확한 메소드
	// 파라미터(2) int id 값은 "content://mms" 테이블의 "_id" 칼럼 값
	// 특히 int id 값을 toString 형변환하는 점에 주의
	public String getAddressNumber(ContentResolver resolver, int id) {
		String selectionAdd = new String("msg_id=" + id);
		String uriStr = MessageFormat.format("content://mms/{0}/addr", Integer.toString(id)); // id를 형변환해주지 않으면, 천단위 넘어가면 콤마가 붙으므로 오류가 나게 된다.
		Uri uriAddress = Uri.parse(uriStr);
		Cursor cAdd = resolver.query(uriAddress, null, selectionAdd, null, null);

		String name = null;
		//if (cAdd.moveToFirst()) {
		while (cAdd.moveToNext()) {
			String number = cAdd.getString(cAdd.getColumnIndex("address"));
			if (number != null) {
				try {
					Long.parseLong(number.replace("-", ""));
					name = number;
				}
				catch (NumberFormatException nfe) {
					if (name == null) {
						name = number;
					}
				}
			}
		}
		//}
		if (cAdd != null) {
			cAdd.close();
		}
		return name;
	}

	public Bitmap getMmsImage(ContentResolver resolver, String _id) {
		Uri partURI = Uri.parse("content://mms/part/" + _id);
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			is = resolver.openInputStream(partURI);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		return bitmap;
	}
}
