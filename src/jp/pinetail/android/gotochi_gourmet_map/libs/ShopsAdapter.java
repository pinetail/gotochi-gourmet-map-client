package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.util.ArrayList;
import java.util.Arrays;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.Shops;
import jp.pinetail.android.gotochi_gourmet_map.R.drawable;
import jp.pinetail.android.gotochi_gourmet_map.R.id;
import jp.pinetail.android.gotochi_gourmet_map.R.layout;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopsAdapter extends ArrayAdapter {
  
    private ArrayList<Shops> items;
    private LayoutInflater inflater;
    private Context context;
    private Shops item;
    private String[] favList;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private Integer[] favStates;
    private static CategoryImage category;

    
    public ShopsAdapter(Context context, int textViewResourceId, ArrayList<Shops> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        favStates = new Integer[items.size()];
        dbHelper = new DatabaseHelper(context);
        category = new CategoryImage(context);
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        
        if (convertView == null) {
            // 受け取ったビューがnullなら新しくビューを生成
            convertView = inflater.inflate(R.layout.list_row, null);
            // 背景画像をセットする
            convertView.setBackgroundResource(R.drawable.back);
            
            holder = new ViewHolder();
            holder.Name  = (TextView) convertView.findViewById(R.id.name);
            holder.Address        = (TextView) convertView.findViewById(R.id.address);
            holder.imgCategory    = (ImageView) convertView.findViewById(R.id.img_category);
            holder.imgStar01      = (ImageView) convertView.findViewById(R.id.img_star01);
            holder.imgStar02      = (ImageView) convertView.findViewById(R.id.img_star02);
            holder.imgStar03      = (ImageView) convertView.findViewById(R.id.img_star03);
            holder.imgStar04      = (ImageView) convertView.findViewById(R.id.img_star04);
            holder.imgStar05      = (ImageView) convertView.findViewById(R.id.img_star05);
//            holder.Category       = (TextView) convertView.findViewById(R.id.txt_category);
            holder.BusinessHours  = (TextView) convertView.findViewById(R.id.txtBusinessHours);
            holder.Holiday        = (TextView) convertView.findViewById(R.id.txt_holiday);
//            holder.dist        = (TextView) convertView.findViewById(R.id.distance);
//            holder.brand       = (ImageView) convertView.findViewById(R.id.icon);
//            holder.imgFavorite = (ImageView)  convertView.findViewById(R.id.img_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 表示すべきデータの取得
        item = (Shops)items.get(position);
      
        if (item != null) {

            // スクリーンネームをビューにセット
            holder.Name.setTypeface(Typeface.DEFAULT_BOLD);
            if (holder.Name != null) {
                holder.Name.setText(item.Name);
            }
            
            if (holder.imgCategory != null) {
                holder.imgCategory.setImageDrawable(category.getDrawable(item.Category));
            }
            
            if (holder.Category != null) {
                holder.Category.setText(item.Category);
            }

            if (holder.Address != null) {
                holder.Address.setText(item.Address);
            }
            
            if (holder.BusinessHours != null) {
                holder.BusinessHours.setText(item.BusinessHours);
            }

            if (holder.Holiday != null) {
                holder.Holiday.setText(item.Holiday);
            }
            
            Float score = Float.parseFloat(item.Score);
            
            if (score == 5) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_full24);
                holder.imgStar04.setImageResource(R.drawable.star_full24);
                holder.imgStar05.setImageResource(R.drawable.star_full24);
            } else if (score >= 4.5) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_full24);
                holder.imgStar04.setImageResource(R.drawable.star_full24);
                holder.imgStar05.setImageResource(R.drawable.star_half_full24);
            } else if (score >= 4) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_full24);
                holder.imgStar04.setImageResource(R.drawable.star_full24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 3.5) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_full24);
                holder.imgStar04.setImageResource(R.drawable.star_half_full24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 3) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_full24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 2.5) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_half_full24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 2) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_full24);
                holder.imgStar03.setImageResource(R.drawable.star_empty24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 1.5) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_half_full24);
                holder.imgStar03.setImageResource(R.drawable.star_empty24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 1) {
                holder.imgStar01.setImageResource(R.drawable.star_full24);
                holder.imgStar02.setImageResource(R.drawable.star_empty24);
                holder.imgStar03.setImageResource(R.drawable.star_empty24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 0.5) {
                holder.imgStar01.setImageResource(R.drawable.star_half_full24);
                holder.imgStar02.setImageResource(R.drawable.star_empty24);
                holder.imgStar03.setImageResource(R.drawable.star_empty24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            } else if (score >= 0) {
                holder.imgStar01.setImageResource(R.drawable.star_empty24);
                holder.imgStar02.setImageResource(R.drawable.star_empty24);
                holder.imgStar03.setImageResource(R.drawable.star_empty24);
                holder.imgStar04.setImageResource(R.drawable.star_empty24);
                holder.imgStar05.setImageResource(R.drawable.star_empty24);
            }

/*
            // テキストをビューにセット
            if (holder.text != null) {
                holder.text.setText(item.Address);
            }

            // テキストをビューにセット
            if (holder.price != null) {
                if (item.Price.equals("9999")) {
                    holder.price.setText("no data");
                } else {
                    holder.price.setText(item.Price + "円");
                }
                holder.price.setTextColor(item.getDispPriceColor());
            }

            // テキストをビューにセット
            if (holder.dist != null) {
                if (item.Distance != null) {
                    Float distance = Float.parseFloat(item.Distance) / 1000;
                    holder.dist.setText(distance.toString() + "km");
                } else {
                    // 距離が登録されてない（お気に入りGS）の場合、非表示
                    holder.dist.setVisibility(View.GONE);
                }
            }
        
            StandsHelper helper = StandsHelper.getInstance();
            holder.brand.setImageDrawable(context.getResources().getDrawable(helper.getBrandImage(item.Brand, Integer.valueOf(item.Price))));
            
            if (favList == null || Arrays.binarySearch(favList, item.ShopCode) < 0) {
                holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_empty24));
                favStates[position] = 0;
            } else {
                holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_full24));
                favStates[position] = 1;
            }
            holder.imgFavorite.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    
                    Shops item = (Shops)items.get(position);
                    
                    db = dbHelper.getReadableDatabase();
                    FavoritesDao favoritesDao = new FavoritesDao(db);
                    
                    switch (favStates[position]) {
                    case 0:
                        // 登録件数の確認
                        if (favoritesDao.findAll("create_date").size() >= 20) {
                            Toast.makeText(context, "お気に入りは20件までしか登録出来ません。", Toast.LENGTH_SHORT).show();
                        } else {
                            favoritesDao.insert(item);
                            holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_full24));
                            favStates[position] = 1;
                        }
                        break;
                    case 1:
                        favoritesDao.deleteByShopCd(item.ShopCode);
                        holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_empty24));
                        favStates[position] = 0;
                        break;
                    }
                    db.close();
                    updateFavList();
                    Utils.logging(favStates[position].toString());
                }
            });
            */
        }
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView Name;
        TextView Address;
        ImageView image;
        ImageView imgCategory;
        ImageView imgStar01;
        ImageView imgStar02;
        ImageView imgStar03;
        ImageView imgStar04;
        ImageView imgStar05;
        TextView Category;
        TextView BusinessHours;
        TextView Holiday;
        ImageView brand;
        ImageView imgFavorite;
    }
}