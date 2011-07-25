package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import jp.pinetail.android.gotochi_gourmet_map.R;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class ShopsUpdateHelper {

    private SharedPreferences pref;
    public String latest_date;
    private String fname;
    private String zipname;
    private Context context;
    public String msg = "ネットワークに接続できません。電波状況を確認してください。";
    private static String api_url;
    private static String file_postfix = "";
    private static String basic_auth_id = "";
    private static String basic_auth_passwd = "";

    public ShopsUpdateHelper(Context context, SharedPreferences pref) {
        this.pref = pref;
        this.context = context;
        this.api_url = context.getResources().getString(R.string.api_url);
        
        if (Util.isDeguggable(context)) {
            file_postfix = "_debug";
        }
    }
        
    public boolean checkUpdate() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = cm.getActiveNetworkInfo();
        
        if (info == null) {
            msg = "ネットワークに接続できません。電波状況を確認してください。";
            return false;
        }
        
        DefaultHttpClient objHttp = new DefaultHttpClient();
/*
        objHttp.getCredentialsProvider().setCredentials(
                  AuthScope.ANY, new UsernamePasswordCredentials("android", "4y}Q8K1VJ*"));
*/
        HttpParams params = objHttp.getParams();  
        HttpConnectionParams.setConnectionTimeout(params, 3000); //接続のタイムアウト  
        HttpConnectionParams.setSoTimeout(params, 3000); //データ取得のタイムアウト  
        String sReturn = "";
        try {
            HttpGet objGet = new HttpGet(api_url + "system/latest_version" + file_postfix + ".json");
            
            HttpResponse objResponse = objHttp.execute(objGet);
            if (objResponse.getStatusLine().getStatusCode() < 400){
                InputStream objStream = objResponse.getEntity().getContent();
                InputStreamReader objReader = new InputStreamReader(objStream);
                BufferedReader objBuf = new BufferedReader(objReader);
                StringBuilder obj = new StringBuilder();
                String sLine;
                while((sLine = objBuf.readLine()) != null){
                    obj.append(sLine);
                }
                sReturn = obj.toString();
                objStream.close();
                
                String latest_date = pref.getString("latest_date", null);

                if (latest_date == null || latest_date.equals(sReturn) == false) {
                    this.latest_date = sReturn;
                    msg = "最新のデータがあります。";
                    return true;
                } else {
                    msg = "お使いのデータは最新です。";
                    return false;
                }
            }  
        } catch (IOException e) {
            msg = "サーバーに接続できませんでした。時間をおいて再度お試しください。";
            return false;
        }
        
        msg = "サーバーに接続できませんでした。時間をおいて再度お試しください。";
        return false;
    }
    
    public boolean downloadUpdateCsv(ProgressDialog dialog) {
        DefaultHttpClient objHttp = new DefaultHttpClient();
//        objHttp.getCredentialsProvider().setCredentials(
//                  AuthScope.ANY, new UsernamePasswordCredentials("test", "kdlkdl"));
        
        HttpParams params = objHttp.getParams();  
        HttpConnectionParams.setConnectionTimeout(params, 3000); //接続のタイムアウト  
        HttpConnectionParams.setSoTimeout(params, 30000); //データ取得のタイムアウト  
        Boolean res = false;
        msg = "サーバーに接続できませんでした。時間をおいて再度お試しください。";
        
        try {
            HttpGet objGet = new HttpGet(api_url + "system/files/" + latest_date + ".zip");
            Util.logging(api_url + "system/files/" + latest_date + ".zip");
            HttpResponse objResponse = objHttp.execute(objGet);  
            if (objResponse.getStatusLine().getStatusCode() < 400){
                // レスポンスボディ（ファイルデータ)
                int size = (int) objResponse.getEntity().getContentLength();
                InputStream is = objResponse.getEntity().getContent();
                // 保存先の決定
                String status = Environment.getExternalStorageState();
                File fout;
                
                if (!status.equals(Environment.MEDIA_MOUNTED)) {
                    fout = Environment.getDataDirectory();
                } else {
                    fout = new File("/sdcard/ggmap/csv/");
                    fout.mkdirs();
                }
                
                zipname = fout.getAbsolutePath() + "/" + latest_date + ".zip";
//                File f = new File(zipname);
                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(zipname));
                    byte[] buffer = new byte[8192];
                    int wsize;
                    int sum = 0;
                    while ((wsize = is.read(buffer)) != -1) {
                        os.write(buffer, 0, wsize);
                        sum += wsize;
                        Util.logging(String.valueOf((double) sum / size));
                        dialog.setProgress((int) ((double) sum / size * 50));
                    }
                    
                    dialog.setProgress(50);
                    res = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                if (res == true) {
                    List<String> files = Util.unzip(fout, zipname);
                    Util.logging(String.valueOf(files.size()));
                    if (files.size() > 0) {
                        fname = fout.getAbsolutePath() + "/" + files.get(0);
                    } else {
                        msg = "ファイルの保存に失敗しました。";
                        return false;
                    }
                }
            }  
        } catch (IOException e) {
            msg = "サーバーに接続できませんでした。時間をおいて再度お試しください。";
            return false;
        }
        
        return res;
    }
    
    public String getShopFileName() {
        return fname;
    }
    
    public String getShopZipFileName() {
        return zipname;
    }
    
    public void removeFile(String fname) {
        File f = new File(fname);
        f.delete();
    }


}
