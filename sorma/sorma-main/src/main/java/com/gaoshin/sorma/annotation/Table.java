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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface Table {
	public static final String DEFAULT = "";
	
	/**
	 * The class to which this table is mapping
	 * @return
	 */
    Class mappingClass() default Object.class;
    
	/**
	 * table name
	 * @return
	 */
	String name() default DEFAULT;
	
	/**
	 * SQLs to create table and indexes
	 * @return
	 */
	String[] create() default {};
	
	/**
	 * key column name
	 * @return
	 */
	String keyColumn() default DEFAULT;
	
	/**
	 * is it auto id
	 * @return
	 */
	boolean autoId() default false;
	
	Column[] columns() default {};
}
