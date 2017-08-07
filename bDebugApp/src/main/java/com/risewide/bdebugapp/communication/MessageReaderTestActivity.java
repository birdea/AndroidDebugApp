package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.communication.data.MessageItem;
import com.risewide.bdebugapp.communication.helper.DateUtil;
import com.risewide.bdebugapp.communication.helper.StringMaskHelper;
import com.risewide.bdebugapp.communication.helper.TToast;
import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageReaderTestActivity extends BaseActivity {

	private HandyListAdapter handyListAdapter;
	private CommMessageReader textMessageManager = new CommMessageReader();
	//
	private List<MessageItem> srcList = new ArrayList<>();
	private List<MessageItem> dstList = new ArrayList<>();
	private Map<String, List<MessageItem>> srcMap = new HashMap<>();
	private List<MessageItem> contactList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handletextmessage);

		findView();
		init();
	}

	private void findView() {
		handyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);

		ListView lv_textmessage = (ListView) findViewById(R.id.lv_textmessage);
		lv_textmessage.setAdapter(handyListAdapter);

		// set text on textview
		Button btn_change_mode = (Button) findViewById(R.id.btn_change_mode);
		btn_change_mode.setText(String.valueOf(listMode.strTag));
	}

	private void init() {

		if(textMessageManager.hasPermission(this)==false){
			finish();
		}

		textMessageManager.readMessage(this, new CommMessageReader.OnTextMessageListener() {
			@Override
			public void onComplete(List<MessageItem> list) {
				List<MessageItem> dstList = storeMessageList(list);
				printOutMessageList(dstList);
				loadMessageList(dstList);
				//makeContactMessageMap(list);
			}
		});
	}

	private void makeContactMessageMap(List<MessageItem> fulllist) {

		for (MessageItem item : fulllist) {
			String id = item.address;
			if (srcMap.containsKey(id)) {
				List<MessageItem> bucket = srcMap.get(id);
				item.body = StringMaskHelper.remove(item.body);
				bucket.add(item);
			} else {
				List<MessageItem> bucket = new ArrayList<>();
				item.body = StringMaskHelper.remove(item.body);
				bucket.add(item);
				srcMap.put(id, bucket);
			}
		}

		for (Map.Entry<String, List<MessageItem>> entry : srcMap.entrySet()) {
			List<MessageItem> value = entry.getValue();
			for(MessageItem item : value) {
				SVLog.i("+ entry.key:"+entry.getKey()+", item:"+item.body);
			}
			Collections.sort(value);
			for(MessageItem item : value) {
				SVLog.i("+ entry.key:"+entry.getKey()+", item:"+item.toString());
			}
			contactList.add(value.get(0));
		}

		Collections.sort(contactList);

	}


	private List<MessageItem> storeMessageList(List<MessageItem> list) {
		if (list == null) {
			srcList.clear();
			handyListAdapter.clear();
			handyListAdapter.notifyDataSetChanged();
			return null;
		}
		srcList.addAll(list);
		dstList.addAll(processMaskedMessage(srcList));
		return dstList;
	}

	private List<MessageItem> processMaskedMessage(List<MessageItem> list) {
		if(list==null) {
			return null;
		}
		List<MessageItem> dst = new ArrayList<>();
		for(MessageItem item : list) {
			MessageItem newItem = new MessageItem();
			newItem.address = item.address;
			newItem.body = StringMaskHelper.remove(item.body);
			newItem.date = item.date;
			dst.add(newItem);
		}
		return dst;
	}

	private void changeMessageList() {
		// change mode, set each mode's list
		if (listMode == ListMode.All) {
			listMode = ListMode.Contact;
			loadMessageList(contactList);
		} else {
			listMode = ListMode.All;
			loadMessageList(dstList);
		}
		// set text on textview
		Button btn_change_mode = (Button) findViewById(R.id.btn_change_mode);
		btn_change_mode.setText(String.valueOf(listMode.strTag));
	}

	private void loadMessageList(final List<MessageItem> messageItemList) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handyListAdapter.clear();
				List<HandyListAdapter.Param> list = new ArrayList<>();
				for (MessageItem info : messageItemList) {
					String strDate = DateUtil.getSimpleDate(info.date);
					HandyListAdapter.Param param = new HandyListAdapter.Param();
					param.msgHead = String.format("Address(%s), lastTime(%s)", info.address, strDate);
					param.msgBody = String.format("%s", info.body);
					list.add(param);
				}
				handyListAdapter.set(list);
				int size = handyListAdapter.getCount();
				TToast.show(getBaseContext(), "size:"+size);
				handyListAdapter.notifyDataSetChanged();
			}
		});
	}

	private void printOutMessageList(List<MessageItem> messageItemList) {
		if (messageItemList==null) {
			return;
		}
		for (MessageItem info : messageItemList) {
			SVLog.d(info.toString());
		}
	}

	private enum ListMode {
		All("모드 변경\n(모두>개별)"),
		Contact("모드 변경\n(개별>모두)"),
		;
		public String strTag;

		ListMode(String t) {
			strTag = t;
		}
	}

	private ListMode listMode= ListMode.All;

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btn_change_mode: {
				changeMessageList();
				break;
			}
			case R.id.btn_check: {
				int cntTotal = 0, cntSelected = 0;
				//try {
				//	List<MessageItem> selectedList = handyListAdapter.getSelectedList();
				//	cntTotal = srcList.size();
				//	cntSelected = selectedList.size();
				//} catch (Exception e) {
				//	e.printStackTrace();
				//}
				TToast.show(getBaseContext(), String.format("선택 %s / 전체 %s", cntSelected, cntTotal));
				break;
			}
			case R.id.btn_refresh: {
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
}
