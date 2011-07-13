package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.Shops;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShopsController extends Thread {
    private Handler handler;
    private final Runnable listener;
    private View scroll;
    private Context context;
    private Shops info;
    private LayoutInflater inflater;
    private Handler mHandler = new Handler();
    private static CategoryImage category;
    public View view;
    
    public ShopsController(Handler handler, Runnable listener, Context context, Shops info) {
        this.handler   = handler;
        this.listener  = listener;
        this.context   = context;
        this.info      = info;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.category = new CategoryImage(context);

    }

    @Override
    public void run() {
        setLayout();

        handler.post(listener);
    }
    
    public void setLayout() {
        
        view = inflater.inflate(R.layout.detail, null);
        
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        view.setMinimumWidth(disp.getWidth() - 20);
//        int width = disp.getWidth();
//        int height = disp.getHeight();

        if (info != null) {
            // カテゴリ
            ImageView imgCategory = (ImageView) view.findViewById(R.id.img_category);
            imgCategory.setImageDrawable(category.getDrawable(info.Category));
//            TextView textCategory = (TextView) view.findViewById(R.id.txt_category);
//            textCategory.setText(info.Category);

            // 店名
            TextView textName = (TextView) view.findViewById(R.id.txt_name);
            textName.setText(info.Name);
            
            // 住所
            TextView textAddress = (TextView) view.findViewById(R.id.txt_address);
            textAddress.setText(info.Address);

            // TEL
            TextView textTel = (TextView) view.findViewById(R.id.txt_tel);
            textTel.setText(info.Tel);

            // 営業時間
            TextView textBusinessHours = (TextView) view.findViewById(R.id.txt_business_hours);
            textBusinessHours.setText(info.BusinessHours);

            // 定休日
            TextView textHoliday = (TextView) view.findViewById(R.id.txt_holiday);
            textHoliday.setText(info.Holiday);
            
            // レーティング
            Float score = Float.parseFloat(info.Score);
            ImageView imgStar01 = (ImageView) view.findViewById(R.id.img_star01);
            ImageView imgStar02 = (ImageView) view.findViewById(R.id.img_star02);
            ImageView imgStar03 = (ImageView) view.findViewById(R.id.img_star03);
            ImageView imgStar04 = (ImageView) view.findViewById(R.id.img_star04);
            ImageView imgStar05 = (ImageView) view.findViewById(R.id.img_star05);
            
            if (score == 5) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_full);
                imgStar04.setImageResource(R.drawable.star_full);
                imgStar05.setImageResource(R.drawable.star_full);
            } else if (score >= 4.5) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_full);
                imgStar04.setImageResource(R.drawable.star_full);
                imgStar05.setImageResource(R.drawable.star_half_full);
            } else if (score >= 4) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_full);
                imgStar04.setImageResource(R.drawable.star_full);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 3.5) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_full);
                imgStar04.setImageResource(R.drawable.star_half_full);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 3) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_full);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 2.5) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_half_full);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 2) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_full);
                imgStar03.setImageResource(R.drawable.star_empty);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 1.5) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_half_full);
                imgStar03.setImageResource(R.drawable.star_empty);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 1) {
                imgStar01.setImageResource(R.drawable.star_full);
                imgStar02.setImageResource(R.drawable.star_empty);
                imgStar03.setImageResource(R.drawable.star_empty);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 0.5) {
                imgStar01.setImageResource(R.drawable.star_half_full);
                imgStar02.setImageResource(R.drawable.star_empty);
                imgStar03.setImageResource(R.drawable.star_empty);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            } else if (score >= 0) {
                imgStar01.setImageResource(R.drawable.star_empty);
                imgStar02.setImageResource(R.drawable.star_empty);
                imgStar03.setImageResource(R.drawable.star_empty);
                imgStar04.setImageResource(R.drawable.star_empty);
                imgStar05.setImageResource(R.drawable.star_empty);
            }

            // 点数
            TextView textScore = (TextView) view.findViewById(R.id.txt_score);
            textScore.setText(info.Score);

            final Gallery gallery  = (Gallery) view.findViewById(R.id.Gallery1);
            gallery.setSpacing(20);
            
            new Thread(new Runnable() {
                public void run() {

                    final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar01);

                    String url = "http://api.tabelog.com/Ver1/ReviewImageSearch/?Key=" +
                    context.getResources().getString(R.string.tabelog_access_key) + "&Rcd=" + info.TabelogId;
                    
                    XmlParserFromUrl xml = new XmlParserFromUrl();
                
                    byte[] byteArray = Util.getByteArrayFromURL(url, "GET");
                    if (byteArray == null) {
                        return;
                    }
                    String data = new String(byteArray);
                    
                    ArrayList<ShopImages> images = xml.getShopImages(data);
                    
                    if (images.size() > 0) {
                        
                        final String[] mImageIds   = new String[images.size()];
                        final String[] mUserNames  = new String[images.size()];
                        final String[] mPcSiteUrls = new String[images.size()];
                        int i = 0;
                        for (ShopImages image: images) {
                            mImageIds[i]   = image.ImageUrlL;
                            mUserNames[i]  = image.UserName;
                            mPcSiteUrls[i] = image.PcSiteUrl;
                            i++;
                        }
                        
                        mHandler.post(new Runnable() {
                            public void run() {
                                gallery.setAdapter(new ImageAdapter(context, mImageIds, mUserNames));
                                gallery.setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                                        Intent intent = new Intent(); 
                                        intent.setAction(Intent.ACTION_VIEW); 
                                        intent.setData(Uri.parse(mPcSiteUrls[position])); 
                                        context.startActivity(intent);
                                        
                                    }

                                });
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }).start();


        }
        scroll = view;
    }
    
    public View getView() {
        return scroll;
    }
    
    public Shops getShopInfo() {
        return info;
    }
    
    public class ImageAdapter extends BaseAdapter {

        private final int mGalleryItemBackground;
        private final Context mContext;

        private String[] mImageIds   = new String[1];
        private String[] mUserNames  = new String[1];
        private final float mDensity;

        public ImageAdapter(Context c, String[] images, String[] userNames) {
            mContext = c;
            // See res/values/attrs.xml for the <declare-styleable> that defines
            // Gallery1.
            TypedArray a = context.obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();

            mDensity = c.getResources().getDisplayMetrics().density;
            
            mImageIds   = images;
            mUserNames  = userNames;

        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                convertView = new ImageView(mContext);
                
                imageView = (ImageView) convertView;
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(150, 150));
                imageView.setPadding(10, 0, 10, 0);

                // The preferred Gallery item background
                imageView.setBackgroundResource(R.drawable.border_kind_member);

            } else {
                imageView = (ImageView) convertView;
            }

            TextView txtUserName = (TextView) view.findViewById(R.id.txt_username);
            txtUserName.setText("by " + mUserNames[position]);
            txtUserName.setVisibility(View.VISIBLE);
            
//            new Thread(new Runnable() {
//                public void run() {
                    URL url;
                    try {
                        url = new URL(mImageIds[position]);
                        if (ImageCache.hasImage(mImageIds[position])) {
                            Bitmap bitmap = ImageCache.getImage(mImageIds[position]);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());

                            ImageCache.setImage(mImageIds[position], bitmap);
                            imageView.setImageBitmap(bitmap);
                        }

                        
//                        mHandler.post(new Runnable() {
//                            public void run() {
                                
//                            }
//                        });

                    } catch (MalformedURLException e) {
                        // TODO 自動生成された catch ブロック
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO 自動生成された catch ブロック
                        e.printStackTrace();
                    }
//                }
//            }).start();

            return imageView;
        }
    }
}
