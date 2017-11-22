package com.risewide.bdebugapp.receiver;

/**
 * @author hyunho.mo
 *
 * @since 2017.10.30
 */
public class WaitingCallInfo {
    private String mDispName = null;
    private String mFormattedPhoneNumber = null;
    // The date the call occured, in milliseconds.
    private long mCallDate = -1;


    /**
     * @param dispName
     * @param formattedPhoneNumber
     * @param callDate
     */
    public WaitingCallInfo(String dispName, String formattedPhoneNumber, long callDate) {
        mDispName = (dispName != null) ? dispName : "";
        mFormattedPhoneNumber = (formattedPhoneNumber != null) ? formattedPhoneNumber : "";
        mCallDate = (callDate >= 0) ? callDate : -1;
    }

    /**
     * @return
     */
    public String getDispName() {
        return mDispName;
    }

    /**
     * @return
     */
    public String getFormattedPhoneNumber() {
        return mFormattedPhoneNumber;
    }

    /**
     * @return
     */
    public long getCallDate() {
        return mCallDate;
    }
}
