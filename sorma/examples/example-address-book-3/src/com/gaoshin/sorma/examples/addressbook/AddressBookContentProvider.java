package com.gaoshin.sorma.examples.addressbook;

import com.gaoshin.sorma.annotation.ContentProvider;
import com.gaoshin.sorma.annotation.SormaContentProvider;
import com.gaoshin.sorma.annotation.Upgrade;

@ContentProvider(
    mappingClasses = {
        Contact.class,
        Phone.class,
        Email.class
    },
    version = 3,
    upgrades = {
        @Upgrade(
            version = 2,
            SQLs = {
                "alter table contact add column gender text",
                "create index if not exists index_contact_phone on phone(number)",
            }
        ),
        @Upgrade(
            version = 3,
            SQLs = {
                "create table if not exists email ("
                        + " id INTEGER primary key autoincrement"
                        + ", contactId INTEGER "
                        + ", email text"
                        + ")",
            }
        ),
    })
public class AddressBookContentProvider extends SormaContentProvider {
}
