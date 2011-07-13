package jp.pinetail.android.gotochi_gourmet_map.libs;

import jp.pinetail.android.gotochi_gourmet_map.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class CategoryImage {

    private static Drawable[] images = new Drawable[24];

    public CategoryImage(Context context) {
        images[0] = context.getResources().getDrawable(R.drawable.icon_gourmet);
        images[1] = context.getResources().getDrawable(R.drawable.icon_02);
        images[2] = context.getResources().getDrawable(R.drawable.icon_03);
        images[3] = context.getResources().getDrawable(R.drawable.icon_04);
        images[4] = context.getResources().getDrawable(R.drawable.icon_05);
        images[5] = context.getResources().getDrawable(R.drawable.icon_06);
        images[6] = context.getResources().getDrawable(R.drawable.icon_07);
        images[7] = context.getResources().getDrawable(R.drawable.icon_08);
        images[8] = context.getResources().getDrawable(R.drawable.icon_09);
    }
    
    public Drawable getDrawable(String category) {
    	Drawable draw = null;
    	
        if (category.indexOf("daikokudrug") != -1) {
            draw = images[0];
        } else if (category.indexOf("kokumin") != -1) {
            draw = images[1];
        } else if (category.indexOf("sugi") != -1) {
            draw = images[2];
        } else if (category.indexOf("tsuruha") != -1) {
            draw = images[3];
        } else if (category.indexOf("sundrug") != -1) {
            draw = images[4];
        } else if (category.indexOf("kirindo") != -1) {
            draw = images[5];
        } else if (category.indexOf("midoriyakuhin") != -1) {
            draw = images[6];
        } else if (category.indexOf("matsukiyo") != -1) {
            draw = images[7];
        } else {
            draw = images[0];
        }
    	
    	return draw;
    }
}
