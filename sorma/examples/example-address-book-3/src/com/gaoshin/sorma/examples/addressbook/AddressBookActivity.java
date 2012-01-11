package com.gaoshin.sorma.examples.addressbook;

import java.util.ArrayList;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sorma = SORMA.getInstance(getBaseContext(), AddressBookContentProvider.class);

        Button btn = new Button(this);
        btn.setText("Check Content Provider");
        setContentView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressBookActivity.this, SormaBrowser.class);
                intent.putExtra("base_uri", sorma.getContentBaseUri());
                startActivity(intent);
            }
        });

        // insert contact
        Contact contact = new Contact();
        contact.setFirstName("fname1");
        contact.setLastName("lname1");
        contact.setGender(Gender.Male);
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
        
        // add email
        List<Object> emails = new ArrayList<Object>();
        Email email1 = new Email();
        email1.setContactId(contact.getId());
        email1.setEmail("b@abc.com");
        emails.add(email1);
        Email email2 = new Email();
        email2.setContactId(contact.getId());
        email2.setEmail("d@abc.com");
        emails.add(email2);
        sorma.insert(emails);
        
    	// delete contact
//    	sorma.delete(contact);
//    	sorma.delete(Phone.class, "contactId=?", new String[]{String.valueOf(contact.getId())});
    }
}