package com.gaoshin.sorma.examples.addressbook.withoutsorma;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AddressBookContentProvider extends ContentProvider {
    private static final String DATABASE_NAME = "no_sorma_address_book.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CONTACT_TABLE_NAME = "contact";
    private static final String PHONE_TABLE_NAME = "phone";
    
    private DatabaseHelper mOpenHelper;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CONTACT_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + " firstName TEXT,"
                    + " lastName TEXT"
                    + ");");
            db.execSQL("CREATE TABLE " + PHONE_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + " contactId INTEGER,"
                    + " phone TEXT,"
                    + " type TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PHONE_TABLE_NAME);
            onCreate(db);
        }
    }

    public static String getTableNameFromUri(String uri) {
        String path[] = uri.split("/");
        String table = path[3];
        if(!CONTACT_TABLE_NAME.equals(table) && !PHONE_TABLE_NAME.equals(table)) {
        	throw new RuntimeException("Invalid uri " + uri);
        }
        return table;
    }

    public static String getIdFromUri(String uri) {
        String path[] = uri.split("/");
        if(path.length > 4)
        	return path[4];
        else 
        	return null;
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String suri = uri.toString();
		String table = getTableNameFromUri(suri);
		String id = getIdFromUri(suri);
		if(id != null) {
			if(selection==null || selection.trim().length() == 0) {
				selection = "_id=" + id;
			}
			else {
				selection = "_id=" + id + " and ";
			}
		}
        int count = db.delete(table, selection, selectionArgs);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		String suri = uri.toString();
		String table = getTableNameFromUri(suri);
		String id = getIdFromUri(suri);
		if(CONTACT_TABLE_NAME.equals(table)) {
			if(id == null) {
				return "vnd.android.cursor.dir/vnd.addressbook.contact";
			}
			else {
				return "vnd.android.cursor.item/vnd.addressbook.contact";
			}
		}
		else if(PHONE_TABLE_NAME.equals(table)) {
			if(id == null) {
				return "vnd.android.cursor.dir/vnd.addressbook.phone";
			}
			else {
				return "vnd.android.cursor.item/vnd.addressbook.phone";
			}
		}
		throw new RuntimeException("Invalid uri " + suri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String suri = uri.toString();
		String table = getTableNameFromUri(suri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(table, "", values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Uri.parse("content://com.gaoshin.sorma.examples.addressbook.withoutsorma.AddressBookContentProvider"), rowId);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String suri = uri.toString();
		String table = getTableNameFromUri(suri);
		String id = getIdFromUri(suri);
		if(id != null) {
			if(selection==null || selection.trim().length() == 0) {
				selection = "_id=" + id;
			}
			else {
				selection = "_id=" + id + " and ";
			}
		}
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(table);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String suri = uri.toString();
		String table = getTableNameFromUri(suri);
		String id = getIdFromUri(suri);
		if(id != null) {
			if(selection==null || selection.trim().length() == 0) {
				selection = "_id=" + id;
			}
			else {
				selection = "_id=" + id + " and ";
			}
		}
        int count = db.update(table, values, selection, selectionArgs);
        return count;
	}

}
