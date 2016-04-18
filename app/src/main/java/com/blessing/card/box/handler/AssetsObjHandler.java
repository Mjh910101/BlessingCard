package com.blessing.card.box.handler;

import com.blessing.card.box.AssetObj;
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
 * Created by Hua on 15/11/4.
 */
public class AssetsObjHandler {

    public static List<AssetObj> getAssetsObjList(JSONArray array) {

        List<AssetObj> list = new ArrayList<AssetObj>();

        for (int i = 0; i < array.length(); i++) {
            list.add(getAssetsObj(JsonHandle.getJSON(array, i)));
        }

        return list;

    }

    public static AssetObj getAssetsObj(JSONObject json) {
        AssetObj obj = new AssetObj();

        obj.setObjectId(JsonHandle.getString(json, AssetObj.OBJECT_ID));
        obj.setTemplate(JsonHandle.getString(json, AssetObj.TEMPLATEE));
        obj.setTitle(JsonHandle.getString(json, AssetObj.TITLE));
        obj.setTemplate_cover(JsonHandle.getString(json, AssetObj.TEMPLATEE_COVER));

        return obj;
    }

}
