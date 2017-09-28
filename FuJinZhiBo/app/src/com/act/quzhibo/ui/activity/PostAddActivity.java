package com.act.quzhibo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.common.RecordProgress;
import com.act.quzhibo.download.event.DownloadStatusChanged;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RecordVideoEvent;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.luban.Luban;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.TitleBarView;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    MyPost myPost;
    private ImageView videoThumb;
    String videoLocalUri = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_add);

        mTitleEt = (EditText) findViewById(R.id.et_moment_title);
        mContentEt = (EditText) findViewById(R.id.et_moment_add_content);
        mPhotosSnpl = (BGASortableNinePhotoLayout) findViewById(R.id.snpl_moment_add_photos);
        mPhotosSnpl.setMaxItemCount(9);
        mPhotosSnpl.setEditable(true);
        mPhotosSnpl.setPlusEnable(true);
        mPhotosSnpl.setSortable(true);
        mPhotosSnpl.setDelegate(this);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);

        videoThumb = (ImageView) findViewById(R.id.video_player);
        titlebar.setBarTitle("发 表 状 态");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAddActivity.this.finish();
            }
        });
        findViewById(R.id.tv_moment_add_publish).setOnClickListener(this);
        findViewById(R.id.recordBtn).setOnClickListener(this);
        switch (getIntent().getIntExtra("postType", 0)) {
            case 1:
                mPhotosSnpl.setVisibility(View.GONE);
                break;

            case 2:
                findViewById(R.id.recordBtn).setVisibility(View.VISIBLE);
                break;
        }

        videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPost = new MyPost();
                EventBus.getDefault().register(this);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(videoLocalUri);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(PostAddActivity.this, getApplicationContext().getPackageName() + ".FileProvider", file);
                    intent.setDataAndType(contentUri, "video/*");
                } else {
                    uri = Uri.fromFile(file);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "video/*");
                }
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(RecordVideoEvent event) {
        videoThumb.setVisibility(View.VISIBLE);
        videoLocalUri = event.videoUri;
        videoThumb.setImageBitmap(BitmapFactory.decodeFile(event.videoScreenshot));
    }

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
        final String content = mContentEt.getText().toString().trim();
        final String title = mTitleEt.getText().toString().trim();

        if (view.getId() == R.id.tv_moment_add_publish) {
            if (title.length() == 0) {
                Toast.makeText(this, "必须填写这一刻想法的标题！", Toast.LENGTH_SHORT).show();
                return;
            } else if (content.length() == 0) {
                Toast.makeText(this, "必须填写这一刻的想法！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mPhotosSnpl.getData().size() == 0) {
                myPost.title = title;
                myPost.absText = content;
                myPost.pageView = "0";
                myPost.totalComments = "0";
                myPost.totalImages = "0";
                myPost.rewards = "0";
                myPost.user = BmobUser.getCurrentUser(RootUser.class);
                myPost.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_MOMENT, myPost);
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
                            myPost.images = new ArrayList<>();
                            myPost.images.addAll(urls);
                            myPost.title = title;
                            myPost.absText = content;
                            myPost.pageView = "0";
                            myPost.totalComments = "0";
                            myPost.totalImages = urls.size() + "";
                            myPost.rewards = "0";
                            myPost.user = BmobUser.getCurrentUser(RootUser.class);
                            myPost.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        Intent intent = new Intent();
                                        intent.putExtra(EXTRA_MOMENT, myPost);
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
        } else if (R.id.recordBtn == view.getId()) {

            MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                    .fullScreen(true)
                    .smallVideoWidth(360)
                    .smallVideoHeight(480)
                    .recordTimeMax(6000)
                    .recordTimeMin(1500)
                    .maxFrameRate(20)
                    .videoBitrate(600000)
                    .captureThumbnailsTime(1)
                    .build();

            MediaRecorderActivity.goSmallVideoRecorder(PostAddActivity.this, RecordConfirmActivity.class.getName(), config);
        }
    }

}