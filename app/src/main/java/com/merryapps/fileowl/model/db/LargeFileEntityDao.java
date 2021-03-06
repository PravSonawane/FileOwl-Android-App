package com.merryapps.fileowl.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.merryapps.framework.EntityState;
import com.merryapps.framework.EntityStateConverter;

import com.merryapps.fileowl.model.db.LargeFileEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LARGE_FILE".
*/
public class LargeFileEntityDao extends AbstractDao<LargeFileEntity, Long> {

    public static final String TABLENAME = "LARGE_FILE";

    /**
     * Properties of entity LargeFileEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FilePath = new Property(1, String.class, "filePath", false, "FILE_PATH");
        public final static Property FileSize = new Property(2, long.class, "fileSize", false, "FILE_SIZE");
        public final static Property EntityState = new Property(3, String.class, "entityState", false, "ENTITY_STATE");
    };

    private final EntityStateConverter entityStateConverter = new EntityStateConverter();

    public LargeFileEntityDao(DaoConfig config) {
        super(config);
    }
    
    public LargeFileEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LARGE_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"FILE_PATH\" TEXT NOT NULL UNIQUE ," + // 1: filePath
                "\"FILE_SIZE\" INTEGER NOT NULL ," + // 2: fileSize
                "\"ENTITY_STATE\" TEXT NOT NULL );"); // 3: entityState
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LARGE_FILE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LargeFileEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getFilePath());
        stmt.bindLong(3, entity.getFileSize());
        stmt.bindString(4, entityStateConverter.convertToDatabaseValue(entity.getEntityState()));
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LargeFileEntity readEntity(Cursor cursor, int offset) {
        LargeFileEntity entity = new LargeFileEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // filePath
            cursor.getLong(offset + 2), // fileSize
            entityStateConverter.convertToEntityProperty(cursor.getString(offset + 3)) // entityState
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LargeFileEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFilePath(cursor.getString(offset + 1));
        entity.setFileSize(cursor.getLong(offset + 2));
        entity.setEntityState(entityStateConverter.convertToEntityProperty(cursor.getString(offset + 3)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(LargeFileEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(LargeFileEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
