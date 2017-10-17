package com.act.quzhibo.view;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.MoneyGroup;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;

public class ExpanbleCommonView extends ExpandableListView {

    private ArrayList<MoneyGroup> mGroups;

    private MyAdapter mAdapter;


    public ExpanbleCommonView(Context context) {
        super(context);
        init(context);
    }

    public void setOnMoneyButtonTextListner(OnMoneyWayButtonTextListner listner) {
        this.listner = listner;
    }

    public interface OnMoneyWayButtonTextListner {
        void setText(String text);
    }

    OnMoneyWayButtonTextListner listner;

    public ExpanbleCommonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ExpanbleCommonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    void init(Context context) {
        mGroups =CommonUtil.jsonToArrayList(CommonUtil.getToggle((Activity) context, Constants.MONEY_GROUP).getToggleObject(),MoneyGroup.class);
        this.setVerticalScrollBarEnabled(false);
        setCacheColorHint(Color.TRANSPARENT);
        setDividerHeight(2);
        setChildrenDrawnWithCacheEnabled(false);
        mAdapter = new MyAdapter(context);
        setAdapter(mAdapter);
        this.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = ExpanbleCommonView.this.getExpandableListAdapter().getGroupCount();
                if (listner != null) {
                    listner.setText("开启" + mGroups.get(groupPosition).groupName + "通道");
                }
                for (int j = 0; j < count; j++) {
                    if (j != groupPosition) {
                        ExpanbleCommonView.this.collapseGroup(j);
                    }
                }
            }

        });

    }


    public class MyAdapter extends BaseExpandableListAdapter {

        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroups.get(groupPosition);
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
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_group, null);
            }

            TextView groupText = (TextView) convertView.findViewById(R.id.id_tv_item_group);
            if (isExpanded) {
                groupText.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff5e4c")));
            } else {
                groupText.setTextColor(ColorStateList.valueOf(Color.parseColor("#555555")));
            }
            groupText.setText(mGroups.get(groupPosition).groupName);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_child, null);
            }
            TextView childText = (TextView) convertView.findViewById(R.id.id_tv_item__child);
            childText.setText(Html.fromHtml(mGroups.get(groupPosition).childContent));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}