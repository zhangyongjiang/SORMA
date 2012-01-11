package com.gaoshin.sorma.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gaoshin.sorma.addressbook.PhoneView.DeleteListener;

public class EditContactActivity extends AddressBookActivity {
    private Contact contact;
    private List<PhoneView> phoneViewList = new ArrayList<PhoneView>();
    private EditText displayNameView;
    private LinearLayout phoneListview;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        phoneListview = (LinearLayout) findViewById(R.id.phoneListView);
        getContact();
        setupDeleteButton();
        setupAddPhoneButton();
        setupSaveButton();
    }
    
    private void setupSaveButton() {
        Button btn = (Button) findViewById(R.id.saveBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }
    
    private void save() {
        if(contact.getId() == null) {
            sorma.insert(contact);
            for(Phone phone : contact.getPhoneList()) {
                phone.setContactId(contact.getId());
            }
            sorma.insert(contact.getPhoneList());
        }
        else {
            sorma.update(contact);
            sorma.delete(Phone.class, "contactId=" + contact.getId(), null);
            for(Phone phone : contact.getPhoneList()) {
                phone.setContactId(contact.getId());
                phone.setId(null);
                sorma.insert(phone);
            }
        }
        Button btn = (Button) findViewById(R.id.deleteBtn);
        btn.setEnabled(true);
    }

    private void setupAddPhoneButton() {
        Button btn = (Button) findViewById(R.id.addPhoneBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phone phone = new Phone();
                phone.setType("type");
                phone.setNumber("number");
                contact.getPhoneList().add(phone);
                PhoneView pv = new PhoneView(getBaseContext());
                pv.setPhone(phone);
                pv.setOnDeleteListener(new DeleteListener() {
                    @Override
                    public void onDelete(View view, Phone phone) {
                        for(int i=0; i<contact.getPhoneList().size(); i++) {
                            Phone p = contact.getPhoneList().get(i);
                            if(p.hashCode() == phone.hashCode()) {
                                phoneListview.removeView(phoneViewList.get(i));
                                phoneViewList.remove(i);
                                contact.getPhoneList().remove(i);
                                break;
                            }
                        }
                    }
                });
                phoneListview.addView(pv);
                phoneViewList.add(pv);
            }
        });
    }

    private void setupDeleteButton() {
        Button btn = (Button) findViewById(R.id.deleteBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("Are you sure?");
                dialog.setPositiveButton("Delete It!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sorma.delete(Contact.class, "_id=" + contact.getId(), null);
                        sorma.delete(Phone.class, "contactId=" + contact.getId(), null);
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void getContact() {
        long contactId = getIntent().getLongExtra("contactId", -1);
        if(contactId == -1) {
            contact = new Contact();
            Button btn = (Button) findViewById(R.id.deleteBtn);
            btn.setEnabled(false);
        }
        else {
            contact = sorma.get(Contact.class, "_id=?", new String[]{String.valueOf(contactId)});
            List<Phone> phoneList = sorma.select(Phone.class, "contactId=?", new String[]{String.valueOf(contactId)});
            contact.setPhoneList(phoneList);
        }
        displayNameView = (EditText) findViewById(R.id.displayName);
        displayNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                contact.setDisplayName(displayNameView.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText displayNameView = (EditText) findViewById(R.id.displayName);
        displayNameView.setText(contact.getDisplayName());
        displayPhoneList();
    }

    private void displayPhoneList() {
        for(Phone phone : contact.getPhoneList()) {
            PhoneView pv = new PhoneView(getBaseContext());
            pv.setPhone(phone);
            pv.setOnDeleteListener(new DeleteListener() {
                @Override
                public void onDelete(View view, Phone phone) {
                    for(int i=0; i<contact.getPhoneList().size(); i++) {
                        Phone p = contact.getPhoneList().get(i);
                        if(p.hashCode() == phone.hashCode()) {
                            phoneListview.removeView(phoneViewList.get(i));
                            phoneViewList.remove(i);
                            contact.getPhoneList().remove(i);
                            break;
                        }
                    }
                }
            });
            phoneListview.addView(pv);
            phoneViewList.add(pv);
        }
    }
}
