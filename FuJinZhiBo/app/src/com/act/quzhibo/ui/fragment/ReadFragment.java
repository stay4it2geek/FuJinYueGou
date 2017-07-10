package com.act.quzhibo.ui.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.view.ReadGridView;

import java.util.List;

public class ReadFragment extends Fragment {

    private ReadGridView bookShelf;
    private int[] data = {
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
            R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt

    };
    private String[] name={
            "天龙八部","搜神记","水浒传","黑道悲情"
    };

    private GridView gv;
    private SlidingDrawer sd;
    private Button iv;
    private List<ResolveInfo> apps;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_read,null,false);
        bookShelf = (ReadGridView) view.findViewById(R.id.bookShelf);
        ShlefAdapter adapter=new ShlefAdapter();
        bookShelf.setAdapter(adapter);
        bookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(arg2>=data.length){

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), ""+arg2, Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadApps();
        gv = (GridView) view.findViewById(R.id.allApps);
        sd = (SlidingDrawer) view.findViewById(R.id.sliding);
        iv = (Button) view.findViewById(R.id.imageViewIcon);
        gv.setAdapter(new GridAdapter());
        sd.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
        {
            @Override
            public void onDrawerOpened() {
                iv.setText("返回");
                iv.setBackgroundResource(R.drawable.btn_local);
            }
        });
        sd.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                iv.setText("已下载");
                iv.setBackgroundResource(R.drawable.btn_local);
            }
        });
        return view;
    }

    class ShlefAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.length+5;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            contentView=LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.item_read, null);

            TextView view=(TextView) contentView.findViewById(R.id.imageView1);
            if(data.length>position){
                if(position<name.length){
                    view.setText(name[position]);
                }
                view.setBackgroundResource(data[position]);
            }else{
                view.setBackgroundResource(data[0]);
                view.setClickable(false);
                view.setVisibility(View.INVISIBLE);
            }
            return contentView;
        }

    }

    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps = getActivity().getPackageManager().queryIntentActivities(intent, 0);
    }

    public class GridAdapter extends BaseAdapter {
        public GridAdapter() {

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return apps.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return apps.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            } else {
                imageView = (ImageView) convertView;
            }

            ResolveInfo ri = apps.get(position);
            imageView.setImageDrawable(ri.activityInfo
                    .loadIcon(getActivity().getPackageManager()));

            return imageView;
        }

    }
}
