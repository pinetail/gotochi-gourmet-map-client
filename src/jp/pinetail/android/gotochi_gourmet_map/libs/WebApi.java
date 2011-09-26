package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.dto.CategoriesDto;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WebApi {

    public static InputStream getHttpInputStream(String url)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);
        HttpResponse response = client.execute(getMethod);

        if (response.getStatusLine().getStatusCode() < 400) {
            return response.getEntity().getContent();
        } else {
            throw new ClientProtocolException();
        }
    }
    
    public static InputStream requestHttpInputStream(HttpUriRequest request)
    throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
        Util.logging(String.valueOf(response.getStatusLine().getStatusCode()));
        if (response.getStatusLine().getStatusCode() < 400) {
            return response.getEntity().getContent();
        } else {
            throw new ClientProtocolException();
        }
    }
    
    public static Bitmap getImageBitmapOnWeb(String url, Bitmap bm) {
        
        BitmapFactory.Options bfo = new BitmapFactory.Options(); 
        InputStream in = null;
        try {
            bfo.inPurgeable = true;
            bfo.inPreferredConfig = Bitmap.Config.RGB_565;
//            bfo.inSampleSize = 2;
            
            in = getHttpInputStream(url);
            bm = BitmapFactory.decodeStream(in, null, bfo);
            in.close();
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static InputStream accessURL(String url) {
        try {
            InputStream is = getHttpInputStream(url);
            return is;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static ArrayList<CategoriesDto> getCategories(String url) {
        
        ArrayList<CategoriesDto> categories = new ArrayList<CategoriesDto>();
        InputStream in = null;
        try {
            in = getHttpInputStream(url);
            InputStreamReader objReader = new InputStreamReader(in);
            BufferedReader objBuf = new BufferedReader(objReader);
            StringBuilder objJson = new StringBuilder();
            String sLine;
            while((sLine = objBuf.readLine()) != null){
                objJson.append(sLine);
            }
            JSONArray jsonArray = new JSONArray(objJson.toString());
            in.close();
            for (int i = 0; i < jsonArray.length(); i++) {
            
                JSONObject jsonCategories = jsonArray.getJSONObject(i);
                JSONObject jsonCategory = jsonCategories.getJSONObject("category");
                
                CategoriesDto category = new CategoriesDto();
                category.name       = jsonCategory.getString("name");
                category.prefecture = jsonCategory.getString("prefecture");
                categories.add(category);
            }
            
        } catch (ClientProtocolException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        
        return categories;
    }
    
    public static boolean requestGgmapServer(HttpUriRequest url) {
        
        InputStream in = null;
        boolean res = false;
        try {
            in = requestHttpInputStream(url);
            InputStreamReader objReader = new InputStreamReader(in);
            BufferedReader objBuf = new BufferedReader(objReader);
            StringBuilder objJson = new StringBuilder();
            String sLine;
            while((sLine = objBuf.readLine()) != null){
                objJson.append(sLine);
            }
            Util.logging(objJson.toString());
            JSONArray jsonArray = new JSONArray(objJson.toString());
            in.close();
            
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return res;
    }
}
