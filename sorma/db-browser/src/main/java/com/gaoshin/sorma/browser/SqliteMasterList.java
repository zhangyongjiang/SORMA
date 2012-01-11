package com.gaoshin.sorma.browser;

import java.util.ArrayList;
import java.util.List;

import com.gaoshin.sorma.browser.SqliteMaster;

public class SqliteMasterList {
    private List<SqliteMaster> list = new ArrayList<SqliteMaster>();

    public void setList(List<SqliteMaster> list) {
        this.list = list;
    }

    public List<SqliteMaster> getList() {
        return list;
    }
}
