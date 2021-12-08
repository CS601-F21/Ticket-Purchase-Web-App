package project4test;

import com.google.gson.Gson;
import cs601.project4.Config;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseTests {

    @Test
    void testInsert(){
        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DBCPDataSource.initialize(config);
        try (Connection connection = DBCPDataSource.getConnection()){
            DatabaseManager.executeInsertEvent(connection,
                    "Concert",
                    Timestamp.valueOf("1234-12-12 12:34:56"),
                    "This is a fun concert event.",
                    55.55,
                    33.33,
                    77.77,
                    "billybob@gmail.com");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
