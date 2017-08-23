package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.model.CommMsgReadType;
import com.risewide.bdebugapp.communication.reader.AbsMsgReader;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.DateUtil;
import com.risewide.bdebugapp.communication.util.DelayChecker;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.communication.util.WidgetHelper;
import com.risewide.bdebugapp.util.DeviceInfo;
import com.risewide.bdebugapp.util.SVLog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageReaderTestActivity extends BaseActivity {

	private HandyListAdapter handyListAdapter;
	private CommUnifyMessageReader commUnifyMessageReader = new CommUnifyMessageReader();
	private View llyProtocolTablyTypeLayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handletextmessage);
		initView();
		initCont();
	}

	private void initView() {

		llyProtocolTablyTypeLayer = findViewById(R.id.llyProtocolTablyTypeLayer);

		RadioGroup rgProtocolType = (RadioGroup)findViewById(R.id.rgProtocolType);
		rgProtocolType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbProtocolTypeMmsSms:
						llyProtocolTablyTypeLayer.setVisibility(View.GONE);
						commUnifyMessageReader.setReadProtocolType(CommMsgReadType.CONVERSATION);
						break;
					case R.id.rbProtocolTypeSms:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						commUnifyMessageReader.setReadProtocolType(CommMsgReadType.SMS);
						break;
					case R.id.rbProtocolTypeMms:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						commUnifyMessageReader.setReadProtocolType(CommMsgReadType.MMS);
						break;
					case R.id.rbProtocolTypeThreadId:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						commUnifyMessageReader.setReadProtocolType(CommMsgReadType.THREAD_ID);
						break;
				}
			}
		});
		rgProtocolType.check(R.id.rbProtocolTypeMmsSms);

		RadioGroup rgQuerySelectOrder = (RadioGroup)findViewById(R.id.rgQuerySortOrder);
		rgQuerySelectOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbQuerySortDesc: {
						QueryConfig config = commUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.DESC);
						//commUnifyMessageReader.setQueryConfig(config);
						break;
					}
					case R.id.rbQuerySortAsc: {
						QueryConfig config = commUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.ASC);
						//commUnifyMessageReader.setQueryConfig(config);
						break;
					}
				}
			}
		});
		rgQuerySelectOrder.check(R.id.rbQuerySortDesc);

		RadioGroup rgProtocolTableType = (RadioGroup)findViewById(R.id.rgProtocolTableType);
		rgProtocolTableType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbProtocolTableTypeAll: {
						QueryConfig config = commUnifyMessageReader.getQueryConfig();
						config.setTableType(QueryConfig.TableType.All);
						//commUnifyMessageReader.setQueryConfig(config);
						break;
					}
					case R.id.rbProtocolTableTypeInbox: {
						QueryConfig config = commUnifyMessageReader.getQueryConfig();
						config.setTableType(QueryConfig.TableType.Inbox);
						//commUnifyMessageReader.setQueryConfig(config);
						break;
					}
				}
			}
		});
		rgProtocolTableType.check(R.id.rbProtocolTableTypeAll);


		handyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);

		ListView lv_textmessage = (ListView) findViewById(R.id.lv_textmessage);
		lv_textmessage.setAdapter(handyListAdapter);
		lv_textmessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<CommMsgData> list = commUnifyMessageReader.getCurrentMsgList();
				long threadId = list.get(position)._id;
				TToast.show(MessageReaderTestActivity.this, "Selected thread_id:"+threadId);
				EditText etThreadId = (EditText) findViewById(R.id.etThreadId);
				etThreadId.setText(String.valueOf(threadId));
			}
		});
	}

	private void initCont() {
		if(commUnifyMessageReader.hasPermission(this)==false){
			finish();
			return;
		}
		refresh();
		//
		commUnifyMessageReader.registerContentObserver(this, true, contentObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		commUnifyMessageReader.unregisterContentObserver(this, contentObserver);
	}

	private AbsMsgReader.OnContentObserver contentObserver = new AbsMsgReader.OnContentObserver() {
		@Override
		public void onChange() {
			SVLog.i("*OnContentObserver.onChange()");
			refresh();
		}
	};

	private AtomicBoolean isRefreshing = new AtomicBoolean(false);
	private void refresh() {
		if (isRefreshing.get()) {
			TToast.show(this, "*refresh - Already started.. wait for a sec");
			SVLog.d("*refresh - Already started.. wait for a sec");
			return;
		}
		isRefreshing.set(true);
		//TToast.show(this, "*refresh - Start loading.. wait for a sec");
		SVLog.d("*refresh - Start loading.. wait for a sec");
		final DelayChecker checker = new DelayChecker();
		checker.start("commUnifyMessageReader");
		//
		EditText etThreadId = (EditText) findViewById(R.id.etThreadId);
		EditText etQuerySortOrderColumn = (EditText)findViewById(R.id.etQuerySortOrderColumn);
		EditText etQuerySelectLimitSize = (EditText)findViewById(R.id.etQuerySelectLimitSize);
		CheckBox cbLoadMessageData = (CheckBox)findViewById(R.id.cbLoadMessageData);
		CheckBox cbLoadAddressData = (CheckBox)findViewById(R.id.cbLoadAddressData);
		CheckBox cbLoadOnlyUnread = (CheckBox)findViewById(R.id.cbLoadOnlyUnread);

		String columnName = WidgetHelper.getTextString(etQuerySortOrderColumn);
		int limitSize = WidgetHelper.getTextInteger(etQuerySelectLimitSize);
		long threadId = WidgetHelper.getTextInteger(etThreadId);

		QueryConfig queryConfig = commUnifyMessageReader.getQueryConfig();
		queryConfig.setLimitSize(limitSize);
		queryConfig.setSortOrderColumn(columnName);
		queryConfig.setExtraLoadMessageData(cbLoadMessageData.isChecked());
		queryConfig.setExtraLoadAddressData(cbLoadAddressData.isChecked());
		queryConfig.setSelectLoadOnlyUnread(cbLoadOnlyUnread.isChecked());
		queryConfig.setThreadId(threadId);
		//
		//commUnifyMessageReader.setQueryConfig(queryConfig);
		commUnifyMessageReader.read(this, new CommUnifyMessageReader.OnReadTextMessageListener() {
			@Override
			public void onComplete(List<CommMsgData> list) {
				long timeDelay = checker.end();
				//checker.end();
				printOutMessageList(list);
				loadMessageList(list);
				//checker.end();
				int length = (list==null)?0:list.size();
				TToast.show(getBaseContext(), "Complete loading > size: "+length+" ea");
				SVLog.d("Complete loading > size: "+length+" ea");
				//checker.showToast(getBaseContext());
				notifyLastResultInfo(timeDelay, length);
				SVLog.d("*refresh - Complete loading!");
				isRefreshing.set(false);
			}
		});
	}

	private void loadMessageList(final List<CommMsgData> messageItemList) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handyListAdapter.clear();
				List<HandyListAdapter.Param> list = new ArrayList<>();
				String myPhoneNumber = DeviceInfo.getPhoneNumber(MessageReaderTestActivity.this);
				for (CommMsgData info : messageItemList) {
					String strDate = DateUtil.getSimpleDate(info.getDate());
					HandyListAdapter.Param param = new HandyListAdapter.Param();
					param.msgHead = String.format("id(%s) tid(%s) addr(%s) date(%s) read(%s) type(%s)", info._id, info.thread_id, info.getAddress(myPhoneNumber), strDate, info.getReadStatus(), info.msgType);
					param.msgBody = String.format("%s", info.getBodyMessage());
					list.add(param);
				}
				handyListAdapter.set(list);
				handyListAdapter.notifyDataSetChanged();
			}
		});
	}

	private void notifyLastResultInfo(final double timeDelay, final int size) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tvLastResult = (TextView)findViewById(R.id.tvLastResult);
				String strDelay = String.valueOf(timeDelay);
				String strSize = String.valueOf(size);
				String strType = String.valueOf(commUnifyMessageReader.getReadProtocolType().name());
				String strLimit = String.valueOf(commUnifyMessageReader.getQueryConfig().getLimitSize());
				String strPlusAddress = String.valueOf(commUnifyMessageReader.getQueryConfig().isExtraLoadAddressData());
				String strPlusMessage = String.valueOf(commUnifyMessageReader.getQueryConfig().isExtraLoadMessageData());
				String strOnlyUnread = String.valueOf(commUnifyMessageReader.getQueryConfig().isSelectLoadOnlyUnread());

				String result = String.format("Delayed(ms)[%s]\non\nSize[%s], Type[%s], Limit[%s], +Address[%s], +Message[%s], +Unread[%s]"
				, strDelay, strSize, strType, strLimit, strPlusAddress, strPlusMessage, strOnlyUnread
				);
				tvLastResult.setText(result);
				}
			});
	}

	private void printOutMessageList(List<CommMsgData> messageItemList) {
		if (messageItemList==null) {
			return;
		}
		for (CommMsgData info : messageItemList) {
			SVLog.d(info.toString());
		}
	}

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btn_check: {
				TToast.show(getBaseContext(), String.format("전체 %s", handyListAdapter.getCount()));
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
