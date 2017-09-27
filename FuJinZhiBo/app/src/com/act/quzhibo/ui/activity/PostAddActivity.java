package com.act.quzhibo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.RecordProgress;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.luban.Luban;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.TitleBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PostAddActivity extends ActivityManagePermission implements BGASortableNinePhotoLayout.Delegate, View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;
    public static final String EXTRA_MOMENT = "EXTRA_MOMENT";
    private BGASortableNinePhotoLayout mPhotosSnpl;
    private EditText mContentEt;
    private EditText mTitleEt;

    private TextView mBtnVideo;
    private TextView mTvTipOne;
    private TextView mTvTipTwo;
    private RecordProgress mRp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_add);
        mBtnVideo = (TextView) findViewById(R.id.btnVideo);
        mTvTipOne = (TextView) findViewById(R.id.tvTipOne);
        mTvTipTwo = (TextView) findViewById(R.id.tvTipTwo);
        mRp = (RecordProgress) findViewById(R.id.rp);
        mRp.setRecordTime(10);

        mTitleEt = (EditText) findViewById(R.id.et_moment_title);
        mContentEt = (EditText) findViewById(R.id.et_moment_add_content);
        mPhotosSnpl = (BGASortableNinePhotoLayout) findViewById(R.id.snpl_moment_add_photos);
        mPhotosSnpl.setMaxItemCount(9);
        mPhotosSnpl.setEditable(true);
        mPhotosSnpl.setPlusEnable(true);
        mPhotosSnpl.setSortable(true);
        mPhotosSnpl.setDelegate(this);


        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("发 表 状 态");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAddActivity.this.finish();
            }
        });
        findViewById(R.id.tv_moment_add_publish).setOnClickListener(this);
        switch (getIntent().getIntExtra("postType", 0)) {
            case 1:
                findViewById(R.id.postNoVideo).setVisibility(View.VISIBLE);
                mPhotosSnpl.setVisibility(View.GONE);
                break;

            case 2:
                findViewById(R.id.postNoVideo).setVisibility(View.GONE);
                findViewById(R.id.tv_moment_add_publish).setVisibility(View.GONE);
                findViewById(R.id.record_layout).setVisibility(View.VISIBLE);
                break;


        }
        initListener();
        mYPost = new MyPost();
    }


    private void initListener() {
        mBtnVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRp.start();
                        mRp.setProgressColor(Color.parseColor("#1AAD19"));
                        mTvTipOne.setVisibility(View.VISIBLE);
                        mTvTipTwo.setVisibility(View.GONE);
//                        mVrvVideo.record(PostAddActivity.this);
                        break;
                    case MotionEvent.ACTION_UP:
                        mRp.stop();
                        mTvTipOne.setVisibility(View.GONE);
                        mTvTipTwo.setVisibility(View.GONE);
//                        //判断时间
//                        if (mVrvVideo.getTimeCount() > 3) {
//                            if (!isCancel(v, event)) {
//                                onRecrodFinish();
//                            }
//                        } else {
//                            if (!isCancel(v, event)) {
//                                Toast.makeText(getApplicationContext(), "视频时长太短", Toast.LENGTH_SHORT).show();
//                                if (mVrvVideo.getVecordFile() != null)
//                                    mVrvVideo.getVecordFile().delete();
//                            }
//                        }
//                        resetVideoRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancel(v, event)) {
                            mTvTipOne.setVisibility(View.GONE);
                            mTvTipTwo.setVisibility(View.VISIBLE);
                            mRp.setProgressColor(Color.parseColor("#FF1493"));
                        } else {
                            mTvTipOne.setVisibility(View.VISIBLE);
                            mTvTipTwo.setVisibility(View.GONE);
                            mRp.setProgressColor(Color.parseColor("#1AAD19"));
                        }
                        break;
                }
                return true;
            }
        });
    }

    private boolean isCancel(View v, MotionEvent event) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        if (event.getRawX() < location[0] || event.getRawX() > location[0] + v.getWidth() || event.getRawY() < location[1] - 40) {
            return true;
        }
        return false;
    }
