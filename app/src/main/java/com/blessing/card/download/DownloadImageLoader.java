package com.blessing.card.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.blessing.card.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class DownloadImageLoader {

    private final static int INDEX_IMAGE = R.drawable.user_pig_icon;
    private final static int EMPTY_IMAGE = R.drawable.fail_icon;
    private final static int FAIL_IMAGE = R.drawable.fail_icon;

    private final static String RootName = "ZHUNI";
    private final static String ImageFileName = RootName + "/Image";
    private final static String NOMEDIA = ImageFileName + "/.nomedia";

    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions mDisplayImageOptions = null;
    private static DisplayImageOptions mRoundedDisplayImageOptions = null;
    private static DisplayImageOptions meteorImageOptions = null;

    private static int rounded = 0;

    public static void initLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3).denyCacheImageMultipleSizesInMemory()
                        // .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(200) // 缂傛挸鐡ㄩ惃鍕瀮娴犺埖鏆熼柌锟�
                .discCache(new UnlimitedDiscCache(getImageFile()))// 閼奉亜鐣炬稊澶岀处鐎涙鐭惧锟�
                .writeDebugLogs().build();

        imageLoader.init(config);
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        }
        return "";
    }

    public static String getImagePath() {
        return getSDPath() + "/" + ImageFileName;
    }

    private static File getImageFile() {
        return new File(getImagePath());
    }

    public static void loadImageForFile(ImageView mImageView, String path) {
        loadImage(mImageView, "file://" + path, 0);
    }

    public static void loadImageForFile(ImageView mImageView, String path,
                                        ImageLoadingListener mImageLoadingListener) {
        loadImage(mImageView, "file://" + path, 0, mImageLoadingListener);
    }

    public static void loadImageForID(ImageView mImageView, int id) {
        loadImage(mImageView, "drawable://" + id, 0);
    }

    public static void loadImage(ImageView mImageView, String url) {
        loadImage(mImageView, url, 0);
    }

    public static void loadImage(ImageView mImageView, String url,
                                 ImageLoadingListener mImageLoadingListener) {
        loadImage(mImageView, url, 0, mImageLoadingListener);
    }

    public static void loadRotationImage(ImageView mImageView, String url,
                                         int rounded) {
        loadImage(mImageView, url, rounded, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Log.e("", "onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                Log.e("", "onLoadingFailed");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                Log.e("", "onLoadingComplete");
                if (loadedImage != null) {
                    if (loadedImage.getWidth() > loadedImage.getHeight()) {
                        view.setRotation(90);
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.e("", "onLoadingCancelled");
            }
        });
    }

    public static void loadImage(ImageView mImageView, String url, int rounded) {
        loadImage(mImageView, url, rounded, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Log.e("", "onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                Log.e("", "onLoadingFailed");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                Log.e("", "onLoadingComplete");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.e("", "onLoadingCancelled");
            }
        });
    }

    public static void loadImage(ImageView mImageView, String url, int rounded,
                                 ImageLoadingListener mImageLoadingListener) {
        Log.e("image_URL", url);
        DisplayImageOptions imageOptions = null;
        if (rounded == 0) {
            imageOptions = getDisplayImageOptions();
        } else {
            imageOptions = getRoundedDisplayImageOptions(rounded);
        }
        imageLoader.displayImage(url, mImageView, imageOptions,
                mImageLoadingListener);
    }

    public static void loadImageForUrl(String url, ImageLoadingListener mImageLoadingListener) {
        imageLoader.loadImage(url, mImageLoadingListener);
    }

    public static Bitmap loadImageSync(String url) {
        return imageLoader.loadImageSync(url);
    }

    public static void loadMeteorImageForFile(ImageView mImageView, String url,
                                              ImageLoadingListener imageLoadingListener) {
        imageLoader.displayImage("file://" + url, mImageView,
                getMeteorImageOptions(), imageLoadingListener);
    }

    private static DisplayImageOptions getRoundedDisplayImageOptions(int rounded) {
        if (rounded != DownloadImageLoader.rounded) {
            mRoundedDisplayImageOptions = null;
            DownloadImageLoader.rounded = rounded;
        }
        if (mRoundedDisplayImageOptions == null) {
            mRoundedDisplayImageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(INDEX_IMAGE)
                    .showImageForEmptyUri(EMPTY_IMAGE)
                    .showImageOnFail(FAIL_IMAGE)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                    .cacheOnDisc(true).considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(rounded)).build();
        }
        return mRoundedDisplayImageOptions;
    }

    private static DisplayImageOptions getMeteorImageOptions() {
        if (meteorImageOptions == null) {
            meteorImageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(INDEX_IMAGE)
                    .showImageForEmptyUri(EMPTY_IMAGE)
                    .showImageOnFail(FAIL_IMAGE)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                    .considerExifParams(true).build();
        }
        return meteorImageOptions;
    }

    private static DisplayImageOptions getDisplayImageOptions() {
        if (mDisplayImageOptions == null) {
            mDisplayImageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(INDEX_IMAGE)
                    .showImageForEmptyUri(EMPTY_IMAGE)
                    .showImageOnFail(FAIL_IMAGE)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisc(true)
                    .considerExifParams(true).build();
        }
        return mDisplayImageOptions;
        // .displayer(new
        // RoundedBitmapDisplayer(20))閺勵垰鎯佺拋鍓х枂娑撳搫娓剧憴鎺炵礉瀵冨娑撳搫顧嬬亸锟�
        // .displayer(new
        // FadeInBitmapDisplayer(100))閺勵垰鎯侀崶鍓у閸旂姾娴囨總钘夋倵濞撴劕鍙嗛惃鍕З閻㈢粯妞傞梻锟�
    }

    public static void loadImage(String uri, ImageLoadingListener listener) {
        imageLoader.loadImage(uri, listener);
    }

}
