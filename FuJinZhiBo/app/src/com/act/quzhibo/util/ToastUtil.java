package com.act.quzhibo.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    private static Toast cacheToast = null;

    /**
     * show toast
     *
     * @param context
     * @param msg
     */
    public static final void showToast(Context context, String msg) {
        if (cacheToast != null) {
            cacheToast.cancel();
        }
        cacheToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        cacheToast.show();
        
    }
}
