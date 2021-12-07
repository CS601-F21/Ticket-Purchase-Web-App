package cs601.project4.database;

import cs601.project4.Config;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Example of using Apache DBCP ConnectionPool.
 * Taken from https://www.baeldung.com/java-connection-pooling
 * source: https://github.com/CS601-F21/code-examples/blob/main/JDBC/src/main/java/examples/jdbc/DBCPDataSource.java
 * @author Wyatt Mumford
 */
public class DBCPDataSource {

    // Apache commons connection pool implementation
    private static BasicDataSource ds = new BasicDataSource();

    /**
     * TODO
     */
    public static void initialize(Config config){
        ds.setUrl("jdbc:mysql://localhost:3306/" + config.database);
        ds.setUsername(config.username);
        ds.setPassword(config.password);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
    }


    /**
     * Return a Connection from the pool.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
