package com.risewide.bdebugapp.receiver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 전화 수발신 동작의 기본 상태인 CallState의 상세 상태를 위한 enum Class.
 *
 * @author hyunho.mo
 *
 * @since 2018.08.21
 */
public enum CallSubState implements Parcelable {
    /** 전화 수신이 들어온 상태. */
    INCOMING_CALL_RECEIVED,     // CallState.RINGING
    /** 수신된 전화를 연결한 상태. */
    INCOMING_CALL_ANSWERED,     // CallState.OFFHOOK
    /** 수신된 전화의 통화가 종료된 상태. */
    INCOMING_CALL_ENDED,        // CallState.IDLE
    /** 수신된 전화를 받지 못한 상태(부재중 전화). */
    MISSED_CALL,                // CallState.IDLE

    /** 전화 발신를 시작한 상태. */
    OUTGOING_CALL_STARTED,      // CallState.OFFHOOK
    /** 발신한 전화가 종료된 상태. */
    OUTGOING_CALL_ENDED,        // CallState.IDLE

    /** 전화 통화중 상태에서 대기통화가 발생된 상태 */
    WAITING_CALL_RECEIVED,      // CallState.OFFHOOK

    /** Default */
    IDLE,                       // CallState.IDLE
    ;

    public static final Creator<CallSubState> CREATOR = new Creator<CallSubState>() {
        @Override
        public CallSubState createFromParcel(Parcel in) {
            return CallSubState.values()[in.readInt()];
        }

        @Override
        public CallSubState[] newArray(int size) {
            return new CallSubState[size];
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
