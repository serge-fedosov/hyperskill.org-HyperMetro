package metro;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Specify the path to the file with the list of stations");
            return;
        }

//        String fileName = "example-file.txt";
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.isFile()) {
            System.out.println("Error! Such a file doesn't exist!");
            return;
        } else if (file.length() == 0) {
            return;
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (IOException e) {
            System.out.println("IO Error!");
            return;
        }

        List<String> list = new LinkedList<>();
        list.add("depot");

        while(scanner.hasNextLine()){
            list.add(scanner.nextLine());
        }
        scanner.close();

        list.add("depot");

        //System.out.println(list);

        for (int i = 0; i < list.size() - 2; i++) {
            System.out.println(list.get(i) + " - " + list.get(i + 1) + " - " + list.get(i + 2));
        }
    }
}
