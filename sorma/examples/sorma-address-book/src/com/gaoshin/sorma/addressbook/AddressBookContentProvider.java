package com.gaoshin.sorma.addressbook;

import com.gaoshin.sorma.annotation.ContentProvider;
import com.gaoshin.sorma.annotation.SormaContentProvider;

@ContentProvider(
        version = 1,
        mappingClasses = {
                Contact.class,
                Phone.class,
                Configuration.class,
        }
)
public class AddressBookContentProvider extends SormaContentProvider {
}
