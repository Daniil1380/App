package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        }

    public void onClick(View view) {
        startService(
                new Intent(this, DownloadService.class).putExtra(
                "URL",
                "https://sun9-8.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3&type=album"
        ));
    }
}



