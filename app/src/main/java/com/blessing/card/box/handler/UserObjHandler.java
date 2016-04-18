package com.blessing.card.box.handler;

import android.content.Context;

import com.blessing.card.activity.AuthLoginActivity;
import com.blessing.card.activity.LoginActivity;
import com.blessing.card.box.UserObj;
import com.blessing.card.dailog.MessageDialog;
import com.blessing.card.handler.JsonHandle;
import com.blessing.card.handler.MessageHandler;
import com.blessing.card.handler.SystemHandle;
import com.blessing.card.tool.Passageway;

import org.json.JSONObject;

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
public class UserObjHandler {

    public static UserObj getUserObj(JSONObject json) {
        UserObj obj = new UserObj();

        JSONObject avater = JsonHandle.getJSON(json, UserObj.AVATER);
        if (avater != null) {
            obj.setAvatar(JsonHandle.getString(avater, "url"));
        }
        obj.setBirthday(JsonHandle.getString(json, UserObj.BIRTHDAY));
        obj.setCreatedAt(JsonHandle.getString(json, UserObj.CREATED_AT));
        obj.setNickname(JsonHandle.getString(json, UserObj.MICK_NAME));
        obj.setObjectId(JsonHandle.getString(json, UserObj.OBJECT_ID));
        obj.setSessionToken(JsonHandle.getString(json, UserObj.SESSION_TOKEN));
        obj.setSex(JsonHandle.getString(json, UserObj.SEX));
        obj.setUsername(JsonHandle.getString(json, UserObj.USER_NAME));

        return obj;
    }

    public static void deleteUser(Context context) {
        saveAvatar(context, "");
        saveBirthday(context, "");
        saveCreatedAt(context, "");
        saveNickname(context, "");
        saveObjectId(context, "");
        saveSessionToken(context, "");
        saveSex(context, "");
        saveUsername(context, "");
    }

    public static void saveUser(Context context, UserObj obj) {
        saveAvatar(context, obj.getAvatar());
        saveBirthday(context, obj.getBirthday());
        saveCreatedAt(context, obj.getCreatedAt());
        saveNickname(context, obj.getNickname());
        saveObjectId(context, obj.getObjectId());
        saveSessionToken(context, obj.getSessionToken());
        saveSex(context, obj.getSex());
        saveUsername(context, obj.getUsername());
    }

    private final static String KEY = "user_";

    public static void saveAvatar(Context context, String avatar) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.AVATER, avatar);
    }

    public static String getAvatar(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.AVATER);
    }

    public static void saveBirthday(Context context, String birthday) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.BIRTHDAY, birthday);
    }

    public static String getBirthday(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.BIRTHDAY);
    }

    public static void saveCreatedAt(Context context, String createdAt) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.CREATED_AT, createdAt);
    }

    public static void saveNickname(Context context, String nickname) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.MICK_NAME, nickname);
    }

    public static String getNickname(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.MICK_NAME);
    }

    public static void saveObjectId(Context context, String objectId) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.OBJECT_ID, objectId);
    }

    public static String getObjectId(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.OBJECT_ID);
    }

    public static String getSessionToken(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.SESSION_TOKEN);
    }

    public static void saveSessionToken(Context context, String sessionToken) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.SESSION_TOKEN, sessionToken);
    }

    public static void saveSex(Context context, String sex) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.SEX, sex);
    }

    public static void saveUsername(Context context, String username) {
        SystemHandle.saveStringMessage(context, KEY + UserObj.USER_NAME, username);
    }

    public static String getUserName(Context context) {
        return SystemHandle.getString(context, KEY + UserObj.USER_NAME);
    }


    public static boolean isLogin(Context context) {
        String token = getSessionToken(context);
        return !token.equals("");
    }

    public static boolean isLoginShowMessage(Context context) {
        boolean b = isLogin(context);
        if (!b) {
//            MessageHandler.showToast(context, "请先登陆!");
            showMessageDialog(context);
        }
        return b;
    }

    private static void showMessageDialog(final Context context) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("请先登录!");
        dialog.setCommitStyle("登录");
        dialog.setCommitListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                Passageway.jumpActivity(context, AuthLoginActivity.class, AuthLoginActivity.LOGIN_REQUEST_CODE);
            }
        });
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
    }
}
