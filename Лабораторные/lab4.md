# Цели работы

- Ознакомиться с принципами работы adapter-based views
- Получить практические навки разработки адаптеров для view

# 1. Знакомство с библиотекой (unit test)

Ознакомьтесь со strict mode библиотеки, проиллюстрировав его работу unit-тестом.

Библиотека имеет 2 режима работы: normal и strict. В strict mode работает искусственное ограничение: в памяти нельзя хранить более `name.ank.lab4.BibConfig#maxValid=20` записей одновременно. При извлечении `maxValid+1`-ой записи 1-ая извелеченная запись становится невалидной (при доступе к полям кидаются исключения `IllegalStateException` с сообщениями типа: `This object has already been invalidated. myOrder=%d, latestOrder=%d`).

В классе `BibDatabaseTest.java`, в котором находятся тесты для библиотеки, в реализованных тестовых методах запрашиваются экземпляры класса `BibEntry`, которые хранят информацию о записях в формате bibtex. Данные экземпляры класса подсчитывают свой порядковый номер и сравнивают его при запросе (например `getField()`). Если же номер уже не является валидным, то бросается `IllegalStateException`, который мы и должны обработать.

## Тест strictModeThrowsException()

Метод `strictModeThrowsException()` реализован практически как и метод `normalModeDoesNotThrowException()`, но с некоторыми изменениями. Так как у нас стоит ограничение в 20 записей, а при вызове первой `BibEntry` вне цикла мы присваиваем ей 1 порядковый номер, то и количество повторений в цикле сокращено до `cfg.maxValid - 1`.  Таким образом, при вызове последнего `BibEntry` его порядковый номер будет равняться 20, и исключение не будет выброшено. Далее в блоке кода `try` создаётся ещё один новый `BibEntry` уже с порядковым номером 21 и запрашивается метод `getType()` для нашей самой первой `BibEntry`. При вызове метода выбрасывается ошибка `IllegalStateException`, которая в свою очередь обрабатывается в блоке `catch`. Таким образом, тест проходит проверку.

## Тест shuffleFlag()

Для проверки работы флага `shuffle` создан новый файл `shuffle.bib` с двумя записями разного типа. Первой записью в новом файле всегда хранится тип ARTICLE, поэтому мы открываем файл `shuffle.bib`, ставим флаг `shuffle = false` и достаём первую `BibEntry`, тип записи которой всегда является ARTICLE. Так мы ещё и проверяем работоспособность выключенного флага `shuffle`, так как по умолчанию он включён. Поскольку тест может быть не пройден, если метод не перемешает последовательность, поэтому эта операция повторяется несколько раз.

## Сборка biblib.jar

Все тесты пройдены, поэтому мы можем собрать .jar файл библиотеки. Для этого через консоль Windows открываем папку с проектом и запускаем `gradlew.bat build`. Результаты сборки доступны по пути `build/libs/biblib.jar`.

# 2. Знакомство с RecyclerView (Однородный список)

