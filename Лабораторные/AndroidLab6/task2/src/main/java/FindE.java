import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class FindE {
    public static void main(String[] args) {
        withOutThreads();
        withThreads();
    }

    public static void withOutThreads() {
        long in = System.currentTimeMillis();
        BigDecimal e = new BigDecimal(2);
        for (int i = 3; i < 500; i++) {
            BigDecimal a = new BigDecimal(1);
            for (int j = 1; j < i; j++) {
                BigDecimal z = new BigDecimal(j);
                a = a.divide(z, 10000 ,RoundingMode.HALF_UP);
            }
            e=e.add(a);
        }
        File dir = new File("withOut.txt");
        try {
            dir.createNewFile();
            FileWriter fileWriter = new FileWriter(dir);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(e.toString());
            bufferedWriter.close();
            long out = System.currentTimeMillis();
            System.out.println("Времени затрачено без потоков(мс): "+ (out-in));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public static void withThreads(){
        long in = System.currentTimeMillis();
        ArrayList<BigDecimal> array = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            int shift = j;
            Thread thread = new Thread(() -> {
                BigDecimal e = new BigDecimal(0);
                for (int i = 3 + shift; i < 500; i += 2) {
                    BigDecimal a = new BigDecimal(1);
                    for (int k = 1; k < i; k++) {
                        BigDecimal z = new BigDecimal(k);
                        a = a.divide(z, 10000, RoundingMode.HALF_UP);
                    }
                    e = e.add(a);
                }
                array.add(e);
                if (array.size()==2) {
                    try {
                        printE(array);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    long out = System.currentTimeMillis();
                    System.out.println("Времени затрачено c потоками(мс): "+ (out-in));
                }
            });
            thread.start();
        }

    }

    public static void printE(ArrayList<BigDecimal> arrayList) throws IOException {
        BigDecimal answer = new BigDecimal(2);
        for (BigDecimal bigDecimal : arrayList) {
            answer = answer.add(bigDecimal);
        }
        File dir = new File("with.txt");
        dir.createNewFile();
        FileWriter fileWriter = new FileWriter(dir);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(answer.toString());
        bufferedWriter.close();
    }
}
