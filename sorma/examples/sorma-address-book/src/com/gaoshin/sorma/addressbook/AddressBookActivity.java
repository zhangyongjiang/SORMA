package com.gaoshin.sorma.addressbook;

import android.app.Activity;
import android.os.Bundle;

import com.gaoshin.sorma.SORMA;

public class AddressBookActivity extends Activity {
    protected SORMA sorma;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sorma = SORMA.getInstance(getApplicationContext(), AddressBookContentProvider.class);
    }

}