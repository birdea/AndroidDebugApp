package com.risewide.bdebugapp.util;

import android.util.ArrayMap;

/**
 * Created by doosik_kim on 2017. 10. 25..
 */

public class ClientApplicationStatus {
    private final static String TAG = "ClientApplicationStatus";
    private ClientApplicationStatus() {}
    private static class ClientApplicationStatusHolder {
        public static ClientApplicationStatus mInstance = new ClientApplicationStatus();
    }
    public static ClientApplicationStatus getInstance() {
        return ClientApplicationStatusHolder.mInstance;
    }

    private String mClientApplicationStatus;
    private ArrayMap<String, ActivityStatusData> mClientActivityStatus = new ArrayMap<>();
    private Object applicationLock = new Object();
    private Object activityLock = new Object();

    /**
     * ClientApplication의 Application lifecycle 상태 저장
     * @param lifeCycle lifeCycle 상태
     */
    public void setApplicationStatus(String lifeCycle) {
        synchronized (applicationLock) {
            SLog.d(TAG, String.format("setApplicationStatus = " + mClientApplicationStatus));
            mClientApplicationStatus = lifeCycle;
        }
    }

    /**
     * ClientApplication의 Application lifeCycle 상태 반환
     * @return lifeCycle 상태
     */
    public String getApplicationStatus() {
        synchronized (applicationLock) {
			SLog.d(TAG, String.format("getApplicationStatus = " + mClientApplicationStatus));
            return mClientApplicationStatus;
        }
    }

    /**
     * ClientApplication의 Activity lifeCycle 상태 저장
     * @param className Activity 이름
     * @param lifeCycle lifeCycle 상태
     */
    public void putActivityStatus(String className, String lifeCycle) {
        synchronized (activityLock) {
			SLog.d(TAG, String.format("putActivityStatus className = %s, lifeCycle = %s", className, lifeCycle));
            mClientActivityStatus.put(className, new ActivityStatusData(lifeCycle, System.currentTimeMillis()));
        }
    }

    /**
     * ClientApplication의 Activity lifeCycle 상태 반환
     * @param className Activity 이름
     * @return lifeCycle 상태
     */
    public ActivityStatusData getActivityStatus(String className) {
        synchronized (activityLock) {
            ActivityStatusData activityStatusData = mClientActivityStatus.get(className);
			SLog.d(TAG, String.format("getActivityStatus className = %s, lifecycle = %s", className, activityStatusData != null? activityStatusData.lifeCycle : null));
            return mClientActivityStatus.get(className);
        }
    }

    public class ActivityStatusData {
        public ActivityStatusData(String lifeCycle, long changedTime) {
            this.lifeCycle = lifeCycle;
            this.changedTime = changedTime;
        }
        public String lifeCycle;
        public long changedTime;
    }

}
