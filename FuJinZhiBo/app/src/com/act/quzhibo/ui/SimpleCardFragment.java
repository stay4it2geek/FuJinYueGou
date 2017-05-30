package com.act.quzhibo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.act.quzhibo.R;


@SuppressLint("ValidFragment")
public class SimpleCardFragment extends Fragment {
    private String mTitle;
    OnCallShowViewListner onCallShowViewListner;

    public static SimpleCardFragment getInstance(String title) {
        SimpleCardFragment sf = new SimpleCardFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewShowActivity) {
            onCallShowViewListner = (OnCallShowViewListner) context;
        }
    }

    public interface OnCallShowViewListner {
        void onShowVideo(String url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_simple_card, null);
        TextView card_title_tv = (TextView) v.findViewById(R.id.card_title_tv);
        card_title_tv.setText(mTitle);
        card_title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallShowViewListner.onShowVideo("http://pull.kktv8.com/livekktv/102950202.flv");
            }
        });
        return v;
    }
}