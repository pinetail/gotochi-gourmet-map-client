package jp.pinetail.android.gotochi_gourmet_map;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsCategoryActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_category);
	}
		
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		
	    super.onCreateOptionsMenu( menu );

	    // メニューアイテムを追加
	    MenuItem item0 = menu.add( 0, 0, 0, "すべて選択" );
	    MenuItem item1 = menu.add( 0, 1, 0, "すべて解除" );
/*
	    // 追加したメニューアイテムのアイコンを設定
	    item0.setIcon( android.R.drawable.ic_menu_mylocation);
	    item1.setIcon( android.R.drawable.ic_menu_preferences );
	*/    
	    return true;
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item){

		PreferenceScreen screen = (PreferenceScreen) findPreference("category");
    	int cnt = screen.getPreferenceCount();

		switch(item.getItemId()){  
	    case 0:
	    	for(int index = 0; index < cnt; index++) {
	    		CheckBoxPreference pref = (CheckBoxPreference) screen.findPreference(screen.getPreference(index).getKey());
            	pref.setChecked(true);
	    	}
            break;
	    case 1:
	    	for(int index = 0; index < cnt; index++) {
	    		CheckBoxPreference pref = (CheckBoxPreference) screen.findPreference(screen.getPreference(index).getKey());
            	pref.setChecked(false);
	    	}
            break;
	    }  
	    return true;  
	}
	
}
