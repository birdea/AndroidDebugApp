package com.risewide.bdebugapp.aidltest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by birdea on 2017-09-05.
 */

public class TestAidlConnector {

	/*private IAidlTestInterface iAidlTestInterface;

	public void connect(Context context) {
		Intent serviceIntent = new Intent(context, TestAidlService.class);
		context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void disconnect(Context context) {
		context.unbindService(serviceConnection);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iAidlTestInterface = IAidlTestInterface.Stub.asInterface(service);
			try {
				iAidlTestInterface.asBinder().linkToDeath(new IBinder.DeathRecipient() {
					@Override
					public void binderDied() {
					}
				}, 0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	public IAidlTestInterface getIAidlTestInterface() {
		return iAidlTestInterface;
	}*/
}
