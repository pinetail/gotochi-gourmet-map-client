package jp.pinetail.android.gotochi_gourmet_map.libs;

import jp.pinetail.android.gotochi_gourmet_map.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class CategoryImage {

    private static Drawable[] images = new Drawable[24];

    public CategoryImage(Context context) {
        images[0] = context.getResources().getDrawable(R.drawable.icon_gourmet);
    }
    
    public Drawable getDrawable(String category) {
    	Drawable draw = null;
    	
        if (category.indexOf("daikokudrug") != -1) {
            draw = images[0];
        } else {
            draw = images[0];
        }
    	
    	return draw;
    }
}
