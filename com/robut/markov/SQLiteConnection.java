package com.robut.markov;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SQLiteConnection {
    private String url;

    SQLiteConnection(String filepath){
        this.url = "jdbc:sqlite:" + filepath;
        createDatabase();
    }

    private void createDatabase(){
        try (Connection connection = DriverManager.getConnection(this.url)){
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Driver name: " + metaData.getDriverName());
            System.out.println("New DB created at: " + this.url);
        }
        catch (SQLException e){
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
