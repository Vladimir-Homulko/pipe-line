package service;

import model.PipeModl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InputCSVToDB {

    public void savePipelineToDB(String filePath) throws IOException {
        List<PipeModl> pipeModls = parsePipeCsv(filePath);
        try {
            SettingsDB settingsDB = new SettingsDB();
            Class.forName(settingsDB.getDB_Driver()).newInstance();
            Connection connection = DriverManager.getConnection(settingsDB.getDB_URL(), settingsDB.getUSER(), settingsDB.getPASS() );
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE pipeline (start int NOT NULL, end int NOT NULL, length int NOT NULL)");
            String sql = "INSERT INTO pipeline (START, END, LENGTH) Values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (PipeModl pipeModl : pipeModls) {
                int start = pipeModl.getStartPoint();
                int end = pipeModl.getEndPoint();
                int length = pipeModl.getLength();
                preparedStatement.setInt(1, start);
                preparedStatement.setInt(2, end);
                preparedStatement.setInt(3, length);
            }
        } catch (Exception e) {
        }
    }

    private static List<PipeModl> parsePipeCsv(String filePath) throws IOException {
        List<PipeModl> pipeModls = new ArrayList<PipeModl>();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath));
        for (String fileLine : fileLines) {
            String[] splitedText = fileLine.split(";");
            ArrayList<String> columnList = new ArrayList<String>();
            for (int i = 0; i < splitedText.length; i++) {
                if (IsColumnPart(splitedText[i])) {
                    String lastText = columnList.get(columnList.size() - 1);
                    columnList.set(columnList.size() - 1, lastText + ";" + splitedText[i]);
                } else {
                    columnList.add(splitedText[i]);
                }
            }
            PipeModl pipeModl = new PipeModl();
            pipeModl.setStartPoint(Integer.parseInt(columnList.get(0)));
            pipeModl.setEndPoint(Integer.parseInt(columnList.get(1)));
            pipeModl.setLength(Integer.parseInt(columnList.get(2)));
            pipeModls.add(pipeModl);
        }
        return pipeModls;
    }

    private static boolean IsColumnPart(String text) {
        String trimText = text.trim();
        return trimText.indexOf("\"") == trimText.lastIndexOf("\"") && trimText.endsWith("\"");
    }
}
