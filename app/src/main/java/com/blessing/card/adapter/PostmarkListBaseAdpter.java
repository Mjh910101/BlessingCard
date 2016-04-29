package com.blessing.card.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blessing.card.R;
import com.blessing.card.box.PoststampObj;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.tool.WinTool;

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
public class PostmarkListBaseAdpter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private int onChoosePosition = 0;
    private List<PoststampObj> itemList;

    public PostmarkListBaseAdpter(Context context, List<PoststampObj> list) {
        initBaseAdapter(context);
        this.itemList = list;
    }

    private void initBaseAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getOnChoosePosition() {
        return onChoosePosition;
    }

    public PoststampObj getOnChoosePostmar() {
        return itemList.get(getOnChoosePosition());
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderView holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_postmark_list_item, null);
            holder = new HolderView();

            holder.postmarkBox = (LinearLayout) convertView.findViewById(R.id.postmark_item_postmarkBox);
            holder.postmarkImage = (ImageView) convertView.findViewById(R.id.postmark_item_postmarkImage);
            holder.onChoose = (ImageView) convertView.findViewById(R.id.postmark_item_onChoose);

            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        setView(holder, position);
        setOnclick(convertView, position);

        return convertView;
    }

    private void setView(HolderView holder, int position) {
        if (onChoosePosition == position) {
            holder.postmarkBox.setBackgroundResource(R.drawable.postmark_white_bg);
            holder.onChoose.setVisibility(View.VISIBLE);
        } else {
            holder.postmarkBox.setBackgroundResource(R.drawable.postmark_lucency_bg);
            holder.onChoose.setVisibility(View.GONE);
        }

        int p = 10;
        double w = WinTool.getWinWidth(context);
        double h = w / 640d * 184d;
        holder.postmarkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) h));
//        holder.postmarkBox.setPadding(p, p, p, p);
        setPostmarkImage(holder.postmarkImage, position, w, h);
    }

    private void setOnclick(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChoosePosition != position) {
                    onChoosePosition = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void setPostmarkImage(ImageView view, int position, double width, double height) {
        PoststampObj obj = itemList.get(position);
        int p = 15;
        double h = height / 184d * 160d;
        double w = h / (double) obj.getThumbnails_height() * (double) obj.getThumbnails_wdith();
//        double w = WinTool.getWinWidth(context);
//        double h = w / 640d * 184d;
        view.setLayoutParams(new LinearLayout.LayoutParams((int) h, (int) h));
        view.setPadding(p, p, p, p);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        DownloadImageLoader.loadImage(view, obj.getThumbnails_url());
    }

    class HolderView {
        LinearLayout postmarkBox;
        ImageView postmarkImage;
        ImageView onChoose;
    }
}
