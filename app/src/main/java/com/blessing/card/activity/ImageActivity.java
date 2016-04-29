package com.blessing.card.activity;

import android.os.Bundle;

import com.blessing.card.R;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import uk.co.senab.photoview.PhotoView;

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
 * Created by Hua on 16/4/29.
 */
public class ImageActivity extends BaseActivity {

    @ViewInject(R.id.image_photoView)
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    private void initActivity() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            DownloadImageLoader.loadImage(photoView, mBundle.getString(PreviewActivity.TEMPLATE_KEY));
        }
    }

}
