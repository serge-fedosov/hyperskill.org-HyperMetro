import java.util.Scanner;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ZoneId yerevanZone = ZoneId.of("Asia/Yerevan");
        ZoneOffset yerevanOffset = ZoneOffset.of("+04:00");

        LocalDateTime localDateTime1 = LocalDateTime.of(1991, 4, 15, 13, 0);
        LocalDateTime localDateTime2 = createLocalDateTime(scanner);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime1, yerevanZone);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime2, yerevanOffset);

        System.out.println(getHoursUntil(zonedDateTime, offsetDateTime));
    } 

    public static LocalDateTime createLocalDateTime(Scanner scanner) {
        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int dayOfMonth = scanner.nextInt();
        int hour = scanner.nextInt();
        int minute = scanner.nextInt();

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public static long getHoursUntil(ZonedDateTime zonedDateTime, OffsetDateTime offsetDateTime) {
        return (offsetDateTime.toEpochSecond() - zonedDateTime.toEpochSecond()) / 3600;
    }
}