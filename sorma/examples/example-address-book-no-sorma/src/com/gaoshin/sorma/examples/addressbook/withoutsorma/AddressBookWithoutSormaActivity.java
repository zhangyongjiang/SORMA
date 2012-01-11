package com.gaoshin.sorma.examples.addressbook.withoutsorma;

import java.util.List;
import java.util.Random;

import com.gaoshin.sorma.examples.addressbook.Contact;
import com.gaoshin.sorma.examples.addressbook.Phone;
import com.gaoshin.sorma.examples.addressbook.PhoneNumberType;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class AddressBookWithoutSormaActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Contact contact1 = new Contact();
        contact1.setFirstName("fname1");
        contact1.setLastName("lname1");
        insertContact(contact1);
        
        Phone phone1 = new Phone();
        phone1.setContactId(contact1.getId());
        phone1.setPhone("124");
        phone1.setType(PhoneNumberType.Mobile);
        insertPhone(phone1);
        
        Phone phone2 = new Phone();
        phone2.setContactId(contact1.getId());
        phone2.setPhone("456");
        phone2.setType(PhoneNumberType.Home);
        insertPhone(phone2);
        
        findContact(contact1.getId());
        
        updateContact(contact1.getId(), "new fname1", "new lname1");
    	findContact(contact1.getId());
    	
    	deleteContact(contact1.getId());
    	findContact(contact1.getId());
    }
    
    private void deleteContact(Long contactId) {
		Toast.makeText(this, "Delete contact " + contactId, Toast.LENGTH_LONG).show();
    	ContentResolver cr = this.getContentResolver();
    	Uri uri = ContentUris.withAppendedId(getContactUri(), contactId);
    	cr.delete(uri, null, null);
    	
    	cr.delete(getPhoneUri(), "contactId=?", new String[]{String.valueOf(contactId)});
	}

	private void updateContact(Long contactId, String fname, String lname) {
		Toast.makeText(this, "Update contact " + contactId + " with new name", Toast.LENGTH_LONG).show();
    	ContentResolver cr = this.getContentResolver();
    	Uri uri = ContentUris.withAppendedId(getContactUri(), contactId);
    	ContentValues cv = new ContentValues();
    	cv.put("firstName", fname);
    	cv.put("lastName", lname);
    	int count = cr.update(uri, cv, null, null);
	}

	private void findContact(Long contactId) {
    	StringBuilder sb = new StringBuilder();
    	
    	ContentResolver cr = this.getContentResolver();
    	Uri uri = ContentUris.withAppendedId(getContactUri(), contactId);
    	Cursor cursor = cr.query(uri, null, null, null, null);
    	if(cursor.getCount() == 0) {
    		Toast.makeText(this, "No contact found for " + contactId, Toast.LENGTH_LONG).show();
    		cursor.close();
    		return;
    	}
    	
    	while(cursor.moveToNext()) {
    		Long id = cursor.getLong(cursor.getColumnIndex("_id"));
    		String fname = cursor.getString(cursor.getColumnIndex("firstName"));
    		String lname = cursor.getString(cursor.getColumnIndex("lastName"));
    		sb.append("ID: " + id + "\nFirst Name: " + fname + "\nLast Name: " + lname + "\n");
    	}
    	cursor.close();
    	
    	uri = getPhoneUri();
    	cursor = cr.query(uri, null, "contactId=?", new String[]{String.valueOf(contactId)}, null);
    	while(cursor.moveToNext()) {
    		String phone = cursor.getString(cursor.getColumnIndex("phone"));
    		String type = cursor.getString(cursor.getColumnIndex("type"));
    		sb.append("\t" + type + ": " + phone + "\n");
    	}
    	cursor.close();
    	
		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}

	private void insertContact(Contact contact) {
    	ContentResolver cr = this.getContentResolver();
    	ContentValues cv = new ContentValues();
    	cv.put("firstName", contact.getFirstName());
    	cv.put("lastName", contact.getLastName());
    	cv.put("gender", contact.getGender().name());
    	cv.put("married", true);
    	Uri uri = cr.insert(getContactUri(), cv);
    	String suri = uri.toString();
    	String id = suri.substring(suri.lastIndexOf('/')+1);
    	contact.setId(Long.parseLong(id));
    }
	
	private void insertPhone(Phone phone) {
    	ContentResolver cr = this.getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put("contactId", phone.getContactId());
		cv.put("phone", phone.getPhone());
		cv.put("type", phone.getType().name());
		Uri uri = cr.insert(getPhoneUri(), cv);
    	String suri = uri.toString();
    	String id = suri.substring(suri.lastIndexOf('/')+1);
    	phone.setId(Long.parseLong(id));
	}
	
	private Uri getContactUri() {
		return Uri.parse("content://com.gaoshin.sorma.examples.addressbook.withoutsorma.AddressBookContentProvider/contact");
	}
	
	private Uri getPhoneUri() {
		return Uri.parse("content://com.gaoshin.sorma.examples.addressbook.withoutsorma.AddressBookContentProvider/phone");
	}
}
