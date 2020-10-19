package service;

import model.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InputCSVWithRoute {

    public void saveRoutesToDB(String filePath) throws IOException {
        List<Route> routes = parseRouteCsv(filePath) ;
        try {
            SettingsDB settingsDB = new SettingsDB();
            Class.forName(settingsDB.getDB_Driver()).newInstance();
            Connection connection = DriverManager.getConnection(settingsDB.getDB_URL(), settingsDB.getUSER(), settingsDB.getPASS() );
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE routes (start int NOT NULL, end int NOT NULL)");
            String sql = "INSERT INTO pipeline (START, END) Values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Route route : routes) {
                int start = route.getStartPoint();
                int end = route.getEndPoint();
                preparedStatement.setInt(1, start);
                preparedStatement.setInt(2, end);
            }
        } catch (Exception e) {
        }
    }

    private static List<Route> parseRouteCsv(String filePath) throws IOException {
        List<Route> routes = new ArrayList<>();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath));
        for (String fileLine : fileLines) {
            String[] splitedText = fileLine.split(";");
            ArrayList<String> columnList = new ArrayList<>();
            for (int i = 0; i < splitedText.length; i++) {
                if (IsColumnPart(splitedText[i])) {
                    String lastText = columnList.get(columnList.size() - 1);
                    columnList.set(columnList.size() - 1, lastText + ";" + splitedText[i]);
                } else {
                    columnList.add(splitedText[i]);
                }
            }
            Route route = new Route();
            route.setStartPoint(Integer.parseInt(columnList.get(0)));
            route.setEndPoint(Integer.parseInt(columnList.get(1)));
            routes.add(route);
        }
        return routes;
    }

    private static boolean IsColumnPart(String text) {
        String trimText = text.trim();
        return trimText.indexOf("\"") == trimText.lastIndexOf("\"") && trimText.endsWith("\"");
    }
}
