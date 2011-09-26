package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.math.BigDecimal;
import java.util.ArrayList;

import jp.pinetail.android.gotochi_gourmet_map.R;
import jp.pinetail.android.gotochi_gourmet_map.dto.ShopsDto;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShopsAdapter extends ArrayAdapter {
  
    private ArrayList<ShopsDto> items;
    private LayoutInflater inflater;
    private Context context;
    private ShopsDto item;
    private String[] favList;
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private Integer[] favStates;
    private static CategoryImage category;

    
    public ShopsAdapter(Context context, int textViewResourceId, ArrayList<ShopsDto> items) {
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
            holder.Distance       = (TextView) convertView.findViewById(R.id.txt_distance);
            holder.layoutDistance = (LinearLayout) convertView.findViewById(R.id.layout_distance);
//            holder.dist        = (TextView) convertView.findViewById(R.id.distance);
//            holder.brand       = (ImageView) convertView.findViewById(R.id.icon);
//            holder.imgFavorite = (ImageView)  convertView.findViewById(R.id.img_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 表示すべきデータの取得
        item = (ShopsDto)items.get(position);
      
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
            
            if (holder.Distance != null) {
            	if (item.Distance != 0) {
                
                    String dist = "";
                    
                    if (item.Distance > 1000) {
                        BigDecimal bi = new BigDecimal(String.valueOf((double) item.Distance / 1000));
                        //小数第一位で切り捨て
                        double k0 = bi.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
                        dist = String.valueOf(k0) + "km";
                    } else {
                        BigDecimal bi = new BigDecimal(String.valueOf(item.Distance));
    
                        //小数第一位で切り捨て
                        double k0 = bi.setScale(0,BigDecimal.ROUND_DOWN).doubleValue();
                        dist = String.valueOf(k0) + "m";
                    }
    
                    holder.Distance.setText("現在地から" + getOrientation(item.Bearing) + "へ" + dist);
                } else {
                    holder.layoutDistance.setVisibility(View.GONE);
                }
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

        }
        
        return convertView;
    }
    
    private String getOrientation(float bearing) {
        
        String orientation = "";
        
        if (-180 < bearing && bearing <= -165) {
            orientation = "南";
        } else if (-165 < bearing && bearing <= -105) {
            orientation = "南西";
        } else if (-105 < bearing && bearing <= -75) {
            orientation = "西";
        } else if (-75 < bearing && bearing <= -15) {
            orientation = "北西";
        } else if (-15 < bearing && bearing <= 15) {
            orientation = "北";
        } else if (15 < bearing && bearing <= 75) {
            orientation = "北東";
        } else if (75 < bearing && bearing <= 105) {
            orientation = "東";
        } else if (105 < bearing && bearing <= 165) {
            orientation = "南東";
        } else if (165 < bearing && bearing <= 195) {
            orientation = "南";
        } else if (195 < bearing && bearing <= 255) {
            orientation = "南西";
        } else if (255 < bearing && bearing <= 285) {
            orientation = "西";
        } else if (285 < bearing && bearing <= 345) {
            orientation = "北西";
        } else if (345 < bearing && bearing <= 360) {
            orientation = "北";
        }
        return orientation;
        
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
        TextView Distance;
        ImageView brand;
        ImageView imgFavorite;
        LinearLayout layoutDistance;
    }
}