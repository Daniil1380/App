package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.FileOutputStream;
import java.io.InputStream;

public class DownloadService extends JobIntentService {
    Messenger messenger;

    @SuppressLint("HandlerLeak")
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        messenger = new Messenger(new ServiceHandler());
        return messenger.getBinder();
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String url = intent.getStringExtra("URL");
        if (url == null) sendBroadcast(new Intent("BROADCAST").putExtra("MESSAGE", "null url"));
        else {
            String path = loadAndSave(url);
            sendBroadcast(new Intent("BROADCAST").putExtra("MESSAGE", path));
        }
    }



    private String loadAndSave(String url) {
        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            FileOutputStream foStream = this.getApplicationContext().openFileOutput("image123", 0);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, foStream);
            foStream.close();
            return getApplicationContext().getFileStreamPath("image123").getAbsolutePath();
        } catch (Exception ignored) {
        }
        return "";
    }

    class DownloadAsyncTask extends AsyncTask<String, Void, String> {
        Messenger toActivity;

        public DownloadAsyncTask(Messenger toActivity) {
            this.toActivity = toActivity;
        }


        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String str = null;
            try {
                str = loadAndSave(url);
            } catch (Exception e) {
                Log.e("ERROR", "IN_ASYNC_TASK");
            }
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Message message = Message.obtain(null, 0, "TO_ACTIVITY");
            Bundle bundle = new Bundle();
            bundle.putString("ANSWER", s);
            message.setData(bundle);
            try {
                toActivity.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    class ServiceHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.obj.equals("TO_SERVICE")) {
                Log.d("thread", Thread.currentThread().getName());
                new DownloadAsyncTask(msg.replyTo).execute(
                        msg.getData().getString("URL")
                );
            }
        }
    }

}

