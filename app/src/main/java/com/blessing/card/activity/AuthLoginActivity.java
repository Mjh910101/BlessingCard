package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.box.UserObj;
import com.blessing.card.box.handler.UserObjHandler;
import com.blessing.card.handler.ColorHandle;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mob.tools.utils.UIHandler;

import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

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
 * Created by Hua on 15/10/26.
 */
public class AuthLoginActivity extends BaseActivity {

    public final static int LOGIN_REQUEST_CODE = 2048;
    public final static int CLOSE_REQUEST_CODE = 2046;
    public final static String CLOSE_KEY = "close_key";

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;


    @ViewInject(R.id.title_bg)
    private RelativeLayout titleBg;
    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_backIcon)
    private ImageView backIcon;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.login_progress)
    private ProgressBar progress;
    @ViewInject(R.id.login_weixinIcon)
    private ImageView weixinIcon;
    @ViewInject(R.id.login_qqIcon)
    private ImageView qqIcon;
    @ViewInject(R.id.login_weiboIcon)
    private ImageView weiboIcon;
    @ViewInject(R.id.login_weixinText)
    private TextView weixinText;
    @ViewInject(R.id.login_qqText)
    private TextView qqText;
    @ViewInject(R.id.login_weiboText)
    private TextView weiboText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_login);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (UserObjHandler.isLogin(context)) {
            MessageHandler.showToast(context, "登录成功");
            close();
        }
//        MessageHandler.showToast(context, UserObjHandler.getSessionToken(context));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AuthLoginActivity.CLOSE_REQUEST_CODE:
                if (data != null) {
                    Bundle b = data.getExtras();
                    if (b != null) {
                        if (b.getBoolean(CLOSE_KEY)) {
                            finish();
                        }
                    }
                }
                break;
        }

    }


    @OnClick({R.id.title_backIcon, R.id.login_weixinBtn, R.id.login_registerBtn, R.id.login_loginBtn, R.id.login_qqBtn, R.id.login_weiboBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.login_weixinBtn:
                if (isClientValid(Wechat.NAME)) {
                    authorize(new Wechat(this), "weixin");
                }
//                Platform wechat= ShareSDK.getPlatform(context, Wechat.NAME);
//                wechat.setPlatformActionListener(new PlatformAction("weixin"));
//                wechat.authorize();
                break;
            case R.id.login_qqBtn:
                if (isClientValid(QQ.NAME)) {
                    authorize(new QQ(this), "qq");
                }
                break;
            case R.id.login_weiboBtn:
                if (isClientValid(SinaWeibo.NAME)) {
                    authorize(new SinaWeibo(this), "weibo");
                }
                break;
            case R.id.login_registerBtn:
                jumpRegisterActivity();
                break;
            case R.id.login_loginBtn:
                jumpLoginActivity();
                break;
        }
    }

    private void close() {
        setResult(LOGIN_REQUEST_CODE);
        finish();
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleBg.setBackgroundResource(R.color.white);
        backIcon.setImageResource(R.drawable.back_b_icon);
        titleName.setText("登录");
        titleName.setTextColor(ColorHandle.getColorForID(context, R.color.text_black_01));


        String noInstallation = "请先安装";
        if (isClientValid(Wechat.NAME)) {
            weixinText.setText("微信");
            weixinIcon.setImageResource(R.drawable.weixin_login_icon);
        } else {
            weixinText.setText(noInstallation);
            weixinText.setTextColor(ColorHandle.getColorForID(context, R.color.text_gray_02));
            weixinIcon.setImageResource(R.drawable.weixin_login_gary_icon);
        }

        if (isClientValid(QQ.NAME)) {
            qqText.setText("QQ");
            qqIcon.setImageResource(R.drawable.qq_login_icon);
        } else {
            qqText.setText(noInstallation);
            qqText.setTextColor(ColorHandle.getColorForID(context, R.color.text_gray_02));
            qqIcon.setImageResource(R.drawable.qq_login_gary_icon);
        }

        if (isClientValid(SinaWeibo.NAME)) {
            weiboText.setText("微博");
            weiboIcon.setImageResource(R.drawable.weibo_login_icon);
        } else {
            weiboText.setText(noInstallation);
            weiboText.setTextColor(ColorHandle.getColorForID(context, R.color.text_gray_02));
            weiboIcon.setImageResource(R.drawable.weibo_login_gary_icon);
        }
    }

    private boolean isClientValid(String name) {
        return ShareSDK.getPlatform(context, name).isClientValid();
    }

    private void jumpLoginActivity() {
        Passageway.jumpActivity(context, LoginActivity.class);
    }

    private void jumpRegisterActivity() {
        Passageway.jumpActivity(context, RegisterActivity.class, CLOSE_REQUEST_CODE);
    }

    private void authorize(Platform plat, String platform) {
        if (plat.isValid()) {
            plat.getDb().removeAccount();
//            String userId = plat.getDb().getUserId();
//            if (!TextUtils.isEmpty(userId)) {
//                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, mCallback);
//                login(plat.getName(), userId, null);
//                return;
//            }
        }
        plat.setPlatformActionListener(new PlatformAction(platform));
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, mCallback);
    }

    private void saveUser(UserObj obj) {
        UserObjHandler.saveUser(context, obj);
    }

    private void authLogin(String nickname, String avatar, String platform, String openid, String access_token) {

        Log.e("", "nickname : " + nickname + " +avatar : " + avatar + " platform : " + platform + " openid : " + openid + " access_token : " + access_token);

        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getAuth();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("avatar", avatar);
        params.addBodyParameter("platform", platform);
        params.addBodyParameter("openid", openid);
        params.addBodyParameter("access_token", access_token);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
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
                                    UserObj obj = UserObjHandler.getUserObj(json);
                                    saveUser(obj);
                                    close();
                                }
                            }
                        }
                    }

                });
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_USERID_FOUND: {
                    String text = getString(R.string.userid_found, msg.obj);
                    Log.e("MSG_USERID_FOUND", " ++++++++++++++++++++ " + text + " ++++++++++++++++++++ ");
                }
                break;
                case MSG_LOGIN: {
                    String text = getString(R.string.logining, msg.obj);
                    Log.e("MSG_LOGIN", " ++++++++++++++++++++ " + text + " ++++++++++++++++++++ ");
                }
                break;
                case MSG_AUTH_CANCEL: {
                    String text = getString(R.string.auth_cancel, msg.obj);
                    Log.e("MSG_AUTH_CANCEL", " ++++++++++++++++++++ " + text + " ++++++++++++++++++++ ");
                }
                break;
                case MSG_AUTH_ERROR: {
                    String text = getString(R.string.auth_error, msg.obj);
                    Log.e("MSG_AUTH_ERROR", " ++++++++++++++++++++ " + text + " ++++++++++++++++++++ ");
//                    MessageHandler.showToast(context,"");
                }
                break;
                case MSG_AUTH_COMPLETE: {
                    String text = getString(R.string.auth_complete, msg.obj);
                    Log.e("MSG_AUTH_COMPLETE", " ++++++++++++++++++++ " + text + " ++++++++++++++++++++ ");
                }
                break;
            }
            return false;
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Platform platform = (Platform) msg.obj;

            String nickname = platform.getDb().getUserName();
            String avatar = platform.getDb().getUserIcon();
            String openid = platform.getDb().getUserId();
            String access_token = platform.getDb().getToken();
