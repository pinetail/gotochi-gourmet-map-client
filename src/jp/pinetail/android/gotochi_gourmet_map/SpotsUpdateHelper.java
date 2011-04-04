package jp.pinetail.android.gotochi_gourmet_map;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

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

public class SpotsUpdateHelper {

	private SharedPreferences pref;
	public String latest_date;
	private String fname;
	private String zipname;
	private Context context;
	private String fname_suffix = "_poweronly";
	public String msg = "ネットワークに接続できません。電波状況を確認してください。";
	
	SpotsUpdateHelper(Context context, SharedPreferences pref) {
		this.pref = pref;
		this.context = context;
		
		if (Util.isPaymentApp(context) == true) {
			fname_suffix = "_all";
		} else {
			fname_suffix = "_poweronly";			
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
	        HttpGet objGet = new HttpGet("http://oasis.mogya.com/api/latest_csv_version");
	        
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
//		objHttp.getCredentialsProvider().setCredentials(
//		          AuthScope.ANY, new UsernamePasswordCredentials("test", "kdlkdl"));
		objHttp.getCredentialsProvider().setCredentials(
		          AuthScope.ANY, new UsernamePasswordCredentials("android", "4y}Q8K1VJ*"));
		
	    HttpParams params = objHttp.getParams();  
	    HttpConnectionParams.setConnectionTimeout(params, 3000); //接続のタイムアウト  
	    HttpConnectionParams.setSoTimeout(params, 30000); //データ取得のタイムアウト  
	    Boolean res = false;
		msg = "サーバーに接続できませんでした。時間をおいて再度お試しください。";
	    
	    try {
//	        HttpGet objGet = new HttpGet("http://www.shoptweet.info/" + latest_date + fname_suffix + ".zip");
	        HttpGet objGet = new HttpGet("http://oasis.mogya.com/data/" + latest_date + fname_suffix + ".zip");
	        
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
	    			fout = new File("/sdcard/dengenkensaku/csv/");
	    			fout.mkdirs();
	    		}
	    		
	    		zipname = fout.getAbsolutePath() + "/spots_" + latest_date + fname_suffix + ".zip";
//	    		File f = new File(zipname);
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
	
	public String getSpotFileName() {
		return fname;
	}
	
	public String getSpotZipFileName() {
		return zipname;
	}
	
	public void removeFile(String fname) {
		File f = new File(fname);
		f.delete();
	}


}
