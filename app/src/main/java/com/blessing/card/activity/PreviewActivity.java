package com.blessing.card.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.blessing.card.R;
import com.blessing.card.box.PoststampObj;
import com.blessing.card.box.UserObj;
import com.blessing.card.box.handler.PoststampObjHandler;
import com.blessing.card.box.handler.UserObjHandler;
import com.blessing.card.dailog.MessageDialog;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.handler.DateHandle;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MapHandler;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.TransHandler;
import com.blessing.card.http.HttpUtilsBox;
import com.blessing.card.http.UrlHandler;
import com.blessing.card.sound.Mp3Recorder;
import com.blessing.card.sound.SoundMeter;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
 * Created by Hua on 15/10/27.
 */

public class PreviewActivity extends BaseActivity {

    public final static String POSTEMARK_KEY = "section";
    public final static String MESSAGE_KEY = "content";
    public final static String SOUND_KEY = "sound";
    public final static String SOUND_TIME_KEY = "sound_time";
    public final static String TEMPLATE_KEY = "template";
    public final static String DURATION_KEY = "duration";

    private String voiceName, durationTime;
    private MediaPlayer player;

    private boolean isPlay = false;

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_nextIcon)
    private TextView nextIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.preview_progress)
    private ProgressBar progress;
    @ViewInject(R.id.preview_postmark)
    private ImageView postmark;
    @ViewInject(R.id.preview_userPic)
    private ImageView userPic;
    @ViewInject(R.id.preview_messageText)
    private TextView messageText;
    @ViewInject(R.id.preview_soundTime)
    private TextView soundTime;
    @ViewInject(R.id.preview_caraImage)
    private ImageView caraImage;
    @ViewInject(R.id.preview_playIcon)
    private ImageView plauIcon;
    @ViewInject(R.id.preview_playBox)
    private RelativeLayout playBox;
    @ViewInject(R.id.preview_userName)
    private TextView userName;
    @ViewInject(R.id.preview_postmarkBox)
    private RelativeLayout postmarkBox;

    private Bundle mBundle;

    private File postmarkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlay) {
            stopSound();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AuthLoginActivity.LOGIN_REQUEST_CODE:
                setUserMessage();
                sendCard();
                break;
        }

    }

    @OnClick({R.id.title_backIcon, R.id.title_nextIcon, R.id.preview_sendBtn, R.id.preview_playIcon, R.id.preview_backMainBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.title_nextIcon:
            case R.id.preview_sendBtn:
                if (progress.getVisibility() != View.VISIBLE) {
                    sendCard();
                }
                break;
            case R.id.preview_playIcon:
                if (isPlay) {
                    stopSound();
                } else {
                    playSound();
                }
                break;
            case R.id.preview_backMainBtn:
                backToMain();
                break;
        }
    }

    private void backToMain() {
        Passageway.jumpToActivity(context, ImageListActivity.class);
    }

    public MessageDialog getMessageDialog() {

        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("明信片正在上传中，文件可能较大，请耐心等候...");
        return dialog;
    }

    private void sendCard() {
        if (UserObjHandler.isLoginShowMessage(context)) {
//            progress.setVisibility(View.VISIBLE);

            final MessageDialog dialog = getMessageDialog();
            String url = UrlHandler.getPost();

            RequestParams params = HttpUtilsBox.getRequestParams(context);
            params.addBodyParameter("sessionToken", UserObjHandler.getSessionToken(context));
            params.addBodyParameter(MESSAGE_KEY, messageText.getText().toString());
            params.addBodyParameter("pic", getCard());
            params.addBodyParameter(POSTEMARK_KEY, postmarkFile);
            params.addBodyParameter(DURATION_KEY, getDurationTime(durationTime));
            saveSoundInParams(params);

            Log.e("", "" + params.toString());

            HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException exception, String msg) {
//                            progress.setVisibility(View.GONE);
                            dialog.dismiss();
                            MessageHandler.showFailure(context,
                                    exception);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
//                            progress.setVisibility(View.GONE);
                            dialog.dismiss();
                            String result = responseInfo.result;
                            Log.d("", result);
                            JSONObject resultJson = JsonHandle.getJSON(result);
                            if (JsonHandle.getBoolean(resultJson, "status")) {
                                JSONObject json = JsonHandle.getJSON(resultJson, "results");
                                if (json != null) {
                                    String url = JsonHandle.getString(json, "url");
                                    jumpShareActivity(url);
                                }
                            }
                        }

                    });
        }
    }

    private String getDurationTime(String d) {
        try {
            String[] s = d.split(":");
            int f = getInteger(s[0]);
            int m = getInteger(s[1]);
            int sum = f * 60 + m;
            Log.e("", "mmmmmmmmm " + sum);
            return String.valueOf(sum);

        } catch (Exception e) {
            return "0";
        }
    }

    private int getInteger(String s) {
        char[] c = s.toCharArray();
        return Integer.valueOf(String.valueOf(c[0])) * 10 + Integer.valueOf(String.valueOf(c[1]));
    }

    private void jumpShareActivity(String url) {
        Bundle b = new Bundle();
        b.putString(ShareActivity.URL_KEY, url);
        Passageway.jumpActivity(context, ShareActivity.class, b);
    }

    private void saveSoundInParams(RequestParams params) {
        File f = new File(Mp3Recorder.getSoundPath() + "/" + voiceName);
        if (f.exists()) {
            params.addBodyParameter(SOUND_KEY, f);
        }
    }

    private void initActivity() {
        userIcon.setVisibility(View.GONE);
        nextIcon.setVisibility(View.VISIBLE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("预览");
        nextIcon.setText("发送");

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            initView();
        }

        setUserMessage();
    }

    private void setUserMessage() {

        if (UserObjHandler.isLogin(context)) {
            DownloadImageLoader.loadImage(userPic, UserObjHandler.getAvatar(context));
            userName.setText(UserObjHandler.getNickname(context));
        }
    }

    private void initView() {
        voiceName = mBundle.getString(SOUND_KEY);
        durationTime = mBundle.getString(SOUND_TIME_KEY);
        messageText.setText(mBundle.getString(MESSAGE_KEY));
        soundTime.setText(durationTime);

        setCardImage();

        if (!new File(Mp3Recorder.getSoundPath() + "/" + voiceName).exists()) {
            playBox.setVisibility(View.GONE);
        } else {
            playBox.setVisibility(View.VISIBLE);
        }

        double w = WinTool.getWinWidth(context);
        double h = w / 640d * 256d;
        double uh = h / 256d * 191d;
        int p = WinTool.dipToPx(context, 10);
        //        int w = WinTool.getWinWidth(context) / 3;
        postmarkBox.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
        userPic.setLayoutParams(new LinearLayout.LayoutParams((int) uh, (int) uh));
        userPic.setPadding(p, p, p, p);
        setPostmarkText();
    }

    private void setCardImage() {
        double w = WinTool.getWinWidth(context);
        double h = w / 640d * 1006d;
        caraImage.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
        DownloadImageLoader.loadImage(caraImage, mBundle.getString(PreviewActivity.TEMPLATE_KEY));
    }

    private void setPostmarkText() {
        progress.setVisibility(View.VISIBLE);
        final PoststampObj obj = PoststampObjHandler.getClickPoststampObj();
        double h = WinTool.getWinWidth(context) / 3;
        double w = h / obj.getHeight() * obj.getWdith();
//        postmark.setLayoutParams(new RelativeLayout.LayoutParams((int) w, (int) h));
//        DownloadImageLoader.loadImage(postmark, obj.getUrl());
        DownloadImageLoader.loadImageForUrl(obj.getUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Bitmap b = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Log.e("", b.getWidth() + "   ----    " + b.getHeight());
                setPostmarkText(obj, b);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    private void setPostmarkText(final PoststampObj obj, final Bitmap b) {
        isRepeat = true;
        LocationClient locationClient = MapHandler.getPicAddress(context, new MapHandler.MapListener() {
            @Override
            public void callback(BDLocation location) {
                isRepeat = false;
                Canvas c = new Canvas(b);
                Paint p = new Paint();
                p.setColor(Color.BLACK);

                p.setTextSize(obj.getTimeLabel().getS());
                c.drawText(DateHandle.format(DateHandle.getTime() * 1000, DateHandle.DATESTYP_4), obj.getTimeLabel().getX(), obj.getTimeLabel().getY(), p);


                String city = location.getCity();
                if (city != null) {
                    String cityCase = TransHandler.trans2PinYin(city.replace("市", "")).toUpperCase();
                    if (cityCase.length() > 10) {
                        cityCase = cityCase.substring(0, 9) + "...";
                    } else if (cityCase.length() < 10) {
                        double k = (double) (10 - cityCase.length()) / 2d;
                        for (int i = 0; i < (int) (k + 0.5); i++) {
                            cityCase = " " + cityCase;
                        }
                    }
                    Log.e("", cityCase);
                    p.setTextSize(obj.getAddressLabel().getS());
                    c.drawText(cityCase, obj.getAddressLabel().getX(), obj.getAddressLabel().getY(), p);
                }


                postmarkFile = savePostmark(b);
                DownloadImageLoader.loadImageForFile(postmark, postmarkFile.toString());
                progress.setVisibility(View.GONE);
            }
        });
        startMapRum(locationClient);
    }

    private boolean isRepeat = true;
    private Handler mHandler = new Handler();

    private void startMapRum(final LocationClient locationClient) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRepeat) {
                    progress.setVisibility(View.GONE);
//                    locationClient.stop();
                    showRepeatMessage();
                }
            }
        }, 5 * 1000);

    }

    private void showRepeatMessage() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("未能获取当前地理位置信息，请确定网络通畅~");
        dialog.setCommitStyle("重新加载");
        dialog.setCommitListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                setPostmarkText();
            }
        });
    }

    public File getPostemark() {
        File f = savePostmark(convertViewToBitmap(postmark));
//        setPostmarkText();
        return f;
    }

    public File getCard() {
        File f = saveCard(convertViewToBitmap(caraImage));
        setCardImage();
        return f;
    }

    private File savePostmark(Bitmap b) {
        String path = DownloadImageLoader.getImagePath() + "/" + "postmark_" + DateHandle.getTime() + ".png";
        return savePic(b, path, Bitmap.CompressFormat.PNG);
    }

    private File saveCard(Bitmap b) {
        String path = DownloadImageLoader.getImagePath() + "/" + "card_" + DateHandle.getTime() + ".jpg";
        return savePic(b, path, Bitmap.CompressFormat.JPEG);
    }

    private File savePic(Bitmap b, String path, Bitmap.CompressFormat format) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (null != fos) {
                b.compress(format, 80, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(path);
    }

    private void stopSound() {
        if (player != null) {
            player.stop();
            player.release();
            setStopSoundIcon();
        }
    }

    private void setStopSoundIcon() {
        plauIcon.setImageResource(R.drawable.play_lucency_icon);
        isPlay = false;
    }

    private void playSound() {
        player = new MediaPlayer();
        setOnCompletionListener();
        try {
            player.setDataSource(Mp3Recorder.getSoundPath() + "/" + voiceName);
            player.prepare();
            player.start();
            plauIcon.setImageResource(R.drawable.stop_lucency_icon);
            isPlay = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnCompletionListener() {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setStopSoundIcon();
            }
        });
    }

    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            Log.d("bitmap", "bitmap no null");
        } else {
            Log.d("bitmap", "bitmap null");
        }
        return bitmap;
    }


}
