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

import com.gaoshin.sorma.core.DatabaseSchema;
import com.gaoshin.sorma.core.DbColumn;
import com.gaoshin.sorma.core.DbTable;

public abstract class SormaBase {
    protected SormaContentResolver contentResolver;
    protected DatabaseSchema schema;

    public SormaBase(DatabaseSchema schema) {
        this.schema = schema;
    }

    public DatabaseSchema getSchema() {
        return schema;
    }
    
    public <T> DbTable<T> getMappingTable(Class<T>cls) {
        return schema.getTable(cls);
    }

    public SormaBase setContentResolver(SormaContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        return this;
    }

    public <T> ResultSet<T> select(String uri, Class<T> clazz, String where, String[] whereValues) {
        return select(uri, clazz, where, whereValues, null);
    }

    public <T> ResultSet<T> select(String uri, Class<T> clazz, String where, String[] whereValues, String sort) {
        return select(uri, clazz, where, whereValues, sort, Integer.MAX_VALUE);
    }

    public <T> Cursor selectCursor(String uri, String where, String[] whereValues, String sort) {
        Cursor cursor = contentResolver.query(uri, null, where, whereValues, sort);
        return cursor;
    }

    public int count(String uri, String where, String[] whereValues) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, where, whereValues, null);
            return cursor.getCount();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public <T> ResultSet<T> select(String uri, Class<T> clazz, String where, String[] whereValues, String sort, int size) {
        return select(uri, clazz, where, whereValues, sort, 0, size);
    }

    public <T> ResultSet<T> select(String uri, Class<T> clazz, String where, String[] whereValues, String sort, int offset, int size) {
        Cursor cursor = null;
        ResultSet<T> result = new ResultSet<T>();
        try {
            DbTable<T> table = schema.getTable(clazz);

            if (size == -1) {
                cursor = contentResolver.query(uri, null, where, whereValues, null);
                if (cursor != null) {
                    result.setCount(cursor.getCount());
                }
            }
            else {
                cursor = contentResolver.query(uri, null, where, whereValues, sort);
                if (cursor != null) {
                    if (offset > 0) {
                        cursor.move(offset);
                    }
                    while (cursor.moveToNext() && size > 0) {
                        result.add((T) table.getObjectFromRow(cursor));
                        size--;
                    }
                    result.setCount(cursor.getCount());
                }
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long insert(String uri, Object obj) {
        DbTable table = schema.getTable(obj.getClass());
        long id = contentResolver.insert(uri, table.getContentValues(obj));
        DbColumn keyColumn = table.getKeyColumn();
        if (keyColumn != null && keyColumn.isAutoId()) {
            try {
                keyColumn.setValueToObject(obj, String.valueOf(id));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public int update(String uri, Object obj, String selection, String[] selectionArgs) {
        DbTable table = schema.getTable(obj.getClass());
        ContentValues values = table.getContentValues(obj);
        return contentResolver.update(uri, values, selection, selectionArgs);
    }

    public int update(String uri, ContentValues values, String selection, String[] selectionArgs) {
        return contentResolver.update(uri, values, selection, selectionArgs);
    }

    public <T> T getObjectFromRow(Cursor cursor, Class<T> clazz) {
        return (T) schema.getTable(clazz).getObjectFromRow(cursor);
    }

    public int delete(String uri, String selection, String[] selectionArgs) {
        return contentResolver.delete(uri, selection, selectionArgs);
    }

    public int delete(String tableUri, Object obj) {
        DbTable table = schema.getTable(obj.getClass());
        DbColumn column = table.getKeyColumn();
        String id = column.getStringValueFromObject(obj);
        tableUri = tableUri + "/" + id;
        String where = column.getColumnName() + "=?";
        String[] whereValues = new String[] { id };
        return contentResolver.delete(tableUri, where, whereValues);
    }

    public int update(String tableUri, Object obj) {
        DbTable table = schema.getTable(obj.getClass());
        DbColumn column = table.getKeyColumn();
        String id = column.getStringValueFromObject(obj);
        tableUri = tableUri + "/" + id;
        String where = column.getColumnName() + "=?";
        String[] whereValues = new String[] { id };
        ContentValues values = table.getContentValues(obj);
        return contentResolver.update(tableUri, values, where, whereValues);
    }
}
