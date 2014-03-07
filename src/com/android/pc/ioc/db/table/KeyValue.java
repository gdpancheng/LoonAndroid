
package com.android.pc.ioc.db.table;

public class KeyValue {
    private String key;
    private Object value;

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = ColumnUtils.convert2DbColumnValueIfNeeded(value);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
