package com.blessing.card.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.blessing.card.R;
import com.blessing.card.handler.BitmapHandler;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.tool.Passageway;
import com.blessing.card.tool.WinTool;
import com.blessing.card.view.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

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
public class ShareActivity extends BaseActivity {

    public final static String URL_KEY = "url";
    public final static String TITIE_TEXT = "祝你-不一样的祝福选择";
    public final static String CONTENT_TEXT = "我在@祝你 挑了张明信片送给你，希望你会喜欢！";

//    @ViewInject(R.id.title_userIcon)
//    private ImageView userIcon;
//    @ViewInject(R.id.title_seekText)
//    private TextView seekText;
//    @ViewInject(R.id.title_titleName)
//    private TextView titleName;
    @ViewInject(R.id.share_dataGrid)
    private GridView dataGrid;
    @ViewInject(R.id.share_progress)
    private ProgressBar progress;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ViewUtils.inject(this);
        context = this;

        initActivity();
    }


    @OnClick({R.id.title_backIcon, R.id.share_backMainBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_backIcon:
                finish();
                break;
            case R.id.share_backMainBtn:
                backToMain();
                break;
        }
    }

    private void backToMain() {
        Passageway.jumpToActivity(context, ImageListActivity.class);
    }

    private void initActivity() {
//        userIcon.setVisibility(View.GONE);
//        seekText.setVisibility(View.GONE);
//        titleName.setVisibility(View.VISIBLE);

//        titleName.setText("发送明信片");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            url = b.getString(URL_KEY);
//            MessageHandler.showToast(context, url);
            dataGrid.setAdapter(new ShareBaseAdapter());
        }

    }

    class ShareBaseAdapter extends BaseAdapter {

        List<Integer> dataList;
        LayoutInflater inflater;

        ShareBaseAdapter() {
            initDataList();
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private void initDataList() {
            dataList = new ArrayList<Integer>();

            if (isClientValid(Wechat.NAME)) {
                dataList.add(R.drawable.share_weixin_icon);
                dataList.add(R.drawable.share_pengyouquan_icon);
            } else {
                dataList.add(R.drawable.weixin_login_gary_icon);
                dataList.add(R.drawable.share_pengyouquan_gray_icon);
            }
//            dataList.add(R.drawable.share_weibo_icon);
            dataList.add(R.drawable.share_msm_icon);
            if (isClientValid(QQ.NAME)) {
                dataList.add(R.drawable.share_qq_icon);
            } else {
                dataList.add(R.drawable.qq_login_gary_icon);
            }

            dataList.add(R.drawable.share_copy_icon);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            HolderView holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_share_item, null);
                holder = new HolderView();

                holder.shareIcon = (ImageView) convertView.findViewById(R.id.share_item_shareIcon);
                holder.shareText = (TextView) convertView.findViewById(R.id.share_item_shareText);

                convertView.setTag(holder);
            } else {
                holder = (HolderView) convertView.getTag();
            }

            setViewMessage(holder.shareIcon, holder.shareText, position);
            serOnClickView(convertView, position);

            return convertView;
        }

        private void serOnClickView(View view, final int position) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            if (isClientValid(Wechat.NAME)) {
                                shareWeixinBtn();
                            }
                            break;
                        case 1:
                            if (isClientValid(Wechat.NAME)) {
                                shareFriendBtn();
                            }
                            break;
//                        case 2:
//                            shareWeiboBtn();
//                            break;
                        case 2:
                            shareMsmBtn();
                            break;
                        case 3:
                            if (isClientValid(QQ.NAME)) {
                                shareQqBtn();
                            }
                            break;
                        default:
                            copy(url);
                            break;
                    }
                }
            });
        }

        private boolean isClientValid(String name) {
            return ShareSDK.getPlatform(context, name).isClientValid();
        }

        private void shareWeiboBtn() {
            StatService.onEvent(context, "ShareTo", "WeiBo", 1);
            SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
            sp.setText(CONTENT_TEXT + " " + url);
            sp.setImagePath(BitmapHandler.getLogoForPath(context));

            Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
            weibo.share(sp);
            MessageHandler.showToast(context, "分享到微博");

        }

        public void copy(String content) {
            StatService.onEvent(context, "ShareTo", "COPY", 1);
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
            MessageHandler.showToast(context, "已复制至复制面板");
        }

        private void shareMsmBtn() {
            StatService.onEvent(context, "ShareTo", "SMS", 1);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(Uri.parse("smsto:"));
            sendIntent.putExtra("sms_body", CONTENT_TEXT + " " + url);
            startActivity(sendIntent);
        }

        private void shareQqBtn() {
            StatService.onEvent(context, "ShareTo", "QQ", 1);
            QQ.ShareParams sp = new QQ.ShareParams();
            sp.setTitle(TITIE_TEXT);
            sp.setText(CONTENT_TEXT);
            sp.setTitleUrl(url);
            sp.setImagePath(BitmapHandler.getLogoForPath(context));

            Platform qq = ShareSDK.getPlatform(QQ.NAME);
            qq.share(sp);
        }

        private void shareFriendBtn() {
            StatService.onEvent(context, "ShareTo", "FriendsCircle", 1);
            Wechat.ShareParams sp = new Wechat.ShareParams();
            sp.setShareType(Wechat.SHARE_WEBPAGE);
            sp.setTitle(TITIE_TEXT);
            sp.setText(CONTENT_TEXT);
            sp.setUrl(url);
            sp.setImageData(BitmapHandler.getLogo(context));

            Platform wcshat = ShareSDK.getPlatform(WechatMoments.NAME);
            wcshat.share(sp);
        }

        private void shareWeixinBtn() {
            StatService.onEvent(context, "ShareTo", "WeiXin", 1);
            Wechat.ShareParams sp = new Wechat.ShareParams();
            sp.setShareType(Wechat.SHARE_WEBPAGE);
            sp.setTitle(TITIE_TEXT);
            sp.setText(CONTENT_TEXT);
            sp.setUrl(url);
            sp.setImageData(BitmapHandler.getLogo(context));

            Platform wcshat = ShareSDK.getPlatform(Wechat.NAME);
            wcshat.share(sp);
        }

        private void setViewMessage(ImageView shareIcon, TextView shareText, int position) {
            int w = WinTool.getWinWidth(context) / 5;
            int p = WinTool.dipToPx(context, 8);
            shareIcon.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            shareIcon.setPadding(p, p, p, p);
            shareIcon.setImageResource(dataList.get(position));

            shareText.setText(getShareText(position));
        }

        private String getShareText(int position) {
            switch (position) {
                case 0:
                    if (isClientValid(Wechat.NAME)) {
                        return "微信";
                    } else {
                        return "请先安装";

                    }
                case 1:
                    if (isClientValid(Wechat.NAME)) {
                        return "朋友圈";
                    } else {
                        return "请先安装";
                    }
//                case 2:
//                    return "微博";
                case 2:
                    return "短信";
                case 3:
                    if (isClientValid(QQ.NAME)) {
                        return "QQ";
                    } else {
                        return "请先安装";
                    }
                default:
                    return "复制网址";
            }
        }


        class HolderView {
            ImageView shareIcon;
            TextView shareText;
        }
    }

}
