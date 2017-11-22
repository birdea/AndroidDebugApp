package com.risewide.bdebugapp.receiver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 전화 수발신 동작의 기본 상태를 위한 enum Class.
 *
 * @author hyunho.mo
 *
 * @since 2018.08.21
 */
public enum CallState implements Parcelable {
    /** 기본 상태. (전화 통화 종료 및 부재중 발생후) */
    IDLE,
    /** 전화 수신중 상태 */
    RINGING,
    /** 전화 통화중 상태. (전화 발신이나 수신으로 전화가 연결된 상태) */
    OFFHOOK,
    ;

    public static final Creator<CallState> CREATOR = new Creator<CallState>() {
        @Override
        public CallState createFromParcel(Parcel in) {
            return CallState.values()[in.readInt()];
        }

        @Override
        public CallState[] newArray(int size) {
            return new CallState[size];
        }
    };

    /**
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
}
