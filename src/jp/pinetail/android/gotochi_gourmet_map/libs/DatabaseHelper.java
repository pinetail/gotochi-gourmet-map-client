package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.pinetail.android.gotochi_gourmet_map.dto.ShopsDto;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import au.com.bytecode.opencsv.CSVReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gotochi_gourmet_map";
    private static final int DATABASE_VERSION = 2;
    private Context mContext;
    private static boolean import_status = true;

    private static final String CREATE_SHOPS_TABLE_SQL = "create table shops "
            + "(rowid integer primary key autoincrement, "
            + "name text not null, " + "address text not null, "
            + "tel text null, " + "category text not null, "
            + "tabelog_id integer null, " + "business_hours text null, "
            + "holiday text null, " + "latitude real not null, "
            + "longitude real not null, " + "score text null, "
            + "tabelog_url null, " + "tabelog_mobile_url null, "
            + "station text null," + "memo text null)";

    private static final String DROP_SHOPS_TABLE_SQL = "drop table if exists shops";

    private static final String CREATE_FAVORITES_TABLE_SQL = "create table if not exists favorites "
            + "(rowid integer primary key autoincrement, "
            + "tabelog_id integer not null,"
            + "memo text null,"
            + "updated_at text not null," + "created_at text not null)";

    private static final String DROP_FAVORITES_TABLE_SQL = "drop table if exists favorites";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHOPS_TABLE_SQL);
        db.execSQL(CREATE_FAVORITES_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SHOPS_TABLE_SQL);
        onCreate(db);
    }

    public boolean updateShops(SQLiteDatabase db, String fname,
            ProgressDialog dialog) {

        CSVReader reader = null;
        db.beginTransaction();
        db.execSQL(DROP_SHOPS_TABLE_SQL);
        db.execSQL(CREATE_SHOPS_TABLE_SQL);
        SQLiteStatement stmt = db
                .compileStatement("insert into shops (name, address, tel, category, tabelog_id, "
                        + "business_hours, holiday, latitude, longitude, score, tabelog_url, tabelog_mobile_url, station, memo) "
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        try {
            InputStream input = new FileInputStream(fname);
            InputStreamReader ireader = new InputStreamReader(input, "SJIS");
            reader = new CSVReader(ireader, ',', '"');
            int size = Util.getRowNum(fname);
            Util.logging(String.valueOf(size));
            int num = 0;
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 14) {
                    continue;
                }
                stmt.bindString(1, nextLine[0].trim()); // name
                stmt.bindString(2, nextLine[1].trim()); // address
                stmt.bindString(3, nextLine[2].trim()); // tel
                stmt.bindString(4, nextLine[3].trim()); // category
                stmt.bindString(5, nextLine[4].trim()); // tabelog_id
                stmt.bindString(6, nextLine[5].trim()); // business_hours
                stmt.bindString(7, nextLine[6].trim()); // holiday
                stmt.bindString(8, nextLine[7].trim()); // latitude
                stmt.bindString(9, nextLine[8].trim()); // longitude
                stmt.bindString(10, nextLine[9].trim()); // score
                stmt.bindString(11, nextLine[10].trim().replace(
                        ShopsDto.TABELOG_PC_DOMAIN, "")); // tabelog_url
                stmt.bindString(12, ""); // tabelog_mobile_url
                stmt.bindString(13, nextLine[12].trim()); // station
                stmt.bindString(14, nextLine[13].trim()); // memo
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
            db.execSQL("vacuum");
        }

        return false;
    }

    public boolean getImportStatus() {
        return import_status;
    }

}
