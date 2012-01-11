package com.gaoshin.sorma.addressbook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gaoshin.sorma.SORMA;

public class PhoneView extends LinearLayout {
    private Phone phone;
    private DeleteListener deleteListener;
    private SORMA sorma;
    private EditText phoneNumberView;
    private EditText phoneTypeView;

    public PhoneView(Context context) {
        super(context);
        sorma = SORMA.getInstance(context, AddressBookContentProvider.class);
        setOrientation(LinearLayout.HORIZONTAL);
        setupPhoneTypeView();
        setupPhoneNumberView();
        setupDeleteButton();
    }

    private void setupDeleteButton() {
        Button btn = new Button(getContext());
        btn.setText("X");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(v, phone);
            }
        });
        addView(btn);
    }

    private void setupPhoneNumberView() {
        phoneNumberView = new EditText(getContext());
        addView(phoneNumberView);
        phoneNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                phone.setNumber(phoneNumberView.getText().toString());
            }
        });
    }

    private void setupPhoneTypeView() {
        phoneTypeView = new EditText(getContext());
        addView(phoneTypeView);
        phoneTypeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                phone.setType(phoneTypeView.getText().toString());
            }
        });
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
        phoneNumberView.setText(phone.getNumber());
        phoneTypeView.setText(phone.getType());
    }

    public void setOnDeleteListener(DeleteListener listener) {
        this.deleteListener = listener;
    }
    
    public static interface DeleteListener {
        void onDelete(View view, Phone phone);
    }
}
