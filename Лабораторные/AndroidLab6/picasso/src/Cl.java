public class Cl {
    public static void main(String[] args) {
        String str = "Даниил";
        String str1 = str + " Ткаченко";
        System.out.println(str1);
        str1 = str1.replace("Т", "Д");
        System.out.println(str1);
        str1 = str1.toUpperCase();
        System.out.println(str1);
        str1 = str1.substring(1, 10);
        System.out.println(str1);
    }
}
