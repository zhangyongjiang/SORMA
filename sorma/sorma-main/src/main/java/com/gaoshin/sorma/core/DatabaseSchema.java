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

import java.util.ArrayList;
import java.util.List;

public class DatabaseSchema {
    protected List<DbTable> tables = new ArrayList<DbTable>();
	
    public void addTable(DbTable dbTable) {
        tables.add(dbTable);
    }

    public DbTable getTable(Class mappingClass) {
        for (DbTable table : tables) {
			if(table.getMappingClas().equals(mappingClass)) {
				return table;
			}
		}
		throw new RuntimeException("cannot find table for class " + mappingClass);
	}
	
    public DbTable getTableByName(String name) {
        for (DbTable table : tables) {
			if(table.getTableName().equals(name)) {
				return table;
			}
		}
		throw new RuntimeException("cannot find table " + name);
	}
}
