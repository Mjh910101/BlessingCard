package com.blessing.card.box;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

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
 * Created by Hua on 15/11/27.
 */
public class PoststampObj implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    public final static String CREATED_AT = "createdAt";
    public final static String OBJECT_ID = "objectId";
    public final static String UPLAED_AT = "updatedAt";
    public final static String ADDRESS_LABEL = "addressLabel";
    public final static String TIME_LABEL = "timeLabel";
    public final static String URL = "url";
    public final static String THUMBAILS_URL = "thumbnails_url";
    public final static String WDITH = "wdith";
    public final static String HEIGHT = "height";
    public final static String THUMBAILS_WDITH = "thumbnails_wdith";
    public final static String THUMBAILS_HEIGHT = "thumbnails_height";

    private String createdAt;
    private String objectId;
    private String updatedAt;
    private String url;
    private String thumbnails_url;
    private int wdith;
    private int height;
    private int thumbnails_wdith;
    private int thumbnails_height;

    public int getThumbnails_wdith() {
        return thumbnails_wdith;
    }

    public void setThumbnails_wdith(int thumbnails_wdith) {
        this.thumbnails_wdith = thumbnails_wdith;
    }

    public int getThumbnails_height() {
        return thumbnails_height;
    }

    public void setThumbnails_height(int thumbnails_height) {
        this.thumbnails_height = thumbnails_height;
    }

    private Label addressLabel;
    private Label timeLabel;

    public String getThumbnails_url() {
        return thumbnails_url;
    }

    public void setThumbnails_url(String thumbnails_url) {
        this.thumbnails_url = thumbnails_url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Label getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String address) {
        this.addressLabel = new Label(address);
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String time) {
        this.timeLabel = new Label(time);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWdith() {
        return wdith;
    }

    public void setWdith(int wdith) {
        this.wdith = wdith;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public class Label {

        boolean isExistence = false;

        int x;
        int y;
        int w;
        int h;
        int s;

        public Label(String str) {
            try {
                String[] strs = str.trim().split(",");
                if (strs.length == 5) {
                    isExistence = true;

                    x = Integer.valueOf(strs[0]) + 8;
                    y = Integer.valueOf(strs[1]) + 15;
                    w = Integer.valueOf(strs[2]);
                    h = Integer.valueOf(strs[3]);
                    s = Integer.valueOf(strs[4]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                isExistence = false;
            }
        }

        public boolean isExistence() {
            return isExistence;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getW() {
            return w;
        }

        public int getH() {
            return h;
        }

        public int getS() {
            return s;
        }
    }

}
