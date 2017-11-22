package com.risewide.bdebugapp.util;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

/**
 * @author hyunho.mo
 *
 * RequiresPermission {@link Manifest.permission#READ_CONTACTS}
 *
 * @since 2017.06.13
 */
public class ContactInfoLoader {
    private static final String TAG = ContactInfoLoader.class.getSimpleName();

    /**
     * @param phoneNumber
     * @param context
     * @return
     */
    public static String loadDisplayNameFromPhoneLookupDatabase(Context context, String phoneNumber) {
        // define the columns I want the query to return.
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.HAS_PHONE_NUMBER};

        // encode the phone phoneNumber and build the filter URI
        Uri contactUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        // querying all contacts
        //Uri contactUri =ContactsContract.Contacts.CONTENT_URI;

        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
        if (cursor == null) {
            SLog.e(TAG, "loadDisplayNameFromPhoneLookupDatabase() : cursor is null.");
            return null;
        }

        String contactName = null;
        if (cursor.moveToFirst()) {
            int dispNameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            contactName = cursor.getString(dispNameIndex);
        }
        cursor.close();

        return !TextUtils.isEmpty(contactName) ? contactName : null;
    }
}