//            String p = msg.what == 1 ? "qq" : "weixin";
            String p;
            switch (msg.what) {
                case 0:
                    p = "weixin";
                    break;
                case 1:
                    p = "qq";
                    break;
                default:
                    p = "weibo";
                    break;
            }
            authLogin(nickname, avatar, p, openid, access_token);
        }
    };

    class PlatformAction implements PlatformActionListener {

        String p;

        PlatformAction(String platform) {
            this.p = platform;
            Log.e("", "----- PlatformAction -----");

        }

        @Override
        public void onComplete(Platform platform, int action,
                               HashMap<String, Object> res) {
            Log.e("", "----- onComplete -----");

            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, mCallback);
                login(platform.getName(), platform.getDb().getUserId(), res);

//                int what = p.equals("qq") ? 1 : 0;
                int what;
                if (p.equals("qq")) {
                    what = 1;
                } else if (p.equals("weixin")) {
                    what = 0;
                } else {
                    what = 2;
                }
                Message.obtain(handler, what, platform).sendToTarget();
            }
            System.out.println(res);
            Log.e("PlatformActionListener", platform.getDb().getUserName() + "  |+|   " + platform.getDb().getUserId());

        }

        @Override
        public void onError(Platform platform, int action, Throwable t) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, mCallback);
            }
            Log.e("", "----- onError -----");
            t.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int action) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, mCallback);
            }
            Log.e("", "----- onCancel -----");
        }
    }

}
