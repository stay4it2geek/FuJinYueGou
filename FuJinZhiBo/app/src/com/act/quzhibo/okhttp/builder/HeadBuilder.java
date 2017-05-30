package com.act.quzhibo.okhttp.builder;


import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.request.OtherRequest;
import com.act.quzhibo.okhttp.request.RequestCall;

public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
