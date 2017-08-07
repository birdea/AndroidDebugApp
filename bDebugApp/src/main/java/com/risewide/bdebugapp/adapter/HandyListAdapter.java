package com.risewide.bdebugapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.risewide.bdebugapp.R;

public class HandyListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<Param> items = new ArrayList<>();


	public enum Mode {
		HEAD_ONLY,
		BODY_ONLY,
		HEAD_BODY,
	}

	private Mode mode;

	public HandyListAdapter(Context context) {
		this.context = context;
		this.mode = Mode.HEAD_BODY;
	}

	public HandyListAdapter(Context context, Mode mode) {
		this.context = context;
		this.mode = mode;
	}

	private class ViewHolder {
		TextView tv_head;
		TextView tv_body;
	}

	public static class Param {
		public String msgHead;
		public String msgBody;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (inflater == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_simple, null);
			viewHolder = new ViewHolder();
			//
			viewHolder.tv_head = (TextView) convertView.findViewById(R.id.tv_head);
			viewHolder.tv_head.setVisibility(View.GONE);
			viewHolder.tv_body = (TextView) convertView.findViewById(R.id.tv_body);
			//
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		adjustViewMode(viewHolder);

		if (items != null && items.size() > position) {
			Param item = items.get(position);
			viewHolder.tv_head.setText(String.valueOf(item.msgHead));
			viewHolder.tv_body.setText(String.valueOf(item.msgBody));
		}
		return convertView;
	}

	private void adjustViewMode(ViewHolder viewHolder) {
		if(Mode.HEAD_BODY.equals(mode)) {
			viewHolder.tv_head.setVisibility(View.VISIBLE);
			viewHolder.tv_head.setVisibility(View.VISIBLE);
		} else if (Mode.HEAD_ONLY.equals(mode)) {
			viewHolder.tv_body.setVisibility(View.GONE);
		} else if (Mode.BODY_ONLY.equals(mode)) {
			viewHolder.tv_head.setVisibility(View.GONE);
		} else {
			viewHolder.tv_head.setVisibility(View.VISIBLE);
			viewHolder.tv_head.setVisibility(View.VISIBLE);
		}
	}

	private String getBodyMessage(Param item) {
		if (item == null) {
			return null;
		}
		try {
			return String.format("%s / %s", item.msgHead, item.msgBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "err";
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	@Override
	public Param getItem(int position) {
		if (items == null) {
			return null;
		}
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void set(List<Param> list) {
		if (null == list) {
			return;
		}
		items = list;
	}

	public void add(Param item) {
		if (null == items) {
			items = new ArrayList<>();
		}
		items.add(item);
	}

	public void clear() {
		if (null == items) {
			return;
		}
		items.clear();
	}

	public void addAndnotifyDataSetChanged(final String head, final Object body) {
		getUiHandler().post(new Runnable() {
			@Override
			public void run() {
				Param param = new Param();
				param.msgHead = head;
				param.msgBody = String.valueOf(body);
				add(param);
				notifyDataSetChanged();
			}
		});
	}

	private Handler uiHandler;
	private Handler getUiHandler() {
		if(uiHandler == null) {
			uiHandler = new Handler(Looper.getMainLooper());
		}
		return uiHandler;
	}

}