# Лабораторная работа №7. Сервисы и Broadcast Receivers.

## Цели
Получить практические навыки разработки сервисов (started и bound) и Broadcast Receivers.

## Задачи
### Задача 1. Started сервис для скачивания изображения
В [лабораторной работе №6](../06/TASK.md) был разработан код, скачивающий картинку из интернета. На основе этого кода разработайте started service, скачивающий файл из интернета. URL изображения для скачивания должен передаваться в Intent. Убедитесь (и опишите доказательство в отчете), что код для скачивания исполняется не в UI потоке

Добавьте в разработанный сервис функцию отправки broadcast сообщения по завершении скачивания. Сообщение (Intent) должен содержать путь к скачанному файлу.

Service – это компонент приложения, который используется для выполнения долгих фоновых операций без взаимодействия с пользователем.
Любой компонент приложения может запустить сервис, который продолжит работу, даже если пользователь перейдет в другое приложение.
Примеры использования сервисов: проигрывание музыки, трекинг локации водителя в приложении такси, загрузка файла из сети.

Сервисы делятся на два вида по способу использования: Started и Bound

Started Service запускается методом startService(Intent intent). 

Посмотрим на жизненный цикл Started и Bound сервисов.

![27](C:\Users\Даниил\Desktop\27.png)

###### Методы жизненного цикла started сервиса:

onCreate() – вызывается, когда сервис создается системой. Для создания started сервиса используется метод startService().

onStartCommand() – вызывается, когда сервис переходит в активное состояние. Код, который выполняет сервис, должен быть написан в этом методе.

onDestroy() – вызывается, когда сервис уничтожается системой. Это происходит после вызова stopSelf() или stopService(). Также система может убить процесс с фоновым сервисом когда не хватает ресурсов или, начиная с Android 8.0, для ограничения фоновых работы.

###### Методы жизненного цикла bound сервиса:

onCreate() – вызывается когда первый клиент присоединяется к сервису вызовом bindService() с флагом BIND_AUTO_CREATE.

onBind() – вызывается системой, когда первый клиент присоединяется к сервису вызовом метода bindService(). После вызова этого метода bound сервис переходит в активное состояние.

onUnbind() – вызывается системой, когда все клиенты отсоединились от сервиса вызовом метода unbindService().

onDestroy() – вызывается после onUnbind(), перед тем как система уничтожит сервис.

```java
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
        } catch (Exception ignored) {
            Log.d("DOWNLOAD", "SOMETHING WRONG");
        }
        return "";
    }
}
```

В Android 8.0 (API level 26) введены ограничения на работу с фоновыми сервисами. Эти ограничения касаются приложений с `targetSdkVersion ≥ 26`, но пользователь может включить ограничения для всех приложений в настройках.

Начиная с Android 8.0, Фоновые сервисы работают пока пользователь взаимодействует с приложением. Система убивает все фоновые сервисы через несколько минут после того, как пользователь покидает приложение.
Нельзя запустить фоновый сервис для приложения, с которым не взаимодействует пользователь.

MODE_PRIVATE = 0, режим создания файла: режим по умолчанию, в котором к созданному файлу может получить доступ только вызывающее приложение.

### Задача 2. Broadcast Receiver

Разработайте два приложения: первое приложение содержит 1 activity с 1 кнопкой, при нажатии на которую запускается сервис по скачиванию файла. Второе приложение содержит 1 broadcast receiver и 1 activity. Broadcast receiver по получении сообщения из сервиса инициирует отображение *пути* к изображению в `TextView` в Activity.

IntentService – это `Service`, который работает (выполняет код метода onHandleIntent()) в фоновом потоке.
`IntentService` останавливается сам после завершения выполнения метода `onHandleIntent()`, т.е. не нужно вызывать stopSelf()
`IntentService` работает на одном фоновом потоке и выполняет задачи в порядке очереди
Используется когда нужно выполнить фоновую задачу (*не* привязанную активити) в фоновом потоке (*не* в UI треде).

> Начиная с Android 8.0 (API level 26) ОС ограничивает работу фоновых сервисов. `IntentService` – не исключение, поэтому если target api приложения – 26 или выше, используется JobIntentService.

```java
public class MainActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("PATH");
                TextView textView = findViewById(R.id.textView);
                textView.setText(msg);
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("BROADCAST"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
```

