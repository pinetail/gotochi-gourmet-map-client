package jp.pinetail.android.gotochi_gourmet_map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.libs.Comments;
import jp.pinetail.android.gotochi_gourmet_map.libs.CommentsAdapter;
import jp.pinetail.android.gotochi_gourmet_map.libs.DatabaseHelper;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopImages;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsAdapter;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsController;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsDao;
import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import jp.pinetail.android.gotochi_gourmet_map.libs.XmlParserFromUrl;
import jp.pinetail.android.gotochi_gourmet_map.libs.ShopsController.ImageAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CommentsActivity extends AbstractGgmapActivity implements OnScrollListener {
    private CommentsAdapter adapter = null;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ShopsDao shopsDao = null;
    private Shops info = null;
    private final Handler mHandler = new Handler();
    private View mFooter = null;
    private ListView mListView;
    private static String tabelogAccessKey = null;
    private  int maxCount = 1;
    private int mCount = 1;
    private static ArrayList<Comments> items;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_list);
        
        tabelogAccessKey = getResources().getString(R.string.tabelog_access_key);
        
        init();
        dbHelper = new DatabaseHelper(this);

        Bundle extras=getIntent().getExtras();
        if (extras != null) {
            Integer index = extras.getInt("rowid");
            Util.logging(index.toString());
            db = dbHelper.getReadableDatabase();
            shopsDao = new ShopsDao(db,CommentsActivity.this);
            info = shopsDao.findById(index);
            db.close();
            
            if (info == null) {
                Toast.makeText(this, "コメント情報が取得できません", Toast.LENGTH_SHORT).show();
                finish();
            }
            
            getComments();
        }
    }
    
    private void getComments() {
    	
        //プログレスダイアログを表示
        dialog = new ProgressDialog(CommentsActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("コメント取得中");
        dialog.show();

        new Thread(new Runnable() {
            public void run() {
            	

                String url = "http://api.tabelog.com/Ver1/ReviewSearch/?Key=" +
                tabelogAccessKey + "&Rcd=" + info.TabelogId;
                
                XmlParserFromUrl xml = new XmlParserFromUrl();
            
                byte[] byteArray = Util.getByteArrayFromURL(url, "GET");
                if (byteArray == null) {
                    
                }
                String data = new String(byteArray);
                
                items = xml.getComments(data);
                
                maxCount = (xml.commentsCnt + 20 - 1) / 20;
                Util.logging(String.valueOf(xml.commentsCnt));
                Util.logging(String.valueOf(maxCount));
                
                mHandler.post(new Runnable() {
                    public void run() {
                        if (items.size() > 0) {
                            ListView listView = (ListView) findViewById(R.id.savedList);
                            if (mCount < maxCount) {
                                listView.addFooterView(getFooter());
                            }
                            listView.setOnScrollListener(CommentsActivity.this);

                            adapter = new CommentsAdapter(CommentsActivity.this, R.layout.list, items);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new OnItemClickListener() {
                     
                                @Override
                                public void onItemClick(AdapterView<?> adapter,
                                        View view, int position, long id) {
                                	
                                	if(view == getFooter()) {
                                		ProgressBar progress = (ProgressBar) view.findViewById(R.id.progress);
                                		progress.setVisibility(View.VISIBLE);
                                		additionalReading();
                                	} else {
                                		final Comments item = items.get(position);
                                		
                                        // イベントトラック（ブラウザ）
                                        tracker.trackEvent(
                                            "Detail",      // Category
                                            "Browser",     // Action
                                            info.TabelogId.toString(), // Label
                                            0);
                                        
                                        Intent intent = new Intent(); 
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(item.PcSiteUrl));
                                        startActivity(intent);
                                	}
                                }
                            });
                        }
                        
                        dialog.dismiss();
                    }
                });
            }
        }).start();

    }
    
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
//            additionalReading();
        }
    }
    
    private AsyncTask<Long, Void, Void> mTask;

    private void additionalReading() {
        // 読み込み回数が最大値以上ならスキップ。フッタを消す
        if (mCount >= maxCount) {
            invisibleFooter();
            return;
        }
        // 既に読み込み中ならスキップ
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mTask = new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long[] params) {
                try {
                    addListData();
                    Thread.sleep(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            };

            protected void onPostExecute(Void result) {
                getListView().invalidateViews();
                if (mCount >= maxCount) {
                    invisibleFooter();
                }
        		ProgressBar progress = (ProgressBar) getFooter().findViewById(R.id.progress);
        		progress.setVisibility(View.INVISIBLE);

            };
        }.execute(Math.abs(new Random(System.currentTimeMillis()).nextLong() % 3000));
    }
    
    private ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.savedList);
        }
        return mListView;
    }

    private View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer,
                    null);
        }
        return mFooter;
    }

    private void visibleFooter() {
        getListView().addFooterView(getFooter());
    }

    private void invisibleFooter() {
        getListView().removeFooterView(getFooter());
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO 自動生成されたメソッド・スタブ
        
    }
    
    private void addListData() {
        String url = "http://api.tabelog.com/Ver1/ReviewSearch/?Key=" +
        tabelogAccessKey + "&Rcd=" + info.TabelogId + "&PageNum=" + (mCount + 1);
        
        XmlParserFromUrl xml = new XmlParserFromUrl();
    
        byte[] byteArray = Util.getByteArrayFromURL(url, "GET");
        if (byteArray == null) {
            
        }
        String data = new String(byteArray);
        
        items.addAll(xml.getComments(data));
        mCount++;
    }
}
