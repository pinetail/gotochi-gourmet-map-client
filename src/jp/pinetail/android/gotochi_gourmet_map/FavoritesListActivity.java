package jp.pinetail.android.gotochi_gourmet_map;

import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.dao.FavoritesDao;
import jp.pinetail.android.gotochi_gourmet_map.dao.ShopsDao;
import jp.pinetail.android.gotochi_gourmet_map.dto.FavoritesDto;
import jp.pinetail.android.gotochi_gourmet_map.dto.ShopsDto;
import jp.pinetail.android.gotochi_gourmet_map.libs.DatabaseHelper;
import jp.pinetail.android.gotochi_gourmet_map.libs.GotochiApplication;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsAdapter;
import yanzm.products.quickaction.lib.ActionItem;
import yanzm.products.quickaction.lib.QuickAction;
import android.app.Activity;
import android.app.TabActivity;
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
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritesListActivity extends AbstractGgmapActivity {
    private ArrayList<ShopsDto> list = new ArrayList<ShopsDto>();
    private ArrayList<FavoritesDto> favoritesDtolist = null;
    private ShopsAdapter adapter = null;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private FavoritesDao favoritesDao = null;
    private ShopsDao shopsDao = null;
    private static String mode = "score";
    private SharedPreferences pref = null;
    private QuickAction qa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_list);
        
        // 初期化
        pref = PreferenceManager.getDefaultSharedPreferences(FavoritesListActivity.this);
//        mode = pref.getString("settings_sort", getResources().getString(R.string.settings_sort_default));
        dbHelper = new DatabaseHelper(this);

    }
    
    protected void init() {
        super.init();
        
        ListView savedList = (ListView) findViewById(R.id.savedList);
        savedList.setFastScrollEnabled(true);

        if (list.size() == 0) {
            ShopsAdapter sAdapter = (ShopsAdapter) savedList.getAdapter();
            if (sAdapter != null) {
                sAdapter.clear();
            }
        } else {
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
                                "FavoritesList",         // Category
                                "Map",          // Action
                                item.TabelogId.toString(),  // Label
                                0);
                            
                            Intent intent = new Intent();
                            intent.putExtra("lat", item.Lat);
                            intent.putExtra("lon", item.Lng);
                            GotochiApplication app = (GotochiApplication) FavoritesListActivity.this.getApplication();
                            app.setLat(item.Lat);
                            app.setLng(item.Lng);
                            
                            qa.dismiss();
                            
                            TabHost tabHost = ((TabActivity) getParent()).getTabHost();
                            tabHost.setCurrentTab(0);
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
                                "FavoritesList",         // Category
                                "Detail",          // Action
                                item.TabelogId.toString(),  // Label
                                0);
                            
                            Intent intent1 = new Intent(FavoritesListActivity.this, DetailTabActivity.class);
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
                                "FavoritesList",         // Category
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
    
    @Override
    protected void onResume() {
        super.onResume();
       
        db = dbHelper.getReadableDatabase();
//      Utils.logging(mode);
        favoritesDao = new FavoritesDao(db, FavoritesListActivity.this);
        shopsDao = new ShopsDao(db, FavoritesListActivity.this);
        Bundle extras=getIntent().getExtras();
        list.clear();

        favoritesDtolist = favoritesDao.findAll();
        if (favoritesDtolist.size() > 0) {
            for (FavoritesDto dto : favoritesDtolist) {
                list.add(shopsDao.findByTabelogId(dto.tabelogId));
            }
        }
        db.close();
        init();

    }
    
    protected void onActivityResult(int requestCode,
            int resultCode,Intent intent) {
        
        if (qa != null) {
            qa.dismiss();
        }
    }

}