BroadcastReceiver –  используется для получения сообщений системы, других компонентов приложения и сторонних приложений.

События обрабатываются в методе BroadcastReceiver.onReceive(), который вызывает система.

Пример использования: регистрация ресивера для прослушивания событий изменения языка в системе. Для этого используется ACTION_LOCALE_CHANGED

### Задача 3. Bound Service для скачивания изображения
Сделайте разработанный сервис одновременно bound И started: переопределите метод `onBind`. Из тела метода возвращайте `IBinder`, полученный из класса [`Messenger`](https://developer.android.com/guide/components/bound-services?hl=ru#Messenger). Убедитесь (доказательство опишите в отчете), что код скачивания файла исполняется не в UI потоке.

Измените способ запуска сервиса в первом приложении: вместо `startService` используйте `bindService`. При нажатии на кнопку отправляйте сообщение [`Message`](https://developer.android.com/reference/android/os/Message.html?hl=ru), используя класс `Messenger`, полученный из интерфейса `IBinder` в методе [`onServiceConnected`](https://developer.android.com/reference/android/content/ServiceConnection.html?hl=ru#onServiceConnected(android.content.ComponentName,%20android.os.IBinder)).

Добавьте в первое приложение `TextView`, а в сервис отправку [обратного](https://developer.android.com/reference/android/os/Message.html?hl=ru#replyTo) сообщения с местоположением скачанного файла. При получении сообщения от сервиса приложение должно отобразить путь к файлу на экране.

Обратите внимание, что разработанный сервис должен быть одновременно bound И started. Если получен интент через механизм started service, то сервис скачивает файл и отправляет broadcast (started service не знает своих клиентов и не предназначен для двухсторонней коммуникации). Если получен message через механизм bound service, то скачивается файл и результат отправляется тому клиенту, который запросил этот файл (т.к. bound service знает всех своих клиентов и может им отвечать).

##### IBinder

Этот интерфейс описывает абстрактный протокол для взаимодействия с удаленным объектом

##### Message

Определяет сообщение, содержащее описание и произвольный объект данных, которое может быть отправлено обработчику. Этот объект содержит два дополнительных поля типа int и дополнительное поле объекта.

##### Messenger

Ссылка на обработчик, который другие могут использовать для отправки ему сообщений. Это позволяет реализовать обмен сообщениями между процессами, создав Messenger, указывающий на обработчик в одном процессе, и передав этот Messenger другому процессу.

##### Жизненный цикл

Сервис может быть одновременно started и bound. В этом случае вызываются все методы жизненного цикла обоих типов сервисов.
`onDestroy()` у такого сервиса вызывается когда все клиенты отсоединены и сервис остановлен вызовом метода `stopSelf()` или `stopService()`.
Если же все клиенты отсоединяются, но сервис не остановлен, то вызывается метод `onUnbind()` и сервис продолжает работать.
`onUnbind()` возвращает `boolean`. Если вернуть `true`, то при присоединении первого клиента после `onUnbind()` вызывается метод onRebind(), иначе вызывается `onBind()`.



```java
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
```

```java
public class MainActivity extends AppCompatActivity {
    String URL = "https://sun9-8.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3&type=album";
    Messenger serviceMessenger = null;
    Messenger messenger = new Messenger(new ClientHandler());
    ServiceConnection serviceConnection = new ServiceConnectionImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
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
```

## Выводы

Во время выполнения лабораторной работы был создан Broadcast Receiver, started и bound сервисы. Программа написана на языке Java. Изучены жизненные циклы сервисов и проведено сравние двух вариантов сервисов. Итак, 

- Unbound service запускается, когда метод (например, активность) вызывает метод startService(). А bound service вызывает метод bindService().
- Служба Unbound может остановить себя, вызвав метод stopSelf() и не удастся остановить service bound, пока все клиенты не отвяжутся от service.
- Служба Unbound останавливается методом stopService(). А bound service можно отменить, вызвав метод unbindService().

Изучены дополнительные классы, интерфейсы и абстрактные классы, как, например: Message, Messenger, Binder. 

Изучены проблемы, связанные с сервисами в новых версиях API.





# Вспомогательные материалы
## Основные
1. https://developer.android.com/guide/components/services?hl=ru
1. https://developer.android.com/guide/components/broadcasts?hl=ru
1. https://developer.android.com/training/data-storage/app-specific?hl=ru
