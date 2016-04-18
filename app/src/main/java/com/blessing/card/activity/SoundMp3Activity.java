package com.blessing.card.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.dailog.MessageDialog;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.handler.DateHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.SystemHandle;
import com.blessing.card.sound.Mp3Recorder;
import com.blessing.card.sound.SoundMeter;
import com.blessing.card.tool.Passageway;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.IOException;

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
 * Created by Hua on 15/10/29.
 */
public class SoundMp3Activity extends BaseActivity {

    private static final int POLL_INTERVAL = 300;
    private static final int SOUND_TOTAL = 30;
    private static final String TIME_LIMIT_DAILOG = "TIME_LIMIT_DAILOG";

    private long startVoiceT, endVoiceT;
    private int flag = 1;
    private String voiceName;
    private long timeTotalInS = 0;
    private long timeLeftInS = 0;
    private long timeTotal = 0;
    private boolean isPlay = false;

    private Handler mHandler = new Handler();
    private Mp3Recorder recorder;
    private MediaPlayer player;

    private Bundle mBundle;

    @ViewInject(R.id.title_userIcon)
    private ImageView userIcon;
    @ViewInject(R.id.title_nextIcon)
    private TextView nextIcon;
    @ViewInject(R.id.title_seekText)
    private TextView seekText;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.sound_soundIcon)
    private ImageView soundIcon;
    @ViewInject(R.id.sound_soundBtn)
    private Button soundBtn;
    @ViewInject(R.id.sound_timeBox)
    private RelativeLayout timeBox;
    @ViewInject(R.id.sound_timeImage)
    private ImageView timeImage;
    @ViewInject(R.id.sound_timedown)
    private Chronometer timedown;
    @ViewInject(R.id.sound_playBox)
    private RelativeLayout playBox;
    @ViewInject(R.id.sound_soundName)
    private TextView txtName;
    @ViewInject(R.id.sound_cardImage)
    private ImageView cardImage;
    @ViewInject(R.id.sound_playIcon)
    private ImageView plauIcon;
    @ViewInject(R.id.sound_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        showTimeLimitDailog();
        setSoundBtnOnTouchListener();
    }

    private void showTimeLimitDailog() {
        if (!SystemHandle.getBoolean(context, TIME_LIMIT_DAILOG)) {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("亲，请留意，录音时间为30秒哦~");
            dialog.setCommitStyle("不再提示");
            dialog.setCommitListener(new MessageDialog.CallBackListener() {
                @Override
                public void callback() {
                    SystemHandle.saveBooleanMessage(context, TIME_LIMIT_DAILOG, true);
                }
            });
            dialog.setCancelStyle("好的");
            dialog.setCancelListener(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlay) {
            stopSound();
        }
    }


    @OnClick({R.id.title_backIcon, R.id.sound_playIcon, R.id.sound_againIcon, R.id.title_nextIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.sound_playIcon:
                if (isPlay) {
                    stopSound();
                } else {
                    playSound();
                }
                break;
            case R.id.sound_againIcon:
                againSound();
                break;
            case R.id.title_nextIcon:
                jumpInputMessageActivity();
                break;
        }
    }

    private void stopSound() {
        if (player != null) {
            player.stop();
            player.release();
            setStopSoundIcon();
        }
    }

    private void setStopSoundIcon() {
        plauIcon.setImageResource(R.drawable.play_icon);
        isPlay = false;
    }

    private void playSound() {
        player = new MediaPlayer();
        setOnCompletionListener();
        try {
            player.setDataSource(Mp3Recorder.getSoundPath() + "/" + voiceName);
            player.prepare();
            player.start();
            plauIcon.setImageResource(R.drawable.stop_icon);
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
//                stopSound();
            }
        });
    }

    private void jumpInputMessageActivity() {
//        File f = new File(Mp3Recorder.getSoundPath() + "/" + voiceName);
//        if (f.exists()) {
        mBundle.putString(PreviewActivity.SOUND_KEY, voiceName);
        mBundle.putString(PreviewActivity.SOUND_TIME_KEY, getTimeText(timeTotal));
        Passageway.jumpActivity(context, InputMessageActivity.class, mBundle);
//        } else {
//            MessageHandler.showToast(context, "请先录音!");
//        }
    }

    private void againSound() {
        if (isPlay) {
            stopSound();
        }
        deleteSound();
        timeTotal = 0;
        soundIcon.setVisibility(View.VISIBLE);
        playBox.setVisibility(View.GONE);
    }

    private void deleteSound() {
        File f = new File(Mp3Recorder.getSoundPath() + "/" + voiceName);
        if (f.exists()) {
            f.delete();
        }
    }

    private void initActivity() {
        voiceName = DateHandle.getTime() + ".mp3";
        recorder = new Mp3Recorder(voiceName);
        userIcon.setVisibility(View.GONE);
        nextIcon.setVisibility(View.VISIBLE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("制作明信片");

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            DownloadImageLoader.loadImage(cardImage, mBundle.getString(PreviewActivity.TEMPLATE_KEY));
        }
    }

    private void setSoundBtnOnTouchListener() {
        soundBtn.setOnTouchListener(new View.OnTouchListener() {
            boolean isDown = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if(Utils.isFastClick()){
                            MessageHandler.showToast(context,"点击太快");
                            return true;
                        }
                        Log.e("", "ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN");
                        soundIcon.setImageResource(R.drawable.sound_on_icon);
                        int[] location = new int[2];
                        soundBtn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
                        int btn_rc_Y = location[1];
                        int btn_rc_X = location[0];

                        isDown = true;

                        if (flag == 1) {
                            if (event.getY() < btn_rc_Y && event.getX() > btn_rc_X) {
                                progress.setVisibility(View.VISIBLE);
                                mHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (isDown) {
                                            progress.setVisibility(View.GONE);
                                            timeBox.setVisibility(View.VISIBLE);
                                            startVoiceT = System.currentTimeMillis();
                                            start(voiceName);
                                            //设置录音时间
                                            initTimer(SOUND_TOTAL);
                                            timedown.start();
                                            flag = 2;
                                        }
                                    }
                                }, 300);
                            }
                        }

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.e("", "ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP");
                        progress.setVisibility(View.GONE);
                        isDown = false;

                        soundIcon.setImageResource(R.drawable.sound_off_icon);
                        timeBox.setVisibility(View.GONE);

                        timedown.stop();
                        stop();

                        flag = 1;
                        soundUse(voiceName);

                        endVoiceT = System.currentTimeMillis();
                        int time = (int) ((endVoiceT - startVoiceT) / 1000);
                        System.out.println(time);
