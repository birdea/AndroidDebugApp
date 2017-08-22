package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SVLog;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}


	/*
	CommMsgData{_id(188),m_id(y_r8JH8F1C8),thread_id(0),date(1471594654),700118_094634,address(null),read(1),type(0),body(null),snippet(null),snippet_cs(0),msg_box(1),text_only(1),mms_version(18),msg_type(132),subject((ê´ê³ )[ì íì¹´ë, ì´ë²¤í¸ ìë´]),(광고)[신한카드, 이벤트 안내],subject_charset(106),}
	 */
	@Test
	public void encode_test() throws Exception {

		//testAdjustEncodeCharacterSet("(ê´ê³ )[ì íì¹´ë, ì´ë²¤í¸ ìë´]");
		//testAdjustEncodeCharacterSet("[NHëíìí]ìì í");

		String raw = "핥";
		L("\n* Check encoder/decoder - "+raw);
		byte[] bytes = raw.getBytes();
		//L(raw+".getBytes()>"+byteArrayToHex(raw.getBytes()));
		//L(raw+".getBytes(utf8)>"+byteArrayToHex(raw.getBytes("utf8")));
		//L(raw+".getBytes(iso-8859-1)>"+byteArrayToHex(raw.getBytes("iso-8859-1")));
		//
		String encoded = new String(bytes, "iso-8859-1");
		L(raw+".new String(iso-8859-1)>"+byteArrayToHex(encoded.getBytes()));
		encoded = new String(bytes, "utf8");
		L(raw+".new String(utf8)>"+byteArrayToHex(encoded.getBytes()));
	}

	private static void L(String msg) {
		System.out.println(msg);
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

	private final String[] CHARSET_LIST = {
			//"ascii",
			"utf-8",
			"utf-16",
			"euc-kr",
			"ksc5601",
			"iso-8859-1",
			"latin1",
			//"iso-8859-2",
			//"iso-8859-3",
			//"iso-8859-15",
			//"x-windows-949",
	};

	public void testAdjustEncodeCharacterSet(String origin) {
		System.out.println("\n\n*decode-encode tester* with origin:"+origin);
		String decoder, encoder, output;
		for (int i=0;i<CHARSET_LIST.length;i++) {
			for(int j=0;j<CHARSET_LIST.length;j++) {
				decoder = CHARSET_LIST[i];
				encoder = CHARSET_LIST[j];
				output = null;
				try {
					output = new String(origin.getBytes(decoder), encoder);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				String msg =String.format("decoder:%s, encoder:%s : %s",decoder, encoder, output);
				//SVLog.i(String.format("decoder:%s, encoder:%s : %s",decoder, encoder, output));
				System.out.println(msg);
			}
		}
	}

	public void checkBytes(String text) {
		byte[] bytes = null;
		if (text!=null) {
			try {
				bytes = text.getBytes("iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String form = String.format("text:%s > bytes:%s", text, byteArrayToHex(bytes));
		SVLog.i("CheckBytes", form);
	}
}