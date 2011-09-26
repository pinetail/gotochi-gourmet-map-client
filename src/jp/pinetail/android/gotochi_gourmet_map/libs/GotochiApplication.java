package jp.pinetail.android.gotochi_gourmet_map.libs;

import android.app.Application;

public class GotochiApplication extends Application {

    private double lat = 0;
    private double lng = 0;
    public int backCount = 0;
    
    public double getLat() {
        return lat;
    }
    
    public void setLat(double lat) {
        this.lat = lat;
    }
    
    public double getLng() {
        return lng;
    }
    
    public void setLng(double lng) {
        this.lng = lng;
    }
}
