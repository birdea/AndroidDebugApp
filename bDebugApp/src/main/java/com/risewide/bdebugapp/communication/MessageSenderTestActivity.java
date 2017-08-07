package com.risewide.bdebugapp.communication;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.communication.helper.IntentActionHelper;
import com.risewide.bdebugapp.communication.helper.TToast;
import com.risewide.bdebugapp.communication.helper.WidgetHelper;
import com.risewide.bdebugapp.util.DeviceInfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by birdea on 2017-08-02.
 */

public class MessageSenderTestActivity extends BaseActivity{

	CommMessageSender commMessageSender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_test);
		initCont();
		initView();
	}

	private void initView() {
		TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
		//
		StringBuilder sb = new StringBuilder();
		String phoneNumber = String.format("phonenumber = %s", DeviceInfo.getPhoneNumber(this));
		String osInfo = String.format("%s", DeviceInfo.getDeviceAndroidOsInfo());
		String deviceName = String.format("%s", DeviceInfo.getDeviceName());
		String netOperator = DeviceInfo.getNetworkOperatorName(this);
		SmsManager smsManager = SmsManager.getDefault();
		String mmsInfo = String.format("id:%d, %s", smsManager.getSubscriptionId(), smsManager.getCarrierConfigValues());
		sb.append(String.format("%s\n%s\n%s\n%s\n%s", phoneNumber, osInfo, deviceName, netOperator, mmsInfo));
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
						commMessageSender.setProtocolType(CommMessageSender.ProtocolType.AUTO);
						break;
					case R.id.rbProtocolTypeSms:
						commMessageSender.setProtocolType(CommMessageSender.ProtocolType.SMS);
						break;
					case R.id.rbProtocolTypeLms:
						commMessageSender.setProtocolType(CommMessageSender.ProtocolType.LMS);
						break;
					case R.id.rbProtocolTypeMms:
						commMessageSender.setProtocolType(CommMessageSender.ProtocolType.MMS);
						break;
				}
			}
		});
		RadioGroup rgMethodType = (RadioGroup)findViewById(R.id.rgMethodType);
		rgMethodType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.rbMethodTypeDirectCall:
						commMessageSender.setCallMethodType(CommMessageSender.CallMethodType.DirectCall);
						break;
					case R.id.rbMethodTypeUseIntent:
						commMessageSender.setCallMethodType(CommMessageSender.CallMethodType.Intent);
						break;
				}
			}
		});
	}


	private void initCont() {
		commMessageSender = new CommMessageSender();

		if(!commMessageSender.hasPermission(this)){
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

	IntentActionHelper intentActionHelper = new IntentActionHelper();

	private void selectReceiver() {
		intentActionHelper.selectReceiverPhoneNumber(this, new IntentActionHelper.OnActivityResultDispatcher() {
			@Override
			public void dispatcher(int resultCode, Intent data) {
				if(resultCode == RESULT_OK) {
					Cursor cursor = null;
					try {
						cursor = getContentResolver().query(data.getData(),
								new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
										ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
						if (cursor.moveToFirst()) {
							commMessageSender.setNameReceiver(cursor.getString(0));
							commMessageSender.setPhoneNumberReceiver(cursor.getString(1));
						}
					} catch (Exception ignore){
					} finally {
						cursor.close();
					}
					EditText etReceiverName = (EditText)findViewById(R.id.etReceiverName);
					EditText etReceiverNumber = (EditText)findViewById(R.id.etReceiverNumber);
					etReceiverName.setText(String.valueOf(commMessageSender.getMessageData().nameReceiver));
					etReceiverNumber.setText(String.valueOf(commMessageSender.getMessageData().phoneNumberReceiver));
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		intentActionHelper.onActivityResultDispatcher(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private CharSequence[] sampleMsg = {
			"",
			"멜론 TOP 100을 시작합니다.",
			"TWICE의 CHEER UP 들려드릴께요.",
			"휴대폰 찾기를 시작합니다. 소리와 진동에 귀기울여보세요~",
			"현재 서울 하늘은 맑고, 오후늦게 구름이 조금 끼겠습니다. 현재 기온은 영상 32도 이고, 최고 기온은 영상 33도, 최저 기온은 영상 25도로 예상됩니다.",
			"오늘 폭염 주의보가 내려졌어요. 오늘도 엄청 덥겠네요. 더위로 체내 수분이 부족해질 수 있으니 물을 자주 마시는 것이 좋을 것 같아요.",
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
		intentActionHelper.selectGaleryImage(this, new IntentActionHelper.OnActivityResultDispatcher() {
			@Override
			public void dispatcher(int resultCode, Intent data) {
				if (data != null) {
					Uri imageUri = data.getData();
					if (imageUri != null) {
						EditText etImageDataInfo = (EditText)findViewById(R.id.etImageDataInfo);
						etImageDataInfo.setText(imageUri.getPath());
						commMessageSender.setImageUri(imageUri);
					}
				}
			}
		});
	}
	//

	private void processSend() {
		EditText etSenderNumber = (EditText)findViewById(R.id.etSenderNumber);
		EditText etReceiverNumber = (EditText)findViewById(R.id.etReceiverNumber);
		EditText etTextMessage = (EditText)findViewById(R.id.etTextMessage);
		//EditText etImageDataInfo = (EditText)findViewById(R.id.etImageDataInfo);
		//
		String numberSender, numberReceiver, textMessage;
		//Uri image;
		//
		numberSender = WidgetHelper.getText(etSenderNumber);
		numberReceiver = WidgetHelper.getText(etReceiverNumber);
		textMessage = WidgetHelper.getText(etTextMessage);
		//
		commMessageSender.setPhoneNumberSender(numberSender);
		commMessageSender.setPhoneNumberReceiver(numberReceiver);
		commMessageSender.setTextMessage(textMessage);
		//
		commMessageSender.send(this);
	}
}
