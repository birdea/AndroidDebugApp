package com.risewide.bdebugapp.util;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;

/**
 * @author hyunho.mo
 *
 * RequiresPermission {@link Manifest.permission#READ_CONTACTS}
 *
 * @since 2017.06.13
 */
public class ContactInfoManager {
    private static final String TAG = ContactInfoManager.class.getSimpleName();

    private static ContactInfoManager sInstance = null;
    private static final String WORKER_THREAD_NAME = "load_contactInfo_thread";
	private Context mContext;


    /**
     * @param context
     * @return
     */
    public static ContactInfoManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ContactInfoManager(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * @param context
     */
    private ContactInfoManager(Context context) {
        mContext = context;
    }

    /**
     * @param phoneNumager
     * @return
     */
    public String getContactNameWithPhoneNumber(String phoneNumager) {

        if (!PermissionHelper.hasPermission(mContext, Manifest.permission.READ_CONTACTS)) {
            return null;
        }
        if (TextUtils.isEmpty(phoneNumager)) {
            return null;
        }
        return ContactInfoLoader.loadDisplayNameFromPhoneLookupDatabase(mContext, phoneNumager);
    }
}
