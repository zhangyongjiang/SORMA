package com.gaoshin.sorma.examples.addressbook;

import com.gaoshin.sorma.annotation.Table;

@Table(
        name = "email", // table name
        keyColumn = "id", // primary key column name
        autoId = true, // is primary key autoincrement?
        create = {
                "create table if not exists email ("
                        + " id INTEGER primary key autoincrement"
                        + ", contactId INTEGER "
                        + ", email text"
                        + ")",
        })
public class Email {
    private Integer id;
    private Integer contactId;
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
