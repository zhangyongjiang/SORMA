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
package com.gaoshin.sorma.annotation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class SormaContentProvider extends ContentProvider {
    private static final String tag = "SORMA";

    private SormaEngine sormaEngine;
	
    public SormaContentProvider() {
	}
	
	@Override
	public boolean onCreate() {
		AnnotatedSchema schema = new AnnotatedSchema(this.getClass());
		SormaDatabase database = new SormaDatabase(getContext(), schema);
        sormaEngine = new SormaEngine(database);
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    return sormaEngine.delete(uri.toString(), selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		String table = getTableNameFromUri(uri.toString());
        String type = "vnd.android.cursor.dir/vnd." + sormaEngine.getSormaDatabase().getAnnotatedDatabase().getDatabaseName() + "." + table;
		return type;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        String table = getTableNameFromUri(uri.toString());
        long rowid = sormaEngine.insert(table, values);
        String contentUri = getContentUri(table, rowid);
        return Uri.parse(contentUri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return sormaEngine.query(uri.toString(), projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return sormaEngine.update(uri.toString(), values, selection, selectionArgs);
	}
	
    public static String getTableNameFromUri(String uri) {
        String path[] = uri.split("/");
        return path[3];
    }

    public static String getIdFromUri(String uri) {
        String path[] = uri.split("/");
        return path[4];
    }

    private String getContentUri(String tableName) {
        return "content://" + getContentProviderName() + "/" + tableName;
    }
    
    private String getContentUri(String tableName, long id) {
        return getContentUri(tableName) + "/" + id;
    }
    
	public String getContentProviderName() {
        return this.getClass().getName();
	}

	private String toString(String[] s) {
	    if(s == null )
	        return null;
	    StringBuilder sb = new StringBuilder();
	    sb.append('[');
	    for(String i : s) {
	        sb.append(i).append(",");
	    }
	    sb.append(']');
	    return sb.toString();
	}
	
}
