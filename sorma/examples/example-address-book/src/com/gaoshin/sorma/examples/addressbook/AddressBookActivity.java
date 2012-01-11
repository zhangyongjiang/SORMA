package com.gaoshin.sorma.examples.addressbook;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gaoshin.sorma.SORMA;
import com.gaoshin.sorma.browser.SormaBrowser;

public class AddressBookActivity extends Activity {
	private SORMA sorma;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sorma = SORMA.getInstance(getBaseContext(), AddressBookContentProvider.class);

        Button btn = new Button(this);
        btn.setText("SORMA Browser");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AddressBookActivity.this, SormaBrowser.class);
                intent.putExtra("base_uri", sorma.getContentBaseUri());
                startActivity(intent);
            }
        });
        setContentView(btn);
        
        // insert contact
        Contact contact = new Contact();
        contact.setFirstName("fname1");
        contact.setLastName("lname1");
        sorma.insert(contact);
        
        // insert phone
        Phone phone1 = new Phone();
        phone1.setContactId(contact.getId());
        phone1.setNumber("124");
        sorma.insert(phone1);
        
        // another phone
        Phone phone2 = new Phone();
        phone2.setContactId(contact.getId());
        phone2.setNumber("456");
        sorma.insert(phone2);
        
        // search by contact id
    	List<Contact> list = sorma.select(Contact.class, "id=" + contact.getId(), null);

    	// search by name
    	List<Contact> listByName = sorma.select(Contact.class, "lastName like ?", new String[]{"fan%"});
    	
        // update contact
        contact.setFirstName("new fname1");
        contact.setLastName("new lname1");
        sorma.update(contact);
        
    	// delete contact
//    	sorma.delete(contact);
//    	sorma.delete(Phone.class, "contactId=?", new String[]{String.valueOf(contact.getId())});
    }
}