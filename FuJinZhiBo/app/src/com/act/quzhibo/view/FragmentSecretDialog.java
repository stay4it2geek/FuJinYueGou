package com.act.quzhibo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;


public class FragmentSecretDialog extends DialogFragment {


    private Button negtiveBn, positiveBn;


    private Dialog dialog;
    private PsdInputView psdInputView;


    public static final FragmentSecretDialog newInstance(OnClickBottomListener onClickBottomListener) {
        FragmentSecretDialog fragment = new FragmentSecretDialog();
        fragment.onClickBottomListener = onClickBottomListener;
        return fragment;
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听

        psdInputView.setComparePassword(new PsdInputView.onPasswordListener() {
            @Override
            public void onSettingMode(final String text) {

                positiveBn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickBottomListener != null) {
                            onClickBottomListener.onPositiveClick(dialog, text + "");
                        }
                    }
                });
            }

        });
        //设置取消按钮被点击后，向外界提供监听
        negtiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBottomListener.onNegtiveClick(dialog);

            }
        });
    }


    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;


    public interface OnClickBottomListener {

        void onPositiveClick(Dialog dialog, String secretText);

        void onNegtiveClick(Dialog dialog);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.secret_dialog_layout, null, false);
        initDialogStyle(rootView);
        negtiveBn = (Button) rootView.findViewById(R.id.negtive);
        positiveBn = (Button) rootView.findViewById(R.id.positive);
        psdInputView = (PsdInputView) rootView.findViewById(R.id.psdInputView);
        initEvent();
        return dialog;

    }

    private void initDialogStyle(View view) {
        dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 紧贴底部
        window.setAttributes(lp);
    }

    @Override
    public void onResume() {
        super.onResume();
        positiveBn.setText("确定");
        negtiveBn.setText("取消");
        dialog.show();
    }

}
