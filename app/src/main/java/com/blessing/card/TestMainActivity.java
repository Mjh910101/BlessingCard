package com.blessing.card;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


public class TestMainActivity extends Activity {

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        ShareSDK.initSDK(this);
        ViewUtils.inject(this);

        context = this;

    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    @OnClick({R.id.wechat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat:
                authorize(new Wechat(this));
                break;
        }
    }

    private void authorize(Platform plat) {
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, mCallback);
                login(plat.getName(), userId, null);
                return;
            }
        }
        plat.setPlatformActionListener(mPlatformActionListener);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, mCallback);
    }

    PlatformActionListener mPlatformActionListener = new PlatformActionListener() {

        @Override
        public void onComplete(Platform platform, int action,
                               HashMap<String, Object> res) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, mCallback);
                login(platform.getName(), platform.getDb().getUserId(), res);
            }
            System.out.println(res);
            System.out.println("------User Name ---------" + platform.getDb().getUserName());
            System.out.println("------User ID ---------" + platform.getDb().getUserId());
        }

        @Override
        public void onError(Platform platform, int action, Throwable t) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, mCallback);
            }
            t.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int action) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, mCallback);
            }
        }
    };

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_USERID_FOUND: {
                    String text = getString(R.string.userid_found, msg.obj);
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
                break;
                case MSG_LOGIN: {
                    String text = getString(R.string.logining, msg.obj);
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    System.out.println("---------------");
                }
                break;
                case MSG_AUTH_CANCEL: {
                    Toast.makeText(context, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
                    System.out.println("-------MSG_AUTH_CANCEL--------");
                }
                break;
                case MSG_AUTH_ERROR: {
                    Toast.makeText(context, R.string.auth_error, Toast.LENGTH_SHORT).show();
                    System.out.println("-------MSG_AUTH_ERROR--------");
                }
                break;
                case MSG_AUTH_COMPLETE: {
                    Toast.makeText(context, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                    System.out.println("--------MSG_AUTH_COMPLETE-------");
                }
                break;
            }
            return false;
        }
    };
}
