package com.act.quzhibo.okhttp.utils;

import android.util.Log;


public class QuLog
{
    private static boolean debug = false;

    public static void e(String msg)
    {
        if (debug)
        {
            Log.e("OkHttp", msg);
        }
    }

}

