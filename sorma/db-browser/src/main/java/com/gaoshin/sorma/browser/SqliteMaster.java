/**
 * Copyright (c) 2011 SORMA
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Author: sorma@gaoshin.com
 */
package com.gaoshin.sorma.browser;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;

@Table(
    mappingClass = SqliteMaster.class,
    name = "sqlite_master",
    columns = {
        @Column(field = "type"),
        @Column(field = "name"),
        @Column(field = "tableName", name="tbl_name"),
        @Column(field = "rootpage"),
        @Column(field = "sql")
    }
)
public class SqliteMaster {
    private String type;
    private String name;
    private String tableName;
    private Integer rootpage;
    private String sql;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getRootpage() {
        return rootpage;
    }

    public void setRootpage(Integer rootpage) {
        this.rootpage = rootpage;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
