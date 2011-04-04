package jp.pinetail.android.gotochi_gourmet_map;

import java.util.ArrayList;
import java.util.List;

import jp.co.nobot.libYieldMaker.libYieldMaker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {
	
	static Object lock = new Object();
    private static final int E6 = 1000000;
    private MapController mMapController = null;
	private MapView mMapView = null;
	private LocationOverlay overlay;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private SpotsDao spotsDao = null;
	private Drawable[] images = new Drawable[24];
    private Spots point_data;
    private PinItemizedOverlay pinOverlay = null;
	public static String lastEvent = null;
	private GestureDetector gestureDetector;
	public SpotsUpdateHelper helper;
	private static Thread thread = null;

    private Handler mHandler = new Handler();
    
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
//	    requestWindowFeature(Window.FEATURE_PROGRESS);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    setProgressBarIndeterminate(false);
	    
	    gestureDetector = new GestureDetector(MainActivity.this, simpleOnGestureListener);

        mMapView = (MapView) findViewById(R.id.main_map);
	    mMapView.setBuiltInZoomControls(true);
	    mMapView.getZoomButtonsController().setOnZoomListener(new OnZoomListener() {
            @Override
            public void onZoom(boolean zoomIn) {
                if(zoomIn){
                    mMapController.zoomIn();                    
                } else {
                    mMapController.zoomOut();
                }
                
                if (mMapView.getZoomLevel() < Integer.parseInt(getResources().getString(R.string.def_zoom_level))) {
            		Toast.makeText(MainActivity.this, "地図を拡大してください", Toast.LENGTH_SHORT).show();	    			
                }
                drawSpots();
            }

			@Override
			public void onVisibilityChanged(boolean visible) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
	    });

        mMapController = mMapView.getController();

	    if (mMapView.getZoomLevel() < Integer.parseInt(getResources().getString(R.string.def_zoom_level))) {
	    	mMapController.setZoom(Integer.parseInt(getResources().getString(R.string.def_zoom_level)));
        }
        
		// 今回の主役。有効にすることでGPSの取得が可能に
		overlay = new LocationOverlay(getApplicationContext(), mMapView);
		overlay.enableMyLocation();
		overlay.enableCompass();

		// GPS取得が可能な状態になり、GPS初取得時の動作を決定（らしい）
		overlay.runOnFirstFix(new Runnable(){
			public void run() {

				// animateTo(GeoPoint)で指定GeoPoint位置に移動
				// この場合、画面中央がGPS取得による現在位置になる
				
				mMapView.getController().animateTo(overlay.getMyLocation());
				overlay.setMyLocation(overlay.getLastFix());
			}
		});
		
    	// MapView上に表示したいビットマップ情報を、リソースから取得
		images[0] = getResources().getDrawable(R.drawable.icon_01);
		images[1] = getResources().getDrawable(R.drawable.icon_02);
		images[2] = getResources().getDrawable(R.drawable.icon_03);
		images[3] = getResources().getDrawable(R.drawable.icon_04);
		images[4] = getResources().getDrawable(R.drawable.icon_05);
		images[5] = getResources().getDrawable(R.drawable.icon_06);
		images[6] = getResources().getDrawable(R.drawable.icon_07);
		images[7] = getResources().getDrawable(R.drawable.icon_08);
		images[8] = getResources().getDrawable(R.drawable.icon_09);
		images[9] = getResources().getDrawable(R.drawable.icon_10);
		images[10] = getResources().getDrawable(R.drawable.icon_11);
		images[11] = getResources().getDrawable(R.drawable.icon_12);
		images[12] = getResources().getDrawable(R.drawable.icon_13);
		images[13] = getResources().getDrawable(R.drawable.icon_14);
		images[14] = getResources().getDrawable(R.drawable.icon_15);
		images[15] = getResources().getDrawable(R.drawable.icon_16);
		images[16] = getResources().getDrawable(R.drawable.icon_17);
		images[17] = getResources().getDrawable(R.drawable.icon_18);
		images[18] = getResources().getDrawable(R.drawable.icon_19);
		images[19] = getResources().getDrawable(R.drawable.icon_20);
		images[20] = getResources().getDrawable(R.drawable.icon_21);
		images[21] = getResources().getDrawable(R.drawable.icon_22);
		images[22] = getResources().getDrawable(R.drawable.icon_23);
		images[23] = getResources().getDrawable(R.drawable.icon_24);

		dbHelper = new DatabaseHelper(MainActivity.this);

	    start();
    }
	
	private void start() {
		showProgressView();
		
		new Thread(new Runnable() {
			public void run() {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				final boolean status = dbHelper.getImportStatus();
				db.close();
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

		    	mHandler.post(new Runnable() {
		    		public void run() {
		    			hideProgressView();
		    			
		    			if (Util.isPaymentApp(MainActivity.this) == false) {
			    	    	LinearLayout head = (LinearLayout) findViewById(R.id.header_ad);
			    	        head.setVisibility(View.VISIBLE);
			    	        libYieldMaker mv = (libYieldMaker)findViewById(R.id.admakerview);
			    	        mv.setActivity(MainActivity.this);
			    	        mv.setUrl("http://images.ad-maker.info/apps/w34hfu5skwtt.html");
			    	        mv.startView();
		    			}
		    	        
		    			mMapView.invalidate();
		    			
		    			if (status == false) {
					    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
					        alertDialogBuilder.setMessage("データの登録に失敗しました。\n管理者までお問い合わせください。");
					        
				        	// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックを登録します
				        	alertDialogBuilder.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
				        	    @Override
				        	    public void onClick(DialogInterface dialog, int which) {
				        	        
				        	    }});
				        	
				        	// アラートダイアログのキャンセルが可能かどうかを設定します
				        	alertDialogBuilder.setCancelable(true);

					        alertDialogBuilder.show();	
		    			}
		    			
		    			drawSpots();
		    		}
		    	});

			}
		}).start();
	}
	
	private void showProgressView() {
		findViewById(R.id.mainview).setVisibility(View.GONE);
		findViewById(R.id.progressview).setVisibility(View.VISIBLE);
	}
	
	private void hideProgressView() {
		findViewById(R.id.mainview).setVisibility(View.VISIBLE);
		findViewById(R.id.progressview).setVisibility(View.GONE);		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
    		if (lastEvent == "onFling" || lastEvent == "onScroll") {
				drawSpots();
    		}
    		break;
		}
		return super.dispatchTouchEvent(event);
	}

	private final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
    	
        @Override
        public boolean onDoubleTap(MotionEvent event) {
        	lastEvent = "onDoubleTap";
//            Log.i(this.getClass().getName(), "onDoubleTap");
            return super.onDoubleTap(event);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
        	lastEvent = "onDoubleTapEvent";
//            Log.i(this.getClass().getName(), "onDoubleTapEvent");
            return super.onDoubleTapEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent event) {
        	lastEvent = "onDown";
//            Log.i(this.getClass().getName(), "onDown");
            return super.onDown(event);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        	lastEvent = "onFling";
//            Log.i(this.getClass().getName(), "onFling");
            return super.onFling(event1, event2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent event) {
        	lastEvent = "onLongPress";
//            Log.i(this.getClass().getName(), "onLongPress");
            super.onLongPress(event);
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        	lastEvent = "onScroll";
//            Log.i(this.getClass().getName(), "onScroll");
            return super.onScroll(event1, event2, distanceX, distanceY);
        }

        @Override
        public void onShowPress(MotionEvent event) {
        	lastEvent = "onShowPress";
//            Log.i(this.getClass().getName(), "onShowPress");
            super.onShowPress(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
        	lastEvent = "onSingleTapConfirmed";
//            Log.i(this.getClass().getName(), "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
        	lastEvent = "onSingleTapUp";
//            Log.i(this.getClass().getName(), "onSingleTapUp");
            return super.onSingleTapUp(event);
        }    
    };
	
    public void drawSpots() {

	    setProgressBarIndeterminateVisibility(true);

		Thread thread = new Thread() {
			public void run() {
				final ArrayList<PinItemizedOverlay> pins = process();
				
				mHandler.post(new Runnable() {
					@Override
					public void run() {
			    	    setProgressBarIndeterminateVisibility(false);

						mMapView.getOverlays().clear();

						// Overlayとして登録
						mMapView.getOverlays().add(overlay);
						
				        if (mMapView.getZoomLevel() < Integer.parseInt(getResources().getString(R.string.def_zoom_level))) {
				    		return;
				        }
						mMapView.getOverlays().addAll(pins);
						mMapView.invalidate();
					}
				});
			}
		};
		thread.start();
		

	}
	
    synchronized private final ArrayList<PinItemizedOverlay> process() {
    	ArrayList<PinItemizedOverlay> pins = new ArrayList<PinItemizedOverlay>();;

		try {
			db = dbHelper.getReadableDatabase();
	    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
	        PinItemizedOverlay pinOverlay = null;
	        
	    	GeoPoint center = mMapView.getMapCenter();
	    	int top    = center.getLatitudeE6()  - mMapView.getLatitudeSpan()/2;
	        int bottom = center.getLatitudeE6()  + mMapView.getLatitudeSpan()/2;
	        int left   = center.getLongitudeE6() - mMapView.getLongitudeSpan()/2;
	        int right  = center.getLongitudeE6() + mMapView.getLongitudeSpan()/2;
	        
	        SpotsDao spotsDao = new SpotsDao(db);
	    	ArrayList<Spots> spots = spotsDao.find(pref, top, bottom, left, right, Util.isPaymentApp(this));
			
			if (spots.size() > 0) {
				int i = 0;
	    		for (Spots spot : spots) {
	                GeoPoint geoPoint = new GeoPoint((int) ((double) spot.Latitude * E6), (int) ((double) spot.Longitude * E6));
	                
	                if (spot.Category.indexOf("マクドナルド") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[0]);
	                } else if (spot.Category.indexOf("モスバーガー") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[1]);
	                } else if (spot.Category.indexOf("ロッテリア") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[2]);
	                } else if (spot.Category.indexOf("ケンタッキー") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[3]);
	                } else if (spot.Category.indexOf("ミスタードーナツ") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[4]);
	                } else if (spot.Category.indexOf("銀座ルノアール") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[6]);
	                } else if (spot.Category.indexOf("スターバックス") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[7]);
	                } else if (spot.Category.indexOf("カフェ・ド・クリエ") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[8]);
	                } else if (spot.Category.indexOf("シアトルズベスト") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[9]);
	                } else if (spot.Category.indexOf("タリーズコーヒー") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[10]);
	                } else if (spot.Category.indexOf("ドトール/エクセルシオール") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[11]);
	                } else if (spot.Category.indexOf("プロント") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[12]);
	                } else if (spot.Category.indexOf("シャノアール") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[13]);
	                } else if (spot.Category.indexOf("珈琲館") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[14]);
	                } else if (spot.Category.indexOf("上島珈琲店") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[15]);
	                } else if (spot.Category.indexOf("カラオケ") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[20]);
	                } else if (spot.Category.indexOf("メイドカフェなど") != -1) {
	                	pinOverlay = new PinItemizedOverlay(images[21]);
	                } else if (spot.Category.matches(".*(クリスピークリームドーナツ|サブウェイ|バーガーキング|ウェンディーズ|ファーストキッチン|フレッシュネスバーガー|ファストフード).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[5]);
	                } else if (spot.Category.matches(".*(喫茶店|sign).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[16]);
	                } else if (spot.Category.matches(".*(メディアカフェポパイ|アプレシオ|ほっとステーション|まんがランド|まんが広場|ゆう遊空間|らくだ|アイ・カフェ|エアーズカフェ|ゲオカフェ|ゲラゲラ|コミックバスター|サイバック|スペースクリエイト自遊空間|バグース|マンボー|快活CLUB|ワイプ|ネットカフェ).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[17]);
	                } else if (spot.Category.matches(".*(バー|飲食店|居酒屋).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[18]);
	                } else if (spot.Category.matches(".*(コンビニエンスストア|レンタルオフィス).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[19]);
	                } else if (spot.Category.matches(".*(待合室・ラウンジ|公共施設).*")) {
	                	pinOverlay = new PinItemizedOverlay(images[22]);
	                } else {
	                	pinOverlay = new PinItemizedOverlay(images[23]);
	                }
	
	                pinOverlay.addPoint(geoPoint);
	   	            pinOverlay.setSpot(spot);
	   	            pins.add(pinOverlay);
	   	            i++;
	    		}
	    	}
			
		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
			if (db.isOpen()) {
				db.close();
			}
	    }
	    return pins;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
	    super.onCreateOptionsMenu( menu );
	    // メニューアイテムを追加
	    MenuItem item0 = menu.add( 0, 0, 0, "現在地" );
	    MenuItem item1 = menu.add( 0, 1, 0, "設定" );
	    MenuItem item2 = menu.add( 0, 2, 0, "更新チェック" );

	    // 追加したメニューアイテムのアイコンを設定
	    item0.setIcon( android.R.drawable.ic_menu_mylocation);
	    item1.setIcon( android.R.drawable.ic_menu_preferences );
	    item2.setIcon( android.R.drawable.ic_menu_rotate );
	    return true;
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item){

	    final ProgressDialog mProgressDialog;
		switch(item.getItemId()){  
	    case 0:  
    		GeoPoint l = overlay.getMyLocation();
    		
    		if (l == null) {
        		Toast.makeText(this, "現在地を特定できません", Toast.LENGTH_LONG).show();	    			
    		} else {
                // 取得した位置をマップの中心に設定
                mMapController.animateTo(l);
    		}
            return true;
	    case 1:
	        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
	        startActivityForResult(intent, 1);
	        return true;
	    case 2:

			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setTitle("通信中");
			mProgressDialog.setMessage("しばらくお待ちください。");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.show();

			Thread thread = new Thread() {
				public void run() {
					SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
			    	helper = new SpotsUpdateHelper(MainActivity.this, pref);
			    	final boolean res = helper.checkUpdate();
					
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mProgressDialog.dismiss();
							
					    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
					        alertDialogBuilder.setTitle("更新チェック");
					        alertDialogBuilder.setMessage(helper.msg);
					        
							if (res == true) {
					   	        // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックを登録します
						        alertDialogBuilder.setPositiveButton("更新する", new DialogInterface.OnClickListener() {
						            @Override
						            public void onClick(DialogInterface dialog, int which) {
						            	updateRecords(helper);
						            }});
							}

				        	// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックを登録します
				        	alertDialogBuilder.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
				        	    @Override
				        	    public void onClick(DialogInterface dialog, int which) {
				        	        
				        	    }});
				        	
				        	// アラートダイアログのキャンセルが可能かどうかを設定します
				        	alertDialogBuilder.setCancelable(true);

					        alertDialogBuilder.show();								
						}
					});
				}
			};
			thread.start();

	    }  
	    return false;  
	}
	
	public boolean updateRecords(final SpotsUpdateHelper helper) {
		final ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("データ更新中");
		mProgressDialog.setMessage("しばらくお待ちください。");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				thread.interrupt();
				thread = null;
			}
			
		});
		mProgressDialog.show();

		thread = new Thread() {
	        
			public void run() {
				String tmp_msg;
				synchronized (lock) {
			    	if (helper.downloadUpdateCsv(mProgressDialog) == true) {
			    		
						db = dbHelper.getWritableDatabase();
						Util.logging(helper.getSpotFileName());
						if (isInterrupted() == false) {
							if (dbHelper.updateSpots(db, helper.getSpotFileName(), mProgressDialog) == true) {
						    	SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				            	Editor editor = pref.edit();
				            	Util.logging(helper.latest_date);
				
				            	editor.putString("latest_date", helper.latest_date);
				            	editor.commit();
				            	tmp_msg = "データが更新されました。";
				            	helper.removeFile(helper.getSpotFileName());
				            	helper.removeFile(helper.getSpotZipFileName());
							} else {
								tmp_msg = "データの更新に失敗しました。";
							}
						} else {
							tmp_msg = null;
						}
			    	} else {
			    		tmp_msg = helper.msg;
			    	}
			    	
			    	final String msg = tmp_msg;
	
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mProgressDialog.dismiss();
							if (msg != null) {
								final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
								    .setMessage(msg)
								    .setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
						        	    @Override
						        	    public void onClick(DialogInterface dialog, int which) {}
						        	}).show();
							}
						}
					});
				}
			}
		};
		thread.start();

		return true;
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	switch (requestCode) {
    		case 1:
    			this.drawSpots();
    			break;
    	}
    }

	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
    @Override
    protected void onResume() {
    	overlay.enableMyLocation();
        
        super.onResume();
    }
    
    @Override
    protected void onPause() {
    	overlay.disableMyLocation();

        super.onPause();        
    }
	
	public class PinItemizedOverlay extends ItemizedOverlay<PinOverlayItem> implements Runnable {

	    private List<GeoPoint> points = new ArrayList<GeoPoint>();
	    private List<Spots> spots = new ArrayList<Spots>();
		private LayoutInflater inflater;

	    public PinItemizedOverlay(Drawable defaultMarker) {
	        super( boundCenterBottom(defaultMarker) );
	    }

	    @Override
	    protected PinOverlayItem createItem(int i) {
	    	GeoPoint point = points.get(i);
	    	return new PinOverlayItem(point);
	    }

	    @Override
	    public int size() {
	        return points.size();
	    }

	    public void addPoint(GeoPoint point) {
	        points.add(point);
	        populate();
	    }
		
	    public void clearPoint() {
	        points.clear();
	        populate();
	    }
	    	    	    
	    public void setSpot(Spots point) {
	        this.spots.add(point);
	    }
	    
		/**
		 * アイテムがタップされた時の処理
		 */
		@Override
		protected boolean onTap(int index) {
			point_data = spots.get(index);
			
	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
	        alertDialogBuilder.setTitle(point_data.Title);
	        alertDialogBuilder.setView(getSpotView());
	        
   	        // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックを登録します
	        alertDialogBuilder.setPositiveButton("ルート検索", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	            	Intent intent = new Intent(); 
	                intent.setAction(Intent.ACTION_VIEW); 
	                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
	                intent.setData(Uri.parse("http://maps.google.com/maps?myl=saddr&daddr=" + point_data.Address + "&dirflg=d")); 
	                startActivity(intent); 
	            }});
	        
	        alertDialogBuilder.setNeutralButton("PCサイトへ", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	        
	            	Intent intent = new Intent(); 
	                intent.setAction(Intent.ACTION_VIEW); 
	                intent.setData(Uri.parse("http://oasis.mogya.com/spot/" + point_data.EntryId)); 
	                startActivity(intent); 

                }});

        	// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックを登録します
        	alertDialogBuilder.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        	        
        	    }});
        	
        	// アラートダイアログのキャンセルが可能かどうかを設定します
        	alertDialogBuilder.setCancelable(true);

	        alertDialogBuilder.show();

			return true;
		}
		
		/**
		 * ポイントをタップした時に表示されるViewの設定
		 * 
		 * @return
		 */
		private View getSpotView() {
			
		    this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  

	        View view = inflater.inflate(R.layout.spot, null);
	        
	        // 住所
	        TextView txt_address = (TextView) view.findViewById(R.id.txt_address);
	        txt_address.setText(point_data.Address);
	        
	        // TEL
	        TextView txt_tel = (TextView) view.findViewById(R.id.txt_tel);
	        txt_tel.setText(point_data.Tel);
	        
	        // 電源
	        TextView txt_powersupply = (TextView) view.findViewById(R.id.txt_powersupply);
	        txt_powersupply.setText(point_data.PowerSupply);
	        
	        // 無線LAN
	        TextView txt_wireless = (TextView) view.findViewById(R.id.txt_wireless);
	        txt_wireless.setText(point_data.Wireless);

	        // OTHER
	        TextView txt_other = (TextView) view.findViewById(R.id.txt_other);
	        CharSequence source = Html.fromHtml(point_data.Other);
	        txt_other.setText(source);
	        
			return view;
		}
		
		@Override
		public void run() {
			/*
			//プログレスダイアログを閉じる
			dialog.dismiss();

	        */
		}		
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		    if (shadow) {
		    	return;
		    }

		    super.draw(canvas, mapView, shadow);
		    
		    return;
		}
	}
	
	public class PinOverlayItem extends OverlayItem {

	    public PinOverlayItem(GeoPoint point){
	        super(point, "", "");
	    }
	}
}