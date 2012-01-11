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

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

import com.gaoshin.sorma.core.DbColumn;
import com.gaoshin.sorma.reflection.ReflectionUtil;


public class ObjectColumn extends DbColumn {
    private Field idField;

    public ObjectColumn(Field field, String idFieldName) {
        super(field);
        try {
            idField = ReflectionUtil.getField(field.getType(), idFieldName);
            idField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getValueFromCursor(Cursor cursor, Object obj) throws Exception {
        int index = cursor.getColumnIndex(columnName);
        if(index == -1)
            return;
        if(cursor.isNull(index))
            return;
        
        Object fieldValue = field.getType().newInstance();
        field.set(obj, fieldValue);
        
        Class idFieldType = idField.getType();
        if (idFieldType.equals(Integer.class) || idFieldType.equals(int.class)) {
            idField.set(fieldValue, cursor.getInt(index));
        }
        else if (idFieldType.equals(Long.class) || idFieldType.equals(long.class)) {
            idField.set(fieldValue, cursor.getLong(index));
        }
    }
    
    public void putToDbContentValues(ContentValues cv, Object obj) throws Exception {
        Class<?> type = field.getType();
        Object fieldValue = field.get(obj);
        if(fieldValue == null)
            return;

        Object idFieldValue = idField.get(fieldValue);
        if(idFieldValue == null)
            return;
        
        Class idFieldType = idField.getType();
        if (idFieldType.equals(Integer.class) || idFieldType.equals(int.class)) {
            cv.put(getColumnName(), (Integer)idFieldValue);
        }
        else if (idFieldType.equals(Long.class) || idFieldType.equals(long.class)) {
            cv.put(getColumnName(), (Long)idFieldValue);
        }
    }

}
