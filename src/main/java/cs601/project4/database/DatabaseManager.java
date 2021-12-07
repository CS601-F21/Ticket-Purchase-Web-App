package cs601.project4.database;

import cs601.project4.Config;

import java.sql.*;

/**
 * Class to demonstrate how to connect to a MySQL database using JDBC.
 * Some code taken from examples here: https://www.baeldung.com/java-jdbc
 * Also see https://www.baeldung.com/java-connection-pooling
 * source: https://github.com/CS601-F21/code-examples/blob/main/JDBC/src/main/java/examples/jdbc/JDBCExample.java
 * @author Wyatt Mumford
 */
public class DatabaseManager {

    private static DBCPDataSource dataSource;

    /**
     * A method to demonstrate using a PreparedStatement to execute a database insert.
     * @param con
     * @param name
     * @param email
     * @param startdate
     * //TODO
     * @throws SQLException
     */
    public static void executeInsertEvent
        (Connection con,
         String name,
         String email,
         String startdate,
         String description,
         float base_price,
         float student_price,
         float vip_price,
         String creator) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            String insertContactSql = "INSERT INTO events (name, email, startdate, description, base_price, creator) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement insertContactStmt = con.prepareStatement(insertContactSql);
            insertContactStmt.setString(1, name);
            insertContactStmt.setString(2, email);
            insertContactStmt.setString(3, startdate);
            insertContactStmt.setString(4, description);
            insertContactStmt.setDouble(5, base_price);
            insertContactStmt.setString(6, creator);
            insertContactStmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to demonstrate using a PrepareStatement to execute a database select
     * @param con
     * @throws SQLException
     */
    public static ResultSet executeSelectEvent(Connection con) throws SQLException {
        try (Connection connection = dataSource.getConnection()){
            String selectAllContactsSql = "SELECT * FROM tickets;";
            PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
            return selectAllContactsStmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO
     * @param config
     */
    public void setup(Config config){
        // Make sure that mysql-connector-java is added as a dependency.
        // Force Maven to Download Sources and Documentation
        try (Connection con = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/" + config.database, config.username, config.password)) {

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //set up DataSource
        dataSource = new DBCPDataSource();
        dataSource.initialize(config);

    }
}
