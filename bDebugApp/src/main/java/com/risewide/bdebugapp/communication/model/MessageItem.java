package com.risewide.bdebugapp.communication.model;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageItem implements Comparable<MessageItem> {
	public long id;
	public String creator;
	public String address;
	public int person;
	public long date;
	public long dateSent;
	public int protocol;
	public int errorCode;
	public int read;
	public int status;
	public int type;
	public String subject;
	public String body;
	public String serviceCenter;
	public int locked;
	//
	public boolean isSelected = true; // as default value

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"id(" + id + ")," +
				"creator(" + creator + ")," +
				"address(" + address + ")," +
				"person(" + person + ")," +
				"date(" + date + ")," +
				"dateSent(" + dateSent + ")," +
				"protocol(" + protocol + ")," +
				"errorCode(" + errorCode + ")," +
				"read(" + read + ")," +
				"status(" + status + ")," +
				"type(" + type + ")," +
				"subject(" + subject + ")," +
				"body(" + body + ")," +
				"serviceCenter(" + serviceCenter + ")," +
				"locked(" + locked + ")," +
				")}";
	}

	@Override
	public int compareTo(MessageItem another) {
		//int cmp = a > b ? +1 : a < b ? -1 : 0;
		return Long.compare(another.date, date);
	}
}
