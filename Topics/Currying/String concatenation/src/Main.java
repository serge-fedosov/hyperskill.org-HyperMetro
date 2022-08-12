import java.util.Scanner;
import java.util.function.Function;

class CurryConcat {

    public static String calc(String first, String second, String third) {

        Function<String, Function<String, Function<String, String>>> stringFun = 
                x -> y -> z -> x.toLowerCase() + z.toUpperCase() + y.toLowerCase();

        return stringFun.apply(first).apply(second).apply(third);
    }

    // Don't change the code below
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(calc(scanner.next(), scanner.next(), scanner.next()));
    }
}