package com.gaoshin.sorma.addressbook;

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
	private Long contactId;
	private String number;
	private String type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    @Override
    public String toString() {
        return number + "/" + type;
    }
}
