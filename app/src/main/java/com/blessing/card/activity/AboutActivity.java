package com.blessing.card.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.tool.WinTool;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 16/1/28.
 */
public class AboutActivity extends BaseActivity {

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.about_image)
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("关于我们");

        DownloadImageLoader.loadImageForID(image, R.drawable.about_image);
    }

    @OnClick({R.id.title_backIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
        }
    }

}
