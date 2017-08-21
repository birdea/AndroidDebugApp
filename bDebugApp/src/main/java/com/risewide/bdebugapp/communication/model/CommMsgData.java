package com.risewide.bdebugapp.communication.model;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.risewide.bdebugapp.communication.util.DateUtil;
import com.risewide.bdebugapp.util.SVLog;

import android.text.TextUtils;

/**
 * Created by birdea on 2017-05-12.
 */

public class CommMsgData implements Comparable<CommMsgData> {
	// construct
	public enum Type{
		SMS,
		MMS,
		CONVERSATION
	}
	public Type msgType = Type.SMS;
	public CommMsgData(Type type) {
		this.msgType = type;
	}
	// common column data
	public long _id;
	public int _count;
	public int read = Integer.MIN_VALUE;
	public long thread_id;
	public String m_id; //Message-ID
	// odd column data on some android devices
	private long date;
	///////////////////////////////////////////////////////////
	// sms column data from Uri.parse("content://sms");
	///////////////////////////////////////////////////////////
	public int type; //수신=1, 발신=2
	public String body;
	public String address;
	//{@link android.provider.Telephony.Sms.Inbox.STATUS}
	public int status;
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
	// samsung
	///////////////////////////////////////////////////////////
	public boolean isSamsungProjection = false;
	public String recipient_ids;
	public String snippet;
	public int snippet_cs;
	//public int snippet_type;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
		.append(getClass().getSimpleName()).append("{")
		.append("_id").append("(").append(_id).append("),")
		.append("m_id").append("(").append(m_id).append("),")
		.append("thread_id").append("(").append(thread_id).append("),")
		.append("date").append("(").append(date).append("),")
		.append(DateUtil.getSimpleDate(date)).append(",")
		.append("address").append("(").append(address).append("),")
		.append("read").append("(").append(read).append("),")
		.append("type").append("(").append(type).append("),")
		.append("body").append("(").append(body).append("),")
		.append("snippet").append("(").append(snippet).append("),")
		.append("snippet_cs").append("(").append(snippet_cs).append("),")
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
	public int compareTo(CommMsgData another) {
		//int cmp = a > b ? +1 : a < b ? -1 : 0;
		return Long.compare(date, another.date);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Getter, Setter for some picky data
	///////////////////////////////////////////////////////////////////////////////////////////////

	public void setDate(long date) {
		this.date = date;
	}

	private boolean hasNormalizedDateValue = false;

	/**
	 * mms long date 데이터가 짤려있음. OMG.
	 * 단말/통신사별 메시지 규격이 다르기 때문에 발생하는 것으로 추정됨.
	 * maybe the but : down-cast from long value to int value.
	 * - 정상	:	"1,467,195,120,000"	(13자리)
	 * - 비정상	:	"1,502,158,253"		(10자리)
	 * @return
	 */
	public long getDate() {
		if (hasNormalizedDateValue == false) {
			hasNormalizedDateValue = true;
			date = getNormalizeDateValue(date);
		}
		return date;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Getter, Setter for some picky data
	///////////////////////////////////////////////////////////////////////////////////////////////
	public String getAddress(String myPhoneNumber) {
		if (listAddress != null) {
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
		return body;
	}

	public String getReadStatus() {
		if (read == Integer.MIN_VALUE) {
			return "read:n/a";
		}
		return String.format("read:%d",read);
	}

	public static CommMsgData getLastestMsgWithExistBody(CommMsgData base, CommMsgData candi) {
		if (base != null && candi != null) {
			if(!TextUtils.isEmpty(base.body) && !TextUtils.isEmpty(candi.body)) {
				if (Long.compare(base.date, candi.date) >= 0) {
					return base;
				}
				return candi;
			}
			if (TextUtils.isEmpty(base.body) && TextUtils.isEmpty(candi.body)) {
				return base;
			}
			if (TextUtils.isEmpty(base.body)) {
				return candi;
			}
			if (TextUtils.isEmpty(candi.body)) {
				return base;
			}
		}
		if (base != null && !TextUtils.isEmpty(base.body)) {
			return base;
		}
		if (candi != null && !TextUtils.isEmpty(candi.body)) {
			return candi;
		}
		return base;
	}

	public static boolean isEqualDateValueOnNormalize(long base, long candi) {
		long normalBase = getNormalizeDateValue(base);
		long normalCandi = getNormalizeDateValue(candi);
		boolean result = (normalBase==normalCandi);
		SVLog.d("isEqualDateValue["+result+"]:"+normalBase+" vs "+normalCandi);
		return (normalBase==normalCandi);
	}

	private static long getNormalizeDateValue(long val) {
		String strDate = String.valueOf(val);
		int length = strDate.length();
		if (length >= 13) {
			// ok
		} else {
			// not-ok
			if (val > 0) {
				int diff = 13 - length;
				for (int i=0;i<diff;i++) {
					val *= 10;
				}
			}
		}
		return val;
	}
}