//                        if(time<3){
//                            MessageHandler.showToast(context,"時間太短");
//                            againSound();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    againSound();
                    return true;
                }
                return false;
            }
        });
    }

    private void start(String name) {
        try {
            recorder.startRecording();
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        } catch (IOException e) {
            Log.d("Recorder", "Recorder Start error ： " + name);
        }
    }

    private void stop() {
        mHandler.removeCallbacks(mPollTask);
        try {
            recorder.stopRecording();
        } catch (IOException e) {
            Log.d("Recorder", "Recorder Stop error");
        }
        timeImage.setImageResource(R.drawable.sound_amp_001);
    }


    int amp = 0;

    private Runnable mPollTask = new Runnable() {

        public void run() {
            updateDisplay(amp);
            amp = (amp + 1) % 4;
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

    private void updateDisplay(int signalEMA) {

        switch (signalEMA) {
            case 0:
                timeImage.setImageResource(R.drawable.sound_amp_001);
                break;
            case 1:
                timeImage.setImageResource(R.drawable.sound_amp_002);
                break;
            case 2:
                timeImage.setImageResource(R.drawable.sound_amp_003);
                break;
            default:
                timeImage.setImageResource(R.drawable.sound_amp_004);
                break;
        }
    }

    /**
     * 初始化计时器，计时器是通过widget.Chronometer来实现的
     *
     * @param total 一共多少秒
     */
    private void initTimer(long total) {
        this.timeTotalInS = total;
        this.timeLeftInS = total;
        timedown.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (timeLeftInS <= 0) {
                    MessageHandler.showToast(context, "录音时间到");
                    timedown.stop();
                    //录音停止
                    stop();
                    timeBox.setVisibility(View.GONE);
                    return;
                }
                timeLeftInS--;
                refreshTimeLeft();
            }
        });
    }

    private void soundUse(String fileName) {
        //判断sd卡上是否有声音文件，有的话就显示名称并播放
        final String path = Mp3Recorder.getSoundPath() + "/" + voiceName;
        File file = new File(path);
        if (file.exists()) {
            soundIcon.setVisibility(View.GONE);
            playBox.setVisibility(View.VISIBLE);
            String soundName = file.getName();
            setSoundTime(timeTotal);
        }
    }

    private void refreshTimeLeft() {
        timeTotal = timeTotalInS - timeLeftInS;
        this.timedown.setText(getTimeText(timeTotal));
    }

    public void setSoundTime(long t) {
        txtName.setText(getTimeText(t));
    }

    private String getTimeText(long t) {
        if (t < 10) {
            return "00:0" + t;
        } else {
            return "00:" + t;
        }
    }

    static class Utils {
        private static long lastClickTime;
        public synchronized static boolean isFastClick() {
            long time = System.currentTimeMillis();
            if ( time - lastClickTime < 300) {
                return true;
            }
            lastClickTime = time;
            return false;
        }
    }

}
