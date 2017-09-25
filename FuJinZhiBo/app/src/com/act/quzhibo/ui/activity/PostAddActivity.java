package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.luban.Luban;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.SelfDialog;
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
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.act.quzhibo.common.Constants.REQUEST_SETTING;

public class PostAddActivity extends ActivityManagePermission implements BGASortableNinePhotoLayout.Delegate, View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;
    public static final String EXTRA_MOMENT = "EXTRA_MOMENT";
    private BGASortableNinePhotoLayout mPhotosSnpl;
    private EditText mContentEt;
    private EditText mTitleEt;

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
        titlebar.setBarTitle("发 表 状 态");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAddActivity.this.finish();
            }
        });
        findViewById(R.id.tv_moment_add_publish).setOnClickListener(this);
        mYPost = new MyPost();
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