



# Лабораторная работа №6. Многопоточные Android приложения.

## Цели
Получить практические навыки разработки многопоточных приложений:
1. Организация обработки длительных операций в background (worker) thread:
    * Запуск фоновой операции (coroutine/asynctask/thread)
    * Остановка фоновой операции (coroutine/asynctask/thread)
1. Публикация данных из background (worker) thread в main (ui) thread.

Освоить 3 основные группы API для разработки многопоточных приложений:
1. Kotlin Coroutines
1. AsyncTask
1. Java Threads

## Задачи
### Задача 1. Альтернативные решения задачи "не секундомер" из Лаб. 2

#### Java Threads

Начнем с Java Threads. Был взят исходный код из Лаб. 2 и добавлен небольшое количество кода с решением проблемы, предложенное еще при сдаче Лаб. 2 - добавить interrapted и удалить ссылку на поток. Таким образом поток становится interrapted и завершает выполнение кода, а, значит, и завершается сам, а ссылка в Activity стирается для того, чтобы не происходила утечка ресурсов и Activity нормально собиралась сборщиком мусора. 

![](https://github.com/Daniil1380/AndroidLab6/blob/main/res/Screenshot_2.png)

##### Усложнение задачи

Была поставлена задача - исправить проблему с Thread.sleep(), проблема с ним была одна - хоть мы и указываем 1000 мс, но поток может спать куда большее количество времени. Накапливается ошибка, которая достигает 1%, что довольно весомо. Я добавил код, который позволяет учитывать, насколько долгая была задержка в прошлое выполнение цикла - это позволяет postfactum исправлять ошибки. В таком случае программа все еще может давать ошибку в 1%, но эта ошибка не накапливается, то есть для 1000 операций ошибка так и будет составлять 1% от 1 операции, в отличии от первой ситуации, когда ошибка была 1% от всех операций.

```kotlin
class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var background: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
             background = Thread {
                var timePast: Long
                var delay = 1000L
                var delayPast = 1000L
                var time = System.currentTimeMillis() - 1000
                while (true) {
                    timePast = time
                    time = System.currentTimeMillis()
                    delay -= time - timePast - delayPast
                    Log.d("Timer", delay.toString())
                    Thread.sleep(delay)
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    }
                    if (background?.isInterrupted == true) break;
                    delayPast = delay
                    delay = 1000L
                }
        }
        background?.start()
        super.onResume()
    }

    override fun onPause() {
        background?.interrupt()
        background = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("secs", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("secs")
        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)

        super.onRestoreInstanceState(savedInstanceState)
    }
}
```

```java
2020-12-04 16:38:39.423 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 997
2020-12-04 16:38:40.422 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 998
2020-12-04 16:38:41.422 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 998
2020-12-04 16:38:42.421 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 999
2020-12-04 16:38:43.421 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 999
2020-12-04 16:38:44.424 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 998
2020-12-04 16:38:45.424 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 996
2020-12-04 16:38:46.426 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 994
2020-12-04 16:38:47.422 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 998
2020-12-04 16:38:48.424 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 997
2020-12-04 16:38:49.428 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 992
2020-12-04 16:38:50.424 16541-16567/ru.spbstu.icc.kspt.lab2.continuewatch D/Timer: 997
```

Logcat

#### AsyncTask

Посмотрим на AsyncTask. Эта концепция была создана разработчиками андроид и по сути представляет из себя класс-оболочку для нашего потока. Работать с ним приятнее, чем с потоком, но все еще неприятно, как минимум, потому что этот класс устарел, как максимум, потому что легко получить утечку памяти. 

Класс **AsyncTask** предлагает простой механизм для перемещения трудоёмких операций в фоновый поток, однако сейчас его стоит избегать и смотреть в сторону, например, корутин. Возможно, именно поэтому основным языком для разработки сейчас является именно Котлин, а не Java. 

```
AsyncTask<[Input_Parameter Type], [Progress_Report Type], [Result Type]>
```

В коде добавлен inner класс, который в методе onBackGround (он выполняется в отдельном потоке) делает все то, что мы и делали ранее. Потом результат передается в onProgressUpdate, который уже связан с UI. Код представлен ниже:

```kotlin
class MainActivity : AppCompatActivity() {
    private var backgroundTask: ATask? = null
    var secondsElapsed: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        backgroundTask = ATask()
        backgroundTask?.execute()
        super.onResume()
    }

    override fun onPause() {
        backgroundTask?.cancel(true)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("secs", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("secs")
        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)

        super.onRestoreInstanceState(savedInstanceState)
    }

    inner class ATask :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            while (!isCancelled) {
                TimeUnit.SECONDS.sleep(1)
                publishProgress()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)

        }
    }
}
```



##### Усложнение задачи

Реализовано выше, не стал писать для всех вариантов задания один и тот же код. 

#### Корутины

Наиболее удобный способ сделать что-то в background - это корутины. Корутины, в двух словах, это легковесные потоки, встречается еще название "виртуальные потоки", но в точности этой формулировки есть сомнения. 

В чем же удобство: они создаются проще - код выглядит проще и читабельнее. Работа с коритунами "дешевле" работы с потоками для устройства.

> *Подобно потокам, корутины могут работать параллельно, ждать друг друга и общаться. Самое большое различие заключается в том, что корутины очень дешевые, почти бесплатные: мы можем создавать их тысячами и платить очень мало с точки зрения производительности. Потоки же обходятся дорого. Тысяча потоков может стать серьезной проблемой даже для современной машины.* - Официальный сайт с документацией по Kotlin

Прекрасная возможность написать хороший код. Давайте на него посмотрим. Тут создается корутина с диспатчером на Main. Поток Ui не блокируется корутиной, поэтому можно прямо сразу делать delay

```kotlin
class MainActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0
    private lateinit var corutine: Job
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        corutine = scope.launch {
            while (true) {
                
                delay(1000)
                textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
            }
        }
        super.onResume()
    }

    override fun onPause() {
        corutine.cancel()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("secs", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("secs")
        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        super.onRestoreInstanceState(savedInstanceState)
    }
}
```

### Задача 2. Загрузка картинки в фоновом потоке (AsyncTask) 

Было создано приложение с AsyncTask. Большую часть кода создавать не пришлось, однако я специально удалил из кода все не необходимое, чтобы код стал меньше, так работа выглядит нагляднее. Описание же самого AsyncTask можно найти выше (1 задача)

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        new DownloadImageTask().
                execute("https://sun9-56.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3");
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            String url = urls[0];
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView imageView =(ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(result);
        }
    }
}
```

### Задача 3. Загрузка картинки в фоновом потоке (Kotlin Coroutines) 
Аналогично, добавлена загрузка картинки используя корутины, описание корутин и принцип работы описан в задании номер 1, тут скачивание картинки было отправлено на Dispatcher.IO

```java
class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private fun DownloadImageTask(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
        }
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun click(view: View) {
        scope.launch(Dispatchers.IO) {
            val image = DownloadImageTask("https://sun9-56.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3")
            withContext(Dispatchers.Main) {
                findViewById<ImageView>(R.id.imageView).setImageBitmap(image)
            }
        }
    }
}
```

### Задача 4. Использование сторонних библиотек 
Я использовал picasso, код там помещается в одну строку, его достаточно поместить в любой метод и он скачает нам картинку с теми параметрами. которые мы указали.

```java
public void click(View view) {
    Picasso.get()
            .load("https://sun9-56.userapi.com/impf/fgZNB9-g-SSxw4ZaKvNGn7i3EAUycpOPmkGV6g/piwsvYe6IUs.jpg?size=854x640&quality=96&proxy=1&sign=6817a10cd8c4b6122a31595a8e239ae3")
            .into((ImageView) findViewById(R.id.imageView));
}
```

## Выводы: 

Во время выполнения лабораторной работы было улучшено приложение из лаб. 2. Приложение было написано на kotlin, благодаря чему навыки работы с kotlin, полученные в первом семестре и успешно потерянные на втором курсе были освежены. Получены навыки работы с корутинами, асинхронными тасками.

Касаемо проекта: по моему мнению, из потоков, тасков и корутин наиболее удобный способ создать асинхронное приложение - корутины, однако порог вхождения в эту тему, по моему мнению, достаточно высок, как минимум потому, что на сайте kotlin определение корутин:

> Это новый способ писать асинхронный код

Найти стоящую информацию оказалось трудно, однако когда я ее нашел - понимание темы очень сильно улучшилось

Касаемо библиотек: в современно программировании действительно любые типичные операции описаны библиотеками. Очевидно, что 10-15-20 программистов работающих над библиотекой напишут код куда качественнее, чем один, для которого это периферийная задача. Не стоит изобретать велосипед в этом случае. Библиотека позволяет скачать картинку, написав одну строчку кода - это прекрасно. 