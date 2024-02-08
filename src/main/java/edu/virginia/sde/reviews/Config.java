package edu.virginia.sde.reviews;

import java.sql.SQLException;

public class Config {
    private static Config instance;
    public DatabaseDriver db;
    public static Config getInstance(){
        if (instance == null){
            instance = new Config();
        }
        return instance;
    }


    //Don't call this outside of testing
    public static void setupTestDb(){
        if (instance != null){
            throw new RuntimeException("Singleton initialized already");
        }
        instance = new Config("test_db.sqlite");
    }
    private Config(){
        db = new DatabaseDriver("database.sqlite");
        try {
            db.connect();
            db.createTables();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private Config(String s){
        db = new DatabaseDriver(s);
        try {
            db.connect();
            db.createTables();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
