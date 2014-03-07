package com.android.pc.ioc.db.sqlite;

import java.util.List;

import com.android.pc.ioc.db.table.Foreign;
import com.android.pc.ioc.db.table.TableUtils;

public class ForeignLazyLoader<T> {
	private Foreign foreignColumn;
	private Object columnValue;

	public ForeignLazyLoader(Class<?> entityType, String columnName, Object columnValue) {
		this.foreignColumn = (Foreign) TableUtils.getColumnOrId(entityType, columnName);
		this.columnValue = columnValue;
	}

	public ForeignLazyLoader(Foreign foreignColumn, Object columnValue) {
		this.foreignColumn = foreignColumn;
		this.columnValue = columnValue;
	}

	public List<T> getAllFromDb() {
		List<T> entities = null;
		if (foreignColumn != null && foreignColumn.db != null) {
			entities = foreignColumn.db.findAll(Selector.from(foreignColumn.getForeignEntityType()).where(foreignColumn.getForeignColumnName(), "=", columnValue));
		}
		return entities;
	}

	public T getFirstFromDb() {
		T entity = null;
		if (foreignColumn != null && foreignColumn.db != null) {
			entity = foreignColumn.db.findFirst(Selector.from(foreignColumn.getForeignEntityType()).where(foreignColumn.getForeignColumnName(), "=", columnValue));
		}
		return entity;
	}

	public Object getColumnValue() {
		return columnValue;
	}
}
