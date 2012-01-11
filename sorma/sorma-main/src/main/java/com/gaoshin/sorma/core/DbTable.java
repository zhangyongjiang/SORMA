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
package com.gaoshin.sorma.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;
import com.gaoshin.sorma.reflection.FieldFoundCallback;
import com.gaoshin.sorma.reflection.ReflectionUtil;


public class DbTable<T> {
	private Class<T> mappingClass;
	protected List<DbColumn> columns = new ArrayList<DbColumn>();
	private String tableName;
    private DbColumn keyColumn;
    private Table tableAnno;

    public DbTable(Class mappingClass) {
        this(mappingClass, ReflectionUtil.getAnnotation(mappingClass, Table.class));
    }

    public DbTable(Table tableAnno) {
        this(tableAnno.mappingClass(), tableAnno);
    }

    public DbTable(Class<T> mappingClass, Table tableAnno) {
        this.mappingClass = mappingClass;
        this.tableAnno = tableAnno;

        tableName = tableAnno.name();
        if (tableName.equals(Table.DEFAULT)) {
            tableName = mappingClass.getSimpleName();
        }
        
        for(Column column : tableAnno.columns()) {
            String colName = column.name();
            String fieldName = column.field();
            if(colName == null || Column.DEFAULT.equals(colName)) {
                colName = fieldName;
            }
            Field field = ReflectionUtil.getField(mappingClass, fieldName);
            DbColumn dbColumn = new DbColumn(field, colName);
            addMappingColumn(dbColumn);
        }
        
        // no annotated column, use all fields by default
        if (getColumns().size() == 0) {
            ReflectionUtil.iterateFields(mappingClass, (Object) null, new FieldFoundCallback() {
                @Override
                public void field(Object o, Field field) {
                    if(ReflectionUtil.isPrimeField(field)) {
                        addMappingColumn(new DbColumn(field));
                    }
                }
            });
        }
        
        String keyColumnName = tableAnno.keyColumn();
        for (DbColumn column : columns) {
            if (column.getColumnName().equals(keyColumnName)) {
                setKeyColumn(column);
                if(tableAnno.autoId()) {
                    column.setAutoId(true);
                }
                break;
            }
        }
    }
    
    public Table getTableAnnotation() {
        return tableAnno;
    }

    public void addMappingColumn(DbColumn dbColumn) {
        columns.add(dbColumn);
    }
    
    public List<DbColumn> getColumns() {
    	return columns;
    }
	
	public Class<?> getMappingClas() {
		return mappingClass;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public T getObjectFromRow(Cursor cursor) {
		try {
			T obj = mappingClass.newInstance();
			for(DbColumn col : columns) {
				try {
                    col.getValueFromCursor(cursor, obj);
                } catch (Exception e) {
                    System.out.println("SORMA" + "\t" + "cannot get value for " + tableName + "." + col.getColumnName());
                    throw e;
                }
			}
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public void putToDbContentValues(ContentValues cv, Object obj) {
        try {
            for(DbColumn col : columns) {
                col.putToDbContentValues(cv, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putToDbContentValues(String keyPrefix, ContentValues cv, Object obj) {
        try {
            for(DbColumn col : columns) {
                col.putToDbContentValues(keyPrefix, cv, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public ContentValues getContentValues(Object obj) {
		ContentValues cv = new ContentValues();
		putToDbContentValues(cv, obj);
		return cv;
	}

    public DbColumn getKeyColumn() {
        return keyColumn;
    }

    protected void setKeyColumn(DbColumn column) {
        this.keyColumn = column;
    }
    
    public DbColumn getColumnByFieldName(String name){
    	for(DbColumn col : columns){
    		if(col.getField().getName().equals(name))
    			return col;
    	}
    	
    	return null;
    }
    
    public DbColumn getColumnByColumnName(String name){
    	for(DbColumn col : columns){
    		if(col.getColumnName().equals(name))
    			return col;
    	}
    	
    	return null;
    }
    
    public static boolean isOrmableType(Class<?> type) {
        if(byte[].class.equals(type)) {
            return true;
        } 
        else if (type.equals(String.class)) {
            return true;
        }
        else if (type.equals(Integer.class) || type.equals(int.class)) {
            return true;
        }
        else if (type.equals(Float.class) || type.equals(float.class)) {
            return true;
        }
        else if (type.equals(Double.class) || type.equals(double.class)) {
            return true;
        }
        else if (type.equals(Long.class) || type.equals(long.class)) {
            return true;
        }
        else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        }
        else if (type.equals(Date.class)) {
            return true;
        }
        else if (type.equals(Calendar.class)) {
            return true;
        }
        else if(type.isEnum()) {
            return true;
        }
        return false;
    }
}
