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

                if (jsonReader.hasNext()) {
                    jsonReader.nextName();
                    jsonReader.skipValue();
                }

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
            } else if ("/route".equals(command)) {
                String line1 = removeQuotes(params.get(1));
                String station1 = removeQuotes(params.get(2));
                String line2 = removeQuotes(params.get(3));
                String station2 = removeQuotes(params.get(4));

                BFS bfs = new BFS(lines);
                List<StationBFS> stations = bfs.getRoute(line1, station1, line2, station2);

                String previousLine = stations.get(0).getLine();
                int previousStation = -1;
                for (int i = 0; i < stations.size(); i++) {
                    String newLine = stations.get(i).getLine();
                    if (!previousLine.equals(newLine)) {
                        System.out.println("Transition to line " + newLine);
                        System.out.println(stations.get(previousStation).getStation());
                        previousLine = newLine;
                    }

                    previousStation = i;
                    System.out.println(stations.get(i).getStation());
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

class StationBFS {

    private String line;
    private String station;

    public StationBFS(String line, String station) {
        this.line = line;
        this.station = station;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        StationBFS that = (StationBFS) o;
//
//        if (!line.equals(that.line)) return false;
//        return station.equals(that.station);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = line.hashCode();
//        result = 31 * result + station.hashCode();
//        return result;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationBFS that = (StationBFS) o;

        return station.equals(that.station);
    }

    @Override
    public int hashCode() {
        return station.hashCode();
    }
}

class BFS {

    private boolean[][] graph;
    private StationBFS[] stations;
    private boolean[] used;
    private int[] parent;
    private int vNum;
    private HashMap<StationBFS, Integer> addedStations;

    public BFS(HashMap<String, LinkedList<Station>> lines) {

        int countStations = 0;
        for (var line : lines.entrySet()) {
            countStations += line.getValue().size() - 2;
        }

        vNum = countStations;
        graph = new boolean[countStations][countStations];
        stations = new StationBFS[countStations];
        used = new boolean[countStations];
        parent = new int[countStations];
        addedStations = new HashMap<>();

        int n = -1;
        for (var line : lines.entrySet()) {
            String lineName = line.getKey();
            List<Station> listStations = line.getValue();
            StationBFS previousBFSStation = null;
            int previousStationNumber = -1;
            for (int i = 0; i < listStations.size(); i++) {
                Station station = listStations.get(i);

                String stationName = station.getName();
                if ("depot".equals(stationName)) {
                    continue;
                }

                StationBFS currentBFSStation = new StationBFS(lineName, stationName);

                int currentStationNumber = addedStations.getOrDefault(currentBFSStation, -1);
                if (currentStationNumber == -1) {
                    n++;
                    currentStationNumber = n;

                    ArrayList<TransferStation> transferStations = station.getTransferStations();
                    if (transferStations.size() > 0) {
                        currentBFSStation.setLine("");
                    }

                    stations[currentStationNumber] = currentBFSStation;
                    addedStations.put(currentBFSStation, currentStationNumber);
                }

                if (previousBFSStation != null) {
                    graph[previousStationNumber][currentStationNumber] = true;
                    graph[currentStationNumber][previousStationNumber] = true;
                }

                previousBFSStation = currentBFSStation;
                previousStationNumber = currentStationNumber;

//                ArrayList<TransferStation> transferStations = station.getTransferStations();
//                for (int j = 0; j < transferStations.size(); j++) {
//                    TransferStation transferStation = transferStations.get(j);
//                    StationBFS newBFSStation = new StationBFS(transferStation.getTransferLineName(), transferStation.getTransferStationName());
//
//                    int newStationNumber = addedStations.getOrDefault(newBFSStation, -1);
//                    if (newStationNumber == -1) {
//                        n++;
//                        newStationNumber = n;
//                        stations[newStationNumber] = newBFSStation;
//                        addedStations.put(newBFSStation, newStationNumber);
//                    }
//
//                    graph[previousStationNumber][newStationNumber] = true;
//                    graph[newStationNumber][previousStationNumber] = true;
////                    graph[currentStationNumber][newStationNumber] = true;
////                    graph[newStationNumber][currentStationNumber] = true;
//
//                    // соединяем между собой все пересадочные станции из списка
////                    for (int k = j + 1; k < transferStations.size(); k++) {
////                        TransferStation transferStation2 = transferStations.get(j);
////                        StationBFS newBFSStation2 = new StationBFS(transferStation2.getTransferLineName(), transferStation2.getTransferStationName());
////
////                        int newStationNumber2 = addedStations.getOrDefault(newBFSStation2, -1);
////                        if (newStationNumber2 == -1) {
////                            n++;
////                            newStationNumber2 = n;
////                            stations[newStationNumber2] = newBFSStation2;
////                            addedStations.put(newBFSStation2, newStationNumber2);
////                        }
////
////                        graph[previousStationNumber][newStationNumber2] = true;
////                        graph[newStationNumber2][previousStationNumber] = true;
//////                        graph[newStationNumber][newStationNumber2] = true;
//////                        graph[newStationNumber2][newStationNumber] = true;
////
////                    }
//                }
            }
        }
    }

    public List<StationBFS> getRoute(String fromLine, String fromStation, String toLine, String toStation) {
        int from = addedStations.getOrDefault(new StationBFS(fromLine, fromStation), -1);
        int to = addedStations.getOrDefault(new StationBFS(toLine, toStation), -1);

        int v = from;
        int[] queue = new int [vNum]; // очередь
        int qH = 0; // голова очереди
        int qT = 0; // хвост

        /* <обработка вершины v> */
        used[v] = true; // помечаем исходную вершину
        queue[qT++] = v; // помещаем ее в очередь

        while (qH < qT) { // пока очередь не пуста
            v = queue[qH++]; // извлекаем текущую вершину
            for (int nv = 0; nv < vNum; nv++) { // перебираем вершины
                if (!used[nv] && graph[v][nv]) { // если nv не помечена и смежна с v
                    /* <обработка вершины nv> */
                    used[nv] = true; // помечаем ее
                    parent[nv] = v;
                    queue[qT++] = nv; // и добавляем в очередь
                }
            }
        }

        List<StationBFS> route = new ArrayList<>();
        while (from != to) {
            route.add(stations[to]);
            to = parent[to];
        }
        route.add(stations[to]);
        Collections.reverse(route);

        String prevLine = route.get(0).getLine();
        for (int i = 0; i < route.size(); i++) {
            if ("".equals(route.get(i).getLine())) {
                if ("".equals(prevLine)) {
                    prevLine = route.get(i + 1).getLine();
                    route.get(i).setLine(prevLine);
                } else {
                    route.get(i).setLine(prevLine);
                }
            } else {
                prevLine = route.get(i).getLine();
            }
        }


        return route;
    }
}