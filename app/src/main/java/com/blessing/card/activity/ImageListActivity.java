package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.adapter.ImageListBaseAdapter;
import com.blessing.card.box.AssetObj;
import com.blessing.card.box.SlideObj;
import com.blessing.card.box.handler.AssetsObjHandler;
import com.blessing.card.dailog.ListDialog;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.VersionHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.tool.WinTool;
import com.blessing.card.view.BaseActivity;
import com.blessing.card.view.HeaderGridView;
import com.blessing.card.view.PptView;
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
 * Created by Hua on 15/10/21.
 */
public class ImageListActivity extends BaseActivity {

    private final static int limit = 24;

    private final static long EXITTIME = 2000;
    private long EXIT = 0;

    private int page = 1, pages = 1;
    private String nowType = "";

    @ViewInject(R.id.title_backIcon)
    private ImageView backIcon;
    @ViewInject(R.id.imageList_dataGrid)
    private HeaderGridView dataGrid;
    @ViewInject(R.id.imageList_progress)
    private ProgressBar progress;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;

    private ImageListBaseAdapter iba;
    private PptView HeaderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        ViewUtils.inject(this);
        context = this;

        backIcon.setVisibility(View.GONE);
        isUpload();
        setDataListScrollListener();
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
                        } else {
                            downloadData("");
                        }
                    }
                }
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - EXIT < EXITTIME) {
                finish();
            } else {
                MessageHandler.showToast(context, "再次点击退出");
            }
            EXIT = System.currentTimeMillis();
        }
        return false;
    }

    @OnClick({R.id.title_backIcon, R.id.title_userIcon, R.id.title_seekText})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
//                finish();
                break;
            case R.id.title_userIcon:
                jumpUserActivity();
                break;
            case R.id.title_seekText:
                downloadSeekType();
                break;
        }
    }

    private void setDataListScrollListener() {
        dataGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (page <= pages) {
                            if (progress.getVisibility() == View.GONE) {
                                downloadData(nowType);
                            }
                        } else {
                            MessageHandler.showLast(context);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

    private void jumpUserActivity() {
        Passageway.jumpActivity(context, UserActivity.class);
    }


    public void downloadSeekType() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getAssetsType();

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
                                    JSONArray array = JsonHandle.getArray(json, "assetsType");
                                    showTypeDailog(array);
                                }
                            }
                        }

                    }

                });

    }

    private void showTypeDailog(JSONArray array) {
        if (array != null) {
            List<String> list = new ArrayList<String>(array.length());
            for (int i = 0; i < array.length(); i++) {
                list.add(JsonHandle.getString(array, i));
            }

            showTypeDailog(list);
        }
    }

    private void showTypeDailog(final List<String> list) {
        final ListDialog dialog = new ListDialog(context);
        dialog.setList(list);
        dialog.setTitleGone();
        dialog.setItemListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = list.get(position);
                seekText.setText(type);
                page = 1;
                pages = 1;
                iba = null;
                dataGrid.setAdapter(null);
                downloadData(type);
                dialog.dismiss();
            }
        });

    }

    private void downloadData(String type) {
        progress.setVisibility(View.VISIBLE);
        nowType = type;
        String url = UrlHandler.getAssets() + "?type=" + type + "&page=" + page + "&limit=" + limit;

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
                                    setPptView(JsonHandle.getArray(json, "category"));
                                    setCardGrid(JsonHandle.getArray(json, "assets"));

                                }
                                page += 1;
                                pages = JsonHandle.getInt(json, "pages");
                            }
                        }

                    }

                });

    }


    private void setCardGrid(JSONArray array) {
        List<AssetObj> list = new ArrayList<AssetObj>();
        if (array != null) {
            list.addAll(AssetsObjHandler.getAssetsObjList(array));
            setCardGrid(list);
        }
    }

    private void setCardGrid(List<AssetObj> list) {
        int numColumns = 2;
        if (WinTool.getWinWidth(context) >= 720) {
            numColumns = 3;
        } else {
            numColumns = 2;
        }
        dataGrid.setNumColumns(numColumns);

        if (iba == null) {
            iba = new ImageListBaseAdapter(context, list, numColumns);
            dataGrid.setAdapter(iba);
        } else {
            iba.addItems(list);
        }

    }

    private void setPptView(JSONArray array) {
        if (page != 1) {
            return;
        }
        List<SlideObj> list = new ArrayList<SlideObj>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                SlideObj obj = new SlideObj();
                JSONObject json = JsonHandle.getJSON(array, i);
                obj.setCover(JsonHandle.getString(json, "cover"));
                obj.setObj(AssetsObjHandler.getAssetsObj(JsonHandle.getJSON(json, "asset")));
                list.add(obj);
            }
            if (HeaderView == null) {
                HeaderView = new PptView(context, list);
                dataGrid.addHeaderView(HeaderView);
            }
        }
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
        } else {
            downloadData("");
        }
    }


}
