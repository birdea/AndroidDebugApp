package com.risewide.bdebugapp.external.naver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.risewide.bdebugapp.external.naver.config.NaverApiConfig;

/**
 * Created by birdea on 2017-02-14.
 */

public class NaverTTS {

	public static void getMp3File(final Context context, final String s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String clientId = NaverApiConfig.CLIENT_ID;
				String clientSecret = NaverApiConfig.CLIENT_SECRET;
				String speech = s;
				if (speech == null) {
					speech = "안녕하세요 만나서 반갑습니다.";
				}
				try {
					String text = URLEncoder.encode(speech, "UTF-8"); // 13자
					String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";
					URL url = new URL(apiURL);
					HttpURLConnection con = (HttpURLConnection)url.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("X-Naver-Client-Id", clientId);
					con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
					// post request
					String postParams = "speaker=mijin&speed=0&text=" + text;
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(postParams);
					wr.flush();
					wr.close();
					int responseCode = con.getResponseCode();
					BufferedReader br;
					if(responseCode==200) { // 정상 호출
						InputStream is = con.getInputStream();
						int read = 0;
						byte[] bytes = new byte[1024];
						// 랜덤한 이름으로 mp3 파일 생성
						String prefix = "nhntts_";
						String pathname = Environment.getExternalStorageDirectory().getAbsolutePath();
						String tempname = "trunk";//Long.valueOf(new Date().getTime()).toString();
						File f = new File(pathname+"/"+prefix+tempname + ".mp3");
						if(f.exists()) {
							f.delete();
						}
						//f.createNewFile();
						OutputStream outputStream = new FileOutputStream(f);
						while ((read =is.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);
						}
						is.close();
						//
						Uri uri = Uri.fromFile(f);
						MediaPlayer mediaPlayer = MediaPlayer.create(context, uri);
						mediaPlayer.start();
						//
					} else {  // 에러 발생
						br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
						while ((inputLine = br.readLine()) != null) {
							response.append(inputLine);
						}
						br.close();
						System.out.println(response.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
