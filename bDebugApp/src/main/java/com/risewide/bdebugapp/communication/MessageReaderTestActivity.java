package com.risewide.bdebugapp.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.DelayChecker;
import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.util.DateUtil;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.communication.model.SmsMmsMsgReadType;
import com.risewide.bdebugapp.communication.util.WidgetHelper;
import com.risewide.bdebugapp.util.DeviceInfo;
import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-05-12.
 */

public class MessageReaderTestActivity extends BaseActivity {

	private HandyListAdapter handyListAdapter;
	private SmsUnifyMessageReader smsUnifyMessageReader = new SmsUnifyMessageReader();
	//
	private List<MmsSmsMsg> srcList = new ArrayList<>();

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
						smsUnifyMessageReader.setSmsProtocolReadType(SmsMmsMsgReadType.ALL_SEQUENTIAL);
						break;
					case R.id.rbProtocolTypeMmsSms:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsMmsMsgReadType.MMS_SMS_CONVERSATION);
						break;
					case R.id.rbProtocolTypeSms:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsMmsMsgReadType.SMS);
						break;
					case R.id.rbProtocolTypeMms:
						smsUnifyMessageReader.setSmsProtocolReadType(SmsMmsMsgReadType.MMS);
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
						QueryConfig config = smsUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.DESC);
						//smsUnifyMessageReader.setQueryConfig(config);
						break;
					}
					case R.id.rbQuerySortAsc: {
						QueryConfig config = smsUnifyMessageReader.getQueryConfig();
						config.setSortOrder(QueryConfig.Order.ASC);
						//smsUnifyMessageReader.setQueryConfig(config);
						break;
					}
				}
			}
		});
		rgQuerySelectOrder.check(R.id.rbQuerySortDesc);

		handyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.HEAD_BODY);

		ListView lv_textmessage = (ListView) findViewById(R.id.lv_textmessage);
		lv_textmessage.setAdapter(handyListAdapter);
	}

	private void initCont() {
		if(smsUnifyMessageReader.hasPermission(this)==false){
			finish();
			return;
		}
		refresh();
	}

	private AtomicBoolean isProcessing = new AtomicBoolean(false);
	private void refresh() {
		if (isProcessing.get()) {
			TToast.show(this, "already started.. wait for a while to done task");
			return;
		}
		TToast.show(this, "start to refrese.. wait for a sec");
		isProcessing.set(true);
		final DelayChecker checker = new DelayChecker();
		checker.start("smsUnifyMessageReader");
		//
		EditText etQuerySortOrderColumn = (EditText)findViewById(R.id.etQuerySortOrderColumn);
		EditText etQuerySelectLimitSize = (EditText)findViewById(R.id.etQuerySelectLimitSize);
		CheckBox cbLoadMessageData = (CheckBox)findViewById(R.id.cbLoadMessageData);
		CheckBox cbLoadAddressData = (CheckBox)findViewById(R.id.cbLoadAddressData);
		CheckBox cbLoadOnlyUnread = (CheckBox)findViewById(R.id.cbLoadOnlyUnread);

		String columnName = WidgetHelper.getTextString(etQuerySortOrderColumn);
		int limitSize = WidgetHelper.getTextInteger(etQuerySelectLimitSize);

		QueryConfig queryConfig = smsUnifyMessageReader.getQueryConfig();
		queryConfig.setLimitSize(limitSize);
		queryConfig.setSortOrderColumn(columnName);
		queryConfig.setExtraLoadMessageData(cbLoadMessageData.isChecked());
		queryConfig.setExtraLoadAddressData(cbLoadAddressData.isChecked());
		queryConfig.setSelectLoadOnlyUnread(cbLoadOnlyUnread.isChecked());
		//
		//smsUnifyMessageReader.setQueryConfig(queryConfig);
		smsUnifyMessageReader.read(this, new SmsUnifyMessageReader.OnReadTextMessageListener() {
			@Override
			public void onComplete(List<MmsSmsMsg> list) {
//				if (list == null || list.isEmpty()) {
//					TToast.show(getBaseContext(), "load complete, size:0");
//					return;
//				}
				long timeDelay = checker.end();
				List<MmsSmsMsg> dstList = storeMessageList(list);
				//checker.end();
				//printOutMessageList(dstList);
				loadMessageList(dstList);
				//checker.end();
				isProcessing.set(false);
				TToast.show(getBaseContext(), "load complete, size:"+dstList.size());
				SVLog.d("load complete, size:"+dstList.size());
				checker.showToast(getBaseContext());
				notifyLastResultInfo(timeDelay, dstList.size());
			}
		});
	}

	private List<MmsSmsMsg> storeMessageList(List<MmsSmsMsg> list) {
		srcList.clear();
		srcList.addAll(list);
		return srcList;
	}

	private void loadMessageList(final List<MmsSmsMsg> messageItemList) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handyListAdapter.clear();
				List<HandyListAdapter.Param> list = new ArrayList<>();
				String myPhoneNumber = DeviceInfo.getPhoneNumber(MessageReaderTestActivity.this);
				for (MmsSmsMsg info : messageItemList) {
					String strDate = DateUtil.getSimpleDate(info.getDate());
					HandyListAdapter.Param param = new HandyListAdapter.Param();
					param.msgHead = String.format("address(%s) date(%s) read(%s)", info.getAddress(myPhoneNumber), strDate, info.getReadStatus());
					param.msgBody = String.format("%s", info.body);
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
							  String strType = String.valueOf(smsUnifyMessageReader.getSmsProtocolReadType().name());
							  String strLimit = String.valueOf(smsUnifyMessageReader.getQueryConfig().getLimitSize());
							  String strPlusAddress = String.valueOf(smsUnifyMessageReader.getQueryConfig().isExtraLoadAddressData());
							  String strPlusMessage = String.valueOf(smsUnifyMessageReader.getQueryConfig().isExtraLoadMessageData());
							  String strOnlyUnread = String.valueOf(smsUnifyMessageReader.getQueryConfig().isSelectLoadOnlyUnread());

							  String result = String.format("Delayed(ms)[%s]\non\nSize[%s], Type[%s], Limit[%s], +Address[%s], +Message[%s], +Unread[%s]"
									  , strDelay, strSize, strType, strLimit, strPlusAddress, strPlusMessage, strOnlyUnread
							  );
							  tvLastResult.setText(result);
						  }
					  });
	}

	private void printOutMessageList(List<MmsSmsMsg> messageItemList) {
		if (messageItemList==null) {
			return;
		}
		for (MmsSmsMsg info : messageItemList) {
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