//
//    @Override
//    public void onRecrodFinish() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mTvTipOne.setVisibility(View.GONE);
//                mTvTipTwo.setVisibility(View.GONE);
////                resetVideoRecord();
//                Toast.makeText(getApplicationContext(), "录制成功", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onRecording(int timeCount, int recordMaxTime) {
//
//    }
//
//    @Override
//    public void onRecordStart() {
//    }

//    /**
//     * 停止录制（释放相机后重新打开相机）
//     */
//    public void resetVideoRecord() {
//        mVrvVideo.stop();
//        mVrvVideo.openCamera();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    MyPost mYPost;

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "QuPhotoPickerTakePhoto");
        if (!takePhotoDir.exists()) {
            takePhotoDir.mkdirs();
        }
        startActivityForResult(BGAPhotoPickerActivity.newIntent(PostAddActivity.this, takePhotoDir, mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount(), null, false), REQUEST_CODE_CHOOSE_PHOTO);
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mPhotosSnpl.getMaxItemCount(), models, models, position, false), REQUEST_CODE_PHOTO_PREVIEW);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            ArrayList<File> fileList = new ArrayList<>();
            if (BGAPhotoPickerActivity.getSelectedImages(data) != null)
                for (String path : BGAPhotoPickerActivity.getSelectedImages(data)) {
                    fileList.add(new File(path));
                }
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.show();
            Luban.get(this)
                    .load(fileList)
                    .putGear(Luban.THIRD_GEAR)
                    .asList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    })
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<File>>>() {
                        @Override
                        public Observable<? extends List<File>> call(Throwable throwable) {
                            return Observable.empty();
                        }
                    })
                    .subscribe(new Action1<List<File>>() {
                        @Override
                        public void call(List<File> fileList) {
                            ArrayList<String> filePaths = new ArrayList<>();
                            for (int i = 0; i < fileList.size(); i++) {
                                filePaths.add(fileList.get(i).getAbsolutePath());
                            }
                            mPhotosSnpl.addMoreData(filePaths);
                        }
                    });

            dialog.dismiss();
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_moment_add_publish) {
            final String content = mContentEt.getText().toString().trim();
            final String title = mTitleEt.getText().toString().trim();
            if (title.length() == 0) {
                Toast.makeText(this, "必须填写这一刻想法的标题！", Toast.LENGTH_SHORT).show();
                return;
            } else if (content.length() == 0) {
                Toast.makeText(this, "必须填写这一刻的想法！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mPhotosSnpl.getData().size() == 0) {
                mYPost.title = title;
                mYPost.absText = content;
                mYPost.pageView = "0";
                mYPost.totalComments = "0";
                mYPost.totalImages = "0";
                mYPost.rewards = "0";
                mYPost.user = BmobUser.getCurrentUser(RootUser.class);
                mYPost.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_MOMENT, mYPost);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showToast(PostAddActivity.this, "发布失败，原因是：" + e.getErrorCode());
                        }
                    }
                });
            } else {
                final String[] filePaths = mPhotosSnpl.getData().toArray(new String[mPhotosSnpl.getData().size()]);
                BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        if (urls.size() == filePaths.length) {
                            mYPost.images = new ArrayList<>();
                            mYPost.images.addAll(urls);
                            mYPost.title = title;
                            mYPost.absText = content;
                            mYPost.pageView = "0";
                            mYPost.totalComments = "0";
                            mYPost.totalImages = urls.size() + "";
                            mYPost.rewards = "0";
                            mYPost.user = BmobUser.getCurrentUser(RootUser.class);
                            mYPost.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        Intent intent = new Intent();
                                        intent.putExtra(EXTRA_MOMENT, mYPost);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        ToastUtil.showToast(PostAddActivity.this, "发布失败，原因是：" + e.getErrorCode());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        ToastUtil.showToast(PostAddActivity.this, "错误码" + statuscode + ",错误描述：" + errormsg);
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                        //1、curIndex--表示当前第几个文件正在上传
                        //2、curPercent--表示当前上传文件的进度值（百分比）
                        //3、total--表示总的上传文件数
                        //4、totalPercent--表示总的上传进度（百分比）
                        final ProgressDialog dialog = new ProgressDialog(PostAddActivity.this);
                        dialog.setMessage("正在发布");
                        dialog.show();
                        if (totalPercent == 100) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        }
    }


}