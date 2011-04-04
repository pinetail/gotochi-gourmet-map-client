package jp.pinetail.android.gotochi_gourmet_map;

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
	
	private static final String DATABASE_NAME = "dengenkensaku";
	private static final int DATABASE_VERSION = 1;
	private Context mContext;
	private static boolean import_status = true;
	
	private static final String CREATE_SPOTS_TABLE_SQL = "create table spots " +
			"(rowid integer primary key autoincrement, " +
			"entry_id int not null, " +
			"title text not null, " +
			"category text not null, " +
			"address text not null, " +
			"latitude real not null, " +
			"longitude real not null, " +
			"url_pc text null, " +
			"url_mobile text null, " +
			"wireless text null, " +
			"powersupply text null," +
			"tel text null," +
			"other text null," +
			"reference_urls text null," +
			"images text null)";
	
	private static final String DROP_SPOTS_TABLE_SQL = "drop table if exists spots";


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SPOTS_TABLE_SQL);
		
		db.beginTransaction();
	    SQLiteStatement stmt = db.compileStatement("insert into spots (entry_id, title, category, address, latitude, longitude, url_pc, url_mobile, wireless, powersupply, tel, other, reference_urls, images) " +
        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

	    try {
	    	
	    	String version = mContext.getResources().getString(R.string.csv_version);
	    	int file_num = Integer.valueOf(mContext.getResources().getString(R.string.csv_version_split));
	    	String csv_name = "spots_" + version;
	    	/*
			if (Util.isPaymentApp(mContext) == true) {
				csv_name += "_all";
			} else {
				csv_name += "_poweronly";			
			}

			Util.logging(csv_name);
//			InputStream input = mContext.getResources().openRawResource(R.raw.spots_20101027_all);
//			InputStream input = mContext.getResources().openRawResource(mContext.getResources().getIdentifier(csv_name, "raw", mContext.getPackageName()));
			*/
			AssetManager assetManager = mContext.getAssets();
			int num = 2;

			for (int i=1;i<=file_num;i++) {
				InputStream input = assetManager.open(version + "_" + String.valueOf(i), AssetManager.ACCESS_STREAMING);
				
		    	InputStreamReader ireader=new InputStreamReader(input, "SJIS");
		    	
		    	if (i == 1) {
		    		num = 2;
		    	} else {
		    		num = 1;
		    	}
	    		CSVReader reader = new CSVReader(ireader,',','"',num);

		    	Util.logging(reader.getClass().getName());
		    	String[] nextLine;
				while ((nextLine = reader.readNext()) != null) {
					if (nextLine.length < 12) {
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
			        stmt.bindString(13, ""); // reference_urls
			        stmt.bindString(14, ""); // images
			        stmt.executeInsert();

				}
				input.close();
			}

		    db.setTransactionSuccessful();
		    
	    	SharedPreferences pref = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        	Editor editor = pref.edit();
        	editor.putString("latest_date", version);
        	editor.commit();

		} catch (IOException e) {
			import_status = false;
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			stmt.close();
		    db.endTransaction();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_SPOTS_TABLE_SQL);
		onCreate(db);
	}
	
	public boolean updateSpots(SQLiteDatabase db, String fname, ProgressDialog dialog) {
		
		CSVReader reader = null;
		db.beginTransaction();
		db.execSQL(DROP_SPOTS_TABLE_SQL);
		db.execSQL(CREATE_SPOTS_TABLE_SQL);
	    SQLiteStatement stmt = db.compileStatement("insert into spots (entry_id, title, category, address, latitude, longitude, url_pc, url_mobile, wireless, powersupply, tel, other, reference_urls, images) " +
        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

	    try {
	    	InputStream input=new FileInputStream(fname);
	    	InputStreamReader ireader=new InputStreamReader(input, "SJIS");
	    	reader = new CSVReader(ireader,',','"',2);
	    	int size = Util.getRowNum(fname);
	    	Util.logging(String.valueOf(size));
	    	int num = 0;
	    	String[] nextLine;
	    	
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length < 12) {
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
		        stmt.bindString(13, ""); // reference_urls
		        stmt.bindString(14, ""); // images
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
