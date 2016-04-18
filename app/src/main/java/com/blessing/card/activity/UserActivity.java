package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.box.UserObj;
import com.blessing.card.box.handler.UserObjHandler;
import com.blessing.card.dailog.MessageDialog;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.download.DownloadNewAppService;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.SystemHandle;
import com.blessing.card.handler.VersionHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.tool.WinTool;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

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
public class UserActivity extends BaseActivity {


    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.user_loginBtn)
    private TextView loginBtn;
    @ViewInject(R.id.user_userMessageBox)
    private LinearLayout userMessageBox;
    @ViewInject(R.id.user_userName)
    private TextView userName;
    @ViewInject(R.id.user_userBirthday)
    private TextView userBirthday;
    @ViewInject(R.id.user_userID)
    private TextView userID;
    @ViewInject(R.id.user_logoutBtn)
    private TextView logoutBtn;
    @ViewInject(R.id.user_userPic)
    private ImageView userPic;
    @ViewInject(R.id.user_progress)
    private ProgressBar progress;
    @ViewInject(R.id.user_newIcon)
    private ImageView newIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        isLogin();
        isUpload(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isLogin();
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("我 的");

        int w = WinTool.getWinWidth(context) / 7 * 2;
//        userPic.setLayoutParams(new RelativeLayout.LayoutParams(w, w));
    }

    @OnClick({R.id.title_backIcon, R.id.user_loginBtn, R.id.user_logoutBtn, R.id.user_userPic
            , R.id.user_proposeText, R.id.user_uploadText, R.id.user_aboutText})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.user_loginBtn:
                jumpLoginActivity();
                break;
            case R.id.user_logoutBtn:
                logout();
                break;
            case R.id.user_userPic:
                jumpUserSetingActivity();
                break;
            case R.id.user_proposeText:
                jumpProposeActivity();
                break;
            case R.id.user_uploadText:
                uoloadBtn();
                break;
            case R.id.user_aboutText:
                jumpAboutActivity();
                break;
        }
    }

    private void jumpAboutActivity() {
        Passageway.jumpActivity(context, AboutActivity.class);
    }

    private void jumpProposeActivity() {
        Passageway.jumpActivity(context, ProposeActivity.class);
    }

    private void jumpUserSetingActivity() {
        if (UserObjHandler.isLoginShowMessage(context)) {
            Passageway.jumpActivity(context, UserSetingActivity.class);
        }
    }

    private void logout() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("确定退出当前帐号？");
        dialog.setCommitStyle("确定");
        dialog.setCommitListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                UserObjHandler.deleteUser(context);
                isLogin();
            }
        });
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
    }

    private void jumpLoginActivity() {
        Passageway.jumpActivity(context, AuthLoginActivity.class);
    }

    public void isLogin() {
        if (UserObjHandler.isLogin(context)) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            userMessageBox.setVisibility(View.VISIBLE);
            setUserMessage();
        } else {
            loginBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            userMessageBox.setVisibility(View.GONE);
            userPic.setImageResource(R.drawable.user_pig_icon);
        }
    }

    private void setUserMessage() {
        userName.setText(UserObjHandler.getNickname(context));
        userBirthday.setText("生日 " + UserObjHandler.getBirthday(context));
        userID.setText("ID : " + UserObjHandler.getUserName(context));
        DownloadImageLoader.loadImage(userPic, UserObjHandler.getAvatar(context));
    }

    private void showMessage(String message, final String uploadUrl) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setTitel("发现新版本，请先更新");
        dialog.setMessage(message);
        dialog.setCommitListener(new MessageDialog.CallBackListener() {

            @Override
            public void callback() {
                downloadApp(uploadUrl);
            }

        });
        dialog.setCancelListener(new MessageDialog.CallBackListener() {

            @Override
            public void callback() {
//                finish();
            }
        });
    }

    private void downloadApp(String update_url) {
        Bundle b = new Bundle();
        b.putString(DownloadNewAppService.KEY, update_url);
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(context, DownloadNewAppService.class);
        startService(i);
    }

    private void uoloadBtn() {
        isUpload(true);
    }

    private void isUpload(final boolean b) {
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
                                        if (VersionHandler.detectionVersion(context, JsonHandle.getInt(android, "version"))) {
                                            newIcon.setVisibility(View.VISIBLE);
                                            if (b) {
                                                showMessage(JsonHandle.getString(android, "changelog"), JsonHandle.getString(android, "update_url"));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                });
    }

}
