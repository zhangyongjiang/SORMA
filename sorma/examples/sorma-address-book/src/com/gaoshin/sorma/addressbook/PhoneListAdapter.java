package com.gaoshin.sorma.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gaoshin.sorma.addressbook.PhoneView.DeleteListener;

public class PhoneListAdapter extends BaseAdapter {
    private List<Phone> phoneList = new ArrayList<Phone>();

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
        notifyDataSetChanged();
    }
    
    public List<Phone> getPhoneList() {
        return phoneList;
    }
    
    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return phoneList.get(position).getId().longValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhoneView phoneView = null;
        if(convertView != null) {
            phoneView = (PhoneView) convertView;
        }
        else {
            phoneView = new PhoneView(parent.getContext());
            phoneView.setOnDeleteListener(new DeleteListener() {
                @Override
                public void onDelete(View view, Phone phone) {
                    for(int i=0; i<phoneList.size(); i++) {
                        if(phoneList.get(i).getId() == phone.getId()) {
                            phoneList.remove(i);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            });
        }
        phoneView.setPhone(phoneList.get(position));
        
        return phoneView;
    }

}
