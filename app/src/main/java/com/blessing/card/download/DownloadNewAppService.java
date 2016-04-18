package com.blessing.card.download;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import com.blessing.card.R;
import com.blessing.card.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadNewAppService extends Service {

    public final static String KEY = "url";

    private final static int NOTIFICATION_ID = 100;
    private final static int NOTIFICATION_FLAG = 1;

    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;

    private File updateDir = null;
    private File updateFile = null;

//    private NotificationManager updateNotificationManager = null;
//    private Notification updateNotification = null;
    private Intent updateIntent = null;
//    private PendingIntent updatePendingIntent = null;

    private Context context;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(),
                    "ZHUNI");
            updateFile = new File(updateDir.getPath(), "district_up.apk");
            if (updateFile.exists()) {
                updateFile.delete();
            }
        }

        updateIntent = new Intent(context, MainActivity.class);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            sendNotification("准备下载");
            new Thread(new updateRunnable(bundle.getString(KEY))).start();
        } else {
            sendNotification("下载失败");
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:

                    Uri uri = Uri.fromFile(updateFile);
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(installIntent);
                    sendNotification("下载完成");
                    break;
                case DOWNLOAD_FAIL:
                    sendNotification("下载失败");
                    break;
                default:
                    stopService(updateIntent);
            }
            stopSelf();
        }

    };

    private long downloadUpdateFile(String downloadURL, File saveFile)
            throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadURL);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection
                    .setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes="
                        + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() != 200) {
                throw new Exception("fail");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte[] buffer = new byte[1024 * 4];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
                    downloadCount += 10;
                    sendNotification((int) totalSize * 100 / updateTotalSize + "%");
                }
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String content) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification notify = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getResources()
                        .getString(R.string.app_name))
                .setContentTitle(getResources()
                        .getString(R.string.app_name))
                .setContentText(content)
                .setContentIntent(pendingIntent).setNumber(NOTIFICATION_ID).build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(NOTIFICATION_FLAG, notify);
    }

    class updateRunnable implements Runnable {

        private String url;

        private updateRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }

                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }

                long downloadSize = downloadUpdateFile(url, updateFile);

                if (downloadSize > 0) {
                    Message.obtain(handler, DOWNLOAD_COMPLETE).sendToTarget();
                } else {
                    Message.obtain(handler, DOWNLOAD_FAIL).sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Message.obtain(handler, DOWNLOAD_FAIL).sendToTarget();
            }
        }

    }

}
