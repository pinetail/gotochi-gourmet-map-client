package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.IOException;
import java.io.InputStream;


import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
            return null;
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
}
