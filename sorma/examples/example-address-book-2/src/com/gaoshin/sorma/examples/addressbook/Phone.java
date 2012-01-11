package com.gaoshin.sorma.examples.addressbook;

import com.gaoshin.sorma.annotation.Table;

@Table(
		name="phone",	// table name
		keyColumn="id",	// primary key column name
		autoId=true,	// is primary key autoincrement?
		create={
			"create table if not exists phone (" 
				+ " id INTEGER primary key autoincrement"
				+ ", contactId INTEGER "
				+ ", number text"
				+ ", type text"
				+ ")",
		}
	)
public class Phone {
	private Integer id;
	private Integer contactId;
	private String number;
	private PhoneNumberType type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public PhoneNumberType getType() {
		return type;
	}

	public void setType(PhoneNumberType type) {
		this.type = type;
	}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
