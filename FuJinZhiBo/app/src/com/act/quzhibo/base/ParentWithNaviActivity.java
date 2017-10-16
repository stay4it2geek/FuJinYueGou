package com.act.quzhibo.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;

import cn.bmob.v3.BmobUser;

/**封装了导航条的类均需继承该类

 */
public abstract class ParentWithNaviActivity extends BaseActivity {

    public TextView tv_title;


    @SuppressWarnings("unchecked")
    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }


    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(this.getPackageName(), bundle);
        startActivity(intent);
    }

    public String getCurrentUid(){
        return BmobUser.getCurrentUser(RootUser.class).getObjectId();
    }

}
