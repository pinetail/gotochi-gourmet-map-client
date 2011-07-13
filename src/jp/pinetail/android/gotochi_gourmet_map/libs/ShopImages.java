package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ShopImages {

	public String ImageUrlS = null;
	public String ImageUrlM = null;
	public String ImageUrlL = null;
	public String ImageComment = null;
	public String PcSiteUrl = null;
	public String MobileSiteUrl = null;
	public String ImageStatus = null;
	public String UserName = null;
	
    public void setData(String key, String value) {

        if (key.compareTo("ImageUrlS") == 0) {
            this.ImageUrlS = value;
        } else if (key.compareTo("ImageUrlM") == 0) {
            this.ImageUrlM = value;
        } else if (key.compareTo("ImageUrlL") == 0) {
            this.ImageUrlL = value;
        } else if (key.compareTo("ImageComment") == 0) {
            this.ImageComment = value;
        } else if (key.compareTo("PcSiteUrl") == 0) {
            this.PcSiteUrl = value;
        } else if (key.compareTo("MobileSiteUrl") == 0) {
            this.MobileSiteUrl = value;
        } else if (key.compareTo("ImageStatus") == 0) {
            this.ImageStatus = value;
        } else if (key.compareTo("UserName") == 0) {
            this.UserName = value;
        }
    }

}
