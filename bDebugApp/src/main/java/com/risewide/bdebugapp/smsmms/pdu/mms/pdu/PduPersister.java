/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.risewide.bdebugapp.smsmms.pdu.mms.pdu;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import android.provider.Telephony;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Mms.*;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;

import com.risewide.bdebugapp.smsmms.pdu.ContentType;
import com.risewide.bdebugapp.smsmms.pdu.EncodedStringValue;
import com.risewide.bdebugapp.smsmms.pdu.InvalidHeaderValueException;
import com.risewide.bdebugapp.smsmms.pdu.MmsException;
import com.risewide.bdebugapp.smsmms.pdu.PduHeaders;
import com.risewide.bdebugapp.smsmms.pdu.mms.util.PduCache;
import com.risewide.bdebugapp.smsmms.pdu.mms.util.PduCacheEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 2. 15.
 */
public class PduPersister {

}
