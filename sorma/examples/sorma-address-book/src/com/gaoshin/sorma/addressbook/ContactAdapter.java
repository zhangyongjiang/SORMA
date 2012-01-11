package com.gaoshin.sorma.addressbook;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ContactAdapter extends BaseAdapter {
    private Contact[] contacts = new Contact[0];

    @Override
    public int getCount() {
        return contacts.length;
    }

    @Override
    public Object getItem(int position) {
        return contacts[position];
    }

    @Override
    public long getItemId(int position) {
        return contacts[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = contacts[position];
        ContactView contactView = null;
        if(convertView != null) {
            contactView = (ContactView) convertView;
        }
        else {
            contactView = new ContactView(parent.getContext());
        }
        contactView.setContact(contact);
        return contactView;
    }

    public void applyData(Contact[] allContacts) {
        this.contacts = allContacts;
        notifyDataSetChanged();
    }
}
