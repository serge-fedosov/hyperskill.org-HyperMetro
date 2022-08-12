package metro;

import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("Specify the path to the file with the list of stations");
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.isFile()) {
            System.out.println("Error! Such a file doesn't exist!");
            return;
        } else if (file.length() == 0) {
            return;
        }

        HashMap<String, LinkedList<String>> lines = new HashMap<>();

        JsonReader jsonReader = new JsonReader(new FileReader(fileName));
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String lineName = jsonReader.nextName();
            HashMap<Integer, String> map = new HashMap<>();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String stationNumber = jsonReader.nextName();
                String stationName = jsonReader.nextString();
                map.put(Integer.parseInt(stationNumber), stationName);
            }
            jsonReader.endObject();

            map.put(map.size() + 1, "depot");
            map.put(0, "depot");

            LinkedList<String> line = new LinkedList<>();
            for (int i = 0; i < map.size(); i++) {
                line.add(map.get(i));
            }

            lines.put(lineName, line);
        }

        jsonReader.endObject();
        jsonReader.close();

        String command = null;
        do {

            Scanner scanner = new Scanner(System.in);

            List<String> params = new ArrayList<String>();
            Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
            Matcher regexMatcher = regex.matcher(scanner.nextLine());
            while (regexMatcher.find()) {
                params.add(regexMatcher.group());
            }

            command = params.get(0);

            if ("/output".equals(command)) {
                LinkedList<String> line = lines.get(removeQuotes(params.get(1)));
                for (int i = 0; i < line.size() - 2; i++) {
                    System.out.println(line.get(i) + " - " + line.get(i + 1) + " - " + line.get(i + 2));
                }
            } else if ("/append".equals(command)) {
                LinkedList<String> line = lines.get(removeQuotes(params.get(1)));
                line.add(line.size() - 1, removeQuotes(params.get(2)));
            } else if ("/add-head".equals(command)) {
                LinkedList<String> line = lines.get(removeQuotes(params.get(1)));
                line.add(1, removeQuotes(params.get(2)));
            } else if ("/remove".equals(command)) {
                LinkedList<String> line = lines.get(removeQuotes(params.get(1)));
                line.remove(params.get(2));
            } else if ("/exit".equals(command)) {
                // nothing to do
            } else {
                System.out.println("Invalid command");
            }

        } while(!"/exit".equals(command));

    }

    public static String removeQuotes(String s) {
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }

}
