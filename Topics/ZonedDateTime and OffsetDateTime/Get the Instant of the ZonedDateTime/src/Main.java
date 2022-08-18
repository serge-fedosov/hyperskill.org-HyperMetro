import java.util.Scanner;
import java.time.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LocalDateTime  localDateTime = LocalDateTime.of(1991, 4, 15, 15, 0);

        Instant instant = convertToInstant(localDateTime, scanner.nextInt());
        System.out.println(String.format("%s, toEpochSeconds: %d", instant, instant.getEpochSecond()));
    } 

    public static Instant convertToInstant(LocalDateTime localDateTime, int hours) {
        return OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(hours)).toInstant();
    }
}