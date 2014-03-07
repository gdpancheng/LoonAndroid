
package com.android.pc.ioc.db.table;

import java.util.HashMap;


public class Table {

    private String tableName;

    private Id id;

    /**
     * key: columnName
     */
    public final HashMap<String, Column> columnMap;

    /**
     * key: className
     */
    private static final HashMap<String, Table> tableMap = new HashMap<String, Table>();

    private Table(Class entityType) {
        this.tableName = TableUtils.getTableName(entityType);
        this.id = TableUtils.getId(entityType);
        this.columnMap = TableUtils.getColumnMap(entityType);
    }

    public static synchronized Table get(Class entityType) {

        Table table = tableMap.get(entityType.getCanonicalName());
        if (table == null) {
            table = new Table(entityType);
            tableMap.put(entityType.getCanonicalName(), table);
        }

        return table;
    }

    public String getTableName() {
        return tableName;
    }

    public Id getId() {
        return id;
    }

    private boolean checkDatabase;

    public boolean isCheckDatabase() {
        return checkDatabase;
    }

    public void setCheckDatabase(boolean checkDatabase) {
        this.checkDatabase = checkDatabase;
    }

}
