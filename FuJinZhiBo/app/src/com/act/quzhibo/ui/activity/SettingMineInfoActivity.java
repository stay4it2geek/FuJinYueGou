package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.CardBean;
import com.act.quzhibo.entity.JsonBean;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.FragmentSecretDialog;
import com.act.quzhibo.view.TitleBarView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SettingMineInfoActivity extends FragmentActivity {
    private OptionsPickerView ageOptions;
    private ArrayList<CardBean> ageItems = new ArrayList<>();
    TextView age_txt;
    TextView sex_txt;
    Switch sex_switch;
    RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
    TextView openSecret_txt;
    Switch openSecret_switch;
    TextView arealocation_txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_mine_info);
        age_txt = (TextView) findViewById(R.id.age_txt);
        sex_switch = (Switch) findViewById(R.id.sex_switch);
        openSecret_switch = (Switch) findViewById(R.id.openSecret_switch);
        openSecret_txt = (TextView) findViewById(R.id.openSecret_txt);
        sex_txt = (TextView) findViewById(R.id.sex_txt);
        arealocation_txt = (TextView) findViewById(R.id.arealocation_txt);
        findViewById(R.id.modifyPhonelayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingMineInfoActivity.this,ModifyPhoneNumActivity.class));
            }
        });

        findViewById(R.id.modifyPSWlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingMineInfoActivity.this,ModifyPasswordActivity.class));
            }
        });
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("资 料 设 置");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingMineInfoActivity.this.finish();
            }
        });
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        getAgeData();
        initAgeOptionPicker();

        arealocation_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoaded) {
                    ShowPickerView();
                } else {
                    Toast.makeText(SettingMineInfoActivity.this, "请等待省市区数据解析完成！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        age_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ageOptions.show();
            }
        });

        sex_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sex_txt.setText("您的性别已设置为：男");
                } else {
                    sex_txt.setText("您的性别已设置为：女");
                }
                if (rootUser != null) {
                    rootUser.sex = isChecked;
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(SettingMineInfoActivity.this, "性别更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, "性别更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        openSecret_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSecretDialog.newInstance(new FragmentSecretDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(Dialog dialog, String secretText) {
                        openSecret_txt.setText("私密访问已开启");

                        if (rootUser != null) {
                            rootUser.secretScan = true;
                            rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        openSecret_switch.setChecked(true);
                                        Toast.makeText(SettingMineInfoActivity.this, "私密访问开启成功,请牢记密码", Toast.LENGTH_SHORT).show();
                                    } else {
                                        openSecret_switch.setChecked(false);
                                        Toast.makeText(SettingMineInfoActivity.this, "私密访问开启失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegtiveClick(Dialog dialog) {
                        openSecret_switch.setChecked(false);
                        dialog.dismiss();
                    }
                }).show(getSupportFragmentManager(),"secretDilog");
            }
        });


        if (rootUser != null) {
            openSecret_switch.setChecked(rootUser.secretScan);
            sex_switch.setChecked(rootUser.sex);
            openSecret_txt.setText(rootUser.secretScan ? "私密访问已开启" : "私密访问未开启");
            arealocation_txt.setText(TextUtils.isEmpty(rootUser.provinceAndcity) ? "省市区未设置" :"你的地址是"+ rootUser.provinceAndcity);
            age_txt.setText(TextUtils.isEmpty(rootUser.age) ? "年龄未设置" :"你的年龄已设置为："+ rootUser.age);
        }
    }

    private void initAgeOptionPicker() {
        ageOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                age_txt.setText("你的年龄已设置为：" + ageItems.get(options1).getPickerViewText() + "岁");
                if (rootUser != null) {
                    rootUser.age = ageItems.get(options1).getPickerViewText() + "";
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(SettingMineInfoActivity.this, "年龄更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, "年龄更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTextColorCenter(Color.BLACK) .setContentTextSize(20).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ageOptions.returnData();
                        ageOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ageOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        ageOptions.setPicker(ageItems);//添加数据
    }


    private void getAgeData() {
        for (int index = 1; index <= 100; index++) {
            ageItems.add(new CardBean("" + index));
        }

    }


    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private boolean isLoaded = false;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(SettingMineInfoActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    Toast.makeText(SettingMineInfoActivity.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(SettingMineInfoActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                arealocation_txt.setText(text);

                if (rootUser != null) {
                    rootUser.provinceAndcity = text;
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(SettingMineInfoActivity.this, "省市区信息更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, "省市区更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).setTitleText("城市选择").setDividerColor(Color.BLACK).setTextColorCenter(Color.BLACK) .setContentTextSize(20).build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

}
