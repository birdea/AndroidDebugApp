package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.helper.DateUtil;
import com.risewide.bdebugapp.communication.helper.StringMaskHelper;
import com.risewide.bdebugapp.communication.helper.TToast;
import com.risewide.bdebugapp.communication.model.SmsProtocolReadType;
import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageReaderTestActivity extends BaseActivity {

	private HandyListAdapter handyListAdapter;
	private SmsUnifyMessageReader smsUnifyMessageReader = new SmsUnifyMessageReader();
	//
	private List<MessageItem> srcList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handletextmessage);
		initView();
		initCont();
	}

	private void initView() {

		RadioGroup rgProtocolType = (RadioGroup)findViewById(R.id.rgProtocolType);
		rgProtocolType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbProtocolTypeAll1:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsProtocolReadType.ALL_SEQUENTIAL);
						break;
					case R.id.rbProtocolTypeAll2:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsProtocolReadType.ALL_SMS_MMS);
						break;
					case R.id.rbProtocolTypeSms:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsProtocolReadType.SMS);
						break;
					case R.id.rbProtocolTypeMms:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsProtocolReadType.MMS);
						break;
				}
			}
		});
		rgProtocolType.check(R.id.rbProtocolTypeSms);

		handyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);

		ListView lv_textmessage = (ListView) findViewById(R.id.lv_textmessage);
		lv_textmessage.setAdapter(handyListAdapter);
	}

	private void initCont() {
		if(smsUnifyMessageReader.hasPermission(this)==false){
			finish();
		}
		refresh();
	}

	private AtomicBoolean isProcessing = new AtomicBoolean(false);
	private void refresh() {
		if (isProcessing.get()) {
			TToast.show(this, "refresing.. wait for a sec");
			return;
		}
		isProcessing.set(true);
		smsUnifyMessageReader.read(this, new SmsUnifyMessageReader.OnReadTextMessageListener() {
			@Override
			public void onComplete(List<MessageItem> list) {
				List<MessageItem> dstList = storeMessageList(list);
				printOutMessageList(dstList);
				loadMessageList(dstList);
				isProcessing.set(false);
			}
		});
	}

	private List<MessageItem> storeMessageList(List<MessageItem> list) {
		if (list == null) {
			srcList.clear();
			handyListAdapter.clear();
			handyListAdapter.notifyDataSetChanged();
			return null;
		}
		srcList.addAll(list);
		return srcList;
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

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btn_check: {
				TToast.show(getBaseContext(), String.format("전체 %s", srcList.size()));
				break;
			}
			case R.id.btn_refresh: {
				refresh();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
}
