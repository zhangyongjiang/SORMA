package com.gaoshin.sorma.addressbook;

import java.util.ArrayList;
import java.util.List;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;

@Table(
    name = "contacts",
    keyColumn = "_id",
    autoId = true,
    create = {
            "create table contacts (" +
                    " _id INTEGER primary key autoincrement" +
                    ", contact_id bigint " +
                    ", display_name text " +
            ")"
    },
    columns={
            @Column(field = "id",               name="_id"),
            @Column(field = "contactId",        name="contact_id"),
            @Column(field = "displayName",      name="display_name"),
    }
)
public class Contact {
    private Long id;
    private Long contactId;
	private String displayName;
	private List<Phone> phoneList = new ArrayList<Phone>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getDisplayName() {
        return displayName;
    }

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    public List<Phone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(displayName);
        for(Phone phone : phoneList) {
            sb.append("\n\t").append(phone.getType()).append(" ").append(phone.getNumber());
        }
        return sb.toString();
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }
}