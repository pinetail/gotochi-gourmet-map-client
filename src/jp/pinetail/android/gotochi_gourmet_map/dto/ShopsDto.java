package jp.pinetail.android.gotochi_gourmet_map.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShopsDto {
    public Integer Rowid = null;
    public String Name = null;
    public String Address = null;
    public String Tel = null;
    public String Category = null;
    public Integer TabelogId = null;
    public String BusinessHours = null;
    public String Holiday = null;
    public Double Lat = null;
    public Double Lng = null;
    public String Score = null;
    private String TabelogUrl = null;
	public String TabelogMobileUrl = null;
    public String Station = null;
    public String Memo = null;
    public Float Distance = 0.0F;
    public Float Bearing = 0.0F;
    
    public static final String TABELOG_PC_DOMAIN     = "http://r.tabelog.com/";
    public static final String TABELOG_MOBILE_DOMAIN = "http://m.tabelog.com/";
    
    //URL由来のストリーム
    protected InputStream is;
    
    protected ArrayList<ShopsDto> list;
    
    //ストリームを閉じる処理を共通メソッドとして定義
    public void close() {
        if (is != null) {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public ArrayList<ShopsDto> getList() {
        return this.list;
    }

    public void setData(String key, String value) {
    }
    
    public String getTabelogUrl() {
        return TABELOG_PC_DOMAIN + TabelogUrl;
    }

    public void setTabelogUrl(String tabelogUrl) {
        
        TabelogUrl = tabelogUrl.replace(TABELOG_PC_DOMAIN, "");
    }

    public String getTabelogMobileUrl() {
        return TABELOG_MOBILE_DOMAIN + TabelogMobileUrl;
    }

    public void setTabelogMobileUrl(String tabelogMobileUrl) {
        TabelogMobileUrl = tabelogMobileUrl;
    }


}

