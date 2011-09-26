package jp.pinetail.android.gotochi_gourmet_map.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.pinetail.android.gotochi_gourmet_map.dto.FavoritesDto;
import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FavoritesDao {
    
    private static final String TABLE_NAME                       = "favorites";
    private static final String COLUMN_ID                        = "rowid";
    private static final String COLUMN_TABELOG_ID                = "tabelog_id";
    private static final String COLUMN_MEMO                      = "memo";
    private static final String COLUMN_UPDATED_AT                = "updated_at";
    private static final String COLUMN_CREATED_AT                = "created_at";

    private static final String[] COLUMNS = 
         {COLUMN_ID, COLUMN_TABELOG_ID, COLUMN_MEMO, COLUMN_UPDATED_AT, COLUMN_CREATED_AT};

    private SQLiteDatabase db;

    
    public FavoritesDao(SQLiteDatabase db, Context context) {
        this.db = db;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<FavoritesDto> findAll() {
        return findAll(COLUMN_UPDATED_AT + " desc");
    }
    
    /**
     * 
     * @param orderBy
     * @return
     */
    public ArrayList<FavoritesDto> findAll(String orderBy) {
        ArrayList<FavoritesDto> favorites = new ArrayList<FavoritesDto>();

        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, orderBy);
        
        while(cursor.moveToNext()) {
            FavoritesDto info = new FavoritesDto();
            
            info.rowid         = cursor.getInt(0);
            info.tabelogId     = cursor.getInt(1);
            info.memo          = cursor.getString(2);
            info.updatedAt     = cursor.getString(3);
            info.createdAt     = cursor.getString(4);
            
            favorites.add(info);
        }
        
        cursor.close();
        
        return favorites;
    }
    
    public FavoritesDto findByTabelogId(int uid) {
        FavoritesDto favoritesDto = new FavoritesDto();
        
        String conditions = COLUMN_TABELOG_ID + " = " + uid;
        String orderBy    = COLUMN_UPDATED_AT + " desc";
        
        Util.logging(conditions);
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, orderBy, "1");

        while(cursor.moveToNext()) {
        	favoritesDto.rowid     = cursor.getInt(0);
        	favoritesDto.tabelogId = cursor.getInt(1);
        	favoritesDto.memo      = cursor.getString(2);
        	favoritesDto.updatedAt = cursor.getString(3);
        	favoritesDto.createdAt = cursor.getString(4);
        }
        
        cursor.close();
        
        return favoritesDto;
    }
    
    
    public boolean isExists(int tabelogId) {
        FavoritesDto favoritesDto = new FavoritesDto();
        
        String conditions = COLUMN_TABELOG_ID + " = " + tabelogId;
        String orderBy    = COLUMN_UPDATED_AT + " desc";
        
        Util.logging(conditions);
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, orderBy, "1");

        int count = cursor.getCount();
        
        cursor.close();
        
        return (count > 0);
    }

    
    /**
     * insertメソッド
     * 
     * @param dto
     * @return
     */
    public long insert(FavoritesDto dto) {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        dto.createdAt = simpleDateFormat.format(date);
        dto.updatedAt = simpleDateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TABELOG_ID, dto.tabelogId);
        values.put(COLUMN_MEMO,       dto.memo);
        values.put(COLUMN_UPDATED_AT, dto.updatedAt);
        values.put(COLUMN_CREATED_AT, dto.createdAt);

        return db.insert(TABLE_NAME, null, values);
    }
    
    public int deleteByTabelogId(int tabelogId) {
    	String where = COLUMN_TABELOG_ID + " = " + tabelogId;
    	return db.delete(TABLE_NAME, where, null);
    }

    public int deleteAll() {
        return db.delete(TABLE_NAME, null, null);
    }

}
