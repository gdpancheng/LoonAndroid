package com.android.pc.ioc.db.sqlite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.db.table.DbModel;
import com.android.pc.ioc.db.table.Id;
import com.android.pc.ioc.db.table.KeyValue;
import com.android.pc.ioc.db.table.Table;
import com.android.pc.ioc.db.table.TableUtils;

public class DbUtils {

	// *************************************** create instance ****************************************************
	/**
	 * key: dbName
	 */
	private static HashMap<String, DbUtils> daoMap = new HashMap<String, DbUtils>();

	private SQLiteDatabase database;
	private DaoConfig config;
	private boolean debug = false;
	private boolean allowTransaction = false;

	private DbUtils(DaoConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("daoConfig may not be null");
		}

		if (config.getContext() == null) {
			throw new IllegalArgumentException("context mey not be null");
		}

		String sdCardPath = config.getSdCardPath();
		if (TextUtils.isEmpty(sdCardPath)) {
			this.database = new SQLiteDbHelper(config).getWritableDatabase();
		} else {
			this.database = createDbFileOnSDCard(config);
		}
		this.config = config;
	}

	private synchronized static DbUtils getInstance(DaoConfig daoConfig) {
		DbUtils dao = daoMap.get(daoConfig.getDbName());
		if (dao == null) {
			dao = new DbUtils(daoConfig);
			daoMap.put(daoConfig.getDbName(), dao);
		} else {
			dao.config = daoConfig;
		}
		return dao;
	}

	public static DbUtils create(Context context) {
		DaoConfig config = new DaoConfig(context);
		return getInstance(config);
	}

	public static DbUtils create(Context context, String dbName) {
		DaoConfig config = new DaoConfig(context);
		config.setDbName(dbName);
		return getInstance(config);
	}

	public static DbUtils create(Context context, String sdCardPath, String dbName) {
		DaoConfig config = new DaoConfig(context);
		config.setSdCardPath(sdCardPath);
		config.setDbName(dbName);
		return getInstance(config);
	}

	public static DbUtils create(Context context, String dbName, int dbVersion, DbUpgradeListener dbUpgradeListener) {
		DaoConfig config = new DaoConfig(context);
		config.setDbName(dbName);
		config.setDbVersion(dbVersion);
		config.setDbUpgradeListener(dbUpgradeListener);
		return getInstance(config);
	}

	public static DbUtils create(Context context, String sdCardPath, String dbName, int dbVersion, DbUpgradeListener dbUpgradeListener) {
		DaoConfig config = new DaoConfig(context);
		config.setSdCardPath(sdCardPath);
		config.setDbName(dbName);
		config.setDbVersion(dbVersion);
		config.setDbUpgradeListener(dbUpgradeListener);
		return getInstance(config);
	}

	public static DbUtils create(DaoConfig daoConfig) {
		return getInstance(daoConfig);
	}

	public DbUtils configDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	public DbUtils configAllowTransaction(boolean allowTransaction) {
		this.allowTransaction = allowTransaction;
		return this;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public String getSdCardPath() {
		return config.getSdCardPath();
	}

	// *********************************************** operations ********************************************************

	public void saveOrUpdate(Object entity) {
		try {
			beginTransaction();

			saveOrUpdateWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void saveOrUpdateAll(List<?> entities) {
		try {
			beginTransaction();

			for (Object entity : entities) {
				saveOrUpdateWithoutTransaction(entity);
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void replace(Object entity) {
		try {
			beginTransaction();

			replaceWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void replaceAll(List<?> entities) {
		try {
			beginTransaction();

			for (Object entity : entities) {
				replaceWithoutTransaction(entity);
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void save(Object entity) {
		try {
			beginTransaction();

			saveWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void saveAll(List<?> entities) {
		try {
			beginTransaction();

			for (Object entity : entities) {
				saveWithoutTransaction(entity);
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public boolean saveBindingId(Object entity) {
		boolean result = false;
		try {
			beginTransaction();

			result = saveBindingIdWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
		return result;
	}

	public void saveBindingIdAll(List<?> entities) {
		try {
			beginTransaction();

			for (Object entity : entities) {
				if (!saveBindingIdWithoutTransaction(entity)) {
					ApplicationBean.logger.e("saveBindingId error, transaction will not commit!");
				}
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void delete(Object entity) {
		if (!tableIsExist(entity.getClass()))
			return;
		try {
			beginTransaction();

			deleteWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void deleteAll(List<?> entities) {
		if (entities == null || entities.size() < 1 || !tableIsExist(entities.get(0).getClass()))
			return;
		try {
			beginTransaction();

			for (Object entity : entities) {
				deleteWithoutTransaction(entity);
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void deleteById(Class<?> entityType, Object idValue) {
		if (!tableIsExist(entityType))
			return;
		try {
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entityType, idValue));

			setTransactionSuccessful();
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		} finally {
			endTransaction();
		}
	}

	public void delete(Class<?> entityType, WhereBuilder whereBuilder) {
		if (!tableIsExist(entityType))
			return;
		try {
			beginTransaction();

			SqlInfo sql = SqlInfoBuilder.buildDeleteSqlInfo(entityType, whereBuilder);
			execNonQuery(sql);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void update(Object entity) {
		if (!tableIsExist(entity.getClass()))
			return;
		try {
			beginTransaction();

			updateWithoutTransaction(entity);

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void updateAll(List<?> entities) {
		if (entities == null || entities.size() < 1 || !tableIsExist(entities.get(0).getClass()))
			return;
		try {
			beginTransaction();

			for (Object entity : entities) {
				updateWithoutTransaction(entity);
			}

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	public void update(Object entity, WhereBuilder whereBuilder) {
		if (!tableIsExist(entity.getClass()))
			return;
		try {
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder));

			setTransactionSuccessful();
		} finally {
			endTransaction();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T findById(Class<T> entityType, Object idValue) {
		if (!tableIsExist(entityType))
			return null;

		Id id = Table.get(entityType).getId();
		Selector selector = Selector.from(entityType).where(id.getColumnName(), "=", idValue);

		String sql = selector.limit(1).toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null) {
			return (T) obj;
		}

		Cursor cursor = execQuery(sql);
		try {
			if (cursor.moveToNext()) {
				T entity = (T) CursorUtils.getEntity(this, cursor, entityType, seq);
				findTempCache.put(sql, entity);
				return entity;
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T findFirst(Selector selector) {
		if (!tableIsExist(selector.getEntityType()))
			return null;

		String sql = selector.limit(1).toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null) {
			return (T) obj;
		}

		Cursor cursor = execQuery(sql);
		try {
			if (cursor.moveToNext()) {
				T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityType(), seq);
				findTempCache.put(sql, entity);
				return entity;
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	public <T> T findFirst(Object entity) {
		if (!tableIsExist(entity.getClass()))
			return null;
		Selector selector = Selector.from(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null) {
			WhereBuilder wb = WhereBuilder.b();
			for (KeyValue keyValue : entityKvList) {
				wb.append(keyValue.getKey(), "=", keyValue.getValue());
			}
			selector.where(wb);
		}
		return findFirst(selector);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Selector selector) {
		if (!tableIsExist(selector.getEntityType()))
			return null;

		String sql = selector.toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null) {
			return (List<T>) obj;
		}

		Cursor cursor = execQuery(sql);
		List<T> result = new ArrayList<T>();
		try {
			while (cursor.moveToNext()) {
				T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityType(), seq);
				result.add(entity);
			}
			findTempCache.put(sql, result);
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return result;
	}

	public <T> List<T> findAll(Object entity) {
		if (!tableIsExist(entity.getClass()))
			return null;
		Selector selector = Selector.from(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null) {
			WhereBuilder wb = WhereBuilder.b();
			for (KeyValue keyValue : entityKvList) {
				wb.append(keyValue.getKey(), "=", keyValue.getValue());
			}
			selector.where(wb);
		}
		return findAll(selector);
	}

	public DbModel findDbModelFirst(SqlInfo sqlInfo) {
		Cursor cursor = execQuery(sqlInfo);
		try {
			if (cursor.moveToNext()) {
				return CursorUtils.getDbModel(cursor);
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	public DbModel findDbModelFirst(DbModelSelector selector) {
		if (!tableIsExist(selector.getEntityType()))
			return null;
		Cursor cursor = execQuery(selector.limit(1).toString());
		try {
			if (cursor.moveToNext()) {
				return CursorUtils.getDbModel(cursor);
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	public List<DbModel> findDbModelAll(SqlInfo sqlInfo) {
		Cursor cursor = execQuery(sqlInfo);
		List<DbModel> dbModelList = new ArrayList<DbModel>();
		try {
			while (cursor.moveToNext()) {
				dbModelList.add(CursorUtils.getDbModel(cursor));
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return dbModelList;
	}

	public List<DbModel> findDbModelAll(DbModelSelector selector) {
		if (!tableIsExist(selector.getEntityType()))
			return null;
		Cursor cursor = execQuery(selector.toString());
		List<DbModel> dbModelList = new ArrayList<DbModel>();
		try {
			while (cursor.moveToNext()) {
				dbModelList.add(CursorUtils.getDbModel(cursor));
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
		return dbModelList;
	}

	// ******************************************** config ******************************************************

	public static class DaoConfig {
		private Context context;
		private String dbName = "xUtils.db"; // default db name
		private int dbVersion = 1;
		private DbUpgradeListener dbUpgradeListener;

		private String sdCardPath;

		public DaoConfig(Context context) {
			this.context = context;
		}

		public Context getContext() {
			return context;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public int getDbVersion() {
			return dbVersion;
		}

		public void setDbVersion(int dbVersion) {
			this.dbVersion = dbVersion;
		}

		public DbUpgradeListener getDbUpgradeListener() {
			return dbUpgradeListener;
		}

		public void setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
			this.dbUpgradeListener = dbUpgradeListener;
		}

		public String getSdCardPath() {
			return sdCardPath;
		}

		public void setSdCardPath(String sdCardPath) {
			this.sdCardPath = sdCardPath;
		}
	}

	public interface DbUpgradeListener {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}

	private class SQLiteDbHelper extends SQLiteOpenHelper {

		private DbUpgradeListener mDbUpgradeListener;

		public SQLiteDbHelper(DaoConfig config) {
			super(config.getContext(), config.getDbName(), null, config.getDbVersion());
			this.mDbUpgradeListener = config.getDbUpgradeListener();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (mDbUpgradeListener != null) {
				mDbUpgradeListener.onUpgrade(db, oldVersion, newVersion);
			} else {
				try {
					dropDb();
				} catch (Exception e) {
					ApplicationBean.logger.e(e);
				}
			}
		}
	}

	private SQLiteDatabase createDbFileOnSDCard(DaoConfig config) {
		SQLiteDatabase result = null;

		File dbFile = new File(config.getSdCardPath(), config.getDbName());
		boolean dbFileExists = dbFile.exists();
		result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

		if (result != null) {
			int oldVersion = result.getVersion();
			int newVersion = config.getDbVersion();
			if (oldVersion != newVersion) {
				if (dbFileExists) {
					config.getDbUpgradeListener().onUpgrade(result, oldVersion, newVersion);
				}
				result.setVersion(newVersion);
			}
		}

		return result;
	}

	// ***************************** private operations with out transaction *****************************
	private void saveOrUpdateWithoutTransaction(Object entity) {
		if (TableUtils.getIdValue(entity) != null) {
			updateWithoutTransaction(entity);
		} else {
			saveBindingIdWithoutTransaction(entity);
		}
	}

	private void replaceWithoutTransaction(Object entity) {
		createTableIfNotExist(entity.getClass());
		execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
	}

	private void saveWithoutTransaction(Object entity) {
		createTableIfNotExist(entity.getClass());
		execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
	}

	private boolean saveBindingIdWithoutTransaction(Object entity) {
		createTableIfNotExist(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null && entityKvList.size() > 0) {
			Table table = Table.get(entity.getClass());
			ContentValues cv = new ContentValues();
			DbUtils.fillContentValues(cv, entityKvList);
			Long id = database.insert(table.getTableName(), null, cv);
			if (id == -1) {
				return false;
			}
			table.getId().setValue2Entity(entity, id.toString());
			return true;
		}
		return false;
	}

	private void deleteWithoutTransaction(Object entity) {
		try {
			execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
	}

	private void updateWithoutTransaction(Object entity) {
		try {
			execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity));
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
	}

	// ************************************************ tools ***********************************

	private static void fillContentValues(ContentValues contentValues, List<KeyValue> list) {
		if (list != null && contentValues != null) {
			for (KeyValue kv : list) {
				contentValues.put(kv.getKey(), kv.getValue().toString());
			}
		} else {
			ApplicationBean.logger.w("List<KeyValue> is empty or ContentValues is empty!");
		}
	}

	private void createTableIfNotExist(Class<?> entityType) {
		if (!tableIsExist(entityType)) {
			SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(entityType);
			execNonQuery(sqlInfo);
		}
	}

	public boolean tableIsExist(Class<?> entityType) {
		Table table = Table.get(entityType);
		if (table.isCheckDatabase()) {
			return true;
		}

		Cursor cursor = null;
		try {
			cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table.getTableName() + "'");
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					table.setCheckDatabase(true);
					return true;
				}
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}

		return false;
	}

	public void dropDb() {
		Cursor cursor = null;
		try {
			cursor = execQuery("SELECT name FROM sqlite_master WHERE type ='table'");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						execNonQuery("DROP TABLE " + cursor.getString(0));
					} catch (Exception e) {
						ApplicationBean.logger.e(e);
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(cursor);
		}
	}

	public void dropTable(Class<?> entityType) {
		if (!tableIsExist(entityType))
			return;
		Table table = Table.get(entityType);
		execNonQuery("DROP TABLE " + table.getTableName());
	}

	// /////////////////////////////////// exec sql /////////////////////////////////////////////////////
	private void debugSql(String sql) {
		if (config != null && debug) {
			ApplicationBean.logger.d(sql);
		}
	}

	private void beginTransaction() {
		if (allowTransaction) {
			database.beginTransaction();
		}
	}

	private void setTransactionSuccessful() {
		if (allowTransaction) {
			database.setTransactionSuccessful();
		}
	}

	private void endTransaction() {
		if (allowTransaction) {
			database.endTransaction();
		}
	}

	public void execNonQuery(SqlInfo sqlInfo) {
		debugSql(sqlInfo.getSql());
		try {
			if (sqlInfo.getBindArgs() != null) {
				database.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
			} else {
				database.execSQL(sqlInfo.getSql());
			}
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
	}

	public void execNonQuery(String sql) {
		debugSql(sql);
		try {
			database.execSQL(sql);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
	}
	
	public void deleteAll(Class clazz) {
		try {
			Table table = Table.get(clazz);
			database.delete(table.getTableName(), null, null);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
	}

	public Cursor execQuery(SqlInfo sqlInfo) {
		debugSql(sqlInfo.getSql());
		try {
			return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
		return null;
	}

	public Cursor execQuery(String sql) {
		debugSql(sql);
		try {
			return database.rawQuery(sql, null);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
		return null;
	}

	// ///////////////////// temp cache ////////////////////////////////////////////////////////////////
	private final FindTempCache findTempCache = new FindTempCache();

	private class FindTempCache {
		private FindTempCache() {
		}

		/**
		 * key: sql; value: find result
		 */
		private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

		private long seq = 0;

		public void put(String sql, Object result) {
			cache.put(sql, result);
		}

		public Object get(String sql) {
			return cache.get(sql);
		}

		public void setSeq(long seq) {
			if (this.seq != seq) {
				cache.clear();
				this.seq = seq;
			}
		}
	}

}
