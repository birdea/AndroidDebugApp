package com.risewide.bdebugapp.external;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Environment;

import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-02-14.
 */

public class SttQualityTestRecorder {

	private String filePath;
	//private FileOutputStream fos;
	private BufferedWriter br;
	private AtomicBoolean activated = new AtomicBoolean();
	private int count;

	public SttQualityTestRecorder(String service) {
		String prefix = "stt_qual_";
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		this.filePath = path + "/" + prefix + service +".csv";
		this.activated.set(false);
	}

	public void init() {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
			br = new BufferedWriter(fw);
			//
			count = 0;
			SVLog.i("init!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void release() {
		try {
			if(br != null) {
				br.close();
				br = null;
				SVLog.i("release!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isActivate() {
		return activated.get();
	}

	public void setActivate(boolean activate) {
		activated.set(activate);
	}

	public void write(String lineMessage) {

		if(false == activated.get()) {
			return;
		}

		try {
			if(br != null) {
				count++;
				String text = String.format("[%d] %s",count,lineMessage);
				String encoded = new String(text.getBytes(), "UTF-8");
				br.write(encoded);
				br.newLine();
				br.flush();
				SVLog.i("write:"+encoded);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}