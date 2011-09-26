package jp.pinetail.android.gotochi_gourmet_map.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SpotsDto {
	public Integer Rowid = null;
	public Integer EntryId = null;
	public String Title = null;
	public String Category = null;
	public String Address = null;
	public Float Latitude = null;
	public Float Longitude = null;
	public String UrlPc = null;
	public String UrlMobile = null;
	public String Wireless = null;
	public String PowerSupply = null;
	public String Tel = null;
	public String Other = null;
	public String ReferenceUrls = null;
	public String Images = null;
	
	//URL由来のストリーム
    protected InputStream is;
    
    protected ArrayList<SpotsDto> list;
    
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
	
	public ArrayList<SpotsDto> getGSInfoList() {
		return this.list;
	}

	public void setData(String key, String value) {
	}
	
}

