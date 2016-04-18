package com.blessing.card.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.handler.DateHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.TransHandler;
import com.blessing.card.sound.SoundMeter;
import com.blessing.card.tool.Passageway;
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
 * Created by Hua on 15/10/23.
 */
public class InputMessageActivity extends BaseActivity {

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_nextIcon)
    private TextView nextIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.message_inputMessage)
    private EditText inputMessage;
    @ViewInject(R.id.message_inputSize)
    private TextView inputSize;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_message);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        setTextChangedListener();


    }

    @OnClick({R.id.title_backIcon, R.id.title_nextIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.title_nextIcon:
                jumpPostmarkListActivity();
                break;
        }
    }

    private void jumpPostmarkListActivity() {
//        MessageHandler.showToast(context, TransHandler.trans2PinYin(inputMessage.getText().toString()));
        mBundle.putString(PreviewActivity.MESSAGE_KEY, getText(inputMessage));
        Passageway.jumpActivity(context, PostmarkListActivity.class, mBundle);
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        nextIcon.setVisibility(View.VISIBLE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("填写文字");

        mBundle = getIntent().getExtras();
    }

    private String getText(EditText e) {
        return e.getText().toString();
    }

    private final static int MAXLEN = 40;

    private void setTextChangedListener() {
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (getInputSize() <= 0) {
//                    inputMessage.setEnabled(false);
//                } else {
//                    inputMessage.setEnabled(true);
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("", "start : " + start);
                Log.e("", "before : " + before);
                Log.e("", "count : " + count);

                inputSize.setText("(可以输入" + (MAXLEN - getInputSize()) + "个字)");
                Editable editable = inputMessage.getText();
                if (getInputSize() > MAXLEN) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, str.length() - 1);
                    inputMessage.setText(newStr);
                    editable = inputMessage.getText();
                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public int getInputSize() {
        double sum = 0;
        String s = getText(inputMessage);
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                sum += 0.5;
            } else {
                sum += 1;
            }
        }
        return (int) Math.ceil(sum);
    }
}
