# Цели работы

- Ознакомиться с принципами работы adapter-based views
- Получить практические навыки разработки адаптеров для view

# 1. Знакомство с библиотекой (unit test)

Ознакомьтесь со strict mode библиотеки, проиллюстрировав его работу unit-тестом.

Библиотека имеет 2 режима работы: normal и strict. В `strict mode` работает искусственное ограничение: в памяти нельзя хранить более `20` записей одновременно. При извлечении 21-ой записи 1-ая извлечённая запись становится невалидной (при доступе к полям кидаются исключения `IllegalStateException`)

В классе `BibDatabaseTest.java`, в котором находятся тесты для библиотеки, в реализованных тестовых методах запрашиваются экземпляры класса `BibEntry`, которые хранят информацию о записях в формате bibtex. Данные экземпляры класса подсчитывают свой порядковый номер и сравнивают его при запросе (например `getField()`). Если же номер уже не является валидным, то бросается `IllegalStateException`, который мы и должны обработать.

## Тест strictModeThrowsException()

Метод `strictModeThrowsException()` реализован таким образом: создаем Entry, далее создаем цикл, где вытаскиваем `19 Entry` из нашей базы данных и так как у нас уже есть `20` записей, теперь нам достаточно вытащить лишь одну Entry и обратиться к первой Entry, чтобы получить ожидаемую реакцию. Далее запрашивается метод `getType()` для нашей самой первой `BibEntry`. При вызове метода выбрасывается ошибка `IllegalStateException`.

## Тест shuffleFlag()

Для проверки shuffle был использован новый .bibtex файл, в котором есть 3 разных Entry, 15 раз мы проверяем, получилось ли перемещать entry, и если операция хотя бы один раз из 1 выполнена, то тест считается пройденным. 

Не идеальность теста: 

Очевидно, что проверку функционала, завязанного на `Random` нужно производить, зная конкретные `seed`, в данном конкретном тесте все еще существует шанс не прохода данного теста, однако мной была предпринята попытка уменьшить вероятность такой ошибки, сейчас она равняется `1/3^15 = 6,96e-8`

## Сборка biblib.jar

Все тесты пройдены, поэтому мы можем собрать .jar файл библиотеки. Для этого через консоль Windows открываем папку с проектом и запускаем `gradlew.bat build`. Результаты сборки доступны по пути `build/libs/biblib.jar`.

# 2. Знакомство с RecyclerView (Однородный список)

![](https://github.com/Daniil1380/App/blob/master/%D0%9B%D0%B0%D0%B1%D0%BE%D1%80%D0%B0%D1%82%D0%BE%D1%80%D0%BD%D1%8B%D0%B5/Screenshot_6.png)



Напишите Android приложение, которое выводит все записи из bibtex файла на экран, используя предложенную библиотеку и `RecyclerView`.

## Изменения в biblib

В качестве исходных данных должен использоваться файл, который содержит один тип данных Article, в нем были созданы новые данные для использования их в RecylerView

## RecyclerView

 Создан Layout, который будет отображать элемент списка RecyclerView. В состав него входит: imageView + 2 текстовых поля.  Ресурс для ImageView выбирается из ресурсов drawable.

## NewsFragment

В данном классе извлекаются наши исходный файл с данными, который размещён в `raw` ресурсах. Здесь к нашему RecyclerView подключается `LinearLayoutManager`, который позволяет располагать данные в виде списка, это наиболее удобный вариант в данном случае. Также к RecyclerView добавлен `DividerItemDecoration` - это специальный декоратор, который позволяет, при помощи метода `addItemDecoration()`, разделить данные в списке для более удобного отображения, используя горизонтальную линию. В конце подключается `Adapter `, на вход которому подаётся извлечённый нами исходный файл.

## Adapter

Данный класс реализует адаптер для RecyclerView, обеспечивающий привязку данных к View, которые отображает RecyclerView (в нашем случае это TextView). `BibLibAdapter` содержит в себе вложенный статический класс `BibLibViewHolder`, который описывает представление элемента и данные о месте в RecyclerView. 

В конструкторе класса `BibLibAdapter`  `InputStreamReader()` считывает файл .bibtex, создаётся экземпляр класса `BibDatabase()`

В методе `onCreateViewHolder()` создаётся экземпляр класса `LayoutInflater`, который позволяет из содержимого layout-файла создать View-элемент, и возвращается экземпляр нашего вложенного класса `BibLibViewHolder` с созданным View на входе.

В методе `onBindViewHolder()` позволяет отобразить новые данные из .bibtex, экономя при этом ресурсы. 

В методе `getItemCount()` выводится количество элементов в списке, в данном случае они == Integer.MAX_VALUE для бесконечного списка и database.size() для ограниченного списка.

# 3. Бесконечный список

Сделайте список из предыдущей задачи бесконечным: после последнего элемента все записи повторяются, начиная с первой.

Изменим наш адаптер:

В методе `onBindViewHolder()` указываем не просто позицию, а берем остаток от деления на размер базы данных

В методе `getItemCount()` возвращаем Integer.MAX_VALUE

# Выводы:

При выполнении данной лабораторной работы произведено ознакомление с принципами работы Recycler View, изучены особенности и различия между List View и Recycler. Получены базовые навыки написания тестов для тестирования библиотек, используя тесты JUnit. Созданы Adapter для работы с Recycler View и были добавлены Divider для создания визуальной составляющей и Linear Layout для отображения Entry линейно.

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
public class NewsFragment extends Fragment {
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
        if (position % database.size() == 1) holder.image.setImageResource(R.drawable.cup);
        else if (position % database.size() == 2 ) holder.image.setImageResource(R.drawable.face);
        else holder.image.setImageResource(R.drawable.cat);
        holder.textViewTitle.setText(entry.getField(Keys.TITLE));
        holder.textViewAuthor.setText(entry.getField(Keys.AUTHOR));

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class BibLibViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewAuthor;
        ImageView image;


        public BibLibViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.titleN);
            textViewAuthor = itemView.findViewById(R.id.author);

        }
    }
}


```

### Листинг 5: BibDatabaseTest.java

```java
@Test
  public void strictModeThrowsException() throws IOException {
    BibDatabase database = openDatabase("/mixed.bib");
    BibConfig cfg = database.getCfg();
    cfg.strict = true;BibEntry first = database.getEntry(0);
	for (int i = 0; i < cfg.maxValid - 1; i++) {
  		BibEntry unused = database.getEntry(0);
  		assertNotNull("Should not throw any exception @" + i, first.getType());
	}

	try {
  		BibEntry unused = database.getEntry(0);
  		first.getType();
  		fail();
	} catch (IllegalStateException e) {
  		System.out.println("Throw IllegalStateException with message: " + 			e.getMessage());
	}

@Test
  public void shuffleFlag() throws IOException {
    boolean check = false;
    BibDatabase firstDatabase = openDatabase("/shuffle.bib");
    BibConfig cfg = firstDatabase.getCfg();
    cfg.shuffle = false;
    BibEntry first = firstDatabase.getEntry(0); // always ARTICLE
    for (int i = 0; i < 20; i++) {
  		BibDatabase database = openDatabase("/shuffle.bib");
  		BibConfig databaseCfg = database.getCfg();
  		databaseCfg.shuffle = true;
  		if (database.getEntry(0).getType() != first.getType()) check = true;
	}
	assertTrue(check);
}
```

