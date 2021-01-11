package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String URL = "https://sun9-8.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3&type=album";
    Messenger serviceMessenger = null;
    Messenger messenger = new Messenger(new ClientHandler());
    ServiceConnection serviceC = new ServiceConnectionImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, serviceC, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceC);
    }


    public void click(View view) throws RemoteException {
        Message message = Message.obtain(null, 0, "TO_SERVICE");
        message.replyTo = messenger;
        Bundle bundle = new Bundle();
        bundle.putString("URL", URL);
        message.setData(bundle);
        serviceMessenger.send(message);
    }

    class ClientHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.obj.equals("TO_ACTIVITY")){
                TextView textView = findViewById(R.id.textView);
                textView.setText(msg.getData().getString("ANSWER"));
            }
        }
    }

    class ServiceConnectionImpl implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
        }
    }
    }








