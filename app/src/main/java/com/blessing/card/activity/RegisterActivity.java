package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.box.UserObj;
import com.blessing.card.box.handler.UserObjHandler;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

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
public class RegisterActivity extends BaseActivity {

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_closeIcon)
    private ImageView closeIcon;
    @ViewInject(R.id.register_progress)
    private ProgressBar progress;
    @ViewInject(R.id.register_userInput)
    private EditText userInput;
    @ViewInject(R.id.register_passInput)
    private EditText passInput;
    @ViewInject(R.id.register_repeatPassInput)
    private EditText repeatPassInput;
    @ViewInject(R.id.register_repeatPassInputIcon)
    private ImageView repeatPassInputIcon;
    @ViewInject(R.id.register_repeatPassInput_false)
    private TextView passInputFalse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        setTextChangedListener();
    }

    @OnClick({R.id.title_backIcon, R.id.title_closeIcon, R.id.register_registerBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.title_closeIcon:
                closeRegister();
                break;
            case R.id.register_registerBtn:
                registerBtn();
                break;
        }
    }

    private void closeRegister() {
        Intent i=new Intent();
        Bundle b=new Bundle();
        b.putBoolean(AuthLoginActivity.CLOSE_KEY,true);
        i.putExtras(b);
        setResult(AuthLoginActivity.CLOSE_REQUEST_CODE, i);
        finish();
    }

    private void registerBtn() {
        if (isFulfil()) {
            register();
        } else {
            MessageHandler.showToast(context, "资料不完整");
        }
    }

    private void register() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getRegister();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("username", getText(userInput));
        params.addBodyParameter("password", getText(passInput));

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
                                    finish();
                                }
                            } else {
                                JSONObject json = JsonHandle.getJSON(resultJson, "results");
                                MessageHandler.showException(context, json);
                            }
                        }
                    }

                });
    }

    private void saveUser(UserObj obj) {
        UserObjHandler.saveUser(context, obj);
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);
        closeIcon.setVisibility(View.VISIBLE);

        titleName.setText("注册");
    }

    public boolean isFulfil() {
        if (getText(userInput).equals("")) {
            return false;
        }

        if (getText(passInput).equals("null")) {
            return false;
        }
//
//        if (getText(userInput).length() < 4) {
//            return false;
//        }
//
//        if (getText(userInput).length() > 16) {
//            return false;
//        }

        if (getText(passInput).equals("")) {
            return false;
        }

        if (getText(passInput).equals("null")) {
            return false;
        }

//        if (getText(passInput).length() < 6) {
//            return false;
//        }
//
//        if (getText(passInput).length() > 16) {
//            return false;
//        }

        if (!getText(repeatPassInput).equals(getText(passInput))) {
            return false;
        }

        return true;
    }

    private String getText(EditText view) {
        return view.getText().toString();
    }


    private void setTextChangedListener() {
        repeatPassInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isInputSame();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void isInputSame() {
        repeatPassInputIcon.setVisibility(View.VISIBLE);
        if (getText(repeatPassInput).equals(getText(passInput))) {
            repeatPassInputIcon.setImageResource(R.drawable.input_true_icon);
            passInputFalse.setVisibility(View.GONE);
        } else {
            repeatPassInputIcon.setImageResource(R.drawable.input_false_icon);
            passInputFalse.setVisibility(View.VISIBLE);
        }
    }
}
