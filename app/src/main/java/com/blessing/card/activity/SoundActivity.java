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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blessing.card.R;
import com.blessing.card.handler.DateHandle;
import com.blessing.card.handler.MessageHandler;
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
 * Created by Hua on 15/10/21.
 */
public class SoundActivity extends BaseActivity {

    private static final int POLL_INTERVAL = 300;
    private static final int SOUND_TOTAL = 59;

    private long startVoiceT, endVoiceT;
    private int flag = 1;
    private String voiceName;
    private long timeTotalInS = 0;
    private long timeLeftInS = 0;
    private long timeTotal = 0;

    private Handler mHandler = new Handler();
    private SoundMeter mSensor;
    private MediaPlayer player;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        ViewUtils.inject(this);
        context = this;

        initActivity();
        setSoundBtnOnTouchListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @OnClick({R.id.title_backIcon, R.id.sound_playIcon, R.id.sound_againIcon, R.id.title_nextIcon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.sound_playIcon:
                playSound();
                break;
            case R.id.sound_againIcon:
                againSound();
                break;
            case R.id.title_nextIcon:
                jumpInputMessageActivity();
                break;
        }
    }


    private void initActivity() {
        voiceName = DateHandle.getTime() + ".amr";
        mSensor = new SoundMeter();
        userIcon.setVisibility(View.GONE);
        nextIcon.setVisibility(View.VISIBLE);
        seekText.setVisibility(View.GONE);
        titleName.setVisibility(View.VISIBLE);

        titleName.setText("制作明信片");
    }

    private void jumpInputMessageActivity() {
        Passageway.jumpActivity(context, InputMessageActivity.class);
    }

    private void againSound() {
        soundIcon.setVisibility(View.VISIBLE);
        playBox.setVisibility(View.GONE);
    }

    private void setSoundBtnOnTouchListener() {
        soundBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("", "ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN+ACTION_DOWN");
                    soundIcon.setImageResource(R.drawable.sound_on_icon);
                    int[] location = new int[2];
                    soundBtn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
                    int btn_rc_Y = location[1];
                    int btn_rc_X = location[0];

                    if (flag == 1) {
                        if (event.getY() < btn_rc_Y && event.getX() > btn_rc_X) {
                            timeBox.setVisibility(View.VISIBLE);
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                }
                            }, 300);
                            startVoiceT = System.currentTimeMillis();
//							voiceName = startVoiceT + ".amr";
                            start(voiceName);
                            //设置录音时间
                            initTimer(SOUND_TOTAL);
                            timedown.start();
                            flag = 2;
                        }
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("", "ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP+ACTION_UP");

                    soundIcon.setImageResource(R.drawable.sound_off_icon);
                    timeBox.setVisibility(View.GONE);

                    timedown.stop();
                    stop();

                    flag = 1;
                    soundUse(voiceName);

                    endVoiceT = System.currentTimeMillis();
                    int time = (int) ((endVoiceT - startVoiceT) / 1000);
                    System.out.println(time);
                }
                return false;
            }
        });
    }

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };

    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
            case 2:
            case 3:
                timeImage.setImageResource(R.drawable.sound_amp_001);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                timeImage.setImageResource(R.drawable.sound_amp_002);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
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

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        timeImage.setImageResource(R.drawable.sound_amp_001);
    }

    private void refreshTimeLeft() {
        timeTotal = timeTotalInS - timeLeftInS;
        this.timedown.setText(getTimeText(timeTotal));
    }


    private void playSound() {
        player = new MediaPlayer();
        try {
            player.setDataSource(mSensor.getSoundPath() + "/" + voiceName);
            player.prepare();
            player.start();
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

    private void soundUse(String fileName) {
        //判断sd卡上是否有声音文件，有的话就显示名称并播放
        final String path = mSensor.getSoundPath() + "/" + voiceName;
        File file = new File(path);
        if (file.exists()) {
            soundIcon.setVisibility(View.GONE);
            playBox.setVisibility(View.VISIBLE);
            String soundName = file.getName();
            setSoundTime(timeTotal);
        }
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
}
