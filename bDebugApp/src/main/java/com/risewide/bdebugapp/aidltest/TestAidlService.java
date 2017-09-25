package com.risewide.bdebugapp.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by birdea on 2017-09-05.
 */

public class TestAidlService extends Service{
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/*@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return new IAidlTestInterfaceImpl();
	}

	private class IAidlTestInterfaceImpl extends IAidlTestInterface.Stub {

		@Override
		public void onSimpleData(String data) throws RemoteException {

		}

		@Override
		public void onComplexMapData(Map mapData) throws RemoteException {

		}

		@Override
		public String getSimpleData() throws RemoteException {
			return "it is simple string!";
		}

		@Override
		public Map<String, List<TextMessageInboxData>> getComplexMapData() throws RemoteException {
			Map<String, List<TextMessageInboxData>> map = new HashMap<>();
			map.put("1", getRandomItemList("one"));
			map.put("2", getRandomItemList("two"));
			map.put("3", getRandomItemList("three"));
			return map;
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm:ss");

	private List<TextMessageInboxData> getRandomItemList(String name) {
		List<TextMessageInboxData> list = new ArrayList<>();
		list.add(getRandomItem(name));
		list.add(getRandomItem(name));
		list.add(getRandomItem(name));
		list.add(getRandomItem(name));
		return list;
	}

	private TextMessageInboxData getRandomItem(String name) {
		int rInt = randInt(0, 1000);
		TextMessageInboxData item = new TextMessageInboxData();
		item.date = System.currentTimeMillis() + rInt;
		item.bodyMessage = sdf.format(new Date(item.date));
		item.name = name;
		item.phoneNumber = String.format("%03d-%04d-%04d",randInt(0,20),randInt(0,1000),randInt(0,1000));
		return item;
	}

	Random rand = new Random();

	private int randInt(int min, int max) {
		return rand.nextInt((max - min)+1)+min;
	}*/
}
