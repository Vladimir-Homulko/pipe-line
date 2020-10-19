package ua.com.pipeline;

import model.PipeModl;
import model.Route;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import service.SettingsDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class WorkWithPipeline {

    public Multigraph<Integer, DefaultWeightedEdge> createPipeline() throws Exception {

        SettingsDB settingsDB = new SettingsDB();
        Class.forName(settingsDB.getDB_Driver()).newInstance();
        Connection connection = DriverManager.getConnection(settingsDB.getDB_URL(), settingsDB.getUSER(), settingsDB.getPASS());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from pipeline");
        Set<Integer> set = new HashSet<>();
        List<PipeModl> edgeModel = new ArrayList<>();

        while (rs.next()) {
            int start = rs.getInt("START");
            int end = rs.getInt("END");
            int length = rs.getInt("LENGTH");
            set.add(start);
            set.add(end);
            PipeModl pipeModl = new PipeModl();
            pipeModl.setStartPoint(start);
            pipeModl.setEndPoint(end);
            pipeModl.setLength(length);
            edgeModel.add(pipeModl);
        }


        Multigraph<Integer, DefaultWeightedEdge> pipeline = new Multigraph<>(DefaultWeightedEdge.class);

        for (int i : set) {
            pipeline.addVertex(i);
        }

        for (PipeModl pipeModl : edgeModel) {
            int start = pipeModl.getStartPoint();
            int end = pipeModl.getEndPoint();
            int length = pipeModl.getLength();
            DefaultWeightedEdge edge = pipeline.addEdge(start, end);
            pipeline.setEdgeWeight(edge, length);
        }

        return pipeline;
    }

    public List<Route> getRoutesFromDB() throws Exception {
        SettingsDB settingsDB = new SettingsDB();
        Class.forName(settingsDB.getDB_Driver()).newInstance();
        Connection connection = DriverManager.getConnection(settingsDB.getDB_URL(), settingsDB.getUSER(), settingsDB.getPASS());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from routes");
        List<Route> routes = new ArrayList<>();

        while (rs.next()) {
            int start = rs.getInt("START");
            int end = rs.getInt("END");
            Route route = new Route();
            route.setStartPoint(start);
            route.setEndPoint(end);
            routes.add(route);
        }
        return routes;
    }

    public int shortestPath(Multigraph<Integer, DefaultWeightedEdge> pipeline, int start, int end) {
        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(pipeline);
        int shortestPath = dijkstraShortestPath
                .getPath(start,end).getLength();

        return shortestPath;

    }


}
