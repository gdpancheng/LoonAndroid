
package com.android.pc.ioc.db.sqlite;

import android.database.Cursor;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.db.table.Column;
import com.android.pc.ioc.db.table.DbModel;
import com.android.pc.ioc.db.table.Finder;
import com.android.pc.ioc.db.table.Foreign;
import com.android.pc.ioc.db.table.Table;

public class CursorUtils {

    public static <T> T getEntity(DbUtils db, Cursor cursor, Class<T> entityType, long findCacheSequence) {
        if (db == null || cursor == null) return null;

        EntityTempCache.setSeq(findCacheSequence);
        try {
            Table table = Table.get(entityType);
            int idIndex = cursor.getColumnIndex(table.getId().getColumnName());
            String idStr = cursor.getString(idIndex);
            T entity = EntityTempCache.get(entityType, idStr);
            if (entity == null) {
                entity = entityType.newInstance();
                EntityTempCache.put(entity, idStr);
            } else {
                return entity;
            }
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                Column column = table.columnMap.get(columnName);
                if (column != null) {
                    if (column instanceof Foreign) {
                        Foreign foreign = (Foreign) column;
                        if (foreign.getFieldValue(entity) == null) {
                            foreign.db = db;
                            foreign.setValue2Entity(entity, cursor.getString(i));
                        }
                    } else {
                        column.setValue2Entity(entity, cursor.getString(i));
                    }
                } else if (columnName.equals(table.getId().getColumnName())) {
                    table.getId().setValue2Entity(entity, cursor.getString(i));
                }
            }

            for (Column column : table.columnMap.values()) {
                if (column instanceof Finder) {
                    Finder finder = (Finder) column;
                    if (finder.getFieldValue(entity) == null) {
                        finder.db = db;
                        finder.setValue2Entity(entity, null);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
        	Ioc.getIoc().getLogger().e(e);
        }

        return null;
    }

    public static DbModel getDbModel(Cursor cursor) {
        DbModel result = null;
        if (cursor != null) {
            result = new DbModel();
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                result.add(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        return result;
    }

    public static class FindCacheSequence {
        private static long seq = 0;
        private static final String FOREIGN_LAZY_LOADER_CLASS_NAME = ForeignLazyLoader.class.getName();
        private static final String FINDER_LAZY_LOADER_CLASS_NAME = FinderLazyLoader.class.getName();

        public static long getSeq() {
            String findMethodCaller = Thread.currentThread().getStackTrace()[4].getClassName();
            if (!findMethodCaller.equals(FOREIGN_LAZY_LOADER_CLASS_NAME) && !findMethodCaller.equals(FINDER_LAZY_LOADER_CLASS_NAME)) {
                ++seq;
            }
            return seq;
        }
    }

    private static class EntityTempCache {
        private EntityTempCache() {
        }

        /**
         * k1: entityType;
         * k2: idValue
         * value: entity
         */
        private static final DoubleKeyValueMap<Class, String, Object> cache = new DoubleKeyValueMap<Class, String, Object>();

        private static long seq = 0;

        public static void put(Object entity, String idStr) {
            cache.put(entity.getClass(), idStr, entity);
        }

        @SuppressWarnings("unchecked")
        public static <T> T get(Class<T> entityType, String idStr) {
            return (T) cache.get(entityType, idStr);
        }

        public static void setSeq(long seq) {
            if (EntityTempCache.seq != seq) {
                cache.clear();
                EntityTempCache.seq = seq;
            }
        }
    }
}
