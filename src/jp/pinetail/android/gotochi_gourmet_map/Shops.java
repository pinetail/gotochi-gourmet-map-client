package jp.pinetail.android.gotochi_gourmet_map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Shops {
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
    public String TabelogUrl = null;
    public String TabelogMobileUrl = null;
    public String Station = null;
    public String Memo = null;
    
    //URL由来のストリーム
    protected InputStream is;
    
    protected ArrayList<Shops> list;
    
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
    
    public ArrayList<Shops> getList() {
        return this.list;
    }

    public void setData(String key, String value) {
    }
    
}

