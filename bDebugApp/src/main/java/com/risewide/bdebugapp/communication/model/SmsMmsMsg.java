package com.risewide.bdebugapp.communication.model;

import com.risewide.bdebugapp.communication.helper.DateUtil;

import java.util.List;

/**
 * Created by birdea on 2017-05-12.
 */

public class SmsMmsMsg implements Comparable<SmsMmsMsg> {
	// common column data
	public long _id;
	public int _count;
	public long date;
	public int read;
	// msg type
	private enum Type{
		SMS,
		MMS
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
		return getClass().getSimpleName() + "{" +
				"_id(" + _id + ")," +
				"address(" + address + ")," +
				"date(" + DateUtil.getSimpleDate(date) + ")," +
				"read(" + read + ")," +
				"type(" + type + ")," +
				"body(" + body + ")," +
				")}";
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