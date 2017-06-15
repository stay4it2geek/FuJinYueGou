package com.act.quzhibo.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;

/**
 * Created by weiminglin on 17/6/15.
 */

public class BuyPowerExpandableListAdapter implements ExpandableListAdapter {

    int[] logos = new int[]{
            R.drawable.default_head,
            R.drawable.default_head,
            R.drawable.default_head,
            R.drawable.default_head
    };
    private String[] armTypes = new String[]{
            "视频无限观看下载", "解锁全部帖子", "解锁各种权限", "书籍无限观看下载", "音频课程无限下载", "视频课程无限下载"
    };
    private String[][] arms = new String[][]{
            {"文档编辑", "文档排版", "文档处理", "文档打印"},
            {"表格编辑", "表格排版", "表格处理", "表格打印"},
            {"收发邮件", "管理邮箱", "登录登出", "注册绑定"},
            {"演示编辑", "演示排版", "演示处理", "演示打印"},
            {"演示编辑", "演示排版", "演示处理", "演示打印"},
    };

    Activity activity;

    public BuyPowerExpandableListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return armTypes.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return arms[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return armTypes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return arms[groupPosition][childPosition];
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ImageView logo = new ImageView(activity);
        logo.setImageResource(logos[groupPosition]);
        logo.setPadding(36, 15, 0, 0);
        ll.addView(logo);
        TextView textView = getTextView();
        textView.setText(getGroup(groupPosition).toString());
        textView.setPadding(10, 0, 0, 0);
        ll.addView(textView);
        return ll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView = getTextView();
        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(activity);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(36, 0, 0, 0);
        textView.setTextSize(20);
        return textView;
    }
}
