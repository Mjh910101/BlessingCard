package com.blessing.card.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.baidu.mobstat.StatService;
import com.blessing.card.R;
import com.lidroid.xutils.ViewUtils;

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
public class BaseActivity extends Activity {

    public static boolean e = false;
    public  Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!e) {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause (context);
    }

}
