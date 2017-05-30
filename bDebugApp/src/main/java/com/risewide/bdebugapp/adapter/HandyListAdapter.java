package com.risewide.bdebugapp.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.risewide.bdebugapp.R;

public class HandyListAdapter extends BaseAdapter {

	public static final int MAX_ITEM = 100;

	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Param> items = new ArrayList<Param>();


	public enum Mode {
		HEAD_ONLY,
		BODY_ONLY,
		HEAD_BODY,
	}

	private Mode mode;

	public HandyListAdapter(Context context, Mode mode) {
		this.context = context;
		this.mode = mode;
	}

	private class ViewHolder {
		TextView tv_head;
		TextView tv_body;
	}

	public enum From {
		ME,
		YOU,
	}

	public static class Param {
		public String msgHead;
		public String msgBody;
		public From from;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (inflater == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_simple, null);
		}

		TextView tv_head = (TextView) convertView.findViewById(R.id.tv_head);
		TextView tv_body = (TextView) convertView.findViewById(R.id.tv_body);

		if(Mode.HEAD_BODY.equals(mode)) {
			tv_head.setVisibility(View.VISIBLE);
			tv_head.setVisibility(View.VISIBLE);
		} else if (Mode.HEAD_ONLY.equals(mode)) {
			tv_body.setVisibility(View.GONE);
		} else if (Mode.BODY_ONLY.equals(mode)) {
			tv_head.setVisibility(View.GONE);
		} else {
			tv_head.setVisibility(View.VISIBLE);
			tv_head.setVisibility(View.VISIBLE);
		}

		if (items != null && items.size() > position) {
			Param item = items.get(position);
			tv_head.setText(String.valueOf(item.msgHead));
			tv_body.setText(String.valueOf(item.msgBody));

			if(From.ME.equals(item.from)) {
				tv_body.setGravity(Gravity.RIGHT);
			} else {
				tv_body.setGravity(Gravity.LEFT);
			}
		}
		return convertView;
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

	public void set(ArrayList<Param> list) {
		if (null == list) {
			return;
		}
		items = list;
	}

	public void add(Param item) {
		if (null == items) {
			return;
		}
		if (items.size() >= MAX_ITEM) {
			items.remove(0);
		}
		items.add(item);
	}

	public void clear() {
		if (null == items) {
			return;
		}
		items.clear();
	}
}