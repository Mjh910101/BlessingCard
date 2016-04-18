package com.blessing.card.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.adapter.PostmarkListBaseAdpter;
import com.blessing.card.box.PoststampObj;
import com.blessing.card.box.handler.PoststampObjHandler;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
public class PostmarkListActivity extends BaseActivity {

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_nextIcon)
    private TextView nextIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.postmarkList_dataGrid)
    private ListView dataList;
    @ViewInject(R.id.postmarkList_progress)
    private ProgressBar progress;

    private Bundle mBundle;

    private PostmarkListBaseAdpter mAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postmark_list);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        downloadData();
    }


    @OnClick({R.id.title_backIcon, R.id.title_nextIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.title_nextIcon:
                jumpPreviewActivity();
                break;
        }
    }

    private void jumpPreviewActivity() {
        if (mAdpter != null) {
            int p = mAdpter.getOnChoosePosition();
            if (p < 0) {
                MessageHandler.showToast(context, "请先选择邮戳");
            } else {
//            mBundle.putSerializable(PreviewActivity.POSTEMARK_KEY, mAdpter.getOnChoosePostmar());
                PoststampObjHandler.saveClickPoststampObj(mAdpter.getOnChoosePostmar());
                Passageway.jumpActivity(context, PreviewActivity.class, mBundle);
            }
        }
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        nextIcon.setVisibility(View.VISIBLE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("邮戳");

        mBundle = getIntent().getExtras();
    }


    public void setDataList(List<PoststampObj> list) {
        mAdpter = new PostmarkListBaseAdpter(context, list);
        dataList.setAdapter(mAdpter);
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getPoststamp();

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
                                JSONArray array = JsonHandle.getArray(resultJson, "results");
                                if (array != null) {
                                    List<PoststampObj> list = PoststampObjHandler.getPoststampObjList(array);
                                    setDataList(list);
                                }
                            }
                        }

                    }

                });
    }

}
