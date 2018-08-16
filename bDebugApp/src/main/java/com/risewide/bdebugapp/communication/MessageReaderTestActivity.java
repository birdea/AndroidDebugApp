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
import com.risewide.bdebugapp.util.SLog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageReaderTestActivity extends BaseActivity {

	private HandyListAdapter mHandyListAdapter;
	private CommUnifyMessageReader mCommUnifyMessageReader = new CommUnifyMessageReader();

	//- view component
	private View llyProtocolTablyTypeLayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_message_read);
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
						mCommUnifyMessageReader.setReadProtocolType(CommMsgReadType.CONVERSATION);
						break;
					case R.id.rbProtocolTypeSms:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						mCommUnifyMessageReader.setReadProtocolType(CommMsgReadType.SMS);
						break;
					case R.id.rbProtocolTypeMms:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						mCommUnifyMessageReader.setReadProtocolType(CommMsgReadType.MMS);
						break;
					case R.id.rbProtocolTypeThreadId:
						llyProtocolTablyTypeLayer.setVisibility(View.VISIBLE);
						mCommUnifyMessageReader.setReadProtocolType(CommMsgReadType.THREAD_ID);
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
						QueryConfig config = mCommUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.DESC);
						//mCommUnifyMessageReader.setQueryConfig(config);
						break;
					}
					case R.id.rbQuerySortAsc: {
						QueryConfig config = mCommUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.ASC);
						//mCommUnifyMessageReader.setQueryConfig(config);
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
						QueryConfig config = mCommUnifyMessageReader.getQueryConfig();
						config.setTableType(QueryConfig.TableType.All);
						//mCommUnifyMessageReader.setQueryConfig(config);
						break;
					}
					case R.id.rbProtocolTableTypeInbox: {
						QueryConfig config = mCommUnifyMessageReader.getQueryConfig();
						config.setTableType(QueryConfig.TableType.Inbox);
						//mCommUnifyMessageReader.setQueryConfig(config);
						break;
					}
				}
			}
		});
		rgProtocolTableType.check(R.id.rbProtocolTableTypeAll);


		mHandyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);

		ListView lv_textmessage = (ListView) findViewById(R.id.lv_textmessage);
		lv_textmessage.setAdapter(mHandyListAdapter);
		lv_textmessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<CommMsgData> list = mCommUnifyMessageReader.getReadMsgList();
				long threadId = list.get(position).getThreadId();
				TToast.show(MessageReaderTestActivity.this, "Selected thread_id:"+threadId);
				EditText etThreadId = (EditText) findViewById(R.id.etThreadId);
				etThreadId.setText(String.valueOf(threadId));
			}
		});
	}

	private void initCont() {
		if(mCommUnifyMessageReader.hasPermission(this)==false){
			finish();
			return;
		}
		refresh();
		//
		mCommUnifyMessageReader.registerContentObserver(this, true, contentObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCommUnifyMessageReader.unregisterContentObserver(this, contentObserver);
	}

	private AbsMsgReader.OnContentObserver contentObserver = new AbsMsgReader.OnContentObserver() {
		@Override
		public void onChange() {
			SLog.d("*OnContentObserver.onChange()");
			refresh();
		}
	};

	private AtomicBoolean isRefreshing = new AtomicBoolean(false);
	private void refresh() {
		if (isRefreshing.get()) {
			TToast.show(this, "*refresh - Already started.. wait for a sec");
			SLog.d("*refresh - Already started.. wait for a sec");
			return;
		}
		isRefreshing.set(true);
		//TToast.show(this, "*refresh - Start loading.. wait for a sec");
		SLog.d("*refresh - Start loading.. wait for a sec");
		final DelayChecker checker = new DelayChecker();
		checker.start("mCommUnifyMessageReader");
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

		QueryConfig queryConfig = mCommUnifyMessageReader.getQueryConfig();
		queryConfig.setLimitSize(limitSize);
		queryConfig.setSortOrderColumn(columnName);
		queryConfig.setExtraLoadMessageData(cbLoadMessageData.isChecked());
		queryConfig.setExtraLoadAddressData(cbLoadAddressData.isChecked());
		queryConfig.setSelectLoadOnlyUnread(cbLoadOnlyUnread.isChecked());
		queryConfig.setThreadId(threadId);
		//
		//mCommUnifyMessageReader.setQueryConfig(mQueryConfig);
		mCommUnifyMessageReader.read(this, new CommUnifyMessageReader.OnReadTextMessageListener() {
			@Override
			public void onComplete(List<CommMsgData> list) {
				long timeDelay = checker.end();
				int length = (list==null)?0:list.size();
				printOutMessageList(list);
				loadMessageList(list);
				notifyLastResultInfo(timeDelay, length);
				isRefreshing.set(false);
				TToast.show(getBaseContext(), "Complete loading > size: "+length+" ea");
				SLog.d("*refresh - Complete loading! size:"+length+" ea");
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
				loadMessageList(null);
				notifyLastResultInfo(0, 0);
				isRefreshing.set(false);
				TToast.show(getBaseContext(), "err:"+e.getLocalizedMessage());
			}
		});
	}

	private void loadMessageList(final List<CommMsgData> messageItemList) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mHandyListAdapter.clear();
				List<HandyListAdapter.Param> list = new ArrayList<>();
				if (messageItemList!=null) {
					String myPhoneNumber = DeviceInfo.getPhoneNumber(MessageReaderTestActivity.this);
					for (CommMsgData info : messageItemList) {
						String strDate = DateUtil.getSimpleDate(info.getDate());
						HandyListAdapter.Param param = new HandyListAdapter.Param();
						param.msgHead = String.format("id(%s) tid(%s) addr(%s) date(%s) read(%s) type(%s)", info._id, info.thread_id, info.getAddress(myPhoneNumber), strDate, info.getReadStatus(), info.msgType);
						param.msgBody = String.format("%s", info.getBodyMessage());
						list.add(param);
					}
				}
				mHandyListAdapter.set(list);
				mHandyListAdapter.notifyDataSetChanged();
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
				String strType = String.valueOf(mCommUnifyMessageReader.getReadProtocolType().name());
				String strLimit = String.valueOf(mCommUnifyMessageReader.getQueryConfig().getLimitSize());
				String strPlusAddress = String.valueOf(mCommUnifyMessageReader.getQueryConfig().isExtraLoadAddressData());
				String strPlusMessage = String.valueOf(mCommUnifyMessageReader.getQueryConfig().isExtraLoadMessageData());
				String strOnlyUnread = String.valueOf(mCommUnifyMessageReader.getQueryConfig().isSelectLoadOnlyUnread());

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
			SLog.d(info.toString());
		}
	}

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btn_check: {
				TToast.show(getBaseContext(), String.format("전체 %s", mHandyListAdapter.getCount()));
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
