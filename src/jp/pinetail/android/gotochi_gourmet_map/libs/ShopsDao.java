package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.Shops;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class ShopsDao {
	
    private static final int E6 = 1000000;
	private static final String TABLE_NAME                = "shops";
	private static final String COLUMN_ID                 = "rowid";
	private static final String COLUMN_NAME               = "name";
	private static final String COLUMN_ADDRESS            = "address";
	private static final String COLUMN_TEL                = "tel";
	private static final String COLUMN_CATEGORY           = "category";
	private static final String COLUMN_TABELOG_ID         = "tabelog_id";
	private static final String COLUMN_BUSINESS_HOURS     = "business_hours";
	private static final String COLUMN_HOLIDAY            = "holiday";
	private static final String COLUMN_LATITUDE           = "latitude";
	private static final String COLUMN_LONGITUDE          = "longitude";
	private static final String COLUMN_SCORE              = "score";
	private static final String COLUMN_TABELOG_URL        = "tabelog_url";
	private static final String COLUMN_TABELOG_MOBILE_URL = "tabelog_mobile_url";
	private static final String COLUMN_STATION            = "station";
	private static final String COLUMN_MEMO               = "memo";
    private static String[] CATEGORY_KEYS       = {};
    private static String[] CATEGORY_VALUES     = {};

    private static final String[] COLUMNS = 
         {COLUMN_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_TEL, COLUMN_CATEGORY, COLUMN_TABELOG_ID, COLUMN_BUSINESS_HOURS, COLUMN_HOLIDAY, 
          COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_SCORE, COLUMN_TABELOG_URL, COLUMN_TABELOG_MOBILE_URL, COLUMN_STATION,COLUMN_MEMO};

    private SQLiteDatabase db;
    private HashMap<String, String> category_map = new HashMap <String, String>();

    
    public ShopsDao(SQLiteDatabase db, Context context) {
        this.db = db;
        
        CATEGORY_KEYS = context.getResources().getStringArray(R.array.list_category_key);
        CATEGORY_VALUES = context.getResources().getStringArray(R.array.list_category_name);
        
        for ( int i = 0; i < CATEGORY_KEYS.length; ++i ) {
            category_map.put(CATEGORY_KEYS[i], CATEGORY_VALUES[i]);
        }
    
    }
    
    public ArrayList<Shops> findAll() {
        ArrayList<Shops> shops = new ArrayList<Shops>();
                
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, COLUMN_ID);
        
        while(cursor.moveToNext()) {
            Shops info = new Shops();
            
            info.Rowid            = cursor.getInt(0);
            info.Name             = cursor.getString(1);
            info.Address          = cursor.getString(2);
            info.Tel              = cursor.getString(3);
            info.Category         = cursor.getString(4);
            info.TabelogId        = cursor.getInt(5);
            info.BusinessHours    = cursor.getString(6);
            info.Holiday          = cursor.getString(7);
            info.Lat              = Double.valueOf(cursor.getString(8));
            info.Lng              = Double.valueOf(cursor.getString(9));
            info.Score            = cursor.getString(10);
            info.TabelogUrl       = cursor.getString(11);
            info.TabelogMobileUrl = cursor.getString(12);
            info.Station          = cursor.getString(13);
            info.Memo             = cursor.getString(14);
            
            shops.add(info);
            
        }
        
        return shops;
    }

    public ArrayList<Shops> find(SharedPreferences pref, int top, int bottom, int left, int right, int lat, int lng, String sort) {
        ArrayList<Shops> shops = new ArrayList<Shops>();
        
        List<String> conditions_category = new ArrayList<String>();
        List<String> conditions_wireless = new ArrayList<String>();
        
        for ( String key : category_map.keySet() ) {
            String data = category_map.get( key );
            
            if (pref.getBoolean(key, true) == true) {
                conditions_category.add("category = '" + data + "'");
            }
        }

        String conditions = "";
        conditions += "(" + (float)top / E6 + " <= latitude and latitude <=" + (float)bottom / E6 + ") and ";
        conditions += "(" + (float)left / E6 + " <= longitude and longitude <=" + (float)right / E6 + ") and ";
        
        if (conditions_category.size() > 0 && CATEGORY_KEYS.length != conditions_category.size()) {
            conditions += "(" + Util.join(conditions_category, " or ") + ") and ";
        }
        conditions += "1 = 1";
        Util.logging("i:" + conditions);
        
        String orderby = COLUMN_ID;
        if (sort.equals("score")) {
            orderby = COLUMN_SCORE + " desc";
        } else if (sort.equals("name")) {
            orderby = COLUMN_NAME + " asc";
        }
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, orderby);
//        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, COLUMN_ID);

        while(cursor.moveToNext()) {
            Shops info = new Shops();
            
            info.Rowid            = cursor.getInt(0);
            info.Name             = cursor.getString(1);
            info.Address          = cursor.getString(2);
            info.Tel              = cursor.getString(3);
            info.Category         = cursor.getString(4);
            info.TabelogId        = cursor.getInt(5);
            info.BusinessHours    = cursor.getString(6);
            info.Holiday          = cursor.getString(7);
            info.Lat              = Double.valueOf(cursor.getString(8));
            info.Lng              = Double.valueOf(cursor.getString(9));
            info.Score            = cursor.getString(10);
            info.TabelogUrl       = cursor.getString(11);
            info.TabelogMobileUrl = cursor.getString(12);
            info.Station          = cursor.getString(13);
            info.Memo             = cursor.getString(14);
            
            if (lat != 0 && lng != 0) {
                float[] results = {0,0,0};
                Location.distanceBetween((double) (lat / 1E6), (double) (lng / 1E6), info.Lat, info.Lng, results);
                info.Distance         = results[0];
                info.Bearing          = results[1];
                Util.logging(String.valueOf(results[1]));
            }
            shops.add(info);
        }
        cursor.close();
        
        return shops;
    }

    public int deleteAll() {
        return db.delete(TABLE_NAME, null, null);
    }

    public Shops findById(int rowid) {
        Shops shop = new Shops();
        
        String conditions = "rowid = " + rowid;
        Util.logging("i:" + conditions);
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, COLUMN_ID);

        while(cursor.moveToNext()) {
            Shops info = new Shops();
            
            info.Rowid            = cursor.getInt(0);
            info.Name             = cursor.getString(1);
            info.Address          = cursor.getString(2);
            info.Tel              = cursor.getString(3);
            info.Category         = cursor.getString(4);
            info.TabelogId        = cursor.getInt(5);
            info.BusinessHours    = cursor.getString(6);
            info.Holiday          = cursor.getString(7);
            info.Lat              = Double.valueOf(cursor.getString(8));
            info.Lng              = Double.valueOf(cursor.getString(9));
            info.Score            = cursor.getString(10);
            info.TabelogUrl       = cursor.getString(11);
            info.TabelogMobileUrl = cursor.getString(12);
            info.Station          = cursor.getString(13);
            info.Memo             = cursor.getString(14);
            
            return info;
        }
        
        return shop;
    }

}
