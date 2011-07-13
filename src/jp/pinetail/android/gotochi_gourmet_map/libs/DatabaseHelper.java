package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import au.com.bytecode.opencsv.CSVReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gotochi_gourmet_map";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;
    private static boolean import_status = true;
    
    private static final String CREATE_SHOPS_TABLE_SQL = "create table shops " +
            "(rowid integer primary key autoincrement, " +
            "name text not null, " +
            "address text not null, " +
            "tel text null, " +
            "category text not null, " +
            "tabelog_id integer null, " +
            "business_hours text null, " +
            "holiday text null, " +
            "latitude real not null, " +
            "longitude real not null, " +
            "score text null, " +
            "tabelog_url null, " +
            "tabelog_mobile_url null, " +
            "station text null," +
            "memo text null)";
    
    private static final String DROP_SHOPS_TABLE_SQL = "drop table if exists shops";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHOPS_TABLE_SQL);
        
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SHOPS_TABLE_SQL);
        onCreate(db);
    }
    
    public boolean updateShops(SQLiteDatabase db, String fname, ProgressDialog dialog) {
        
        CSVReader reader = null;
        db.beginTransaction();
        db.execSQL(DROP_SHOPS_TABLE_SQL);
        db.execSQL(CREATE_SHOPS_TABLE_SQL);
        SQLiteStatement stmt = db.compileStatement("insert into shops (name, address, tel, category, tabelog_id, "+ 
                "business_hours, holiday, latitude, longitude, score, tabelog_url, tabelog_mobile_url, station, memo) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        try {
            InputStream input=new FileInputStream(fname);
            InputStreamReader ireader=new InputStreamReader(input, "SJIS");
            reader = new CSVReader(ireader,',','"');
            int size = Util.getRowNum(fname);
            Util.logging(String.valueOf(size));
            int num = 0;
            String[] nextLine;
            
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 14) {
                    continue;
                }
                stmt.bindString(1, nextLine[0].trim()); // entry_id
                stmt.bindString(2, nextLine[1].trim()); // title
                stmt.bindString(3, nextLine[2].trim()); // category
                stmt.bindString(4, nextLine[3].trim()); // address
                stmt.bindString(5, nextLine[4].trim()); // latitude
                stmt.bindString(6, nextLine[5].trim()); // longitude
                stmt.bindString(7, nextLine[6].trim()); // url_pc
                stmt.bindString(8, nextLine[7].trim()); // url_mobile
                stmt.bindString(9, nextLine[8].trim()); // wireless
                stmt.bindString(10, nextLine[9].trim()); // powersupply
                stmt.bindString(11, nextLine[10].trim()); // tel
                stmt.bindString(12, nextLine[11].trim()); // other
                stmt.bindString(13, nextLine[12].trim()); // other
                stmt.bindString(14, nextLine[13].trim()); // other
                stmt.executeInsert();
                int increment = (int) ((double) num / size * 50);
                Util.logging(String.valueOf(increment));
                dialog.setProgress(50 + increment);
                num++;
            }
            dialog.setProgress(100);

            db.setTransactionSuccessful();
            return true;

        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
            }
            stmt.close();
            db.endTransaction();
        }
    
        return false;
    }
    
    public boolean getImportStatus() {
        return import_status;
    }

}
