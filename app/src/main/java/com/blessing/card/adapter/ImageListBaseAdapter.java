package com.blessing.card.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.blessing.card.R;
import com.blessing.card.activity.PreviewActivity;
import com.blessing.card.activity.SoundMp3Activity;
import com.blessing.card.box.AssetObj;
import com.blessing.card.download.DownloadImageLoader;
import com.blessing.card.tool.Passageway;
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
 * Created by Hua on 15/10/21.
 */
public class ImageListBaseAdapter extends BaseAdapter {

    private Context context;
    private List<AssetObj> imageIdList;
    private LayoutInflater inflater;
    private int numColumns;

    public ImageListBaseAdapter(Context context, List<AssetObj> list, int numColumns) {
        initBaseAdapter(context);
        this.imageIdList = list;
        this.numColumns = numColumns;
    }

    public void addItems(List<AssetObj> list) {
        for (AssetObj obj : list) {
            imageIdList.add(obj);
        }
        notifyDataSetChanged();
    }

    private void initBaseAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderView holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_iamge_list_item, null);
            holder = new HolderView();

            holder.cardImage = (ImageView) convertView.findViewById(R.id.imageList_item_cardImage);
            holder.titleBox = (RelativeLayout) convertView.findViewById(R.id.imageList_item_titleBox);
            holder.title = (TextView) convertView.findViewById(R.id.imageList_item_titleName);
            holder.itemBox = (RelativeLayout) convertView.findViewById(R.id.imageList_item_box);

            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        setItemBox(holder.itemBox, position);
        setCardImage(holder.cardImage, position);
        setCardName(holder, position);

        return convertView;
    }

    public void setItemBox(RelativeLayout itemBox, int position) {
        int p = 10;
        int t, b = p, l = p, r = p;
        if (position < numColumns) {
            t = p * 2;
        } else {
            t = p;
        }
        if (numColumns == 2) {
            switch (position % numColumns) {
                case 0:
                    l = p * 2;
                    r = p;
                    break;
                default:
                    l = p;
                    r = p * 2;
                    break;
            }

        } else {
            switch (position % numColumns) {
                case 0:
                    l = p * 2;
                    r = p;
                    break;
                case 2:
                    l = p;
                    r = p * 2;
                    break;
                default:
                    l = p;
                    r = p;
                    break;
            }
        }
        Log.e("", "l : " + l + " t : " + t + " r : " + r + " b : " + b);
        itemBox.setPadding(0, t, 0, b);
    }

    private void setCardName(HolderView holder, int p) {
//        double w = WinTool.getWinWidth(context) / numColumns;//- (10 * (numColumns))
//        double h = WinTool.dipToPx(context, 20) * (4 - numColumns);
//        holder.titleBox.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) h));
//        holder.title.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) h));
        holder.title.setText(imageIdList.get(p).getTitle());
        holder.title.setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setCardImage(ImageView cardImage, final int p) {
        double w = (WinTool.getWinWidth(context) - (20 * (numColumns + 1))) / numColumns - WinTool.dipToPx(context, 3);//- (10 * (numColumns))
//        if (numColumns == 3 && p % numColumns != 1) {
//            w = w - 10;
//        }
        double h = w / 287d * 191d;
        cardImage.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
        cardImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        DownloadImageLoader.loadImage(cardImage, imageIdList.get(p).getTemplate_cover());
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(PreviewActivity.TEMPLATE_KEY, imageIdList.get(p).getTemplate());
                StatService.onEvent(context, "ClickCard", imageIdList.get(p).getObjectId(), 1);
                Passageway.jumpActivity(context, SoundMp3Activity.class, b);
            }
        });
    }


    class HolderView {
        ImageView cardImage;
        RelativeLayout titleBox;
        TextView title;
        RelativeLayout itemBox;
    }
}
