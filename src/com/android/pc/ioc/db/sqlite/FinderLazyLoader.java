package com.android.pc.ioc.db.sqlite;

import java.util.List;

import com.android.pc.ioc.db.table.Finder;
import com.android.pc.ioc.db.table.TableUtils;

/**
 * Author: wyouflf Date: 13-9-10 Time: 下午10:50
 */
public class FinderLazyLoader<T> {
	private Finder finderColumn;
	private Object finderValue;

	public FinderLazyLoader(Class<?> entityType, String fieldName, Object finderValue) {
		this.finderColumn = (Finder) TableUtils.getColumnOrId(entityType, fieldName);
		this.finderValue = finderValue;
	}

	public FinderLazyLoader(Finder finderColumn, Object finderValue) {
		this.finderColumn = finderColumn;
		this.finderValue = finderValue;
	}

	public List<T> getAllFromDb() {
		List<T> entities = null;
		if (finderColumn != null && finderColumn.db != null) {
			entities = finderColumn.db.findAll(Selector.from(finderColumn.getTargetEntityType()).where(finderColumn.getTargetColumnName(), "=", finderValue));
		}
		return entities;
	}

	public T getFirstFromDb() {
		T entity = null;
		if (finderColumn != null && finderColumn.db != null) {
			entity = finderColumn.db.findFirst(Selector.from(finderColumn.getTargetEntityType()).where(finderColumn.getTargetColumnName(), "=", finderValue));
		}
		return entity;
	}
}
