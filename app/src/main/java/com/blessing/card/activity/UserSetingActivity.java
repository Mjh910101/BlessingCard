package com.blessing.card.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.box.UserObj;
import com.blessing.card.box.handler.UserObjHandler;
import com.blessing.card.dailog.ListDialog;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.handler.DateHandle;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.http.HttpFlieBox;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.tool.WinTool;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
 * Created by Hua on 15/11/2.
 */
public class UserSetingActivity extends BaseActivity {

    private static final String RESULT_IMAGE_FILE_NAME = "rh.png";

    private long start = 0;

    private String picPath;

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_nextIcon)
    private TextView nextIcon;
    @ViewInject(R.id.userSeting_uesrPic)
    private ImageView uesrPic;
    @ViewInject(R.id.userSeting_uesrName)
    private EditText uesrName;
    @ViewInject(R.id.userSeting_uesrBirthday)
    private TextView uesrBirthday;
    @ViewInject(R.id.userSeting_progress)
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_seting);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        setUserMessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case Passageway.IMAGE_REQUEST_CODE:
                    if (data != null) {
                        resizeImage(data.getData());
                    }
                    break;
                case Passageway.CAMERA_REQUEST_CODE:
                    if (isSdcardExisting()) {
                        resizeImage(getImageUri());
                    } else {
                        MessageHandler.showToast(context, "找不到SD卡");
                    }
                    break;

                case Passageway.RESULT_REQUEST_CODE:
                    if (data != null) {
                        getResizeImage(data);
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_backIcon, R.id.userSeting_uesrPic, R.id.title_nextIcon, R.id.userSeting_uesrBirthday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.userSeting_uesrPic:
                showPicList();
                break;
            case R.id.title_nextIcon:
                uploadUserMessage();
                break;
            case R.id.userSeting_uesrBirthday:
                showDatePickerDialog();
                break;
        }
    }

    private void showDatePickerDialog() {
        Calendar c = DateHandle.getToday();
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                uesrBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, DateHandle.getYear(c), DateHandle.getMonth(c), DateHandle.getDay(c));

        dialog.show();

    }

    private void showPicList() {
        start = DateHandle.getTime();
        final List<String> msgList = getMsgList();
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(msgList);
        dialog.setItemListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (msgList.get(position).equals("拍照")) {
                    takePhoto();
                } else {
                    selectImage();
                }
                dialog.dismiss();
            }

        });
    }

    private void takePhoto() {
        picPath = Passageway.takePhoto(context);
    }

    private void selectImage() {
        Passageway.selectImage(context);
    }

    public List<String> getMsgList() {
        List<String> list = new ArrayList<String>();
        list.add("拍照");
        list.add("本地相册");
        return list;
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);
        nextIcon.setVisibility(View.VISIBLE);

        nextIcon.setText("保存");
        titleName.setText("个人资料");

        start = DateHandle.getTime();

        int w = WinTool.dipToPx(context, 100);
        uesrPic.setLayoutParams(new RelativeLayout.LayoutParams(w, w));
    }

    private void setUserMessage() {
        DownloadImageLoader.loadImage(uesrPic, UserObjHandler.getAvatar(context));
        uesrName.setText(UserObjHandler.getNickname(context));
        uesrBirthday.setText(UserObjHandler.getBirthday(context));
    }

    public void resizeImage(Uri uri) {
        Passageway.resizeImage(context, uri);
    }

    private Uri getImageUri() {
        return Uri.fromFile(new File(HttpFlieBox.getImagePath(),
                picPath));
    }

    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void getResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            FileOutputStream foutput = null;
            try {
                foutput = new FileOutputStream(getImageFile());
                photo.compress(Bitmap.CompressFormat.PNG, 100, foutput);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (null != foutput) {
                    try {
                        foutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            DownloadImageLoader.loadImageForFile(uesrPic, getImageFile()
                    .toString());
//            upLoadUserPic(getImageFile());
        }
    }

    private File getImageFile() {
        String name = "head_" + start + ".png";
        return new File(HttpFlieBox.getImagePath(), name);
    }

    private void saveUser(UserObj obj) {
        UserObjHandler.saveUser(context, obj);
    }

    private void uploadUserMessage() {
        RequestParams params = HttpUtilsBox.getRequestParams(context);
//        params.setHeader("content-type", "multipart/form-data; boundary=__X_PAW_BOUNDARY__");
//        params.setHeader("Cookie", "avos:sess=; avos:sess.sig=WNDI72BEtkBcc4q9CJJ3Ec9eU2M");
//        params.setHeader("Host", "dev.zhufu.avosapps.com");
//        params.setHeader("Connection", "close");
//        params.setHeader("User-Agent", "Paw/2.2.5 (Macintosh; OS X/10.11.1) GCDHTTPRequest");
//        params.setHeader("Content-Length", "475");
        params.addBodyParameter("sessionToken", UserObjHandler.getSessionToken(context));
        params.addBodyParameter("objectId", UserObjHandler.getObjectId(context));
        params.addBodyParameter("nickname", uesrName.getText().toString());
        params.addBodyParameter("birthday", uesrBirthday.getText().toString());
        addAvatar(params);

        upload(params, true);
    }

    private void addAvatar(RequestParams params) {
        File f = getImageFile();
        if (f.exists()) {
            params.addBodyParameter("avatar", f);
        }
    }

    private void upLoadUserPic(File imageFile) {
        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("sessionToken", UserObjHandler.getSessionToken(context));
        params.addBodyParameter("objectId", UserObjHandler.getObjectId(context));
        params.addBodyParameter("avatar", imageFile);

        upload(params, false);
    }

    private void upload(RequestParams params, final boolean isFinish) {

        progress.setVisibility(View.VISIBLE);

        String url = UrlHandler.getModify();

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
                                    MessageHandler.showToast(context, "修改成功!");
                                    if (isFinish) {
                                        finish();
                                    }
                                }
                            }
                        }

                    }

                });
    }

}
