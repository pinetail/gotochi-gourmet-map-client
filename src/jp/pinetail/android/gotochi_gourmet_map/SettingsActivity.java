package jp.pinetail.android.gotochi_gourmet_map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//		Log.d("a", "aaa:" + key);	
	}
}
