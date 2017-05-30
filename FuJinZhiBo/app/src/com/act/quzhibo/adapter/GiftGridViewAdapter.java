package com.act.quzhibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.Gift;

import java.io.InputStream;
import java.util.ArrayList;

///定影GridView的Adapter
public class GiftGridViewAdapter extends BaseAdapter {
    private int page;
    private int count;
    private ArrayList<Gift> gifts;
    private Context context;

    public void setGifts(ArrayList<Gift> gifts) {
        this.gifts = gifts;
        notifyDataSetChanged();
    }

    public GiftGridViewAdapter(Context context, int page, int count) {
        this.page = page;
        this.count = count;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 8;
    }

    @Override
    public Gift getItem(int position) {
        // TODO Auto-generated method stub
        return gifts.get(page * count + position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        final Gift catogary = gifts.get(page * count + position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift, null);
            viewHolder.grid_fragment_home_item_img =
                    (ImageView) convertView.findViewById(R.id.grid_fragment_home_item_img);
            viewHolder.grid_fragment_home_item_txt =
                    (TextView) convertView.findViewById(R.id.grid_fragment_home_item_txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        viewHolder.grid_fragment_home_item_img.setImageResource(catogary.getImage_source());
        viewHolder.grid_fragment_home_item_txt.setText(catogary.name);
        viewHolder.grid_fragment_home_item_img.setImageBitmap(readBitMap(context, catogary.giftType));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onGridViewClickListener != null) {
                    onGridViewClickListener.click(catogary);
                }
            }
        });

        return convertView;
    }

    public class ViewHolder {
        public ImageView grid_fragment_home_item_img;
        public TextView grid_fragment_home_item_txt;
    }

    public OnGridViewClickListener onGridViewClickListener;

    public void setOnGridViewClickListener(OnGridViewClickListener onGridViewClickListener) {
        this.onGridViewClickListener = onGridViewClickListener;
    }

    public interface OnGridViewClickListener {
        void click(Gift gift);
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
//获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

}
