package jp.pinetail.android.gotochi_gourmet_map;

import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class DetailTabActivity extends TabActivity {

	private Integer index = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_tab);
        
        Resources res = getResources();
        
        Bundle extras=getIntent().getExtras();
        if (extras != null) {
            index = extras.getInt("rowid");
        }
        
        // 戻るボタン
        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                finish();
            }
        });
        
        final TabHost tabHost = getTabHost();
        
        TabHost.TabSpec spec;
        
        Intent intent;

        // 履歴
        intent = new Intent().setClass(this, DetailActivity.class);
        intent.putExtra("rowid", index);
        spec = tabHost.newTabSpec("tab1")
                      .setIndicator(new CustomTabContentView(this, "店舗詳細", R.drawable.info))
//                      .setIndicator(res.getString(R.string.label_graph), res.getDrawable(R.drawable.chart))
                      .setContent(intent);
        tabHost.addTab(spec);

        // コメント
        intent = new Intent().setClass(this, CommentsActivity.class);
        intent.putExtra("rowid", index);
        spec = tabHost.newTabSpec("tab2")
                      .setIndicator(new CustomTabContentView(this, "コメント", R.drawable.comment))
//                      .setIndicator(res.getString(R.string.label_graph), res.getDrawable(R.drawable.chart))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        
        // ルート検索
        intent = new Intent().setClass(this, DetailActivity.class);
        intent.putExtra("rowid", index);
        intent.putExtra("mode", "route");
        spec = tabHost.newTabSpec("tab3")
                      .setIndicator(new CustomTabContentView(this, "ルート検索", R.drawable.green_flag))
                      .setContent(intent);
        tabHost.addTab(spec);

        // ブラウザ
        intent = new Intent().setClass(this, DetailActivity.class);
        intent.putExtra("rowid", index);
        intent.putExtra("mode", "browser");
        spec = tabHost.newTabSpec("tab4")
                      .setIndicator(new CustomTabContentView(this, "ブラウザ", R.drawable.browser))
                      .setContent(intent);
        tabHost.addTab(spec);

        
        if (extras != null && extras.containsKey("currentTab")) {
            tabHost.setCurrentTab(extras.getInt("currentTab"));
        } else {
            tabHost.setCurrentTab(0);
        }
        
        tabHost.setPersistentDrawingCache(TabHost.PERSISTENT_NO_CACHE);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            // タブがクリックされた時のハンドラ
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab3") || tabId.equals("tab4")) {
                    tabHost.setCurrentTab(0);
                }

            }
        });
               

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
    
}
