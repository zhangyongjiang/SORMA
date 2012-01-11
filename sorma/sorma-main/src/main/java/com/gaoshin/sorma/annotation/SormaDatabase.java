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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gaoshin.sorma.SqlExecutor;

public class SormaDatabase extends SQLiteOpenHelper {
	private AnnotatedSchema annotatedDatabase;
	
	public SormaDatabase(Context context, AnnotatedSchema annotatedDatabase) {
        super(context, annotatedDatabase.getDatabaseName(), null, annotatedDatabase.getVersion());
        this.annotatedDatabase = annotatedDatabase;
	}
	
	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db) {
	    annotatedDatabase.createDatabase(new SqlExecutor() {
            @Override
            public void execute(String sql) throws Exception {
                db.execSQL(sql);
            }
        });
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
		    annotatedDatabase.upgradeDatabase(new SqlExecutor() {
                @Override
                public void execute(String sql) throws Exception {
                    db.execSQL(sql);
                }
            }, oldVersion, newVersion);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AnnotatedSchema getAnnotatedDatabase() {
		return annotatedDatabase;
	}
}

