# Цели работы

- Ознакомиться с принципами и получить практические навыки разработки UI тестов для Android приложений.

# 1. Простейший UI тест

Для решения данной задачи подключён Espresso Framework через зависимости в Gradle. было полностью протестировано окно входа приложения.

Для написания тестов создан отдельный класс, который и реализует проверку всех кнопок, используя framework Espresso. Для наиболее верного прохождения тестов добавлен цикл, который 10 раз генерирует случайные данные для входа в личный кабинет, после чего переворачивает экран и проверяет введенные данные.

Все тесты выполняются верно. Содержимое текстового поля сохраняется.

# 2. Тестирование навигации

Я создаю приложение, не используя каких-либо проектов, предложенных в лабораторных работах, поэтому и тестирую собственное приложение. При создании тестов возникли небольшие трудности, связанные с тем, что backStack моего приложения всегда состоит из 1 элемента, так как я использую fragments, а не  разные Activity, однако работоспособность приложения полностью была проверена. 

UPD: добавлена корректная проверка на backstack.

### testRotate()

Первый тест запускает проверку `testRotateOne();` десять раз, для того, чтобы точно убедиться, что при перевороте значения сохраняется в тестовом поле и что при стирании всех данных из поля программа выполняется верно.

### testRotateParam(String str)

Второй тест является параметризированным и он позволяет проверить верно ли работает сохранение данных при разрушении Activity при любых входных данных.

### TestMenu()

Третий тест тестирует наличие кнопок меню при навигации по всему приложению.

### fromHomeScreen()

Четвёртый тест проверяет полную навигацию по приложению, что позволяет проверить верность построенного графа из предыдущих лабораторных работ. 

### createMap()

Тест, который позволяет создать Map, логика которого следующая: "Если я нажал на key, то на экрана обязательно будет value"

### checkOne()

Тест, который проверяет конструкцию, описанную выше для одного конкретно элемента 

KEY-VALUE

## Проверка глубины BaskStack

Для реализации этого теста был создан отдельный метод, который создает список View, который должны появляться после нажатия на back. используя метод forEach и лямбда-выражения полностью проверяется backstack приложения.

# Выводы:

В процессе выполнения данной лабораторной работы получены навыки работы с framework Espresso, в результате получены полностью работоспособные UI-тесты, произведено полное навигационное тестирования собственного приложения. изучены основные составляющие части Espresso. Espresso - весьма удобный фреймфорк для оценки работоспособности приложения.



# Приложение:

## Листинг 1: activity_main для UI теста

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/ConstraintLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity" >

    <EditText
        android:id="@+id/editTextPhone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:inputType="phone"
        app:layout_constraintBottom_toTopOf="@+id/editTextTextPassword"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.9"
        android:autofillHints=""
        tools:ignore="LabelFor" />

    <EditText
        android:id="@+id/editTextTextPassword"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:inputType="textPassword"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.4"
        android:autofillHints=""
        tools:ignore="LabelFor" />

    <TextView
        android:id="@+id/textView6"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/login"
        app:layout_constraintBottom_toTopOf="@+id/editTextPhone"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="@+id/editTextPhone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="1.0" />

    <TextView
        android:id="@+id/textView7"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/password"
        app:layout_constraintBottom_toTopOf="@+id/editTextTextPassword"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="@+id/editTextTextPassword"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="1.0" />

    <Button
        android:id="@+id/buttonLogin"
        android:layout_width="186dp"
        android:layout_height="67dp"
        android:text="@string/enter"
        android:onClick="onEnterClick"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="@+id/editTextTextPassword"
        app:layout_constraintStart_toStartOf="@+id/editTextTextPassword"
        app:layout_constraintTop_toBottomOf="@+id/editTextTextPassword"
        app:layout_constraintVertical_bias="0.257" />

    <Button
        android:id="@+id/button6"
        android:layout_width="186dp"
        android:layout_height="67dp"
        android:text="@string/register"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="@+id/editTextTextPassword"
        app:layout_constraintStart_toStartOf="@+id/editTextTextPassword"
        app:layout_constraintTop_toBottomOf="@+id/buttonLogin"
        app:layout_constraintVertical_bias="0.100000024" />

    <TextView
        android:id="@+id/textView8"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/aboutUs"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button6"
        app:layout_constraintVertical_bias="0.85" />

    <ImageView
        android:id="@+id/imageView8"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toTopOf="@+id/editTextPhone"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:srcCompat="@tools:sample/avatars"
        android:contentDescription="@string/todo" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 2: ActivityMain.java для UI теста

```java

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onEnterClick(View view) {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}
```

## Листинг 3: EspressoTest.java для UI теста

```java
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRotate() {
        for (int i = 0; i < 10; i++) {
            testRotateOne();
        }
    }

    private void testRotateOne(){
        int number = (int)(Math.random()  * 10000000);
        testRotateParam(Integer.toString(number));
    }

    private void testRotateParam(String str) {
        onView(withId(R.id.editTextPhone)).perform(clearText());
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.buttonLogin)).check(matches(withText("Вход")));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        onView(withId(R.id.editTextPhone)).perform(typeText(str));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.editTextPhone)).check(matches(withText(str)));
        //разобраться с матчерами
        //сделать проверку backStack

    }

    private void testMenu() {
        onView(withId(R.id.button)).check(matches(isDisplayed()));
        onView(withId(R.id.button2)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
        onView(withId(R.id.button4)).check(matches(isDisplayed()));
    }

    @Test
    public void fromHomeScreen() {
        testRotateOne();
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).perform(click());
        Map<Integer, Integer> hashMap = createMap();
        hashMap.forEach(this::checkOne);
        List<Integer> list = createListBackStack();
        list.forEach((x)->{
            pressBack();
            onView(withId(x)).check(matches(isDisplayed()));
        });
    }

    private Map<Integer, Integer> createMap(){
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(R.id.button, R.id.textView);
        hashMap.put(R.id.button2, R.id.button2);
        hashMap.put(R.id.button3, R.id.editTextTextPersonName);
        hashMap.put(R.id.button4, R.id.recycler_view);
        return hashMap;
    }

    private List<Integer> createListBackStack(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(R.id.editTextTextPersonName);
        arrayList.add(R.id.button2);
        arrayList.add(R.id.textView);
        return arrayList;

    }

    private void checkOne(int in, int out){
        onView(withId(in)).perform(click());
        testMenu();
        onView(withId(out)).check(matches(isDisplayed()));
    }


    private void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBackUnconditionally());
    }
}
```





