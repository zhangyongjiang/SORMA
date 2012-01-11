package com.gaoshin.sorma.addressbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gaoshin.sorma.addressbook.importer.AndroidContact;
import com.gaoshin.sorma.addressbook.importer.AndroidContactPhone;
import com.gaoshin.sorma.addressbook.importer.Importer.AndroidContactPhoneMapping;
import com.gaoshin.sorma.annotation.Table;
import com.gaoshin.sorma.browser.SormaBrowser;
import com.gaoshin.sorma.core.DbTable;
import com.gaoshin.sorma.reflection.ReflectionUtil;

public class AddressBookListActivity extends AddressBookActivity {
    private static final String ImportStatus = "ImportStatus";
    private ContactAdapter contactAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupAddButton();
        setupImportButton();
        setupDeleteAllButton();
        setupBrowserButton();
        setupListView();
    }

    private void setupDeleteAllButton() {
        Button btn = (Button) findViewById(R.id.deleteAllBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllContacts();
            }
        });
    }

    protected void deleteAllContacts() {
        sorma.delete(Configuration.class, "_key=?", new String[]{ImportStatus});
        sorma.delete(Phone.class, null, null);
        sorma.delete(Contact.class, null, null);
        applyData();
    }

    private void setupListView() {
        ListView listview = (ListView) findViewById(R.id.contactListView);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(getBaseContext(), EditContactActivity.class);
                intent.putExtra("contactId", arg3);
                startActivity(intent);
            }
        });
        contactAdapter = new ContactAdapter();
        listview.setAdapter(contactAdapter);
    }

    private void setupImportButton() {
        Button btn = (Button) findViewById(R.id.importBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDeviceContacts();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        applyData();
    }
    
    private void applyData() {
        Button btn = (Button) findViewById(R.id.importBtn);
        Configuration conf = sorma.get(Configuration.class, "_key=?", new String[]{ImportStatus});
        if(conf == null) {
            btn.setEnabled(true);
        }
        else {
            btn.setEnabled(false);
        }
        
        Contact[] allContacts = getAllContacts();
        contactAdapter.applyData(allContacts);
    }

    private Contact[] getAllContacts() {
        Map<Long, Contact> all = new HashMap<Long, Contact>();
        
        List<Contact> list = sorma.select(Contact.class, null, null);
        for(Contact ac : list) {
            all.put(ac.getId(), ac);
        }
        
        List<Phone> allPhones = sorma.select(Phone.class, null, null);
        for(Phone phone : allPhones) {
            Long contactId = phone.getContactId();
            Contact contact = all.get(contactId);
            if(contact == null) {
                continue;
            }
            contact.getPhoneList().add(phone);
        }
        
        return all.values().toArray(new Contact[0]);
    }

    protected void importDeviceContacts() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading contacts ...");
        progressDialog.show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map<Long, Contact> contactIdMap = new HashMap<Long, Contact>();
                
                {   // import contacts
                    Uri uri = ContactsContract.Contacts.CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri, null, "has_phone_number=1", null, null);
                    if (cursor != null) {
                        DbTable<AndroidContact> table = new DbTable<AndroidContact>(AndroidContact.class);
                        while (cursor.moveToNext()) {
                            AndroidContact deviceContact = table.getObjectFromRow(cursor);
                            Contact contact = new Contact();
                            contact.setDisplayName(deviceContact.getDisplayName());
                            contact.setContactId(deviceContact.getId());
                            sorma.insert(contact);
                            contactIdMap.put(deviceContact.getId(), contact);
                        }
                        cursor.close();
                    }
                }
                
                {   // import phone numbers
                    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri, null, null, null, null);
                    if (cursor != null) {
                        List<Phone> phoneList = new ArrayList<Phone>();
                        Table mapping = ReflectionUtil.getAnnotation(AndroidContactPhoneMapping.class, Table.class);
                        DbTable<AndroidContactPhone> table = new DbTable<AndroidContactPhone>(mapping);
                        while (cursor.moveToNext()) {
                            AndroidContactPhone phoneFromDevice = table.getObjectFromRow(cursor);
                            Phone phone = new Phone();
                            Long deviceContactId = phoneFromDevice.getContactId();
                            Contact contact = contactIdMap.get(deviceContactId);
                            if(contact == null) {
                                continue;
                            }
                            phone.setContactId(contact.getId());
                            phone.setNumber(phoneFromDevice.getNumber());
                            phone.setType(PhoneNumberType.class.getEnumConstants()[phoneFromDevice.getType()].name());
                            phoneList.add(phone);
                        }
                        cursor.close();
                        sorma.insert(phoneList);
                    }
                }
                
                // mark imported. 
                Configuration conf = new Configuration();
                conf.setKey(ImportStatus);
                conf.setValue("done");
                sorma.insert(conf);
                
                return null;
            }
            
            protected void onPostExecute(Void result) {
                Button btn = (Button) findViewById(R.id.importBtn);
                btn.setEnabled(false);
                applyData();
                progressDialog.dismiss();
            };
        }.execute((Void[])null);
    }

    private void setupAddButton() {
        Button btn = (Button) findViewById(R.id.addBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EditContactActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBrowserButton() {
        Button btn = (Button) findViewById(R.id.dbBrowserBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressBookListActivity.this, SormaBrowser.class);
                intent.putExtra("base_uri", sorma.getContentBaseUri());
                startActivity(intent);
            }
        });
    }

}