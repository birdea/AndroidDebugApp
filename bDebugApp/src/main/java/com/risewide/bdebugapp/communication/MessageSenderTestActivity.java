package com.risewide.bdebugapp.communication;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.communication.model.CommMsgSendType;
import com.risewide.bdebugapp.communication.model.MsgSendData;
import com.risewide.bdebugapp.communication.util.IntentActionHelper;
import com.risewide.bdebugapp.communication.util.OnHandyEventListener;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.communication.util.WidgetHelper;
import com.risewide.bdebugapp.util.DeviceInfo;
import com.risewide.bdebugapp.util.SVLog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by birdea on 2017-08-02.
 */

public class MessageSenderTestActivity extends BaseActivity{

	private CommUnifyMessageSender mCommUnifyMessageSender;
	private HandyListAdapter mHandyListAdapter;

	//- view component
	private TextView tvDeviceInfo, tvMsgSize, tvMsgTitle;
	private EditText etTextMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_message_send);
		initCont();
		initView();
		addEventMessage("onCreate.inited");
	}

	private void initView() {
		// find a settable textview
		tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
		tvMsgSize = (TextView) findViewById(R.id.tvMsgSize);
		tvMsgTitle = (TextView) findViewById(R.id.tvMsgTitle);
		//
		StringBuilder sb = new StringBuilder();
		String phoneNumber = String.format("phonenumber = %s", DeviceInfo.getPhoneNumber(this));
		String osInfo = String.format("%s", DeviceInfo.getDeviceAndroidOsInfo());
		String deviceName = String.format("%s", DeviceInfo.getDeviceName());
		String manufacturer = Build.MANUFACTURER;
		String netOperator = DeviceInfo.getNetworkOperatorName(this);
		SmsManager smsManager = SmsManager.getDefault();
		String mmsInfo;
		try {
			mmsInfo = String.format("_id:%d, %s", smsManager.getSubscriptionId(), smsManager.getCarrierConfigValues());
		} catch(Throwable e) {
			mmsInfo = "n/a";
		}
		sb.append(String.format("%s\n%s\n%s\n%s\n%s\n%s", phoneNumber, osInfo, deviceName, manufacturer, netOperator, mmsInfo));
		//
		tvDeviceInfo.setText(sb.toString());
		//
		EditText etSenderNumber = (EditText)findViewById(R.id.etSenderNumber);
		etSenderNumber.setText(DeviceInfo.getPhoneNumber(this));
		//
		RadioGroup rgProtocolType = (RadioGroup)findViewById(R.id.rgProtocolType);
		rgProtocolType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbProtocolTypeAuto:
						mCommUnifyMessageSender.setCommMsgSendType(CommMsgSendType.AUTO_ADJUST);
						break;
					case R.id.rbProtocolTypeSms:
						mCommUnifyMessageSender.setCommMsgSendType(CommMsgSendType.SMS);
						break;
					case R.id.rbProtocolTypeLms:
						mCommUnifyMessageSender.setCommMsgSendType(CommMsgSendType.LMS);
						break;
					case R.id.rbProtocolTypeMms:
						mCommUnifyMessageSender.setCommMsgSendType(CommMsgSendType.MMS);
						break;
				}
				addEventMessage("rgProtocolType.checked:"+checkedId+","+ mCommUnifyMessageSender.getCommMsgSendType());
			}
		});

		etTextMessage = (EditText)findViewById(R.id.etTextMessage);
		etTextMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				addEventMessage("TextWatcher.before-s:"+s+",start:"+start+",cnt:"+count+",after:"+after);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				addEventMessage("TextWatcher.onTextChanged-s:"+s+",start:"+start+",cnt:"+count+",before:"+before);

			}
			@Override
			public void afterTextChanged(Editable s) {
				addEventMessage("TextWatcher.afterTextChanged-s:"+s);
				int charLength = 0, bytes = 0;
				if (s != null) {
					charLength = s.toString().length();
					bytes = s.toString().getBytes().length;
				}
				tvMsgSize.setText(String.format("length:%d (%d bytes)", charLength, bytes));
				tvMsgTitle.setText(String.format("msg [%d set]", CommUnifyMessageSender.getCountOfDivideMessage(s.toString())));
			}
		});

		mHandyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.BODY_ONLY);
		ListView lvEvents = (ListView) findViewById(R.id.lvEvents);
		lvEvents.setAdapter(mHandyListAdapter);
	}

	private void addEventMessage(String event) {
		SVLog.i(event);
		if(mHandyListAdapter == null) {
			return;
		}
		mHandyListAdapter.addAndnotifyDataSetChanged(null, event);
	}

	private void initCont() {
		mCommUnifyMessageSender = new CommUnifyMessageSender();
		mCommUnifyMessageSender.setOnHandyEventListener(new OnHandyEventListener() {
			@Override
			public void onEvent(String msg) {
				addEventMessage(msg);
			}
		});

		if(!mCommUnifyMessageSender.hasPermission(this)){
			TToast.show(this, "need to get permissions..");
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	////////////////////////////////////////////////////////////////////////////////
	// views click event
	////////////////////////////////////////////////////////////////////////////////
	//
	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btnShowDeviceInfo:
				WidgetHelper.changeVisiblity(tvDeviceInfo);
				break;
			case R.id.btnSelectReceiverNumber:
				selectReceiver();
				break;
			case R.id.btnSelectMessage:
				selectSampleMessage();
				break;
			case R.id.btnSelectImageData:
				selectImage();
				break;
			case R.id.btnSendMessage:
				processSend();
				break;
		}
	}

	private IntentActionHelper mIntentActionHelper = new IntentActionHelper();
	private void selectReceiver() {
		mIntentActionHelper.selectReceiverPhoneNumber(this, new IntentActionHelper.OnActivityResultDispatcher() {
			@Override
			public void dispatcher(int resultCode, Intent data) {
				if (resultCode == RESULT_OK) {
					Cursor cursor = null;
					try {
						cursor = getContentResolver().query(data.getData(),
								new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
										ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
						if (cursor.moveToFirst()) {
							MsgSendData messageData = mCommUnifyMessageSender.getMsgSendData();
							messageData.setNameReceiver(cursor.getString(0));
							messageData.setPhoneNumberReceiver(cursor.getString(1));
						}
					} catch (Exception ignore){
					} finally {
						cursor.close();
					}
					EditText etReceiverName = (EditText)findViewById(R.id.etReceiverName);
					EditText etReceiverNumber = (EditText)findViewById(R.id.etReceiverNumber);
					etReceiverName.setText(String.valueOf(mCommUnifyMessageSender.getMsgSendData().nameReceiver));
					etReceiverNumber.setText(String.valueOf(mCommUnifyMessageSender.getMsgSendData().phoneNumberReceiver));
					addEventMessage("selectReceiver-resultCode:"+resultCode+" > see (name, number)");
				} else {
					addEventMessage("selectReceiver-resultCode:"+resultCode+" > not selected");
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mIntentActionHelper.onActivityResultDispatcher(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private CharSequence[] sampleMsg = {
			"",
			"멜론 TOP 100을 시작합니다.",
			"현재 서울 하늘은 맑고, 오후늦게 구름이 조금 끼겠습니다. 현재 기온은 영상 32도 이고, 최고 기온은 영상 33도, 최저 기온은 영상 25도로 예상됩니다.",
			"dkssadlkfaslkdjfaslkjfaslk jfalksdhf;alskdhf alshkf ;aslkhfa;slkhfal ;skhfa;sldkhf ;aslkhfas;lkhf a;slkjfhaslhf aslkhfa;slkhf; alkhfa;slkhf a;slkhfa;asldkfhal;skhdfjal;skhdf;alkshf;lakhsf;lkhasdfl;khasd;lfkhjas;lkfha;l"
	};

	private void selectSampleMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("pick a message")
				.setItems(sampleMsg, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EditText etTextMessage = (EditText)findViewById(R.id.etTextMessage);
						etTextMessage.setText(sampleMsg[which]);
					}
				});
		builder.create().show();
	}

	private void selectImage() {
		mIntentActionHelper.selectGaleryImage(this, new IntentActionHelper.OnActivityResultDispatcher() {
			@Override
			public void dispatcher(int resultCode, Intent data) {
				if (resultCode == RESULT_OK) {
					if (data != null) {
						Uri imageUri = data.getData();
						if (imageUri != null) {
							EditText etImageDataInfo = (EditText)findViewById(R.id.etImageDataInfo);
							etImageDataInfo.setText(imageUri.getPath());
							MsgSendData messageData = mCommUnifyMessageSender.getMsgSendData();
							messageData.setImageUri(imageUri);
						}
					}
					addEventMessage("selectImage-resultCode:"+resultCode+" > selected? data:"+data);
				} else {
					addEventMessage("selectImage-resultCode:"+resultCode+" > not selected");
				}
			}
		});
	}
	//

	private void processSend() {
		EditText etSenderNumber = (EditText)findViewById(R.id.etSenderNumber);
		EditText etReceiverNumber = (EditText)findViewById(R.id.etReceiverNumber);
		EditText etTextMessage = (EditText)findViewById(R.id.etTextMessage);
		//EditText etImageDataInfo = (EditText)findViewById(R._id.etImageDataInfo);
		//
		String numberSender, numberReceiver, textMessage;
		//Uri image;
		//
		numberSender = WidgetHelper.getTextString(etSenderNumber);
		numberReceiver = WidgetHelper.getTextString(etReceiverNumber);
		textMessage = WidgetHelper.getTextString(etTextMessage);
		//
		MsgSendData messageData = mCommUnifyMessageSender.getMsgSendData();
		messageData.setPhoneNumberSender(numberSender);
		messageData.setPhoneNumberReceiver(numberReceiver);
		messageData.setTextMessage(textMessage);
		//
		mCommUnifyMessageSender.send(this, new AbsMessageSender.OnSendTextMessageListener() {
			@Override
			public void onSent(boolean success) {
				SVLog.i("mCommUnifyMessageSender.onSent:"+success);
			}
			@Override
			public void onReceived(boolean success) {
				SVLog.i("mCommUnifyMessageSender.onReceived:"+success);
			}
		});
	}
}
