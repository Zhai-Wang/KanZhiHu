package com.zhai.kanzhihu.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.zhai.kanzhihu.R;

/**
 * Created by 某宅 on 2016/8/14.
 *
 * 自定义标题栏
 */
public class TitleLayout extends RelativeLayout{

    private ImageButton back, close;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        back = (ImageButton) findViewById(R.id.title_back);
        close = (ImageButton) findViewById(R.id.title_close);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
}
