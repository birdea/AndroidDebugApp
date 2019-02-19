package com.risewide.bdebugapp.smsmms.pdu.mms.util;

import com.risewide.bdebugapp.smsmms.pdu.mms.pdu.GenericPdu;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 2. 15.
 */
public class PduCacheEntry {
    private final GenericPdu mPdu;
    private final int mMessageBox;
    private final long mThreadId;
    public PduCacheEntry(GenericPdu pdu, int msgBox, long threadId) {
        mPdu = pdu;
        mMessageBox = msgBox;
        mThreadId = threadId;
    }
    public GenericPdu getPdu() {
        return mPdu;
    }
    public int getMessageBox() {
        return mMessageBox;
    }
    public long getThreadId() {
        return mThreadId;
    }
}
