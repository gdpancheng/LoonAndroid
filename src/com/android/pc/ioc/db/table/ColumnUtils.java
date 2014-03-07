
package com.android.pc.ioc.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.db.annotation.Check;
import com.android.pc.ioc.db.annotation.Column;
import com.android.pc.ioc.db.annotation.Finder;
import com.android.pc.ioc.db.annotation.Foreign;
import com.android.pc.ioc.db.annotation.Id;
import com.android.pc.ioc.db.annotation.NotNull;
import com.android.pc.ioc.db.annotation.Transient;
import com.android.pc.ioc.db.annotation.Unique;
import com.android.pc.ioc.db.sqlite.FinderLazyLoader;
import com.android.pc.ioc.db.sqlite.ForeignLazyLoader;

public class ColumnUtils {

    private ColumnUtils() {
    }

    public static Method getColumnGetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        Method getMethod = null;
        if (field.getType() == boolean.class) {
            getMethod = getBooleanColumnGetMethod(entityType, fieldName);
        }
        if (getMethod == null) {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                getMethod = entityType.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
            	ApplicationBean.logger.d(methodName + " not exist");
            }
        }

        if (getMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnGetMethod(entityType.getSuperclass(), field);
        }
        return getMethod;
    }

    public static Method getColumnSetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        Method setMethod = null;
        if (field.getType() == boolean.class) {
            setMethod = getBooleanColumnSetMethod(entityType, field);
        }
        if (setMethod == null) {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                setMethod = entityType.getDeclaredMethod(methodName, field.getType());
            } catch (NoSuchMethodException e) {
            	ApplicationBean.logger.d(methodName + " not exist");
            }
        }

        if (setMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnSetMethod(entityType.getSuperclass(), field);
        }
        return setMethod;
    }


    public static String getColumnNameByField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !TextUtils.isEmpty(column.column())) {
            return column.column();
        }

        Id id = field.getAnnotation(Id.class);
        if (id != null && !TextUtils.isEmpty(id.column())) {
            return id.column();
        }

        Foreign foreign = field.getAnnotation(Foreign.class);
        if (foreign != null && !TextUtils.isEmpty(foreign.column())) {
            return foreign.column();
        }

        Finder finder = field.getAnnotation(Finder.class);
        if (finder != null) {
            return field.getName();
        }

        return field.getName();
    }

    public static String getForeignColumnNameByField(Field field) {

        Foreign foreign = field.getAnnotation(Foreign.class);
        if (foreign != null) {
            return foreign.foreign();
        }

        return field.getName();
    }

    public static Object getColumnDefaultValue(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !TextUtils.isEmpty(column.defaultValue())) {
            return valueStr2SimpleTypeFieldValue(field.getType(), column.defaultValue());
        }
        return null;
    }

    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    public static boolean isForeign(Field field) {
        return field.getAnnotation(Foreign.class) != null;
    }

    public static boolean isFinder(Field field) {
        return field.getAnnotation(Finder.class) != null;
    }

    public static boolean isSimpleColumnType(Field field) {
        Class<?> clazz = field.getType();
        return isSimpleColumnType(clazz);
    }

    public static boolean isSimpleColumnType(Class columnType) {
        return columnType.isPrimitive() ||
                columnType.equals(String.class) ||
                columnType.equals(Integer.class) ||
                columnType.equals(Long.class) ||
                columnType.equals(Date.class) ||
                columnType.equals(java.sql.Date.class) ||
                columnType.equals(Boolean.class) ||
                columnType.equals(Float.class) ||
                columnType.equals(Double.class) ||
                columnType.equals(Byte.class) ||
                columnType.equals(Short.class) ||
                columnType.equals(CharSequence.class) ||
                columnType.equals(Character.class);
    }

    public static boolean isUnique(Field field) {
        return field.getAnnotation(Unique.class) != null;
    }

    public static boolean isNotNull(Field field) {
        return field.getAnnotation(NotNull.class) != null;
    }

    /**
     * @param field
     * @return check.value or null
     */
    public static String getCheck(Field field) {
        Check check = field.getAnnotation(Check.class);
        if (check != null) {
            return check.value();
        } else {
            return null;
        }
    }

    public static Object valueStr2SimpleTypeFieldValue(Class columnFieldType, final String valueStr) {
        Object value = null;
        if (isSimpleColumnType(columnFieldType) && valueStr != null) {
            if (columnFieldType.equals(String.class) || columnFieldType.equals(CharSequence.class)) {
                value = valueStr;
            } else if (columnFieldType.equals(int.class) || columnFieldType.equals(Integer.class)) {
                value = Integer.valueOf(valueStr);
            } else if (columnFieldType.equals(long.class) || columnFieldType.equals(Long.class)) {
                value = Long.valueOf(valueStr);
            } else if (columnFieldType.equals(java.sql.Date.class)) {
                value = new java.sql.Date(Long.valueOf(valueStr));
            } else if (columnFieldType.equals(Date.class)) {
                value = new Date(Long.valueOf(valueStr));
            } else if (columnFieldType.equals(boolean.class) || columnFieldType.equals(Boolean.class)) {
                value = ColumnUtils.convert2Boolean(valueStr);
            } else if (columnFieldType.equals(float.class) || columnFieldType.equals(Float.class)) {
                value = Float.valueOf(valueStr);
            } else if (columnFieldType.equals(double.class) || columnFieldType.equals(Double.class)) {
                value = Double.valueOf(valueStr);
            } else if (columnFieldType.equals(byte.class) || columnFieldType.equals(Byte.class)) {
                value = Byte.valueOf(valueStr);
            } else if (columnFieldType.equals(short.class) || columnFieldType.equals(Short.class)) {
                value = Short.valueOf(valueStr);
            } else if (columnFieldType.equals(char.class) || columnFieldType.equals(Character.class)) {
                value = valueStr.charAt(0);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static Class<?> getForeignEntityType(com.android.pc.ioc.db.table.Foreign foreignColumn) {
        Class<?> result = (Class<?>) foreignColumn.getColumnField().getType();
        if (result.equals(ForeignLazyLoader.class) || result.equals(List.class)) {
            result = (Class<?>) ((ParameterizedType) foreignColumn.getColumnField().getGenericType()).getActualTypeArguments()[0];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Class<?> getFinderTargetEntityType(com.android.pc.ioc.db.table.Finder finderColumn) {
        Class<?> result = (Class<?>) finderColumn.getColumnField().getType();
        if (result.equals(FinderLazyLoader.class) || result.equals(List.class)) {
            result = (Class<?>) ((ParameterizedType) finderColumn.getColumnField().getGenericType()).getActualTypeArguments()[0];
        }
        return result;
    }

    public static Boolean convert2Boolean(final Object value) {
        if (value != null) {
            String valueStr = value.toString();
            return valueStr.length() == 1 ? "1".equals(valueStr) : Boolean.valueOf(valueStr);
        }
        return false;
    }

    public static Object convert2DbColumnValueIfNeeded(final Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                return ((Boolean) value) ? 1 : 0;
            } else if (value instanceof java.sql.Date) {
                return ((java.sql.Date) value).getTime();
            } else if (value instanceof Date) {
                return ((Date) value).getTime();
            }
        }
        return value;
    }

    public static String fieldType2DbType(Class<?> fieldType) {
        if (fieldType.equals(int.class) ||
                fieldType.equals(Integer.class) ||
                fieldType.equals(boolean.class) ||
                fieldType.equals(Boolean.class) ||
                fieldType.equals(Date.class) ||
                fieldType.equals(java.sql.Date.class) ||
                fieldType.equals(long.class) ||
                fieldType.equals(Long.class) ||
                fieldType.equals(byte.class) ||
                fieldType.equals(Byte.class) ||
                fieldType.equals(short.class) ||
                fieldType.equals(Short.class)) {
            return "INTEGER";
        } else if (fieldType.equals(float.class) ||
                fieldType.equals(Float.class) ||
                fieldType.equals(double.class) ||
                fieldType.equals(Double.class)) {
            return "REAL";
        }
        return "TEXT";
    }

    private static boolean isStartWithIs(final String fieldName) {
        return fieldName != null && fieldName.startsWith("is");
    }

    private static Method getBooleanColumnGetMethod(Class<?> entityType, final String fieldName) {
        String methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (isStartWithIs(fieldName)) {
            methodName = fieldName;
        }
        try {
            return entityType.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
        	ApplicationBean.logger.d(methodName + " not exist");
        }
        return null;
    }

    private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        String methodName = null;
        if (isStartWithIs(field.getName())) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
        } else {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        try {
            return entityType.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
        	ApplicationBean.logger.d(methodName + " not exist");
        }
        return null;
    }

}
