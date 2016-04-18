package com.blessing.card.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.blessing.card.download.DownloadImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapHandler {

    public static byte[] bmpToByte(Bitmap bmp) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, bos);
        byte[] b = bos.toByteArray();
        bmp.recycle();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static Bitmap getLogo(Context context) {
        Bitmap image = null;
        try {
            InputStream is = context.getAssets().open("logo.png");
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String getLogoForPath(Context context) {
        String path = DownloadImageLoader.getImagePath() + "/logo.png";
        File f = new File(path);
        if (!f.exists()) {
            Bitmap b = getLogo(context);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(f);
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;

    }
}
