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

import com.gaoshin.sorma.SqlExecutor;
import com.gaoshin.sorma.core.DatabaseSchema;
import com.gaoshin.sorma.core.DbTable;
import com.gaoshin.sorma.reflection.ReflectionUtil;

public class AnnotatedSchema extends DatabaseSchema {
    private Class<? extends SormaContentProvider> contentProviderClass;
    private int version;
    private ContentProvider contentProviderAnnotation;
	
    public AnnotatedSchema(Class<? extends SormaContentProvider> ormcp) {
        this.contentProviderClass = ormcp;
        this.contentProviderAnnotation = ReflectionUtil.getAnnotation(ormcp, ContentProvider.class);
        version = contentProviderAnnotation.version();
        
        for (Class<?> cls : contentProviderAnnotation.mappingClasses()) {
            try {
                Table annotation = (Table) ReflectionUtil.getAnnotation(cls, Table.class);
                if(annotation == null) {
                    throw new RuntimeException(cls + " class has no @Table annotation");
                }
                Class mappingClass = annotation.mappingClass();
                if(mappingClass == null || Object.class.equals(mappingClass)) {
                    mappingClass = cls;
                }
                addTable(new DbTable(mappingClass, annotation));
            } catch (Exception e) {
                throw new SormaException("cannot map " + cls, e);
            }
        }
        
        for (Table table : contentProviderAnnotation.tables()) {
            try {
                Class cls = table.mappingClass();
                if(cls == null) {
                    throw new RuntimeException("@Table annotation inside of @ContentProvider must have mappingClass attribute");
                }
                addTable(new DbTable(cls, table));
            } catch (Exception e) {
                throw new SormaException(e);
            }
        }
	}

    public Class<? extends SormaContentProvider> getContentProviderClass() {
        return contentProviderClass;
    }

    public String getDatabaseName() {
        String dbname = contentProviderAnnotation.dbname();
        if(ContentProvider.DEFAULT_DB_NAME.equals(dbname)) {
            dbname = contentProviderClass.getSimpleName();
        }
        return dbname;
    }

    public int getVersion() {
        return version;
    }

    public void createDatabase(SqlExecutor executor) {
        for (DbTable table : tables) {
            String[] sqls = table.getTableAnnotation().create();
            for(String sql : sqls) {
                if(sql != null && sql.trim().length()>0) {
                    try {
                        System.out.println("SORMA" + "\t" +"execSQL " + sql);
                        executor.execute(sql);
                    } catch (Exception e) {
                    	throw new SormaException(e);
                    }
                }
            }
        }
    }

    public void upgradeDatabase(SqlExecutor executor, int oldVersion, int newVersion) throws Exception {
    	Upgrade[] upgrades = contentProviderAnnotation.upgrades();
    	for(Upgrade upgrade : upgrades) {
    		if(upgrade.version() <= oldVersion || upgrade.version() > newVersion) {
    			continue;
    		}
    		for(String sql : upgrade.SQLs()) {
                try {
                    System.out.println("SORMA" + "\t" +"execute update " + sql);
                    executor.execute(sql);
                } catch (Exception e) {
                	throw new SormaException(e);
                }
    		}
    	}
    }
}
