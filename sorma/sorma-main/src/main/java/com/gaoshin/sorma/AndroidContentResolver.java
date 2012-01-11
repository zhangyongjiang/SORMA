/**
 * Copyright (c) 2008-2011, Open & Green Inc.
 * All rights reserved.
 * info@gaoshin.com
 * 
 * This version of SORMA is licensed under the terms of the Open Source GPL 3.0 license. http://www.gnu.org/licenses/gpl.html. Alternate Licenses are available.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * AND NON-INFRINGEMENT OF THIRD-PARTY INTELLECTUAL PROPERTY RIGHTS.  
 * See the GNU General Public License for more details.
 * 
 */
package com.gaoshin.sorma;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class AndroidContentResolver implements SormaContentResolver {
    private ContentResolver contentResolver;

    public AndroidContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public Cursor query(String uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return contentResolver.query(Uri.parse(uri), projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public long insert(String uri, ContentValues values) {
        String string = contentResolver.insert(Uri.parse(uri), values).toString();
        int pos = string.lastIndexOf("/");
        return Long.parseLong(string.substring(pos+1));
    }

    @Override
    public int delete(String uri, String selection, String[] selectionArgs) {
        return contentResolver.delete(Uri.parse(uri), selection, selectionArgs);
    }

    @Override
    public int update(String uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return contentResolver.update(Uri.parse(uri), values, selection, selectionArgs);
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    @Override
    public void batchInsert(String uri, ContentValues[] values) throws Exception {
        List<Long> ids = new ArrayList<Long>();
        contentResolver.bulkInsert(Uri.parse(uri), values);
    }
}
