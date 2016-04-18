package com.blessing.card.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blessing.card.R;
import com.blessing.card.activity.PreviewActivity;
import com.blessing.card.activity.SoundMp3Activity;
import com.blessing.card.box.AssetObj;
import com.blessing.card.box.SlideObj;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.tool.AnimationTool;
import com.blessing.card.tool.PPTFlish;
import com.blessing.card.tool.Passageway;
import com.blessing.card.tool.WinTool;
import com.lidroid.xutils.ViewUtils;

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
 * Created by Hua on 15/11/6.
 */
public class PptView extends LinearLayout {
    private Context context;

    private View view;
    private LayoutInflater inflater;
    private List<SlideObj> list;

    private InsideViewFlipper mViewFlipper;
    private LinearLayout pptBallBox;

    private List<ImageView> pptBallList = new ArrayList<ImageView>();

    private PPTFlish flish = null;
    private Thread thread = null;

    public PptView(Context context, List<SlideObj> list) {

        super(context);
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.layout_ppt, null);

        mViewFlipper = (InsideViewFlipper) view.findViewById(R.id.ppt_images);
        pptBallBox = (LinearLayout) view.findViewById(R.id.ppt_ball);

        setPptView(list);
        addView(view);
    }

    private void setPptView(final List<SlideObj> list) {

        if (pptBallList.size() > 0) {
            mViewFlipper.removeAllViews();
            pptBallBox.removeAllViews();
            pptBallList.removeAll(pptBallList);
        }

        double w = WinTool.getWinWidth(context);
        double h = w / 320d * 198d;
        for (int i = 0; i < list.size(); i++) {
            ImageView image = new ImageView(context);
            image.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setId(i);
            DownloadImageLoader.loadImage(image, list.get(i).getCover());
            mViewFlipper.addView(image);
        }

        for (int i = 0; i < list.size(); i++) {
            ImageView image = new ImageView(context);
            if (i == 0) {
                image.setImageResource(R.drawable.ppt_on);
            } else {
                image.setImageResource(R.drawable.ppt_off);
            }
            image.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
            View view = new View(context);
            view.setLayoutParams(new LinearLayout.LayoutParams(5, 5));
            pptBallBox.addView(image);
            pptBallBox.addView(view);
            pptBallList.add(image);
        }

        startFlish();

        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {

            float eventX, eventY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        eventX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (eventX - event.getX() > 120) {// 左
                            setAnimation(true);
                        } else if (event.getX() - eventX > 120) {// 右
                            setAnimation(false);
                        } else if (Math.abs(eventX - event.getX()) < 10) {
                            onClickPPT(list.get(mViewFlipper.getDisplayedChild()).getObj());
                        }
                        break;
                }
                return true;
            }

        });

    }

    private void setAnimation(boolean isLeft) {

        long FLISHTIME = 500;

        if (mViewFlipper != null && mViewFlipper.getCurrentView() != null) {
            stopFlish();
            if (isLeft) {
                mViewFlipper.setInAnimation(AnimationTool
                        .toLeftIn(FLISHTIME));
                mViewFlipper.setOutAnimation(AnimationTool
                        .toLeftOut(FLISHTIME));
                mViewFlipper.showNext();
            } else {
                mViewFlipper.setInAnimation(AnimationTool
                        .toRightIn(FLISHTIME));
                mViewFlipper.setOutAnimation(AnimationTool
                        .toRightOut(FLISHTIME));
                mViewFlipper.showPrevious();
            }
            for (ImageView ball : pptBallList) {
                ball.setImageResource(R.drawable.ppt_off);
            }
            pptBallList.get(mViewFlipper.getDisplayedChild()).setImageResource(
                    R.drawable.ppt_on);
            startFlish();
        }
    }

    public void startFlish() {
        if (flish == null && pptBallList.size() > 1) {
            flish = new PPTFlish(handler);
            thread = new Thread(flish);
            thread.start();
        }
    }

    public void stopFlish() {
        if (flish != null) {
            flish.stop();
            flish = null;
            thread = null;
        }
    }

    private void onClickPPT(AssetObj obj) {
        Bundle b = new Bundle();
        b.putString(PreviewActivity.TEMPLATE_KEY,obj.getTemplate());
        Passageway.jumpActivity(context, SoundMp3Activity.class, b);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PPTFlish.FLISHFORPPT:
                    setAnimation(true);
                    break;
            }
        }

    };

}
