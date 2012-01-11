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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import com.gaoshin.sorma.core.DbColumn;
import com.gaoshin.sorma.core.DbTable;

public class SormaEngine{
    private static final String tag = "SORMA";
    public static final String NATIVE_QUERY = "nativequery";
    public static final String DEBUG_QUERY = "debug";
    
	private SormaDatabase sormaDatabase;
	
    public SormaEngine(SormaDatabase dbHelper) {
    	this.sormaDatabase = dbHelper;
	}
	
    private static String getTableNameFromUri(String uri) {
        String path[] = uri.split("/");
        return path[3];
    }

    private static String getIdFromUri(String uri) {
        String path[] = uri.split("/");
        return path.length > 4 ? path[4] : null;
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
	
    public int delete(String uri, String selection, String[] selectionArgs) {
    	QueryParameter qp = new QueryParameter(uri, selection, selectionArgs);
        if(writeLog())
            log("delete record from table " + uri.toString() + ". selection: " + selection + ", args: " + toString(selectionArgs));
		SQLiteDatabase sqlitedb = sormaDatabase.getWritableDatabase();
        return sqlitedb.delete(qp.table, qp.selection, qp.args);
    }
    
    public long insert(String table, ContentValues values) {
		SQLiteDatabase sqlitedb = sormaDatabase.getWritableDatabase();
        if(values.containsKey("bulkInsert")) {
            values.remove("bulkInsert");
            String[] columns = values.getAsString("columns").split(" ");
            values.remove("columns");
            
            sqlitedb.beginTransaction();
            InsertHelper helper = new DatabaseUtils.InsertHelper(sqlitedb, table);
            try {
                for(int i=0; ;i++) {
                    String testKey = i + "-" + columns[0];
                    if(!values.containsKey(testKey)) {
                        break;
                    }
                    StringBuilder sb = new StringBuilder();
                    helper.prepareForInsert();
                    for(String col : columns) {
                        String key = i + "-" + col;
                        Object value = values.get(key);
                        sb.append(col).append("=").append(value).append("\n");
                        int index = helper.getColumnIndex(col);
                        if(value == null) {
                            helper.bindNull(index);
                        }
                        else if(value instanceof String) {
                            helper.bind(index, (String)value);
                        }
                        else if(value instanceof Boolean) {
                            helper.bind(index, (Boolean)value);
                        }
                        else if(value instanceof Byte) {
                            helper.bind(index, (Byte)value);
                        }
                        else if(value instanceof byte[]) {
                            helper.bind(index, (byte[])value);
                        }
                        else if(value instanceof Double) {
                            helper.bind(index, (Double)value);
                        }
                        else if(value instanceof Float) {
                            helper.bind(index, (Float)value);
                        }
                        else if(value instanceof Integer) {
                            helper.bind(index, (Integer)value);
                        }
                        else if(value instanceof Long) {
                            helper.bind(index, (Long)value);
                        }
                        else if(value instanceof Short) {
                            helper.bind(index, (Short)value);
                        }
                        else 
                            throw new SormaException(value.getClass() + " is not supported");
                    }
                    long insert = helper.execute();
                    if(insert == -1) {
                        throw new SormaException("cannot insert " + sb.toString());
                    }
                    values.put("result-" + i, insert);
                }
            
                helper.close();
                helper = null;
                sqlitedb.setTransactionSuccessful();
            } finally {
                try {
                    if(helper != null)
                        helper.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                sqlitedb.endTransaction();
            }
            return 0;
        } else {
            long rowid = sqlitedb.insertOrThrow(table, " ", values);
            return rowid;
        }
    }

    public Cursor query(String uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
		SQLiteDatabase sqlitedb = sormaDatabase.getReadableDatabase();
    	QueryParameter param = new QueryParameter(uri, selection, selectionArgs, sortOrder);
        if(param.table.equals(NATIVE_QUERY)) {
            log("raw query: " + selection + ", args: " + toString(selectionArgs) + ", order: " + sortOrder);
            Cursor cursor = sqlitedb.rawQuery(selection, selectionArgs);
            return cursor;
        }
        else {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(param.table);
            if (writeLog()) log("query table " + uri + ". selection: " + selection + ", args: " + toString(param.args) + ", order: " + param.order + ", limit:" + param.limit);
            Cursor cursor = qb.query(sqlitedb, projection, param.selection, param.args, null, null, param.order, param.limit);
            return cursor;
        }
    }

    public int update(String uri, ContentValues values, String selection,
            String[] selectionArgs) {
		SQLiteDatabase sqlitedb = sormaDatabase.getWritableDatabase();
    	QueryParameter param = new QueryParameter(uri, selection, selectionArgs);
        if(writeLog())
            log("update records in table " + param.table + ". selection: " + param.selection + ", args: " + toString(param.args));

        String table = param.table;
        if(table.equals(NATIVE_QUERY)) {
            String sql = selection;
            SQLiteStatement statement = sqlitedb.compileStatement(sql);
            try {
                statement.execute();
            }
            finally {
                statement.close();
            }
            return -1;
        }
        else {
            return sqlitedb.update(table, values, selection, selectionArgs);
        }

    }

    private void log(String log) {
        System.out.println(tag + "\t" + log);
    }
    
    private boolean writeLog() {
        return false;
    }
    
    private class QueryParameter {
    	String selection;
    	String[] args;
    	String table;
    	String id;
    	String order;
    	String limit;
    	
    	QueryParameter(String uri, String selection, String[] selectionArgs) {
            table = getTableNameFromUri(uri);
            id = getIdFromUri(uri);
            if(id != null) {
                DbTable tableDef = sormaDatabase.getAnnotatedDatabase().getTableByName(table);
                DbColumn column = tableDef.getKeyColumn();
                if(selection == null || selection.trim().length() == 0) {
    	            selection = column.getColumnName() + "=?";
    	            selectionArgs = new String[]{id};
                }
                else {
    	            selection = selection + " and " + column.getColumnName() + "=?";
    	            if(selectionArgs == null) {
    	            	selectionArgs = new String[]{id};
    	            }
    	            else {
    	            	String[] newArgs = new String[selectionArgs.length+1];
    	            	for(int i=0; i<selectionArgs.length; i++) {
    	            		newArgs[i] = selectionArgs[i];
    	            	}
    	            	newArgs[selectionArgs.length] = id;
    	            	selectionArgs = newArgs;
    	            }
                }
            }
            
            this.selection = selection;
            this.args = selectionArgs;
    	}
    	
    	QueryParameter(String uri, String selection, String[] selectionArgs, String sortOrder) {
    		this(uri, selection, selectionArgs);
    		order = sortOrder;
            if (sortOrder != null) {
                String low = sortOrder.toLowerCase();
                int pos = low.indexOf("limit ");
                if (pos != -1) {
                    limit = sortOrder.substring(pos);
                    order = sortOrder.substring(0, pos);
                }
            }
    	}
    }
    
    public SormaDatabase getSormaDatabase() {
    	return sormaDatabase;
    }
}
