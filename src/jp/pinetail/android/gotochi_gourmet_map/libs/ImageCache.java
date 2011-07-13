package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class ImageCache {
	private static HashMap<String,SoftReference<Bitmap>> cache = new HashMap<String,SoftReference<Bitmap>>();
	
	public static Bitmap getImage(String key) {
	    if (cache.containsKey(key)) {
	      SoftReference<Bitmap> ref = cache.get(key);
	      if (ref != null) {
	        return ref.get();
	      }
	    }
	    return null;
    }  
	 
	public static void setImage(String key, Bitmap image) {
	    cache.put(key, new SoftReference<Bitmap>(image));
	}
	 
	public static boolean hasImage(String key) {
	    return cache.containsKey(key);
	}
	 
	public static void clear() {
	    cache.clear();
	}
}