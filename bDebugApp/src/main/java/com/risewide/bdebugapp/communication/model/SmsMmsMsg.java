package com.risewide.bdebugapp.communication.model;

import com.risewide.bdebugapp.communication.helper.DateUtil;

import java.util.List;

/**
 * Created by birdea on 2017-05-12.
 */

public class SmsMmsMsg implements Comparable<SmsMmsMsg> {
	// construct
	public SmsMmsMsg(Type type) {
		this.msgType = type;
	}
	// common column data
	public long _id;
	public int _count;
	public long date;
	public int read;
	// msg type
	public enum Type{
		SMS,
		MMS,
		CONVERSATION
	}
	public Type msgType = Type.SMS;
	////////////////////////////////////
	// sms column data
	////////////////////////////////////
	public int type; //수신=1, 발신=2
	public String body;
	public String address;
	////////////////////////////////////
	// mms column data
	////////////////////////////////////
	public int msg_box;
	public int text_only;
	public int mms_version;
	public int msg_type;
	public String subject;
	public int subject_charset;
	public List<String> listAddress;

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
		.append("subject").append("(").append(subject).append("),")
		.append("subject_charset").append("(").append(subject_charset).append("),")
		.append("}")
		;
		/*return getClass().getSimpleName() + "{" +
				"_id(" + _id + ")," +
				"date" + date + "),"
				"address(" + address + ")," +
				"date(" + DateUtil.getSimpleDate(date) + ")," +
				"read(" + read + ")," +
				"type(" + type + ")," +
				"body(" + body + ")," +
				")}";*/
		return sb.toString();
	}

	@Override
	public int compareTo(SmsMmsMsg another) {
		//int cmp = a > b ? +1 : a < b ? -1 : 0;
		return Long.compare(another.date, date);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public String getAddress(String myPhoneNumber) {
		if (address != null) {
			return address;
		}
		if (listAddress != null) {
			for (String address : listAddress) {
				String phoneNumber = address.trim();
				if (!address.startsWith("insert-") && !phoneNumber.equals(myPhoneNumber)) {
					return address;
				}
			}
		}
		return null;
	}
}