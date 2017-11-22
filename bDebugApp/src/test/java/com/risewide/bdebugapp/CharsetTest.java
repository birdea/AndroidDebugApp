package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SLog;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by birdea on 2017-10-23.
 */

public class CharsetTest {

	private static final String TAG = CharsetTest.class.getSimpleName();

	@Test
	public void test() {
		log("-- start charset test --");
		String text = "안녕하세요 전화를 받기 위해서는 전화 수신이라고 말씀해주세요.";

		byte[] dataDef, dataUtf8, dataMs949;

		try {
			dataDef = text.getBytes();
			dataUtf8 = text.getBytes("UTF8");
			dataMs949 = text.getBytes("MS949");

			log("str(def):"+new String(dataDef)+" >>> "+byteArrayToHex(dataDef));
			log("str(utf8):"+new String(dataUtf8)+" >>> "+byteArrayToHex(dataUtf8));
			log("str(ms949):"+new String(dataMs949, "ms949")+" >>> "+byteArrayToHex(dataMs949));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		log("-- end charset test --");
	}


	private void log(String msg) {
		SLog.d(TAG, msg);
	}

	private String byteArrayToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		if (bytes != null) {
			for (final byte b: bytes) {
				sb.append(String.format("%02x ", b & 0xff));
				//sb.append(String.format("%s ", Integer.toBinaryString(b & 0xff)));
			}
		}
		return sb.toString();
	}

}
