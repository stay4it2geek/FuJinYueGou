package com.act.quzhibo.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.BuildConfig;
import com.act.quzhibo.R;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.activity.DownloadManagerActivity;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.luban.Luban;
import com.act.quzhibo.ui.activity.CheckOutMoneyActivity;
import com.act.quzhibo.ui.activity.ClipImageActivity;
import com.act.quzhibo.ui.activity.GetQuCoinsActivity;
import com.act.quzhibo.ui.activity.GetVipPayActivity;
import com.act.quzhibo.ui.activity.LoginActivity;
import com.act.quzhibo.ui.activity.MakeMoneyActivity;
import com.act.quzhibo.ui.activity.MyFocusPersonActivity;
import com.act.quzhibo.ui.activity.MyFocusShowerActivity;
import com.act.quzhibo.ui.activity.MyPostListActivity;
import com.act.quzhibo.ui.activity.NoResActivity;
import com.act.quzhibo.ui.activity.RegisterActivity;
import com.act.quzhibo.ui.activity.SettingMineInfoActivity;
import com.act.quzhibo.ui.activity.TermOfUseActivity;
import com.act.quzhibo.ui.activity.VIPConisTableActivity;
import com.act.quzhibo.ui.activity.VipOrdersActivity;
import com.act.quzhibo.ui.activity.WhoLikeThenSeeMeActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.FileUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.HorizontialListView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class PersonalFragment extends Fragment implements View.OnClickListener {

    private RootUser rootUser;
    private View view;
    private CircleImageView circleImageView;
    private static final int REQUEST_CAPTURE = 100;
    private static final int REQUEST_PICK = 101;
    private static final int REQUEST_CROP_PHOTO = 102;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    private File tempFile;
    private PromptDialog promptDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_personal, null, false);
        if (CommonUtil.getToggle(getActivity(), Constants.SQUARE_AND_MONEY).getIsOpen().equals("false")) {
            view.findViewById(R.id.myVideo_download_layout).setVisibility(View.GONE);
            view.findViewById(R.id.myIMG_download_layout).setVisibility(View.GONE);
            view.findViewById(R.id.noReslayout).setVisibility(View.GONE);
            view.findViewById(R.id.myPostlayout).setVisibility(View.GONE);
            view.findViewById(R.id.who_see_me).setVisibility(View.GONE);
            view.findViewById(R.id.myfocus_person).setVisibility(View.GONE);
            view.findViewById(R.id.myfocus_shower).setVisibility(View.GONE);
        }
        view.findViewById(R.id.circle_avatar).setOnClickListener(this);
        view.findViewById(R.id.makemoneyLayoout).setOnClickListener(this);
        view.findViewById(R.id.checkoutMoneyLayout).setOnClickListener(this);
        view.findViewById(R.id.getQuCoinsLayout).setOnClickListener(this);
        view.findViewById(R.id.loginLayout).setOnClickListener(this);
        view.findViewById(R.id.uploadImg).setOnClickListener(this);
        view.findViewById(R.id.makemoneyText).setOnClickListener(this);
        view.findViewById(R.id.vipLevel).setOnClickListener(this);
        view.findViewById(R.id.get_vip).setOnClickListener(this);
        view.findViewById(R.id.vip_order_listlayout).setOnClickListener(this);
        view.findViewById(R.id.who_see_me).setOnClickListener(this);
        view.findViewById(R.id.myfocus_person).setOnClickListener(this);
        view.findViewById(R.id.myfocus_shower).setOnClickListener(this);
        view.findViewById(R.id.settingDetailayout).setOnClickListener(this);
        view.findViewById(R.id.noReslayout).setOnClickListener(this);
        view.findViewById(R.id.myVideo_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myIMG_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myPostlayout).setOnClickListener(this);
        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.noRes).setOnClickListener(this);
        view.findViewById(R.id.registerLayout).setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        promptDialog = new PromptDialog(getActivity());
        return view;
    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.vipLevel) {
            startActivity(new Intent(getActivity(), VIPConisTableActivity.class));
            return;
        } else if (view.getId() == R.id.circle_avatar) {
            FragmentDialog.newInstance(false, "是否要替换头像", "亲，真的想要替换吗", "我要替换", "取消替换", "", "", false, new FragmentDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                    dialog.dismiss();
                    uploadHeadImage();
                }

                @Override
                public void onNegtiveClick(Dialog dialog) {
                    dialog.dismiss();
                }
            }).show(getChildFragmentManager(), "");
            return;

        } else if (view.getId() == R.id.makemoneyLayoout) {
            getActivity().startActivity(new Intent(getActivity(), MakeMoneyActivity.class));
            return;
        } else if (view.getId() == R.id.checkoutMoneyLayout) {
            getActivity().startActivity(new Intent(getActivity(), CheckOutMoneyActivity.class));
            return;
        } else if (view.getId() == R.id.getQuCoinsLayout) {
            getActivity().startActivity(new Intent(getActivity(), GetQuCoinsActivity.class));
            return;
        }else if (view.getId() == R.id.noReslayout) {
            getActivity().startActivity(new Intent(getActivity(),  NoResActivity.class));
            return;
        }else if (view.getId() == R.id.registerLayout) {
            getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
            return;
        } else {
            if (rootUser == null) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

        }

        view.setBackgroundColor(getResources().getColor(R.color.colorbg));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }, 500);

        switch (view.getId()) {
            case R.id.get_vip:
                getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));
                break;
            case R.id.vip_order_listlayout:
                getActivity().startActivity(new Intent(getActivity(), VipOrdersActivity.class));
                break;
            case R.id.who_see_me:
                getActivity().startActivity(new Intent(getActivity(), WhoLikeThenSeeMeActivity.class));
                break;
            case R.id.myfocus_shower:
                getActivity().startActivity(new Intent(getActivity(), MyFocusShowerActivity.class));
                break;
            case R.id.myfocus_person:
                getActivity().startActivity(new Intent(getActivity(), MyFocusPersonActivity.class));
                break;
            case R.id.myVideo_download_layout:
                Intent videoIntent = new Intent();
                videoIntent.putExtra(Constants.DOWN_LOAD_TYPE, Constants.VIDEO_ALBUM);
                videoIntent.setClass(getActivity(), DownloadManagerActivity.class);
                getActivity().startActivity(videoIntent);
                break;
            case R.id.myIMG_download_layout:
                Intent photoIntent = new Intent();
                photoIntent.putExtra(Constants.DOWN_LOAD_TYPE, Constants.PHOTO_ALBUM);
                photoIntent.setClass(getActivity(), DownloadManagerActivity.class);
                getActivity().startActivity(photoIntent);
                break;
            case R.id.settingDetailayout:
                getActivity().startActivity(new Intent(getActivity(), SettingMineInfoActivity.class));
                break;
            case R.id.myPostlayout:
                getActivity().startActivity(new Intent(getActivity(), MyPostListActivity.class));
                break;
            case R.id.noReslayout:
                getActivity().startActivity(new Intent(getActivity(), TermOfUseActivity.class));
                break;
            case R.id.logout:
                rootUser.logOut();
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            default:
                break;
        }
    }

    ArrayList<String> personImgList = new ArrayList<>();

    private void querySeeMeData() {
        BmobQuery<InterestSubPerson> query = new BmobQuery<>();
        query.addWhereEqualTo("seeMeFlag", true);
        query.setLimit(40);
        query.order("-distance");
        query.findObjects(new FindListener<InterestSubPerson>() {
            @Override
            public void done(List<InterestSubPerson> list, BmobException e) {
                if (e == null) {
                    for (InterestSubPerson person : list) {
                        personImgList.add(person.absCoverPic);
                    }
                    HorizontialListView listView = (HorizontialListView) view.findViewById(R.id.who_see_me_list);
                    listView.setAdapter(new PostImageAdapter(getActivity(), personImgList,Constants.ITEM_POST_DETAIL_IMG, false, false));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getActivity(),WhoLikeThenSeeMeActivity.class));
                        }
                    });
                } else {

                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        rootUser = BmobUser.getCurrentUser(RootUser.class);
        if (rootUser != null) {
            CommonUtil.fecth(getActivity());
            view.findViewById(R.id.registerLayout).setVisibility(View.GONE);
            view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.nickName)).setText(rootUser.getUsername() != null ? rootUser.getUsername() : "未设置昵称");
            ((TextView) view.findViewById(R.id.vip_coins)).setText(rootUser.vipConis != null && rootUser.vipConis > 0 ? "已有" + rootUser.vipConis + "趣币" : "您趣币不足");
            String sexAndAge = (TextUtils.isEmpty(rootUser.sex) ? "性别" : rootUser.sex + "性") + "/" + (TextUtils.isEmpty(rootUser.age) ? "年龄" : rootUser.age + "岁");
            ((TextView) view.findViewById(R.id.sexAndAge)).setText(sexAndAge);
            if (0 < rootUser.vipConis && rootUser.vipConis < 3000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("初级趣会员");
            } else if (3000 < rootUser.vipConis && rootUser.vipConis < 5000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("中级趣会员");
            } else if (5000 < rootUser.vipConis && rootUser.vipConis < 8000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("特级趣会员");
            } else if (rootUser.vipConis > 8000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("超级趣会员");

            }

            if (rootUser.sex.equals("男")) {
                if (0 < rootUser.vipConis && rootUser.vipConis < 1000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_0);
                } else if (1000 < rootUser.vipConis && rootUser.vipConis < 2000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_1);
                } else if (2000 < rootUser.vipConis && rootUser.vipConis < 3000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_2);
                } else if (3000 < rootUser.vipConis && rootUser.vipConis < 4000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_3);
                } else if (4000 < rootUser.vipConis && rootUser.vipConis < 5000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_4);
                } else if (5000 < rootUser.vipConis && rootUser.vipConis < 6000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_5);
                } else if (6000 < rootUser.vipConis && rootUser.vipConis < 7000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_6);
                } else if (7000 < rootUser.vipConis && rootUser.vipConis < 7000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_7);
                } else if (8000 < rootUser.vipConis && rootUser.vipConis < 9000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_8);
                } else if (9000 < rootUser.vipConis && rootUser.vipConis < 10000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_9);
                } else if (rootUser.vipConis > 10000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_male_10);

                }
            }
            if (rootUser.sex.equals("女")) {
                if (0 < rootUser.vipConis && rootUser.vipConis < 1000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_0);
                } else if (1000 < rootUser.vipConis && rootUser.vipConis < 2000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_1);
                } else if (2000 < rootUser.vipConis && rootUser.vipConis < 3000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_2);
                } else if (3000 < rootUser.vipConis && rootUser.vipConis < 4000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_3);
                } else if (4000 < rootUser.vipConis && rootUser.vipConis < 5000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_4);
                } else if (5000 < rootUser.vipConis && rootUser.vipConis < 6000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_5);
                } else if (6000 < rootUser.vipConis && rootUser.vipConis < 7000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_6);
                } else if (7000 < rootUser.vipConis && rootUser.vipConis < 7000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_7);
                } else if (8000 < rootUser.vipConis && rootUser.vipConis < 9000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_8);
                } else if (9000 < rootUser.vipConis && rootUser.vipConis < 10000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_9);
                } else if (rootUser.vipConis > 10000) {
                    view.findViewById(R.id.vip_levelimg).setBackgroundResource(R.drawable.level_female_10);

                }
            }
            circleImageView = (CircleImageView) view.findViewById(R.id.circle_avatar);
            if (!TextUtils.isEmpty(rootUser.photoFileUrl)) {
                view.findViewById(R.id.uploadImg).setVisibility(View.GONE);
                Glide.with(getActivity()).load(rootUser.photoFileUrl).into(circleImageView);
            } else {
                circleImageView.setBackgroundResource(R.drawable.man);
            }
            querySeeMeData();
        } else {
            view.findViewById(R.id.who_see_me_list).setVisibility(View.GONE);
            view.findViewById(R.id.uploadImg).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.registerLayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.logout).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.nickName)).setText("未设置昵称");
            ((TextView) view.findViewById(R.id.vip_coins)).setText("您趣币不足");
            ((TextView) view.findViewById(R.id.sexAndAge)).setText("性别/年龄");
        }
    }


    private void uploadHeadImage() {
        promptDialog.getAlertDefaultBuilder().sheetCellPad(5).round(10);
        PromptButton cancle = new PromptButton("取消", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                promptDialog.dismiss();
            }
        });

        PromptButton btnCarema = new PromptButton("拍照", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                }
            }
        });
        PromptButton btnPhoto = new PromptButton("从相册选取", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                //权限判断
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到相册
                    gotoPhoto();
                }
            }
        });
        cancle.setTextColor(Color.parseColor("#0076ff"));
        promptDialog.showAlertSheet("", true, cancle, btnCarema, btnPhoto);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoPhoto();
            }
        }
    }


    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }

    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        //创建拍照存储的图片文件
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".FileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = FileUtil.getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    promptDialog.showLoading("正在上传", true);
                    Luban.get(getActivity())
                            .load(new File(cropImagePath))
                            .putGear(Luban.THIRD_GEAR)
                            .asObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            })
                            .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
                                @Override
                                public Observable<? extends File> call(Throwable throwable) {
                                    return Observable.empty();
                                }
                            })
                            .subscribe(new Action1<File>() {
                                @Override
                                public void call(File file) {
                                    if (file != null) {
                                        Glide.with(getActivity()).load(file.getAbsolutePath()).into(circleImageView);
                                        view.findViewById(R.id.uploadImg).setVisibility(View.GONE);
                                        final BmobFile bmobFile = new BmobFile(new File(file.getAbsolutePath()));
                                        final BmobFile bmobOldFile = new BmobFile();
                                        bmobOldFile.setUrl(rootUser.photoFileUrl);
                                        bmobOldFile.delete(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    bmobFile.uploadblock(new UploadFileListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if (e == null) {
                                                                RootUser updateUser = new RootUser();
                                                                updateUser.photoFileUrl = bmobFile.getFileUrl();
                                                                updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                                                                    @Override
                                                                    public void done(BmobException e) {
                                                                        if (e == null) {
                                                                            CommonUtil.fecth(getActivity());
                                                                            promptDialog.showSuccess("头像上传成功", true);
                                                                        } else {
                                                                            promptDialog.showError("头像上传失败", true);
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                promptDialog.showError("头像上传失败", true);
                                                            }
                                                            promptDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onProgress(Integer value) {

                                                        }
                                                    });
                                                } else {
                                                    promptDialog.showError("源头像删除失败", true);
                                                }
                                            }
                                        });


                                    }
                                }
                            });
                }
                break;
        }
    }

    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClipImageActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

}
