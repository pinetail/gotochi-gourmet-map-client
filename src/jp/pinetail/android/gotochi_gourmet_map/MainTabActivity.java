package jp.pinetail.android.gotochi_gourmet_map;

import jp.pinetail.android.gotochi_gourmet_map.libs.GotochiApplication;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class MainTabActivity extends TabActivity {

	private Integer index = null;
    private int backCount = 0;
    private GotochiApplication app;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        
        Resources res = getResources();
        app = (GotochiApplication) getApplication();
        
        Bundle extras=getIntent().getExtras();

        final TabHost tabHost = getTabHost();
        
        TabHost.TabSpec spec;
        
        Intent intent;

        // 地図
        intent = new Intent().setClass(this, MainActivity.class);
        spec = tabHost.newTabSpec("tab1")
                      .setIndicator(new CustomTabContentView(this, "地図から探す", R.drawable.map_red))
//                      .setIndicator(res.getString(R.string.label_graph), res.getDrawable(R.drawable.chart))
                      .setContent(intent);
        tabHost.addTab(spec);

        // ブックマーク
        intent = new Intent().setClass(this, FavoritesListActivity.class);
        intent.putExtra("rowid", index);
        spec = tabHost.newTabSpec("tab2")
                      .setIndicator(new CustomTabContentView(this, "お気に入り", R.drawable.heart))
//                      .setIndicator(res.getString(R.string.label_graph), res.getDrawable(R.drawable.chart))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        if (extras != null && extras.containsKey("currentTab")) {
            tabHost.setCurrentTab(extras.getInt("currentTab"));
        } else {
            tabHost.setCurrentTab(0);
        }
        
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            
            @Override
            public void onTabChanged(String tabId) {
                app.backCount = 0;
            }
        });
        
        tabHost.setPersistentDrawingCache(TabHost.PERSISTENT_NO_CACHE);
    }
    
    public class CustomTabContentView extends FrameLayout {
        LayoutInflater mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final static int NONE   = 0;
        final static int CENTER = 1;
        final static int LEFT   = 2;
        final static int RIGHT  = 3;
        
        public CustomTabContentView(Context context, String title, int icon) {
            this(context, title, icon, CENTER);
        }
        
        public CustomTabContentView(Context context, String title, int icon, int position) {
            super(context);
            View view = mInflater.inflate(R.layout.custom_tabwidget, this);
            ((TextView) view.findViewById(R.id.textview)).setText(title);
            ((ImageView) view.findViewById(R.id.imageview)).setImageResource(icon);
            
            switch(position) {
                case LEFT:
                    setPadding(0, 0, 2, 0);
                    break;
                case RIGHT:
                    setPadding(2, 0, 0, 0);
                    break;
                case CENTER:
                    setPadding(2, 0, 2, 0);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * backボタンを押したときの制御を追加
     */
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                app.backCount++;
                
                if (app.backCount >= 2) {
                    app.backCount = 0;
                    return super.dispatchKeyEvent(event);
                } else {
                    Toast.makeText(this, "[戻る]をもう一度押して終了", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else {
                app.backCount = 0;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
