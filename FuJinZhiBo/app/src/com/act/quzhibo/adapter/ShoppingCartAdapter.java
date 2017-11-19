package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.CartEvent;
import com.act.quzhibo.ui.activity.CourseDetailActivity;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private boolean isShow = true;//是否显示编辑/完成
    private List<ShoppingCart> shoppingCarts;
    private CheckInterface checkInterface;
    private ModifyListInterface modifyListInterface;
    private Activity context;
    private ArrayList<ShoppingCart> carts=new ArrayList<>();

    public ShoppingCartAdapter(Activity context) {
        this.context = context;
    }
    public void setCartData(List<ShoppingCart> shoppingCarts) {
        this.shoppingCarts = shoppingCarts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return shoppingCarts == null ? 0 : shoppingCarts.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BmobQuery<CommonCourse> query = new BmobQuery<>();
        query.getObject(shoppingCarts.get(position).course.getObjectId(), new QueryListener<CommonCourse>() {
            @Override
            public void done(final CommonCourse course, BmobException e) {
                if(e==null){
                    if (course == null) {
                        return;
                    }

                    ShoppingCart cart=new ShoppingCart();
                    cart.course=course;
                    shoppingCarts.remove(position);
                    holder.courseName.setText(course.courseName);
                    if (course.courseUiType.equals("money")) {
                        holder.courseDetail.setVisibility(View.VISIBLE);
                        holder.leanerCount.setVisibility(View.GONE);
                        holder.selectionNum.setVisibility(View.GONE);
                        holder.courseDetail.setText(Html.fromHtml(course.courseDetail));
                    } else {//pua
                        holder.courseTag.setVisibility(View.VISIBLE);
                        if (course.courseTag.equals(Constants.VIDEO_COURSE)) {
                            holder.courseTag.setText("视频");
                        } else {
                            holder.courseTag.setText("课程");
                        }
                    }

                    holder.courseAppPrice.setText("¥" + (TextUtils.isEmpty(course.courseAppPrice) ? "" : course.courseAppPrice));
                    holder.courseMarketPrice.setText("¥" + (TextUtils.isEmpty(course.courseMarketPrice) ? "" : course.courseMarketPrice));
                    holder.courseMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                    Glide.with(context).load(TextUtils.isEmpty(course.courseImage.getUrl()) ? "" : course.courseImage.getUrl()).placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.courseImage);
                    holder.ckOneChose.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shoppingCarts.get(position).setChoosed(((CheckBox) v).isChecked());
                                    checkInterface.checkGroup(position, ((CheckBox) v).isChecked(),(TextUtils.isEmpty(course.courseAppPrice) ? "0.00" : course.courseAppPrice));//向外暴露接口
                                }
                            }
                    );
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CourseDetailActivity.class);
                            intent.putExtra("courseUiType", course.courseUiType);
                            intent.putExtra(Constants.COURSE, course);
                            context.startActivity(intent);
                        }
                    });
                }
            }

        });



        //删除弹窗
        holder.tvCommodityDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyListInterface.childDelete(position);
            }
        });
        boolean choosed = shoppingCarts.get(position).isChoosed();
        if (choosed) {
            holder.ckOneChose.setChecked(true);
        } else {
            holder.ckOneChose.setChecked(false);
        }

        //判断是否在编辑状态下
        if (isShow) {
            holder.tvCommodityDelete.setVisibility(View.GONE);
        } else {
            holder.tvCommodityDelete.setVisibility(View.VISIBLE);
        }



    }

    /**
     * 单选接口
     *
     * @param checkInterface
     */
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    /**
     * 改变商品数量接口
     *
     * @param modifyListInterface
     */
    public void setModifyListInterface(ModifyListInterface modifyListInterface) {
        this.modifyListInterface = modifyListInterface;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.courseName)
        public TextView courseName;
        @Bind(R.id.leanerCount)
        public TextView leanerCount;
        @Bind(R.id.selectionNum)
        public TextView selectionNum;
        @Bind(R.id.courseMarketPrice)
        public TextView courseMarketPrice;
        @Bind(R.id.courseTag)
        public TextView courseTag;
        @Bind(R.id.courseAppPrice)
        public TextView courseAppPrice;
        @Bind(R.id.courseImage)
        public ImageView courseImage;
        @Bind(R.id.courseDetail)
        public TextView courseDetail;
        @Bind(R.id.ck_chose)
        public CheckBox ckOneChose;
        @Bind(R.id.tv_item_delete)
        public TextView tvCommodityDelete;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            ckOneChose.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param position  元素位置
         * @param isChecked 元素选中与否
         */
        void checkGroup(int position, boolean isChecked,String price);
    }


    /**
     * 改变数量的接口
     */
    public interface ModifyListInterface {
        void childDelete(int position);
    }

    /**
     * 是否显示可编辑
     *
     * @param flag
     */
    public void isCanbeEdite(boolean flag) {
        isShow = flag;
        notifyDataSetChanged();
    }
}
