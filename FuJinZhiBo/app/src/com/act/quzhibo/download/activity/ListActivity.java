package com.act.quzhibo.download.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.activity.BaseActivity;
import com.act.quzhibo.common.adapter.BaseRecyclerViewAdapter;
import com.act.quzhibo.download.adapter.DownloadListAdapter;
import com.act.quzhibo.download.domain.MediaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * How to use Android Downloader in RecyclerView.
 */
public class ListActivity extends BaseActivity implements BaseRecyclerViewAdapter.OnItemClickListener {

    private static final int REQUEST_DOWNLOAD_DETAIL_PAGE = 100;

    private RecyclerView rv;
    private DownloadListAdapter downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public void initListener() {
        downloadListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        downloadListAdapter = new DownloadListAdapter(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(downloadListAdapter);
        final List<MediaInfo> mediaInfoList = getDownloadListData();
        downloadListAdapter.setData(mediaInfoList);
        downloadListAdapter.setOnDeleItemlIstner(new DownloadListAdapter.OnDeleteItemListner() {
            @Override
            public void deleteItem(int position) {
                mediaInfoList.remove(position);
                downloadListAdapter.setData(mediaInfoList);
                downloadListAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<MediaInfo> getDownloadListData() {
        ArrayList<MediaInfo> mediaInfos = new ArrayList<>();
        mediaInfos.add(new MediaInfo("QQ",
                "http://img.wdjimg.com/mms/icon/v1/4/c6/e3ff9923c44e59344e8b9aa75e948c64_256_256.png",
                "http://t10.baidu.com/it/u=64204245,4038352841&fm=58&u_exp_0=3792137926,3705047431&fm_exp_0=86&bpow=500&bpoh=500"));
        mediaInfos.add(new MediaInfo("微信",
                "http://img.wdjimg.com/mms/icon/v1/7/ed/15891412e00a12fdec0bbe290b42ced7_256_256.png",
                "http://wdj-uc1-apk.wdjcdn.com/1/a3/8ee2c3f8a6a4a20116eed72e7645aa31.apk"));
        mediaInfos.add(new MediaInfo("360手机卫士",
                "http://img.wdjimg.com/mms/icon/v1/d/29/dc596253e9e80f28ddc84fe6e52b929d_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/4/0b/ce61a5f6093fe81502fc0092dd6700b4.apk"));
        mediaInfos.add(new MediaInfo("陌陌",
                "http://img.wdjimg.com/mms/icon/v1/a/6e/03d4e21876706e6a175ff899afd316ea_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/b/0a/369eec172611626efff4e834fedce0ab.apk"));
        mediaInfos.add(new MediaInfo("美颜相机",
                "http://img.wdjimg.com/mms/icon/v1/7/7b/eb6b7905241f22b54077cbd632fe87b7_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/a/e9/618d265197a43dab6277c41ec5f72e9a.apk"));
        mediaInfos.add(new MediaInfo("Chrome",
                "http://img.wdjimg.com/mms/icon/v1/d/fd/914f576f9fa3e9e7aab08ad0a003cfdd_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/6/0d/6e93a829b97d671ee56190aec78400d6.apk"));
        return mediaInfos;
    }

    @Override
    public void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
    }

    @Override
    public void onItemClick(int position) {
        MediaInfo data = downloadListAdapter.getData(position);
        Intent intent = new Intent(this, DownloadDetailActivity.class);
        intent.putExtra(DownloadDetailActivity.DATA, data);
        startActivityForResult(intent, REQUEST_DOWNLOAD_DETAIL_PAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        downloadListAdapter.notifyDataSetChanged();
    }
}