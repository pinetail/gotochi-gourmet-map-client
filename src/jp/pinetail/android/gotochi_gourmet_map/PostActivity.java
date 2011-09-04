package jp.pinetail.android.gotochi_gourmet_map;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.pinetail.android.gotochi_gourmet_map.core.AbstractGgmapActivity;
import jp.pinetail.android.gotochi_gourmet_map.libs.Categories;
import jp.pinetail.android.gotochi_gourmet_map.libs.Util;
import jp.pinetail.android.gotochi_gourmet_map.libs.WebApi;
import jp.pinetail.android.gotochi_gourmet_map.libs.XmlParserFromUrl;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PostActivity extends AbstractGgmapActivity {

    private ProgressDialog dialog;
    private EditText editName;
    private EditText editTabelogUrl;
    private Spinner spinCategory;
    private EditText editEtcCategory;
    public static final String TABELOG_URL_PATTERN = "http://s\\.tabelog\\.com/smartphone/restaurant_detail/top/\\?rcd=([0-9]+)(.*)";
    public static final String CATEGORY_URL = "http://ggmap.pinetail.jp/api/get_categories.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.post);
        
        // 画面初期化
        init();
        
        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
            Bundle extras = getIntent().getExtras();
            
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                if (extras.getString(Intent.EXTRA_TEXT).matches(TABELOG_URL_PATTERN) == true) {
                    editName.setText(extras.getString(Intent.EXTRA_SUBJECT).replace("[食べログ]", ""));
                    editTabelogUrl.setText(extras.getString(Intent.EXTRA_TEXT));
                } else {
                    Toast.makeText(this, "食べログの店舗ページではありません", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "不正なアクセスです", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    
    /**
     * 画面初期化
     */
    protected void init() {
        
        super.init();
        
        spinCategory    = (Spinner) findViewById(R.id.spin_category);
        editName        = (EditText) findViewById(R.id.edit_name);
        editTabelogUrl  = (EditText) findViewById(R.id.edit_tabelog_url);
        editEtcCategory = (EditText) findViewById(R.id.edit_etc_category_name);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("");
        
        ArrayList<Categories> categories = WebApi.getCategories(CATEGORY_URL);
        for (Categories category : categories) {
            adapter.add("[" + category.prefecture + "] " + category.name);
        }
        adapter.add("その他");
        spinCategory.setAdapter(adapter);
        spinCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                
                Spinner spinner = (Spinner) parent;
                
                String item = (String) spinner.getSelectedItem();
                
                if (item.equals("その他")) {
                    editEtcCategory.setVisibility(View.VISIBLE);
                } else {
                    editEtcCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO 自動生成されたメソッド・スタブ
                
            }
        });
        
        // 戻るボタン
        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 投稿するボタン
        Button postButton = (Button) findViewById(R.id.btn_post);
        postButton.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                postExecute();
                
                // イベントトラック（地点格投稿）
                tracker.trackEvent(
                    "Post",     // Category
                    "Post",     // Action
                    "Post",      // Label
                    0);

            }
        });

    }
    
    /**
     * 「投稿するボタン」クリック時の処理
     * 
     */
    private void postExecute() {
        //プログレスダイアログを表示
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getResources().getText(R.string.dialog_message_posting_data));
        dialog.show();

        Thread thread = new Thread() {
            public void run() {
                try {
                    // 入力チェック
                    checkEntryData();
                    
                    // データPOST
                    post();
                    
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            
                            if (!isFinishing()) {

                                // 結果の表示
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostActivity.this);
                                alertDialogBuilder.setMessage("登録を受付ました。\n登録いただいた情報は管理者が確認でき次第、反映されます。");
                            
                                // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックを登録します
                                alertDialogBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }});
                                
                                // アラートダイアログのキャンセルが可能かどうかを設定します
                                alertDialogBuilder.setCancelable(true);
                                alertDialogBuilder.show();
                            }
                        }
                    });

                } catch (final PostException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            
                            // エラーメッセージの表示
                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
                
            }
        };
        thread.start();
    }
    
    /**
     * 入力データのチェック
     * 
     * @return
     * @throws Exception
     */
    private boolean checkEntryData() throws PostException {
        
        ArrayList<String> err_msg = new ArrayList<String>();
        
        String spinSelectItem = (String) spinCategory.getSelectedItem();
        if (spinSelectItem.length() == 0) {
            err_msg.add("ジャンルを選択してください。");
        } else if (spinSelectItem.equals("その他") && editEtcCategory.length() == 0) {
            err_msg.add("ジャンルを入力してください。");
        }
        
        // その他が200文字以上の場合
        if (editEtcCategory.getText().length() > 200) {
            err_msg.add("ジャンルは200文字以内で入力してください。");
        }

        if (err_msg.size() > 0) {
            String msg = "";
            String[] array = (String[]) err_msg.toArray(new String[0]); 

            //配列の内容表示 
            for (int i = 0; i < array.length; i++) { 
                msg += array[i];
                if (i < array.length-1) {
                    msg += "\n"; 
                }
            } 
            throw new PostException(msg);
        }
        return true;
    }
    
    /**
     * 店舗の登録
     * 
     * @return
     * @throws Exception
     */
    private boolean post() throws PostException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PostActivity.this);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = cm.getActiveNetworkInfo();
        
        if (info == null) {
            throw new PostException("ネットワークに接続できません。電波状況を確認してください。");
        }
        
        DefaultHttpClient objHttp = new DefaultHttpClient();
        HttpParams params = objHttp.getParams();  
        HttpConnectionParams.setConnectionTimeout(params, 3000); //接続のタイムアウト  
        HttpConnectionParams.setSoTimeout(params, 3000); //データ取得のタイムアウト  
        
        String name        = URLEncoder.encode(String.valueOf(editName.getText()));
        String tabelogUrl  = URLEncoder.encode(String.valueOf(editTabelogUrl.getText()));
        String etcCategory = URLEncoder.encode(String.valueOf(editEtcCategory.getText()));
        String tabelogId   = "";
        
        Matcher matcher = Pattern.compile(TABELOG_URL_PATTERN).matcher(String.valueOf(editTabelogUrl.getText()));
        while (matcher.find()) {
            tabelogId = matcher.group(1);
        }

        String category = URLEncoder.encode(spinCategory.getSelectedItem().toString());
        if (spinCategory.getSelectedItem().toString().equals("その他")) {
            category = etcCategory;
        }

        String url = "http://ggmap.pinetail.jp/api/create.xml?" + 
                     "shops_request[name]=" + name +"&shops_request[tabelog_url]=" + tabelogUrl + 
                     "&shops_request[gotochi_category]=" + category + "&shops_request[tabelog_id]=" + tabelogId;

        Util.logging(url);
        
        XmlParserFromUrl xml = new XmlParserFromUrl();

        for (int i = 0; i< 3;i++) {
            
            byte[] byteArray = Util.getByteArrayFromURL(url, "POST");
            if (byteArray == null) {
                continue;
            }
            return true;
        }
        
        throw new PostException("登録に失敗しました。");
    }
    
    public class PostException extends Exception {
        public PostException(String message) {
            super(message);
        }
    }

}