![](https://github.com/Daniil1380/App/blob/master/%D0%9B%D0%B0%D0%B1%D0%BE%D1%80%D0%B0%D1%82%D0%BE%D1%80%D0%BD%D1%8B%D0%B5/lab4.png)



Напишите Android приложение, которое выводит все записи из bibtex файла на экран, используя предложенную библиотеку и `RecyclerView`.

## Изменения в biblib

В качестве исходных данных должен использоваться файл, который содержит один тип данных Article, в нем были созданы новые данные для использования их в RecylerView

## RecyclerView

Для решения данной задачи нужно подключить к проекту зависимость в gradle файле, чтобы использовать RecyclerView: `implementation 'androidx.recyclerview:recyclerview:1.2.0-alpha06'`. В framgent_news.xml добавлен RecyclerView, с которым мы и будем работать. Далее создан layout, который будет отображать элемент списка RecyclerView. В нём находится 4 TextView, которые отображают картинку, название, автора и день публикации.

## NewsFragment

В данном классе извлекаются наши исходный файл с данными, который размещён в `raw` ресурсах. Здесь к нашему RecyclerView подключается `LinearLayoutManager`, который позволяет располагать данные в виде списка. Далее при помощи метода `setHasFixedSize()` задаётся, что размер RecyclerView будет фиксированного размера, так как в исходном файле конечное число записей. Также к RecyclerView добавлен `DividerItemDecoration` при помощи метода `addItemDecoration()`, который позволяет разделить данные в списке для более удобного отображения. В конце подключается `Adapter `, на вход которому подаётся извлечённый нами исходный файл.

## Adapter

Данный класс реализует адаптер для RecyclerView, обеспечивающий привязку данных к View, которые отображает RecyclerView (в нашем случае это TextView). `BibLibAdapter` содержит в себе вложенный статический класс `BibLibViewHolder`, который описывает представление элемента и данные о месте в RecyclerView. 

В конструкторе класса `BibLibAdapter` считывается при помощи `InputStreamReader()` информация из файла и создаётся экземпляр класса `BibDatabase()` для работы с данными файла. 

В методе `onCreateViewHolder()` создаётся экземпляр класса `LayoutInflater`, который позволяет из содержимого layout-файла создать View-элемент, и возвращается экземпляр нашего вложенного класса `BibLibViewHolder` с созданным View на входе.

В методе `onBindViewHolder()` происходит отображение данных в указанной позиции, то есть он обновляет содержимое наших TextView. При помощи метода `getEntry()` получаем объект класса `BibEntry`, который позволяет извлекать данные из записи. Далее меняем отображаемый текст в наших TextView на нужные данные полей конкретной записи. При изменении типа записи используется конструкция switch case для разного визуального отображения записей разного типа.

В методе `getItemCount()` выводится количество элементов в списке, то есть количество записей в файле.

# 3. Бесконечный список

Сделайте список из предыдущей задачи бесконечным: после последнего элемента все записи повторяются, начиная с первой.

Для того, чтобы сделать список "бесконечным", нужно всего лишь изменить 2 строчки кода, а именно:

В методе `onBindViewHolder()` при получении экземпляра класса указать не просто позицию, а остаток от деления позиции на количество записей в файле.

В методе `getItemCount()` возвращать не количество элементов в списке, а максимальное возможное число Integer.

# Выводы:

При выполнении данной лабораторной работы произведено ознакомление с принципами работы ABV: разработано приложение, выводящее все записи из `bibtex` файла на экран, используя библиотеку biblib и RecyclerView. Для отображения записей различного рода разработан адаптер `Adapter`. Все элементы из заданного файла выведены списком. Также код адаптера модифицирован так, чтобы после прокручивания последнего элемента, все записи повторялись, начиная с первого. А также написаны тесты.

# Приложение:

## Листинг 1: fragment_news.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".NewsFragment">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</FrameLayout>
```

## Листинг 2: biblib_entry.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/round"
    android:orientation="vertical"
    android:padding="16dp"
    android:layout_margin="16dp">

    <TextView
        android:id="@+id/type"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/round"
        android:gravity="center"
        android:text="Type"
        android:textColor="@android:color/background_light"
        android:textSize="28dp" />

    <Space
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/round"
        android:orientation="vertical">

        <TextView
            android:id="@+id/author"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Author"
            android:textColor="#FFFFFF"
            android:textSize="16dp" />

        <TextView
            android:id="@+id/year"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="end"
            android:text="Day"
            android:textColor="#FFFFFF"
            android:textSize="14dp" />

        <TextView
            android:id="@+id/titleN"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Title"
            android:textColor="#FFFFFF"
            android:textSize="24dp" />
    </LinearLayout>

</LinearLayout>
```

## Листинг 3: NewsFragment.java

```java
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterBib adapterBib;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView =  view.findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        try {
            InputStream publications = getResources().openRawResource(R.raw.text);
            adapterBib = new AdapterBib(publications);
            recyclerView.setAdapter(adapterBib);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
}
    private BibLibAdapter bibLibAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream publications = getResources().openRawResource(R.raw.publications_ferro_en);

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        try {
            bibLibAdapter = new BibLibAdapter(publications);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(bibLibAdapter);
    }
}
```

## Листинг 4: Adapter.java

```java
public class AdapterBib extends RecyclerView.Adapter<AdapterBib.BibLibViewHolder> {

    BibDatabase database;

    AdapterBib(InputStream publications) throws IOException {
        InputStreamReader reader = new InputStreamReader(publications);
        database = new BibDatabase(reader);
    }

    @NonNull
    @Override
    public BibLibViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.biblib_entry, parent, false);
        return new BibLibViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BibLibViewHolder holder, int position) {
        BibEntry entry = database.getEntry(position % database.size());
        holder.textViewType.setText("А тут будет картинка");
        holder.textViewTitle.setText(entry.getField(Keys.TITLE));
        holder.textViewAuthor.setText(entry.getField(Keys.AUTHOR));
        holder.textViewYear.setText(entry.getField(Keys.YEAR));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class BibLibViewHolder extends RecyclerView.ViewHolder {

        TextView textViewType;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;

        public BibLibViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewType = itemView.findViewById(R.id.type);
            textViewTitle = itemView.findViewById(R.id.titleN);
            textViewAuthor = itemView.findViewById(R.id.author);
            textViewYear = itemView.findViewById(R.id.year);
        }
    }
}

```