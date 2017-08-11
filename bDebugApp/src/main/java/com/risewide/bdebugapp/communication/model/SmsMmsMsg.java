package com.risewide.bdebugapp.communication.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.risewide.bdebugapp.communication.util.DateUtil;

/**
 * Created by birdea on 2017-05-12.
 */

public class SmsMmsMsg implements Comparable<SmsMmsMsg> {
	// construct
	public enum Type{
		SMS,
		MMS,
		CONVERSATION
	}
	public Type msgType = Type.SMS;
	public SmsMmsMsg(Type type) {
		this.msgType = type;
	}
	// common column data
	public long _id;
	public int _count;
	public int read;
	// odd column data on some android devices
	private long date;
	///////////////////////////////////////////////////////////
	// sms column data from Uri.parse("content://sms");
	///////////////////////////////////////////////////////////
	public int type; //수신=1, 발신=2
	public String body;
	public String address;
	///////////////////////////////////////////////////////////
	// mms column data from Uri.parse("content://mms");
	///////////////////////////////////////////////////////////
	public int msg_box;
	public int text_only;
	public int mms_version;
	public int msg_type;
	public String subject;
	public int subject_charset;

	///////////////////////////////////////////////////////////
	// mms column data from Uri.parse("content://mms/{?}/addr");
	///////////////////////////////////////////////////////////
	public List<String> listAddress;

	///////////////////////////////////////////////////////////
	// mms column data from Uri.parse("content://mms/part");
	///////////////////////////////////////////////////////////
	public String text; // mms 문자 메시지

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
		.append(getClass().getSimpleName()).append("{")
		.append("_id").append("(").append(_id).append("),")
		.append("date").append("(").append(date).append("),")
		.append(DateUtil.getSimpleDate(date)).append(",")
		.append("address").append("(").append(address).append("),")
		.append("read").append("(").append(read).append("),")
		.append("type").append("(").append(type).append("),")
		.append("body").append("(").append(body).append("),")
		.append("msg_box").append("(").append(msg_box).append("),")
		.append("text_only").append("(").append(text_only).append("),")
		.append("mms_version").append("(").append(mms_version).append("),")
		.append("msg_type").append("(").append(msg_type).append("),")
		.append("subject").append("(").append(subject).append("),").append(getEncodedSubjectMessage()).append(",")
		.append("subject_charset").append("(").append(subject_charset).append("),")
		.append("}");
		return sb.toString();
	}

	@Override
	public int compareTo(SmsMmsMsg another) {
		//int cmp = a > b ? +1 : a < b ? -1 : 0;
		return Long.compare(another.date, date);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Getter, Setter for some picky data
	///////////////////////////////////////////////////////////////////////////////////////////////

	public void setDate(long date) {
		this.date = date;
	}

	private String strDate;

	/**
	 * 삼성 갤럭시S7 단말기의 mms long date 데이터가 짤려있음. OMG.
	 * maybe the but : down-cast from long value to int value.
	 * - 정상	:	"1,467,195,120,000"	(13자리)
	 * - 비정상	:	"1,502,158,253"		(9자리)
	 * @return
	 */
	public long getDate() {
		if (strDate == null) {
			strDate = String.valueOf(date);
			int length = strDate.length();
			if (length >= 13) {
				// ok
			} else {
				// not-ok
				if (date > 0) {
					int diff = 13 - length;
					for (int i=0;i<diff;i++) {
						date *= 10;
					}
				}
			}
		}
		return date;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Getter, Setter for some picky data
	///////////////////////////////////////////////////////////////////////////////////////////////
	public String getAddress(String myPhoneNumber) {
		if (Type.MMS.equals(type) && listAddress != null) {
			for (String address : listAddress) {
				String phoneNumber = address.trim();
				if (!address.startsWith("insert-") && !phoneNumber.equals(myPhoneNumber)) {
					return address;
				}
			}
		}
		return address;
	}

	public String getEncodedSubjectMessage() {
		if (subject_charset==106){
			try {
				return new String(subject.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getBodyMessage() {
		if (Type.SMS.equals(type)) {
			return body;
		}
		if (Type.MMS.equals(type)) {
			return text;
		}
		if (Type.CONVERSATION.equals(type)) {

		}
		return null;
	}
}