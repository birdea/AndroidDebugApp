package com.risewide.bdebugapp.communication.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Common usage for closeable interface
 * Created by birdea on 2017-02-09.
 */

public class IOCloser {

	public static void close(Closeable io) {
		if (io == null) {
			return;
		}
		try {
			io.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(HttpURLConnection conn) {
		if (conn == null) {
			return;
		}
		conn.disconnect();
	}
}
