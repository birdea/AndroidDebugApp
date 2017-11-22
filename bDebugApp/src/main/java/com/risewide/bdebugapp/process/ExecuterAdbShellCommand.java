package com.risewide.bdebugapp.process;

import java.io.IOException;

/**
 * Created by birdea on 2017-11-22.
 */

public class ExecuterAdbShellCommand {

	public static void exec(String command) {
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
