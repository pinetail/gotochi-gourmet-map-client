package jp.pinetail.android.gotochi_gourmet_map.core;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.libs.ErrorReporter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.MapActivity;

public abstract class AbstractCoreMapActivity extends MapActivity {

    protected GoogleAnalyticsTracker tracker;
    public static final int PREFERENCE_INIT = 0;
    public static final int PREFERENCE_BOOTED = 1;
    public static String lastEvent = null;

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
    
     
    //データ保存
    protected void setState(int state) {
        // SharedPreferences設定を保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("InitState", state).commit();
     
    }
     
    //データ読み出し
    protected int getState() {
        // 読み込み
        int state;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        state = sp.getInt("InitState", PREFERENCE_INIT);
     
        return state;
    }
    

    protected final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            lastEvent = "onDoubleTap";
            return super.onDoubleTap(event);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            lastEvent = "onDoubleTapEvent";
            return super.onDoubleTapEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            lastEvent = "onDown";
            return super.onDown(event);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            lastEvent = "onFling";
            return super.onFling(event1, event2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            lastEvent = "onLongPress";
            super.onLongPress(event);
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            lastEvent = "onScroll";
            return super.onScroll(event1, event2, distanceX, distanceY);
        }

        @Override
        public void onShowPress(MotionEvent event) {
            lastEvent = "onShowPress";
            super.onShowPress(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            lastEvent = "onSingleTapConfirmed";
            return super.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            lastEvent = "onSingleTapUp";
            return super.onSingleTapUp(event);
        }    
    };
}
