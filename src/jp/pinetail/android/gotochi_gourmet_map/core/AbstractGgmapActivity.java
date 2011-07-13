package jp.pinetail.android.gotochi_gourmet_map.core;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.libs.ErrorReporter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractGgmapActivity extends Activity {

    protected AlertDialog alertDialog = null;
    private Handler mHandler = new Handler();
    protected static final Integer pressed_color = Color.argb(80, 255, 255, 255);
    public GoogleAnalyticsTracker tracker;

    protected void init() {
        ErrorReporter.setup(this);
        ErrorReporter.bugreport(this);
        
        tracker = GoogleAnalyticsTracker.getInstance();
        
        // Start the tracker in manual dispatch mode...
        tracker.start(this.getResources().getString(R.string.tracking_code), 20, this);
        tracker.trackPageView("/" + this.getClass().getName());
        tracker.setCustomVar(1, "Model", Build.MODEL, 1);
        try {
            tracker.setCustomVar(2, "Version", getPackageManager().getPackageInfo(getPackageName(), 1 ).versionName, 1);
        } catch (NameNotFoundException e) {}
        
    }
    
    
    @Override
    protected void onDestroy() {
        // Stop the tracker when it is no longer needed.
        tracker.stop();
        
        super.onDestroy();
        
    	ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
    	cleanupView(findViewById(root.getId()));

    }
    
    /**
     * 指定したビュー階層内のドローワブルをクリアする。
     * （ドローワブルをのコールバックメソッドによるアクティビティのリークを防ぐため）
     * @param view
     */
    public final void cleanupView(View view) {
        
        if(view instanceof ImageButton) {
            ImageButton ib = (ImageButton)view;
            ib.setImageDrawable(null);
        } else if(view instanceof ImageView) {
            ImageView iv = (ImageView)view;
            iv.setImageDrawable(null);
        } else if(view instanceof SeekBar) {
            SeekBar sb = (SeekBar)view;
            sb.setProgressDrawable(null);
            sb.setThumb(null);
        }
        view.setBackgroundDrawable(null);
        if(view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)view;
            int size = vg.getChildCount();
            for(int i = 0; i < size; i++) {
                cleanupView(vg.getChildAt(i));
            }
        }
    }

    
}
