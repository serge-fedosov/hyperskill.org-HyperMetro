import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        final Comparator<Map.Entry<String, Long>> valueComparator =
                Map.Entry.comparingByValue(Comparator.reverseOrder());
        final Comparator<Map.Entry<String, Long>> keyComparator =
                Map.Entry.comparingByKey();

        Scanner scanner = new Scanner(System.in);

        Stream.of(scanner.nextLine().replaceAll("\\W", " ")
                        .replaceAll("\\s+", " ")
                        .toLowerCase()
                        .split(" "))
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(valueComparator.thenComparing(keyComparator))
                .limit(10)
                .map(Map.Entry::getKey)
                .forEach(System.out::println);
    }
}