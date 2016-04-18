package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.download.DownloadNewAppService;
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
 * Created by Hua on 15/11/6.
 */
public class VersionActivity extends BaseActivity {

    public final static int UPLOAD_REQUEST_CODE = 1024;

    private Bundle mBundle;

    @ViewInject(R.id.messageDialog_title)
    private TextView title;
    @ViewInject(R.id.messageDialog_message)
    private TextView message;
    @ViewInject(R.id.messageDialog_commit)
    private TextView commit;
    @ViewInject(R.id.messageDialog_cancel)
    private TextView cancel;
    @ViewInject(R.id.messageDialog_commitBox)
    private FrameLayout commitBox;
    @ViewInject(R.id.messageDialog_cancelBox)
    private FrameLayout cancelBox;

    private boolean isMust = false;
    private String update_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.messageDialog_commit, R.id.messageDialog_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messageDialog_commit:
                downloadApp(update_url);
                finish();
                break;
            case R.id.messageDialog_cancel:
                Intent i = new Intent();
                Bundle b = new Bundle();
                if (isMust) {
                    b.putBoolean("isFinish", true);
                } else {
                    b.putBoolean("isFinish", false);
                }
                i.putExtras(b);
                setResult(UPLOAD_REQUEST_CODE, i);
                finish();
                break;
        }
    }

    private void downloadApp(String update_url) {
        Bundle b = new Bundle();
        b.putString(DownloadNewAppService.KEY, update_url);
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(context, DownloadNewAppService.class);
        startService(i);
    }

    private void initActivity() {
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            commitBox.setVisibility(View.VISIBLE);
            cancelBox.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);

            commit.setText("确定");
            update_url = mBundle.getString("update_url");

            isMust = mBundle.getBoolean("must");
            if (isMust) {
                title.setText("亲，有新版本了，增加了很多功能，要升级之后才能使用哦!");
                cancel.setText("退出");
            } else {
                title.setText("亲，有新版本了，请升级!");
                cancel.setText("稍后");
            }

            String m = mBundle.getString("changelog");
            if (!m.equals("")) {
                message.setVisibility(View.VISIBLE);
                message.setText(m);
            }
        }
    }

}
