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

import com.gaoshin.sorma.core.DbColumn;


public class AnnotatedDbColumn extends DbColumn {
	protected Column annotation;
	
    public AnnotatedDbColumn(Field field) {
        super(field);
        this.annotation = field.getAnnotation(Column.class);

        String columnName = annotation.name();
        if (columnName.equals(Table.DEFAULT)) {
            columnName = field.getName();
        }
        setColumnName(columnName);
    }
}
