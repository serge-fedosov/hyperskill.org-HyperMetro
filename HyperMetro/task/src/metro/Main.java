package metro;

import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



class TransferStation {

    private String transferLineName;
    private String transferStationName;

    public TransferStation(String transferLineName, String transferStationName) {
        this.transferLineName = transferLineName;
        this.transferStationName = transferStationName;
    }

    public String getTransferLineName() {
        return transferLineName;
    }

    public String getTransferStationName() {
        return transferStationName;
    }
}

class Station {

    private String name;
    private ArrayList<TransferStation> transferStations;

    public Station(String name, ArrayList<TransferStation> transferStations) {
        this.name = name;
        this.transferStations = transferStations;
    }

    public String getName() {
        return name;
    }

    public ArrayList<TransferStation> getTransferStations() {
        return transferStations;
    }
}

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

        HashMap<String, LinkedList<Station>> lines = new HashMap<>();

        JsonReader jsonReader = new JsonReader(new FileReader(fileName));
        jsonReader.beginObject(); // begin of lines
        while (jsonReader.hasNext()) {
            String lineName = jsonReader.nextName();
            HashMap<Integer, Station> map = new HashMap<>();

            jsonReader.beginObject(); // begin of line
            while (jsonReader.hasNext()) {
                String stationNumber = jsonReader.nextName();

                jsonReader.beginObject(); // begin of station
                String nameOfStationString = jsonReader.nextName(); // "name"
                if (!"name".equals(nameOfStationString)) {
                    System.out.println("Incorrect file");
                    return;
                }

                String stationName = jsonReader.nextString();

                String transferString = jsonReader.nextName(); // "transfer"
                if (!"transfer".equals(transferString)) {
                    System.out.println("Incorrect file");
                    return;
                }

                ArrayList<TransferStation> transferStations = new ArrayList<>();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    String transferLineString = jsonReader.nextName();
                    if (!"line".equals(transferLineString)) {
                        System.out.println("Incorrect file");
                        return;
                    }

                    String transferLineName = jsonReader.nextString();
                    String transferStationString = jsonReader.nextName();
                    if (!"station".equals(transferStationString)) {
                        System.out.println("Incorrect file");
                        return;
                    }

                    String transferStationName = jsonReader.nextString();
                    jsonReader.endObject();

                    transferStations.add(new TransferStation(transferLineName, transferStationName));
                }
                jsonReader.endArray();

                jsonReader.endObject();

                map.put(Integer.parseInt(stationNumber), new Station(stationName, transferStations));
            }
            jsonReader.endObject(); // end of line

            map.put(map.size() + 1, new Station("depot", new ArrayList<TransferStation>()));
            map.put(0, new Station("depot", new ArrayList<TransferStation>()));

            LinkedList<Station> line = new LinkedList<>();
            for (int i = 0; i < map.size(); i++) {
                line.add(map.get(i));
            }

            lines.put(lineName, line);
        }

        jsonReader.endObject(); // end of lines
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
                LinkedList<Station> line = lines.get(removeQuotes(params.get(1)));
                for (int i = 0; i < line.size(); i++) {
                    ArrayList<TransferStation> transferStation = line.get(i).getTransferStations();
                    if (transferStation != null && transferStation.size() != 0) {
                        System.out.println(line.get(i).getName() + " - " + transferStation.get(0).getTransferStationName()
                                + " (" + transferStation.get(0).getTransferLineName() + " line)");
                    } else {
                        System.out.println(line.get(i).getName());
                    }
                }
            } else if ("/connect".equals(command)) {
                String line1 = removeQuotes(params.get(1));
                String station1 = removeQuotes(params.get(2));
                String line2 = removeQuotes(params.get(3));
                String station2 = removeQuotes(params.get(4));

                LinkedList<Station> listLine1 = lines.get(line1);
                for (int i = 0; i < listLine1.size(); i++) {
                    if (listLine1.get(i).getName().equals(station1)) {
                        ArrayList<TransferStation> transferStation = listLine1.get(i).getTransferStations();
                        transferStation.add(new TransferStation(line2, station2));
                    }
                }

                LinkedList<Station> listLine2 = lines.get(line2);
                for (int i = 0; i < listLine2.size(); i++) {
                    if (listLine2.get(i).getName().equals(station2)) {
                        ArrayList<TransferStation> transferStation = listLine2.get(i).getTransferStations();
                        transferStation.add(new TransferStation(line1, station1));
                    }
                }
            } else if ("/append".equals(command)) {
                LinkedList<Station> line = lines.get(removeQuotes(params.get(1)));
                line.add(line.size() - 1, new Station(removeQuotes(params.get(2)), new ArrayList<>()));
            } else if ("/add-head".equals(command)) {
                LinkedList<Station> line = lines.get(removeQuotes(params.get(1)));
                line.add(1, new Station(removeQuotes(params.get(2)), new ArrayList<>()));
            } else if ("/remove".equals(command)) {
                LinkedList<Station> line = lines.get(removeQuotes(params.get(1)));
                for (int i = 0; i < line.size(); i++) {
                    if (line.get(i).getName().equals(removeQuotes(params.get(2)))) {
                        line.remove(i);
                        break;
                    }
                }
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
