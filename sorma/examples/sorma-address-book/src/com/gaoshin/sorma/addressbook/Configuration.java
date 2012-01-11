package com.gaoshin.sorma.addressbook;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;

@Table (
        name="configuration",
        keyColumn="_id",
        autoId=true,
        create={
                "create table configuration (" +
                        "_id INTEGER primary key autoincrement " +
                        ", _key text " +
                        ", _value text " +
                ")"
        },
        columns = {
                @Column(field="id",     name="_id"),
                @Column(field="key",    name="_key"),
                @Column(field="value",  name="_value"),
        }
)
public class Configuration {
    private Integer id;
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
