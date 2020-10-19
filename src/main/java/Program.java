import model.Route;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import service.InputCSVToDB;
import service.InputCSVWithRoute;
import ua.com.pipeline.WorkWithPipeline;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Program {

    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public void run() throws Exception {
        createDB();
        saveAnswersToCVS();
    }

    public static void createDB() throws IOException {
        InputCSVToDB inputCSV = new InputCSVToDB();
        System.out.println("Введите путь к CSV файлу с описанием водопровода: ");
        String filePath = reader.readLine();
        inputCSV.savePipelineToDB(filePath);
        System.out.println("Введите путь к CSV файлу с набором точек, между которыми нужно найти маршрут: ");
        InputCSVWithRoute inputCSVWithRoute = new InputCSVWithRoute();
        String filePath2 = reader.readLine();
        inputCSVWithRoute.saveRoutesToDB(filePath2);
    }

    private static List<String> writeAnswers() throws Exception {
        WorkWithPipeline workWithPipeline = new WorkWithPipeline();
        Multigraph<Integer, DefaultWeightedEdge> pipeline = workWithPipeline.createPipeline();
        List<Route> routes = workWithPipeline.getRoutesFromDB();
        List<String> answers = new ArrayList<>();

        for (Route route : routes) {
            if (pipeline.containsEdge(route.getStartPoint(), route.getEndPoint())) {
                int shortestPath = workWithPipeline.shortestPath(pipeline, route.getStartPoint(), route.getEndPoint());
                answers.add("TRUE;" + shortestPath);
            } else {
                answers.add("FALSE;");
            }
        }


        return answers;
    }

    public void saveAnswersToCVS() throws Exception {
        File file = new File("Answers.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        List<String> answers = writeAnswers();
        String answer = "";

        for (String s : answers) {
            answer = s + "\n";
        }
        writer.write(answer);
    }

}
