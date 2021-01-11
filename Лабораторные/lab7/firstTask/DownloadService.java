package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DownloadService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String url = intent.getStringExtra("URL");
        if (url == null) sendBroadcast(new Intent("BROADCAST").putExtra("PATH", "NULL_URL"));
        else {
            String path = loadAndSave(url);
            sendBroadcast(new Intent("BROADCAST").putExtra("PATH", path));
        }
    }

    private String loadAndSave(String url) {
        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Log.d("CURRENT_THREAD", Thread.currentThread().toString());
            FileOutputStream foStream = this.getApplicationContext().openFileOutput("image", 0);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, foStream);
            foStream.close();
            return getApplicationContext().getFileStreamPath("image").getAbsolutePath();
        } catch (Exception exception) {
            Log.d("DOWNLOAD", exception.getMessage());
        }
        return "";
    }
}
