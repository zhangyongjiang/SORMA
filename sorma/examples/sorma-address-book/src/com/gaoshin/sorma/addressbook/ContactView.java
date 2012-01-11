package com.gaoshin.sorma.addressbook;

import android.content.Context;
import android.widget.TextView;

public class ContactView extends TextView {
    private Contact contact;

    public ContactView(Context context) {
        super(context);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        setText(contact.toString());
    }
}
