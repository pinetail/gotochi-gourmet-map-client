package jp.pinetail.android.gotochi_gourmet_map;

import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.libs.DatabaseHelper;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsDao;
import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class NoneActivity extends AbstractGgmapActivity {
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ShopsDao shopsDao = null;
    private Shops info = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_list);
        
        init();
        dbHelper = new DatabaseHelper(this);

        Bundle extras=getIntent().getExtras();
        if (extras != null) {
            Integer index = extras.getInt("rowid");
            Util.logging(index.toString());
            db = dbHelper.getReadableDatabase();
            shopsDao = new ShopsDao(db,NoneActivity.this);
            info = shopsDao.findById(index);
            db.close();
            
            if (info == null) {
                Toast.makeText(this, "コメント情報が取得できません", Toast.LENGTH_SHORT).show();
                finish();
            }
            
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
                    intent.setData(Uri.parse(info.getTabelogUrl())); 
                    startActivity(intent);
                }
            }
        }
        
        
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();

        Intent intent = intent = new Intent().setClass(this, DetailTabActivity.class);
        intent.putExtra("rowid", info.Rowid);
        startActivity(intent);

    }
    
}
