import java.util.Scanner;
import java.time.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LocalDateTime  localDateTime = LocalDateTime.of(1991, 4, 15, 15, 0);
        ZoneId zoneId = ZoneId.of(scanner.nextLine());

        printZoneRules(zoneId, localDateTime);
    } 

    public static void printZoneRules(ZoneId zoneId, LocalDateTime  localDateTime) {
        System.out.println(zoneId.getRules().getOffset(localDateTime));
    }
}