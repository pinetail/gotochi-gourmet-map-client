package jp.pinetail.android.gotochi_gourmet_map;

import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.dao.ShopsDao;
import jp.pinetail.android.gotochi_gourmet_map.dto.ShopsDto;
import jp.pinetail.android.gotochi_gourmet_map.libs.DatabaseHelper;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsAdapter;
import yanzm.products.quickaction.lib.ActionItem;
import yanzm.products.quickaction.lib.QuickAction;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListActivity extends AbstractGgmapActivity {
    private ArrayList<ShopsDto> list = null;
    private ShopsAdapter adapter = null;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ShopsDao shopsDao = null;
    private static String mode = "score";
    private SharedPreferences pref = null;
    private QuickAction qa;
    private int top;
    private int bottom;
    private int left;
    private int right;
    private int lat = 0;
    private int lng = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        
        // 初期化
        pref = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
//        mode = pref.getString("settings_sort", getResources().getString(R.string.settings_sort_default));
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
//        Utils.logging(mode);
        shopsDao = new ShopsDao(db, ListActivity.this);
        Bundle extras=getIntent().getExtras();
        if (extras != null) {
            top    = extras.getInt("top");
            bottom = extras.getInt("bottom");
            left   = extras.getInt("left");
            right  = extras.getInt("right");
            if (extras.containsKey("lat")) {
                lat = extras.getInt("lat");
                lng = extras.getInt("lng");
            } else {
                Toast.makeText(this, "現在地を取得できませんでした", Toast.LENGTH_SHORT).show();
            }
            list = shopsDao.find(pref, top, bottom, left, right, lat, lng, mode);
            
        } else {
            list = shopsDao.findAll();
        }
        db.close();
        init();

        RadioButton scoreButton = (RadioButton) findViewById(R.id.sort_score);
        
        scoreButton.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                mode = "score";
                db = dbHelper.getReadableDatabase();
                shopsDao = new ShopsDao(db, ListActivity.this);
                list = shopsDao.find(pref, top, bottom, left, right, lat, lng, mode);
                db.close();
                init();
                
                // イベントトラック（並び順）
                tracker.trackEvent(
                    "List",       // Category
                    "Sort",       // Action
                    "Score",      // Label
                    0);
            }
        });

        RadioButton shopNameButton = (RadioButton) findViewById(R.id.sort_shop_name);
        
        shopNameButton.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                mode = "name";
                db = dbHelper.getReadableDatabase();
                shopsDao = new ShopsDao(db, ListActivity.this);
                list = shopsDao.find(pref, top, bottom, left, right, lat, lng, mode);
                db.close();
                init();
                
                // イベントトラック（並び順）
                tracker.trackEvent(
                    "List",       // Category
                    "Sort",       // Action
                    "ShopName",       // Label
                    0);

            }
        });
        
        if (mode.equals("score")) {
            scoreButton.setChecked(true);
        } else {
            shopNameButton.setChecked(true);
        }
        
        /*
        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */
    }
    
    protected void init() {
        super.init();
        
        if (list.size() > 0) {
            ListView savedList = (ListView) findViewById(R.id.savedList);
            savedList.setFastScrollEnabled(true);
            adapter = new ShopsAdapter(this, R.layout.list, list);
            savedList.setAdapter(adapter);
        
            savedList.setOnItemClickListener(new OnItemClickListener() {
     
                @Override
                public void onItemClick(AdapterView<?> adapter,
                        View view, int position, long id) {
                    final ShopsDto item = list.get(position);
                    
                    ActionItem item1 = new ActionItem();
                    item1.setTitle("地図");
                    item1.setIcon(getResources().getDrawable(R.drawable.map_blue));
                    item1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            // イベントトラック（地図）
                            tracker.trackEvent(
                                "List",         // Category
                                "Map",          // Action
                                item.TabelogId.toString(),  // Label
                                0);
                            
                            Intent intent = new Intent();
                            intent.putExtra("lat", item.Lat);
                            intent.putExtra("lon", item.Lng);
                            
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                    
                    ActionItem item2 = new ActionItem();
                    item2.setTitle("詳細");
                    item2.setIcon(getResources().getDrawable(R.drawable.info));
                    item2.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            // イベントトラック（詳細）
                            tracker.trackEvent(
                                "List",         // Category
                                "Detail",          // Action
                                item.TabelogId.toString(),  // Label
                                0);
                            
                            Intent intent1 = new Intent(ListActivity.this, DetailTabActivity.class);
                            intent1.putExtra("rowid", item.Rowid);
                            startActivityForResult(intent1, 1);
                        }
                    });
                    
                    
                    ActionItem item3 = new ActionItem();
                    item3.setTitle("ルート検索");
                    item3.setIcon(getResources().getDrawable(R.drawable.green_flag));
                    item3.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            // イベントトラック（ルート検索）
                            tracker.trackEvent(
                                "List",         // Category
                                "RouteSearch",  // Action
                                item.TabelogId.toString(),  // Label
                                0);
                            
                            Intent intent = new Intent(); 
                            intent.setAction(Intent.ACTION_VIEW); 
                            intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                            intent.setData(Uri.parse("http://maps.google.com/maps?myl=saddr&daddr=" + item.Address.replaceAll("<br />", " ") + "&dirflg=d")); 
                            startActivity(intent);
                        }
                    });
                    

                    qa = new QuickAction(view);
                    //onCreate()の中で作ったActionItemをセットする
                    qa.addActionItem(item1);
                    qa.addActionItem(item2);
                    qa.addActionItem(item3);
                    
                    //アニメーションを設定する
                    qa.setAnimStyle(QuickAction.ANIM_AUTO);
                    qa.show();
                }
            });

        }
    }
    
    protected void onActivityResult(int requestCode,
            int resultCode,Intent intent) {
        
        if (qa != null) {
            qa.dismiss();
        }
    }
}
