package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.CommonSeeAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CommonPerson;
import com.act.quzhibo.view.MarqueeTextView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by weiminglin on 17/7/30.
 */

public class FuliFragement extends BackHandledFragment {
    private ExpandableListView expandableListView;
    private List<String> group_list;
    private List<List<String>> item_list;
    private List<List<Integer>> item_list2;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_fuli, null, false);

        //随便一堆测试数据
        group_list = new ArrayList<>();
        group_list.add("免费福利区,游客可浏览");
        group_list.add("综合资源区，本区资源全部在线观看");
        group_list.add("网盘资源分享");
        group_list.add("直播资源分享");
        ArrayList<String> freeList = new ArrayList<>();
        ArrayList<String> allList = new ArrayList<>();
        ArrayList<String> netDiskList = new ArrayList<>();
        ArrayList<String> showList = new ArrayList<>();

        freeList.add("『在线福利视频分享』");
        freeList.add("『性感美女图片分享』");
        freeList.add("『bt高清电影下载分享』");

        allList.add("『beautyleg』");
        allList.add("『3AGirl』");
        allList.add("『4k-STAR』");
        allList.add("『RQ-STAR』");
        allList.add("『经典写真』");
        allList.add("『Rosimm』");
        allList.add("『Siyamm』");
        allList.add("『ru1mm』");
        allList.add("『Showgirl』");
        allList.add("『Pantyhose』");
        allList.add("『丽柜ligui』");
        allList.add("『细高跟』");
        allList.add("『微拍福利』");
        allList.add("『学院派私拍』");
        allList.add("『性感车模』");
        allList.add("『PANS写真』");
        allList.add("『动感小站』");
        allList.add("『锦尚天舞』");
        allList.add("『国产私拍』");
        allList.add("『韩国饭拍』");
        allList.add("『街拍美女』");
        allList.add("『爱丝RISS』");
        allList.add("『推女郎』");

        netDiskList.add("写真视频");
        netDiskList.add("网络红人");
        netDiskList.add("美女视频");
        netDiskList.add("美女视讯");

        showList.add("YOLO资源区");
        showList.add("其他主播资源区");

        item_list = new ArrayList<>();
        item_list.add(freeList);
        item_list.add(allList);
        item_list.add(netDiskList);
        item_list.add(showList);

        List<Integer> oneList = new ArrayList<>();
        List<Integer> secondList = new ArrayList<>();
        List<Integer> thirdList = new ArrayList<>();
        List<Integer> fourid = new ArrayList<>();

        oneList.add(R.drawable.icon);
        oneList.add(R.drawable.icon);
        oneList.add(R.drawable.icon);


        secondList.add(R.drawable.beautyleg);
        secondList.add(R.drawable.threeagirl);
        secondList.add(R.drawable.fourk);
        secondList.add(R.drawable.rq_star);
        secondList.add(R.drawable.jingdianxiezhen);
        secondList.add(R.drawable.rosimm);
        secondList.add(R.drawable.siyamm);
        secondList.add(R.drawable.ru1mm);
        secondList.add(R.drawable.showgirl);
        secondList.add(R.drawable.pantyhose);
        secondList.add(R.drawable.ligui);
        secondList.add(R.drawable.xigaogen);
        secondList.add(R.drawable.weipai);
        secondList.add(R.drawable.xueyuansipai);
        secondList.add(R.drawable.xingganchemo);
        secondList.add(R.drawable.pansixiezhen);
        secondList.add(R.drawable.dongganxiaozhan);
        secondList.add(R.drawable.jinshangtianwu);
        secondList.add(R.drawable.guochanshipai);
        secondList.add(R.drawable.fanpai);
        secondList.add(R.drawable.jiepaimeinv);
        secondList.add(R.drawable.riss);
        secondList.add(R.drawable.tuinvlang);

        thirdList.add(R.drawable.icon);
        thirdList.add(R.drawable.icon);
        thirdList.add(R.drawable.icon);
        thirdList.add(R.drawable.icon);

        fourid.add(R.drawable.icon);
        fourid.add(R.drawable.icon);

        item_list2 = new ArrayList<>();
        item_list2.add(oneList);
        item_list2.add(secondList);
        item_list2.add(thirdList);
        item_list2.add(fourid);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expendlist);
        expandableListView.setAdapter(new MyExpandableListViewAdapter(getActivity()));

        return view;
    }

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        private Context context;

        public MyExpandableListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return group_list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return item_list.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return group_list.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return item_list.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.expanlist_parent_item, null);
                groupHolder = new GroupHolder();
                groupHolder.txt = (TextView) convertView.findViewById(R.id.txt);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.txt.setText(group_list.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ItemHolder itemHolder ;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.expanlist_child_item, null);
                itemHolder = new ItemHolder();
                itemHolder.txt = (TextView) convertView.findViewById(R.id.txt);
                itemHolder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }
            itemHolder.txt.setText(item_list.get(groupPosition).get(
                    childPosition));
            itemHolder.img.setBackgroundResource(item_list2.get(groupPosition).get(
                    childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class GroupHolder {
        public TextView txt;
        public ImageView img;
    }

    class ItemHolder {
        public ImageView img;
        public TextView txt;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
