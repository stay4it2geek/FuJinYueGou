package com.act.quzhibo.common;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientManager {

    static OkHttpClient singleton;

    private OkHttpClientManager() {

    }

    private static OkHttpClient newInstance(Context context) {
        if (singleton == null) {
            synchronized (OkHttpClientManager.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                }
            }
        }
        return singleton;
    }

    public static void parseRequest(Context context, String url, Handler handler, int what) {
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).build());
        call.enqueue(new MyCallBack(handler, what));
    }

    static class MyCallBack implements Callback {
        Handler handler;
        int what;

        public MyCallBack(Handler handler, int what) {
            this.handler = handler;
            this.what = what;
        }


        @Override
        public void onFailure(Call call, IOException e) {
            handler.sendEmptyMessage(Constants.NetWorkError);

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message message = handler.obtainMessage();
            message.obj = response.body().string();
            message.what = what;
            handler.sendMessage(message);
        }
    }
}
