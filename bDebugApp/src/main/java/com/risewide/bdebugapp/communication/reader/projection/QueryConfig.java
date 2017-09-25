package com.risewide.bdebugapp.communication.reader.projection;

import com.risewide.bdebugapp.util.SLog;

import android.provider.Telephony;
import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-16.
 */

public class QueryConfig {

	private Order mSortOrder = Order.DESC;
	private TableType mTableType = TableType.All;
	private int mOrderLimitSize = 0;
	private String mSortOrderColumn = "date";
	private boolean isExtraLoadMessageData = false;
	private boolean isExtraLoadAddressData = false;
	private boolean isSelectLoadOnlyUnread = false;
	private long mThreadId;

	public enum TableType {
		All,
		Inbox,
		Sent,
	}

	public enum Order {
		DESC,	// descending mSortOrder(=내림차순)
		ASC,	// ascending mSortOrder(=오름차순)
	}

	public void setSortOrder(Order order) {
		if(order!=null) {
			mSortOrder = order;
		}
	}

	public Order getSortOrder() {
		return mSortOrder;
	}

	public void setSortOrderColumn(String column) {
		if(column!=null && !TextUtils.isEmpty(column.trim())) {
			mSortOrderColumn = column.trim();
		}
	}

	public String getSortOrderClause() {
		if(!TextUtils.isEmpty(mSortOrderColumn)) {
			return String.format(" %s %s ", mSortOrderColumn, mSortOrder.name());
		}
		return null;
	}

	public TableType getTableType() {
		return mTableType;
	}

	public void setTableType(TableType type) {
		mTableType = type;
	}

	public void setLimitSize(int size) {
		mOrderLimitSize = size;
	}

	public int getLimitSize() {
		return mOrderLimitSize;
	}

	private String getClauseLimitSize() {
		if (mOrderLimitSize > 0) {
			return String.format(" LIMIT %d ", mOrderLimitSize);
		}
		return null;
	}

	/**
	 * 특정 CP URI에서 해당 옵션(DESC, ASC)이 반대로 먹히는 경우가 있다
	 * so that,
	 *
	 * 문자 메시지 생성 일자에 대한 sort는 리스트 데이터 가공 후 Collections interface 사용, NOT(cp query time),
	 * 발생 단말 : G5 LG-F700S Android 7.0, CP : {@link Telephony.MmsSms.CONTENT_CONVERSATIONS_URI}
	 * @return
	 */
	public String getComposedSortOrderClause() {
		StringBuilder sb = new StringBuilder();
		appendIfNotNull(sb, getSortOrderClause());
		appendIfNotNull(sb, getClauseLimitSize());
		SLog.i("** ComposedSortOrderClause:"+sb.toString());
		return sb.toString();
	}


	public boolean isExtraLoadMessageData() {
		return isExtraLoadMessageData;
	}

	public void setExtraLoadMessageData(boolean loadable) {
		isExtraLoadMessageData = loadable;
	}

	public boolean isExtraLoadAddressData() {
		return isExtraLoadAddressData;
	}

	public void setExtraLoadAddressData(boolean extraLoadAddressData) {
		isExtraLoadAddressData = extraLoadAddressData;
	}

	public boolean isSelectLoadOnlyUnread() {
		return isSelectLoadOnlyUnread;
	}

	public void setSelectLoadOnlyUnread(boolean loadOnlyUnread) {
		isSelectLoadOnlyUnread = loadOnlyUnread;
	}

	private StringBuilder appendIfNotNull(StringBuilder sb, String clause) {
		if (!TextUtils.isEmpty(clause)) {
			sb.append(clause);
		}
		return sb;
	}

	public long getThreadId() {
		return mThreadId;
	}

	public void setThreadId(long id) {
		mThreadId = id;
	}
}
