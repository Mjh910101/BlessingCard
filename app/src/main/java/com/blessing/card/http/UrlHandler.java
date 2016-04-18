package com.blessing.card.http;

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
 * Created by Hua on 15/10/26.
 */
public class UrlHandler {

    private static String index = "http://stg-zhufu.leanapp.cn";

    public static String getIndex() {
        return index;
    }

    /**
     * 第三方登录接口
     *
     * @return
     */
    public static String getAuth() {
        return getIndex() + "/api/v1/auth";
    }

    /**
     * 站内注册
     *
     * @return
     */
    public static String getRegister() {
        return getIndex() + "/api/v1/signup";
    }

    /**
     * 站内登录
     *
     * @return
     */
    public static String getLogin() {
        return getIndex() + "/api/v1/login";
    }

    /**
     * 上传明信片
     *
     * @return
     */
    public static String getPost() {
        return getIndex() + "/api/v1/post";
    }

    /**
     * 修改用户资料
     *
     * @return
     */
    public static String getModify() {
        return getIndex() + "/api/v1/modify";
    }

    /**
     * 反馈
     *
     * @return
     */
    public static String getFeedback() {
        return getIndex() + "/api/v1/feedback";
    }

    /**
     * 版本检测
     *
     * @return
     */
    public static String getVersion() {
        return getIndex() + "/api/v1/version";
    }


    /**
     * 获取贺卡
     *
     * @return
     */
    public static String getAssets() {
        return getIndex() + "/api/v1/assets";
    }

    /**
     * 贺卡分类
     *
     * @return
     */
    public static String getAssetsType() {
        return getIndex() + "/api/v1/assetsType";
    }

    /**
     * 获取邮戳
     *
     * @return
     */
    public static String getPoststamp() {
        return getIndex() + "/api/v1/poststamp";
    }
}
