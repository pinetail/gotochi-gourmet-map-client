package jp.pinetail.android.gotochi_gourmet_map;

import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.libs.DatabaseHelper;
import jp.pinetail.android.gotochi_gourmet_map.libs.ImageCache;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsController;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsDao;
import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DetailActivity extends AbstractGgmapActivity implements Runnable {
    
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ShopsDao shopsDao = null;
    private ShopsController controller = null;
    private final Handler handler = new Handler();
    private static final Integer pressed_color = Color.argb(80, 255, 255, 255);
    private Shops info = null;
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.detail);
        
        init();

        dbHelper = new DatabaseHelper(this);
        
        Bundle extras=getIntent().getExtras();
        if (extras != null) {
            Integer index = extras.getInt("rowid");
            Util.logging(index.toString());
            db = dbHelper.getReadableDatabase();
            shopsDao = new ShopsDao(db, DetailActivity.this);
            info = shopsDao.findById(index);
            db.close();
            
            if (info == null) {
            	Toast.makeText(this, "店舗情報が取得できません", Toast.LENGTH_SHORT).show();
            	finish();
            }
            
            ImageCache.clear();
            controller = new ShopsController(handler, (Runnable) this, this, info);
            controller.setLayout();
            
            setContentView(controller.getView());
        }
        
    }
    
    @Override
    public void run() {
    }
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu( menu );
        // メニューアイテムを追加
//        MenuItem item0 = menu.add( 0, 0, 0, "ブラウザ" );
        MenuItem item1 = menu.add( 0, 1, 0, "共有" );
        MenuItem item2 = menu.add( 0, 2, 0, "設定" );

        // 追加したメニューアイテムのアイコンを設定
//        item0.setIcon( android.R.drawable.ic_menu_view);
        item1.setIcon( android.R.drawable.ic_menu_share);
        item2.setIcon( android.R.drawable.ic_menu_preferences);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return false;
    }
    
    public void onResume() {
    	super.onResume();
//    	Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
    	
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
	
	        if (extras.containsKey("mode")) {
	            if (extras.get("mode").equals("route")) {
	                // ルート検索
	                tracker.trackEvent(
	                    "Detail",      // Category
	                    "Route",     // Action
	                    info.TabelogId.toString(), // Label
	                    0);
	                
	                Intent intent = new Intent(); 
	                intent.setAction(Intent.ACTION_VIEW); 
	                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
	                intent.setData(Uri.parse("http://maps.google.com/maps?myl=saddr&daddr=" + info.Address.replaceAll("<br />", " ") + "&dirflg=d")); 
	                startActivity(intent);
	
	            } else if (extras.get("mode").equals("browser")) {
	                // イベントトラック（ブラウザ）
	                tracker.trackEvent(
	                    "Detail",      // Category
	                    "Browser",     // Action
	                    info.TabelogId.toString(), // Label
	                    0);
	                
	                Intent intent = new Intent(); 
	                intent.setAction(Intent.ACTION_VIEW); 
	                intent.setData(Uri.parse(info.TabelogUrl)); 
	                startActivity(intent);
	            }
	            
//	            getIntent().removeExtra("mode");

	        }
        }

    }
    
    
}
