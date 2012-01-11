package com.gaoshin.sorma.addressbook.importer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;
import com.gaoshin.sorma.core.DbTable;

/**
 * This Importer class demos how SORMA can help dealing with content provider which is not implemented with SORMA
 *
 */
public class Importer {
    
    @Table(
            mappingClass = AndroidContactPhone.class,
            columns = {
                    @Column(
                            name = ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            field = "contactId"),
                    @Column(
                            name = ContactsContract.CommonDataKinds.Phone.NUMBER,
                            field = "number"),
                    @Column(
                            name = ContactsContract.CommonDataKinds.Phone.TYPE,
                            field = "type"),
                    @Column(
                            name = ContactsContract.CommonDataKinds.Phone.LABEL,
                            field = "label")
            })
    public static class AndroidContactPhoneMapping {
    }
    
    /**
     * Read contacts from stock address book
     * 
     * @param contentResolver
     * @return
     * @throws Exception
     */
    public static List<AndroidContact> getContactList(ContentResolver contentResolver) throws Exception {
        DbTable<AndroidContact> tableDef = new DbTable<AndroidContact>(AndroidContact.class);
        List<AndroidContact> list = new ArrayList<AndroidContact>();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor == null) {
            return list;
        }
        
        while(cursor.moveToNext()) {
            AndroidContact androidContact = tableDef.getObjectFromRow(cursor);
            list.add(androidContact);
        }
        cursor.close();
        
        return list;
    }
    
    /**
     * Read phone numbers from stock address book
     * 
     * @param contentResolver
     * @return
     * @throws Exception
     */
    public static List<AndroidContactPhone> getPhoneList(ContentResolver contentResolver) throws Exception {
        DbTable<AndroidContactPhone> tableDef = new DbTable<AndroidContactPhone>(AndroidContactPhoneMapping.class);
        List<AndroidContactPhone> list = new ArrayList<AndroidContactPhone>();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if(cursor == null) {
            return list;
        }
        
        while(cursor.moveToNext()) {
            AndroidContactPhone androidContactPhone = tableDef.getObjectFromRow(cursor);
            list.add(androidContactPhone);
        }
        cursor.close();
        
        return list;
    }
}
