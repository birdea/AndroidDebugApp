package com.risewide.bdebugapp.communication.reader.projection;

import com.risewide.bdebugapp.util.SVLog;

import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-16.
 */

public class QueryConfig {

	private Order sortOrder = Order.DESC;
	private int orderLimitSize = 0;
	private String sortOrderColumn = "date";
	private boolean isExtraLoadMessageData = false;
	private boolean isExtraLoadAddressData = false;
	private boolean isSelectLoadOnlyUnread = false;

	public enum Order {
		DESC,	// descending sortOrder(=내림차순)
		ASC,	// ascending sortOrder(=오름차순)
	}

	public void setSortOrder(Order order) {
		if(order!=null) {
			this.sortOrder = order;
		}
	}

	public void setSortOrderColumn(String column) {
		if(!TextUtils.isEmpty(column)) {
			this.sortOrderColumn = column;
		}
	}

	public String getSortOrder() {
		if(!TextUtils.isEmpty(sortOrderColumn)) {
			return String.format(" %s %s ", sortOrderColumn, sortOrder.name());
		}
		return null;
	}

	public void setLimitSize(int size) {
		orderLimitSize = size;
	}

	public int getLimitSize() {
		return orderLimitSize;
	}

	private String getClauseLimitSize() {
		if (orderLimitSize > 0) {
			return String.format(" LIMIT %d ", orderLimitSize);
		}
		return null;
	}

	public String getComposedSortOrderClause() {
		StringBuilder sb = new StringBuilder();
		appendIfNotNull(sb, getSortOrder());
		appendIfNotNull(sb, getClauseLimitSize());
		SVLog.i("** ComposedSortOrderClause:"+sb.toString());
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
}
