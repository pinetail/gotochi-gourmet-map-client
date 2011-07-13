package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentsAdapter extends ArrayAdapter {
  
    private ArrayList<Comments> items;
    private LayoutInflater inflater;
    private Context context;
    private Comments item;
    private String[] favList;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private Integer[] favStates;

    
    public CommentsAdapter(Context context, int textViewResourceId, ArrayList<Comments> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        favStates = new Integer[items.size()];
        dbHelper = new DatabaseHelper(context);
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        
        if (convertView == null) {
            // 受け取ったビューがnullなら新しくビューを生成
            convertView = inflater.inflate(R.layout.comments_row, null);
            // 背景画像をセットする
            convertView.setBackgroundResource(R.drawable.back);
            
            holder = new ViewHolder();
            holder.Title      = (TextView) convertView.findViewById(R.id.txt_title);
            holder.Comment    = (TextView) convertView.findViewById(R.id.txt_comment);
            holder.NickName   = (TextView) convertView.findViewById(R.id.txt_nickname);
            holder.ReviewDate = (TextView) convertView.findViewById(R.id.txt_review_date);
//            holder.Category       = (TextView) convertView.findViewById(R.id.category);
//            holder.BusinessHours  = (TextView) convertView.findViewById(R.id.txtBusinessHours);
//            holder.dist        = (TextView) convertView.findViewById(R.id.distance);
//            holder.brand       = (ImageView) convertView.findViewById(R.id.icon);
//            holder.imgFavorite = (ImageView)  convertView.findViewById(R.id.img_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 表示すべきデータの取得
        item = (Comments)items.get(position);
      
        if (item != null) {

            // スクリーンネームをビューにセット
            if (holder.Title != null) {
                holder.Title.setText(item.Title);
            }
            
            if (holder.Comment != null) {
                holder.Comment.setText(item.Comment);
            }
            
            if (holder.NickName != null) {
                holder.NickName.setText(item.NickName);
            }
            if (holder.VisitDate != null) {
                holder.VisitDate.setText(item.VisitDate);
            }
            
            if (holder.ReviewDate != null) {
                holder.ReviewDate.setText(item.ReviewDate);
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
                    
                    Comments item = (Comments)items.get(position);
                    
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
    	TextView NickName = null;
    	TextView VisitDate = null;
    	TextView ReviewDate = null;
    	TextView UseType = null;
    	TextView Situations = null;
    	TextView TotalScore = null;
    	TextView TasteScore = null;
    	TextView ServiceScore = null;
    	TextView MoodScore = null;
    	TextView DinnerPrice = null;
    	TextView LunchPrice = null;
    	TextView Title = null;
    	TextView Comment = null;
    	TextView PcSiteUrl = null;
    	TextView MobileSiteUrl = null;
    }
}