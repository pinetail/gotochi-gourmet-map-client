package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.pinetail.android.gotochi_gourmet_map.Spots;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpotsDao {
	
    private static final int E6 = 1000000;
	private static final String TABLE_NAME            = "spots";
	private static final String COLUMN_ID             = "rowid";
	private static final String COLUMN_ENTRY_ID       = "entry_id";
	private static final String COLUMN_TITLE          = "title";
	private static final String COLUMN_CATEGORY       = "category";
	private static final String COLUMN_ADDRESS        = "address";
	private static final String COLUMN_LATITUDE       = "latitude";
	private static final String COLUMN_LONGITUDE      = "longitude";
	private static final String COLUMN_URL_PC         = "url_pc";
	private static final String COLUMN_URL_MOBILE     = "url_mobile";
	private static final String COLUMN_WIRELESS       = "wireless";
	private static final String COLUMN_POWERSUPPLY    = "powersupply";
	private static final String COLUMN_TEL            = "tel";
	private static final String COLUMN_OTHER          = "other";
	private static final String COLUMN_REFERENCE_URLS = "reference_urls";
	private static final String COLUMN_IMAGES         = "images";
    private static final String[] CATEGORY_KEYS       = 
        {"c_macdnalds", "c_mosbuger", "c_lotteria", "c_kfc", "c_misterdonut", "c_krispykreme", "c_subway", 
         "c_bugerking", "c_wendys", "c_firstkitchen", "c_freshnessburger", "c_fastfood", "c_ginzarenoir", 
         "c_starbucks", "c_cafedecrie", "c_seattlesbest", "c_tullyscoffee", "c_doutor", "c_pronto", "c_chatnoir", 
         "c_kohikan", "c_sign", "c_ueshima_coffee_ten", "c_cafe", "c_media_cafe", "c_aprecio", "c_hotstation", 
         "c_mangaland", "c_mangahiroba", "c_yuyu_kukan", "c_rakutama", "c_icafe", "c_airscafe", "c_geocafe", 
         "c_geragera", "c_comicbuster", "c_cybac", "c_jiqoo", "c_bagus", "c_manboo", "c_kaikatsu", 
         "c_wipe", "c_netcafe", "c_deskat", "c_karaoke", "c_convenience_store", "c_maidcafe", 
         "c_public_institution", "c_lounge", "c_library", "c_bar", "c_restaurant", "c_izakaya"};
    private static final String[] CATEGORY_VALUES     = 
        {"マクドナルド", "モスバーガー", "ロッテリア", "ケンタッキー", "ミスタードーナツ", "クリスピークリームドーナツ", "サブウェイ", 
         "バーガーキング", "ウェンディーズ", "ファーストキッチン", "フレッシュネスバーガー", "ファストフード(その他)", "銀座ルノアール", 
         "スターバックス", "カフェ・ド・クリエ", "シアトルズベスト", "タリーズコーヒー", "ドトール/エクセルシオール", "プロント", "シャノアール", 
         "珈琲館", "sign", "上島珈琲店", "喫茶店(その他)", "メディアカフェポパイ", "アプレシオ", "ほっとステーション", 
         "まんがランド", "まんが広場", "ゆう遊空間", "らくだ", "アイ・カフェ", "エアーズカフェ", "ゲオカフェ", 
         "ゲラゲラ", "コミックバスター", "サイバック", "スペースクリエイト自遊空間", "バグース", "マンボー", "快活CLUB", 
         "ワイプ", "ネットカフェ(その他)", "デスカット", "カラオケ", "コンビニエンスストア", "メイドカフェなど(萌)", 
         "公共施設", "待合室・ラウンジ", "図書館", "バー", "飲食店(その他)", "居酒屋"};
    private static final String[] WIRELESS_KEYS       = 
    {"w_none", "w_bb_mobile_point", "w_flets_spot", "w_softbankwifi", "w_hotspot", "w_mzone", "w_livedoor_wireless", 
     "w_freespot", "w_fon", "w_original"};
    private static final String[] WIRELESS_VALUES     = 
        {"", "BBモバイルポイント", "フレッツ・スポット", "SoftbankWifi", "ホットスポット", "Mzone", "livedoor Wireless", 
         "FREESPOT", "FON", "独自"};

    private static final String[] COLUMNS = 
         {COLUMN_ID, COLUMN_ENTRY_ID, COLUMN_TITLE, COLUMN_CATEGORY, COLUMN_ADDRESS, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_URL_PC, 
        COLUMN_URL_MOBILE, COLUMN_WIRELESS, COLUMN_POWERSUPPLY, COLUMN_TEL, COLUMN_OTHER, COLUMN_REFERENCE_URLS,
        COLUMN_IMAGES};

    private SQLiteDatabase db;
    private HashMap<String, String> category_map = new HashMap <String, String>();
    private HashMap<String, String> wireless_map = new HashMap <String, String>();

    
    public SpotsDao(SQLiteDatabase db) {
        this.db = db;
        
        for ( int i = 0; i < CATEGORY_KEYS.length; ++i ) {
            category_map.put(CATEGORY_KEYS[i], CATEGORY_VALUES[i]);
        }
    
        for ( int i = 0; i < WIRELESS_KEYS.length; ++i ) {
            wireless_map.put(WIRELESS_KEYS[i], WIRELESS_VALUES[i]);
        }
    
    }
    
    public ArrayList<Spots> findAll() {
        ArrayList<Spots> spots = new ArrayList<Spots>();
                
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, COLUMN_ID);
        
        while(cursor.moveToNext()) {
            Spots info = new Spots();
            
            info.Rowid         = cursor.getInt(0);
            info.EntryId       = cursor.getInt(1);
            info.Title         = cursor.getString(2);
            info.Category      = cursor.getString(3);
            info.Address       = cursor.getString(4);
            info.Latitude      = Float.valueOf(cursor.getString(5));
            info.Longitude     = Float.valueOf(cursor.getString(6));
            info.UrlPc         = cursor.getString(7);
            info.UrlMobile     = cursor.getString(8);
            info.Wireless      = cursor.getString(9);
            info.PowerSupply   = cursor.getString(10);
            info.Tel           = cursor.getString(11);
            info.Other         = cursor.getString(12);
            info.ReferenceUrls = cursor.getString(13);
            info.Images        = cursor.getString(14);
            
            spots.add(info);
            
        }
        
        return spots;
    }

    public ArrayList<Spots> find(SharedPreferences pref, int top, int bottom, int left, int right, boolean payment) {
        ArrayList<Spots> spots = new ArrayList<Spots>();
        
        List<String> conditions_category = new ArrayList<String>();
        List<String> conditions_wireless = new ArrayList<String>();
        
        for ( String key : category_map.keySet() ) {
            String data = category_map.get( key );
            
            if (pref.getBoolean(key, true) == true) {
                conditions_category.add("category like '%" + data + "%'");
            }
        }

        for ( String key : wireless_map.keySet() ) {
            String data = wireless_map.get( key );
            
            if (pref.getBoolean(key, true) == true) {
                if (key == "w_none") {
                    conditions_wireless.add("wireless = ''");
                } else {
                    conditions_wireless.add("wireless like '%" + data + "%'");
                }
            }
        }

        String conditions = "";
        conditions += "(" + (float)top / E6 + " <= latitude and latitude <=" + (float)bottom / E6 + ") and ";
        conditions += "(" + (float)left / E6 + " <= longitude and longitude <=" + (float)right / E6 + ") and ";
        
        if (conditions_category.size() > 0 && CATEGORY_KEYS.length != conditions_category.size()) {
            conditions += "(" + Util.join(conditions_category, " or ") + ") and ";
        }
        if (conditions_wireless.size() > 0 && WIRELESS_KEYS.length != conditions_wireless.size()) {
            conditions += "(" + Util.join(conditions_wireless, " or ") + ") and ";
        }
        
        if (payment == true && pref.getBoolean("settings_search_poweronly", true) == true) {
            conditions += "(" + COLUMN_POWERSUPPLY + " = '公式にOK' or " + COLUMN_POWERSUPPLY + " = 'たぶんOK' or " + COLUMN_POWERSUPPLY + " = '実績あり') and ";
        }
        conditions += "1 = 1";
        Util.logging("i:" + conditions);
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, COLUMN_ID);

        while(cursor.moveToNext()) {
            Spots info = new Spots();
            
            info.Rowid         = cursor.getInt(0);
            info.EntryId       = cursor.getInt(1);
            info.Title         = cursor.getString(2);
            info.Category      = cursor.getString(3);
            info.Address       = cursor.getString(4);
            info.Latitude      = Float.valueOf(cursor.getString(5));
            info.Longitude     = Float.valueOf(cursor.getString(6));
            info.UrlPc         = cursor.getString(7);
            info.UrlMobile     = cursor.getString(8);
            info.Wireless      = cursor.getString(9);
            info.PowerSupply   = cursor.getString(10);
            info.Tel           = cursor.getString(11);
            info.Other         = cursor.getString(12);
            info.ReferenceUrls = cursor.getString(13);
            info.Images        = cursor.getString(14);
            
            spots.add(info);
        }
        
        return spots;
    }

    public int deleteAll() {
        return db.delete(TABLE_NAME, null, null);
    }

    public Spots findById(int id) {
        Spots spot = new Spots();
        
        String conditions = "id = " + id;
        Util.logging("i:" + conditions);
        
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, conditions, null, null, null, COLUMN_ID);

        while(cursor.moveToNext()) {
            Spots info = new Spots();
            
            info.Rowid         = cursor.getInt(0);
            info.EntryId       = cursor.getInt(1);
            info.Title         = cursor.getString(2);
            info.Category      = cursor.getString(3);
            info.Address       = cursor.getString(4);
            info.Latitude      = Float.valueOf(cursor.getString(5));
            info.Longitude     = Float.valueOf(cursor.getString(6));
            info.UrlPc         = cursor.getString(7);
            info.UrlMobile     = cursor.getString(8);
            info.Wireless      = cursor.getString(9);
            info.PowerSupply   = cursor.getString(10);
            info.Tel           = cursor.getString(11);
            info.Other         = cursor.getString(12);
            info.ReferenceUrls = cursor.getString(13);
            info.Images        = cursor.getString(14);
            
            return info;
        }
        
        return spot;
    }

}
