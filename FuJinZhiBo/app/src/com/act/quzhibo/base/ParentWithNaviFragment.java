package com.act.quzhibo.base;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;


/**封装了导航条的Fragment类均需继承该类
 *
 */
public abstract class ParentWithNaviFragment extends BaseFragment {

    protected View rootView = null;

    protected <T extends View> T getView(int id) {
        return (T) rootView.findViewById(id);
    }

}
