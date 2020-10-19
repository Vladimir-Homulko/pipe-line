package service;

public class SettingsDB {

    private final String DB_URL = "jdbc:h2:~/test";
    private final String DB_Driver = "org.h2.Driver";
    private final String USER = "root";
    private final String PASS = "root";

    public String getDB_URL() {
        return DB_URL;
    }

    public String getDB_Driver() {
        return DB_Driver;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASS() {
        return PASS;
    }
}
