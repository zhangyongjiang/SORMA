package com.gaoshin.sorma.examples.addressbook;

import com.gaoshin.sorma.annotation.ContentProvider;
import com.gaoshin.sorma.annotation.SormaContentProvider;
import com.gaoshin.sorma.annotation.Upgrade;

@ContentProvider(
    mappingClasses = {
        Contact.class,
        Phone.class
    },
    version = 2,
    upgrades = {
        @Upgrade(
            version = 2,
            SQLs = {
                "alter table contact add column gender text",
                "create index if not exists index_contact_phone on phone(number)",
            }
        )
    })
public class AddressBookContentProvider extends SormaContentProvider {
}
