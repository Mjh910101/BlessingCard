package com.blessing.card.box.handler;

import com.blessing.card.box.PoststampObj;
import com.blessing.card.handler.JsonHandle;

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
 * Created by Hua on 15/11/27.
 */
public class PoststampObjHandler {

    private static PoststampObj clickPoststampObj;

    public static List<PoststampObj> getPoststampObjList(JSONArray array) {
        List<PoststampObj> list = new ArrayList<PoststampObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getPoststampObj(JsonHandle.getJSON(array, i)));
        }
        return list;
    }

    public static PoststampObj getPoststampObj(JSONObject json) {
        PoststampObj obj = new PoststampObj();

        obj.setAddressLabel(JsonHandle.getString(json, PoststampObj.ADDRESS_LABEL));
        obj.setCreatedAt(JsonHandle.getString(json, PoststampObj.CREATED_AT));
        obj.setHeight(JsonHandle.getInt(json, PoststampObj.HEIGHT));
        obj.setObjectId(JsonHandle.getString(json, PoststampObj.OBJECT_ID));
        obj.setTimeLabel(JsonHandle.getString(json, PoststampObj.TIME_LABEL));
        obj.setUpdatedAt(JsonHandle.getString(json, PoststampObj.UPLAED_AT));
        obj.setUrl(JsonHandle.getString(json, PoststampObj.URL));
        obj.setThumbnails_url(JsonHandle.getString(json, PoststampObj.THUMBAILS_URL));
        obj.setThumbnails_wdith(JsonHandle.getInt(json, PoststampObj.THUMBAILS_WDITH));
        obj.setThumbnails_height(JsonHandle.getInt(json, PoststampObj.THUMBAILS_HEIGHT));
        obj.setWdith(JsonHandle.getInt(json, PoststampObj.WDITH));

        return obj;
    }

    public static void saveClickPoststampObj(PoststampObj obj) {
        if (clickPoststampObj != null) {
            clickPoststampObj = null;
        }
        clickPoststampObj = obj;
    }

    public static PoststampObj getClickPoststampObj() {
        return clickPoststampObj;
    }

}
