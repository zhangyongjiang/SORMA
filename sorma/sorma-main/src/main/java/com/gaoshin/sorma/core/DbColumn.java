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
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import com.gaoshin.sorma.reflection.ReflectionUtil;


public class DbColumn {
	protected Field field;
    protected String columnName;
    private boolean key;
    private boolean autoId;
	
    public DbColumn(Field field) {
        this(field, field.getName());
    }

    public DbColumn(Field field, String columnName) {
        this.field = field;
        field.setAccessible(true);
        this.columnName = columnName;
    }

	public Field getField() {
		return field;
	}
	
    protected void setColumnName(String columnName) {
        this.columnName = columnName;
    }

	public String getColumnName() {
        return columnName;
	}
	
	public void getValueFromCursor(Cursor cursor, Object obj) throws Exception {
		int index = cursor.getColumnIndex(columnName);
		if(index == -1)
			return;
		if(cursor.isNull(index))
		    return;
		Class<?> type = field.getType();
		if(byte[].class.equals(type)) {
			byte[] blob = cursor.getBlob(index);
			field.set(obj, blob);
		} 
        else if (type.equals(String.class)) {
            String fieldValue = cursor.getString(index);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Integer.class) || type.equals(int.class)) {
            int fieldValue = cursor.getInt(index);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Float.class) || type.equals(float.class)) {
            float fieldValue = cursor.getFloat(index);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Double.class) || type.equals(double.class)) {
            double fieldValue = cursor.getDouble(index);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Long.class) || type.equals(long.class)) {
            long fieldValue = cursor.getLong(index);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            boolean fieldValue = (cursor.getInt(index) == 1);
            field.set(obj, fieldValue);
        }
        else if (type.equals(Date.class)) {
            Long fieldValue = cursor.getLong(index);
            field.set(obj, new Date(fieldValue));
        }
        else if (type.equals(Calendar.class)) {
            Long fieldValue = cursor.getLong(index);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fieldValue);
            field.set(obj, cal);
        }
        else if(type.isEnum()) {
            String fieldValue = cursor.getString(index);
            if(fieldValue != null) {
                boolean found = false;
                for (Object o : type.getEnumConstants()) {
                    Enum e = (Enum) o;
                    if (e.name().equals(fieldValue)) {
                        field.set(obj, e);
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    int ordinal = Integer.parseInt(fieldValue);
                    field.set(obj, type.getEnumConstants()[ordinal]);
                }
            }
        }
        else {
            System.out.println("SORMA" + "\t" + "cannot convert value for field " + columnName);
        }
	}
	
	public void putToDbContentValues(ContentValues cv, Object obj) throws Exception  {
		putValueToContentValues(cv,field.get(obj));
    }
	
	public void putToDbContentValues(String keyPrefix, ContentValues cv, Object obj) throws Exception  {
		putValueToContentValues(keyPrefix,cv,field.get(obj));
	}
	
    public void putValueToContentValues(ContentValues cv, Object obj){
    	putValueToContentValues("",cv,obj);
    }
    
    public void putValueToContentValues(String keyPrefix, ContentValues cv, Object fieldValue) {
		Class<?> type = field.getType();
		if(fieldValue == null) {
		    cv.putNull(keyPrefix + getColumnName());
		    return;
		}
		
		if (type.equals(String.class)) {
	        cv.put(keyPrefix + getColumnName(), (String)fieldValue);
			return;
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			cv.put(keyPrefix + getColumnName(), (Integer)fieldValue);
			return;
		}
		
		if (type.equals(Float.class) || type.equals(float.class)) {
			cv.put(keyPrefix + getColumnName(), (Float)fieldValue);
			return;
		}
		
		if (type.equals(Double.class) || type.equals(double.class)) {
			cv.put(keyPrefix + getColumnName(), (Double)fieldValue);
			return;
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			cv.put(keyPrefix + getColumnName(), (Long)fieldValue);
			return;
		}
		
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
		    Boolean b = (Boolean)fieldValue;
		    Object v = null;
		    if(b != null) {
		        v = b ? new Integer(1) : new Integer(0);
		    }
			cv.put(keyPrefix + getColumnName(), (Integer)v);
			return;
		}
		
		if (type.equals(Date.class)) {
			Long ts = (fieldValue == null) ? null : ((Date)fieldValue).getTime(); 
			cv.put(keyPrefix + getColumnName(), ts);
			return;
		}
		
		if (type.equals(Calendar.class)) {
			Long ts = (fieldValue == null) ? null : ((Calendar)fieldValue).getTimeInMillis(); 
			cv.put(keyPrefix + getColumnName(), ts);
			return;
		}
		
		if (type.isEnum()) {
			String value = (fieldValue == null) ? null : fieldValue.toString();
			cv.put(keyPrefix + getColumnName(), (String)value);
			return;
		}
		
		if(byte[].class.equals(field.getType())) {
			cv.put(keyPrefix + getColumnName(), (byte[])fieldValue);
		}
	}
    
    

    public void setKey(boolean key) {
        this.key = key;
    }

    public boolean isKey() {
        return key;
    }

    public Object getValueFromObject(Object obj) {
        try {
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringValueFromObject(Object obj) {
        return ReflectionUtil.primeString(getValueFromObject(obj));
    }

    public void setValueToObject(Object obj, String id) {
        try {
            Object fieldValue = ReflectionUtil.convert(id, field.getType());
            field.set(obj, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAutoId(boolean autoId) {
        this.autoId = autoId;
    }

    public boolean isAutoId() {
        return autoId;
    }
}
