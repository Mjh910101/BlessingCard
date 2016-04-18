package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.baidu.bottom.erised.Erised;
import com.blessing.card.R;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.VersionHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

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
 * Created by Hua on 15/10/20.
 */
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.main_enterIcon)
    private ImageView enterIcon;
    @ViewInject(R.id.main_progress)
    private ProgressBar progress;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        e = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Erised.init(context,"");
        ShareSDK.initSDK(this);
        ViewUtils.inject(this);
        DownloadImageLoader.initLoader(this);

        context = this;
//        isUpload();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpImageListActivity();
            }
        }, 3 * 1000);

    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VersionActivity.UPLOAD_REQUEST_CODE:
                if (data != null) {
                    Bundle b = data.getExtras();
                    if (b != null) {
                        if (b.getBoolean("isFinish")) {
                            finish();
                        }
                    }
                }
                break;
        }

    }

    @OnClick({R.id.main_enterIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_enterIcon:
                if (progress.getVisibility() == View.GONE) {
                    jumpImageListActivity();
                }
                break;
        }
    }

    private void jumpImageListActivity() {
        Passageway.jumpActivity(context, ImageListActivity.class);
        finish();
    }

    private void isUpload() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getVersion();

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        MessageHandler.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject resultJson = JsonHandle.getJSON(result);
                        if (resultJson != null) {
                            if (JsonHandle.getBoolean(resultJson, "status")) {
                                JSONObject json = JsonHandle.getJSON(resultJson, "results");
                                if (json != null) {
                                    JSONObject android = JsonHandle.getJSON(json, "android");
                                    if (android != null) {
                                        jumpVersionActivity(VersionHandler.detectionVersion(context, JsonHandle.getInt(android, "version")),
                                                VersionHandler.detectionVersion(context, JsonHandle.getInt(android, "versionShort")),
                                                JsonHandle.getString(android, "changelog"),
                                                JsonHandle.getString(android, "update_url"));
                                    }
                                }
                            }
                        }

                    }

                });
    }

    private void jumpVersionActivity(boolean version, boolean versionShort, String changelog, String update_url) {
        if (version) {
            Bundle b = new Bundle();
            b.putString("changelog", changelog);
            b.putString("update_url", update_url);
            if (versionShort) {
                b.putBoolean("must", true);
            } else {
                b.putBoolean("must", false);
            }
            Passageway.jumpActivity(context, VersionActivity.class, VersionActivity.UPLOAD_REQUEST_CODE, b);
        }
    }

}
