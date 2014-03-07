
package com.android.pc.ioc.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.android.pc.ioc.app.ApplicationBean;

public class Column {

    protected String columnName;
    private Object defaultValue;

    protected Method getMethod;
    protected Method setMethod;

    protected Field columnField;

    protected Column(Class entityType, Field field) {
        this.columnField = field;
        this.columnName = ColumnUtils.getColumnNameByField(field);
        this.defaultValue = ColumnUtils.getColumnDefaultValue(field);
        this.getMethod = ColumnUtils.getColumnGetMethod(entityType, field);
        this.setMethod = ColumnUtils.getColumnSetMethod(entityType, field);
    }

    @SuppressWarnings("unchecked")
    public void setValue2Entity(Object entity, String valueStr) {

        Object value = null;
        if (valueStr != null) {
            Class columnType = columnField.getType();
            value = ColumnUtils.valueStr2SimpleTypeFieldValue(columnType, valueStr);
        }

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, value == null ? defaultValue : value);
            } catch (Exception e) {
            	ApplicationBean.logger.e(e);
            }
        } else {
            try {
                this.columnField.setAccessible(true);
                this.columnField.set(entity, value == null ? defaultValue : value);
            } catch (Exception e) {
            	ApplicationBean.logger.e(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object getColumnValue(Object entity) {
        Object resultObj = null;
        if (entity != null) {
            if (getMethod != null) {
                try {
                    resultObj = getMethod.invoke(entity);
                } catch (Exception e) {
                	ApplicationBean.logger.e(e);
                }
            } else {
                try {
                    this.columnField.setAccessible(true);
                    resultObj = this.columnField.get(entity);
                } catch (Exception e) {
                	ApplicationBean.logger.e(e);
                }
            }
        }
        return ColumnUtils.convert2DbColumnValueIfNeeded(resultObj);
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Field getColumnField() {
        return columnField;
    }

    public String getColumnDbType() {
        return ColumnUtils.fieldType2DbType(columnField.getType());
    }
}
