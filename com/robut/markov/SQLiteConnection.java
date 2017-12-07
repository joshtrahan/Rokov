package com.robut.markov;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SQLiteConnection {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private File filepath;
    private String url;

    private Connection connection;

    public SQLiteConnection(String filepath){
        this.filepath = new File(filepath);
        try {
            this.url = this.SQLITE_PREFIX + this.filepath.getCanonicalPath();
        }
        catch (Exception e){
            System.err.printf("Error connecting to file at %s: %s%n", filepath, e);
        }

        if (!this.filepath.exists()) {
            createDB();
        }
    }

    private void createDB(){
        try {
            this.connection = DriverManager.getConnection(this.url);
            System.out.printf("Created database at: %s%n", this.url);
        }
        catch (SQLException e){
            System.err.printf("Error creating database: %s%n", e.getMessage());
        }
    }

    private void createTables(){
        String createIdMapSql = "CREATE TABLE IF NOT EXISTS WordIDs (\n" +
                "id integer PRIMARY KEY NOT NULL, \n" +
                "word text \n" +
                ");";

        String createWordRelationSql = "CREATE TABLE IF NOT EXISTS WordRelations (\n" +
                "preID integer NOT NULL, \n" +
                "postID integer NOT NULL \n" +
                ");";
    }

    private void connectDB(){
        try{
            this.connection = DriverManager.getConnection(this.url);
            System.out.printf("Connected to database at: %s%n", this.url);
        }
        catch (Exception e){
            System.err.printf("Error connecting to database: %s%n", e);
        }
    }
}
