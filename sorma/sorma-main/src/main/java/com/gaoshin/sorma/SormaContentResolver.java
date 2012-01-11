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

import android.content.ContentValues;
import android.database.Cursor;

public interface SormaContentResolver {
    Cursor query(String uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder);
    
    long insert(String uri, ContentValues values);
    
    int delete(String uri, String selection, String[] selectionArgs);
    
    int update(String uri, ContentValues values, String selection,
            String[] selectionArgs);
    
    void batchInsert(String uri, ContentValues[] values) throws Exception;
}
