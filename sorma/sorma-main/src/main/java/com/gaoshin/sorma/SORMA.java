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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gaoshin.sorma.annotation.AnnotatedSchema;
import com.gaoshin.sorma.annotation.SormaContentProvider;
import com.gaoshin.sorma.annotation.SormaEngine;
import com.gaoshin.sorma.core.DbColumn;
import com.gaoshin.sorma.core.DbTable;

/**
 * SORMA class is used to access the SQLite content provider created by SORMAContentProvider.
 * 
 * To create a SORMA content provider,
 * <li>Create class, for example</li>
 * <blockquote><pre>
 * public class Contact {
 *     private String firstName;
 *     private String lastName;
 *     ...
 * }
 * </pre></blockquote>
 * @author kzhang
 *
 */
public class SORMA extends SormaBase {
    private static Map<Class<? extends SormaContentProvider>, AnnotatedSchema> instances = new HashMap<Class<? extends SormaContentProvider>, AnnotatedSchema>();

    public static synchronized SORMA getInstance(Context context) {
        String packageName = context.getApplicationInfo().packageName;
        String clsName = packageName + ".ContentProvider";
        try {
            Class forName = Class.forName(clsName);
            return getInstance(context, forName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized SORMA getInstance(Context context, Class<? extends SormaContentProvider> sormaContentProviderClass) {
        AnnotatedSchema annotatedSchema = instances.get(sormaContentProviderClass);
        if(annotatedSchema == null) {
            annotatedSchema = new AnnotatedSchema(sormaContentProviderClass);
            instances.put(sormaContentProviderClass, annotatedSchema);
        }
        return new SORMA(context, annotatedSchema);
    }
    
    private String contentProviderName;
	
    private SORMA(Context context, AnnotatedSchema schema) {
    	super(schema);
        contentProviderName = schema.getContentProviderClass().getName();
    	setContentResolver(new AndroidContentResolver(context.getContentResolver()));
	}

    public String getContentProviderName() {
        return contentProviderName;
    }

    public String getTableContentUri(Class tableClass) {
        DbTable table = schema.getTable(tableClass);
        String tableName = table.getTableName();
        if(tableName.startsWith("content://")) {
            return tableName;
        }
        else {
            return getContentBaseUri() + "/" + tableName;
        }
    }

    public String getContentUriForNativeQuery() {
        return getContentBaseUri() + "/" + SormaEngine.NATIVE_QUERY;
    }

    public String getContentBaseUri() {
        return "content://" + contentProviderName;
    }

    public String getObjectContentUri(Class tableClass, Long id) {
        String tableUri = getTableContentUri(tableClass);
        return tableUri + "/" + id;
    }

    public <T> T nativeQuery(Class<T> clazz, String query, String[] whereValues) {
        List<T> list = super.select(getContentUriForNativeQuery(), clazz, query, whereValues, null, Integer.MAX_VALUE);
        if(list.isEmpty())
            return null;
        return list.get(0);
    }

    public <T> ResultSet<T> nativeQueryList(Class<T> clazz, String query, String[] whereValues) {
        return super.select(getContentUriForNativeQuery(), clazz, query, whereValues, null, Integer.MAX_VALUE);
    }

    public <T> ResultSet<T> nativeQueryList(Class<T> clazz, String query, String[] whereValues, int size) {
        return super.select(getContentUriForNativeQuery(), clazz, query, whereValues, null, size);
    }

    public String getObjectContentUri(Object obj) {
        String tableUri = getTableContentUri(obj.getClass());
        DbTable table = schema.getTable(obj.getClass());
        DbColumn column = table.getKeyColumn();
        String id = column.getStringValueFromObject(obj);
        return tableUri +"/" + id;
    }

    public <T> ResultSet<T> select(Class<T> clazz, String where, String[] whereValues, String order) {
        String uri = getTableContentUri(clazz);
        return super.select(uri, clazz, where, whereValues, order);
    }

    public Cursor selectCursor(Class clazz, String where, String[] whereValues, String order) {
        String uri = getTableContentUri(clazz);
        return super.selectCursor(uri, where, whereValues, order);
    }

    public <T> ResultSet<T> select(Class<T> clazz, String where, String[] whereValues, String order, int size) {
        String uri = getTableContentUri(clazz);
        return super.select(uri, clazz, where, whereValues, order, size);
    }

    public <T> ResultSet<T> select(Class<T> clazz, String where, String[] whereValues, String order, int offset, int size) {
        String uri = getTableContentUri(clazz);
        return super.select(uri, clazz, where, whereValues, order, offset, size);
    }

    public <T> List<T> select(Class<T>clazz, String where, String[] whereValues) {
        String uri = getTableContentUri(clazz);
        return super.select(uri, clazz, where, whereValues);
    }
    
    public int count(Class clazz, String where, String[] whereValues) {
        String uri = getTableContentUri(clazz);
        return super.count(uri, where, whereValues);
    }
    
    public int count(Class clazz, String where, List list) {
        String[] whereValues = new String[list.size()];
        for(int i=0; i<list.size(); i++) {
            whereValues[i] = (String) list.get(i);
        }
        
        String uri = getTableContentUri(clazz);
        return super.count(uri, where, whereValues);
    }
    
	public <T> T get(Class<T>clazz, String where, String[] whereValues) {
		List<T> list = select(clazz, where, whereValues, null, 2);
		if(list.size() > 1) 
			throw new RuntimeException("more than one record found for " + clazz + "for where " + where + " values " + toString(whereValues));
		if(list.size() == 0)
			return null;
		else
			return list.get(0);
	}
	
	public long insert(Object obj) {
        DbTable table = schema.getTable(obj.getClass());
        String uri = getTableContentUri(obj.getClass());
        return super.insert(uri, obj);
	}
	
	public void insert(List<?> objs) {
	    if(objs.isEmpty()) {
	        return;
	    }
        ContentValues values = new ContentValues();
        String uri = getTableContentUri(objs.get(0).getClass());
        DbTable table = schema.getTable(objs.get(0).getClass());
	    values.put("bulkInsert", true);
	    StringBuilder sb = new StringBuilder();
	    for(Object col : table.getColumns()) {
	        sb.append(((DbColumn)col).getColumnName()).append(" ");
	    }
	    values.put("columns", sb.substring(0, sb.length()-1));
	    
        for(int i=0; i<objs.size(); i++) {
            Object obj = objs.get(i);
	        table.putToDbContentValues(i+"-", values, obj);
	    }

	    contentResolver.insert(uri, values);
        for(int i=0; i<objs.size(); i++) {
            Object obj = objs.get(i);
            Long autoId = values.getAsLong("result-" + i);
            DbColumn keyColumn = table.getKeyColumn();
            if(keyColumn != null && keyColumn.isAutoId()) {
                try {
                    keyColumn.setValueToObject(obj, String.valueOf(autoId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
    public int update(Object obj, String selection,
            String[] selectionArgs) {
        String uri = getTableContentUri(obj.getClass());
        return super.update(uri, obj, selection, selectionArgs);
    }
    
    public void nativeUpdate(String sql) {
        String uri = getContentUriForNativeQuery();
        super.update(uri, null, sql, null);
    }
    
	public int update(Object obj, String...updateCols) throws Exception{
        String uri = getTableContentUri(obj.getClass());
        DbTable table = schema.getTable(obj.getClass());
        ContentValues cv = new ContentValues();
        DbColumn idCol = table.getKeyColumn();
        String selection = idCol.getColumnName()+" = ?";
        String[] selectionArgs = new String[]{""+idCol.getField().get(obj)};
        
        for(String s : updateCols){
        	DbColumn col = table.getColumnByColumnName(s);
        	col.putToDbContentValues(cv, obj);
        }
        return super.update(uri, cv,selection, selectionArgs);
	}
	
    public int update(Class clazz, ContentValues values, String selection,
            String[] selectionArgs) {
        String uri = getTableContentUri(clazz);
        return super.update(uri, values, selection, selectionArgs);
    }
    
    public int update(Class clazz, String selection, String[] selectionArgs, Object...keyValues) {
        String uri = getTableContentUri(clazz);
        DbTable table = schema.getTable(clazz);
        if(keyValues.length%2!=0) {
        	throw new RuntimeException("invalid key value pair");
        }
        
        ContentValues cv = new ContentValues();
        for(int i =0; i<keyValues.length; i+=2){
        	DbColumn col = table.getColumnByColumnName((String)keyValues[i]) ;
        	if(col!=null)
        		col.putValueToContentValues(cv, keyValues[i+1]);
        }
        return super.update(uri, cv, selection, selectionArgs);
    }
    
    public int delete(Class clazz, String selection,
            String[] selectionArgs) {
        String uri = getTableContentUri(clazz);
        return super.delete(uri, selection, selectionArgs);
    }
    
    public int delete(Object obj) {
        String uri = getTableContentUri(obj.getClass());
        return super.delete(uri, obj);
    }
    
    public int update(Object obj) {
        String uri = getTableContentUri(obj.getClass());
        return super.update(uri, obj);
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
