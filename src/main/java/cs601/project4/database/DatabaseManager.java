package cs601.project4.database;

import cs601.project4.webserver.utilities.ClientInfo;

import java.sql.*;

/**
 * Class to demonstrate how to connect to a MySQL database using JDBC.
 * Some code taken from examples here: https://www.baeldung.com/java-jdbc
 * Also see https://www.baeldung.com/java-connection-pooling
 * source: https://github.com/CS601-F21/code-examples/blob/main/JDBC/src/main/java/examples/jdbc/JDBCExample.java
 * @author Wyatt Mumford
 */
public class DatabaseManager {

    /**
     * return given user
     * @param con connection to database
     * @param clientInfo desired user
     * @return user row
     */
    public static ResultSet executeSelectUser(Connection con, ClientInfo clientInfo) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = '" + clientInfo.getEmail() + "';";
        System.out.println(sql);
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.executeQuery();
    }
    /**
     * add user to users
     * @param con connection to database
     * @param clientInfo information about client
     * @throws SQLException sql insert failed
     */
    public static void executeInsertUser(Connection con, ClientInfo clientInfo) throws SQLException{
        String sql = "INSERT INTO users (name, email) VALUES (?, ?);";
        PreparedStatement insertContactStmt = con.prepareStatement(sql);
        insertContactStmt.setString(1, clientInfo.getName());
        insertContactStmt.setString(2, clientInfo.getEmail());

        insertContactStmt.executeUpdate();
    }

    /**
     * A method to execute a database insert of new event.
     * @param con connection to server
     * @param name name of event (255 characters
     * @param startdate start time and date of event
     * @param description text description of event (512 characters)
     * @param creator email of event creator
     * @param base_price price of ticket
     * @param student_price optional discount price
     * @param vip_price optional exclusive price
     * @throws SQLException insert failed
     */
    public static void executeInsertEvent
        (Connection con,
         String name,
         Timestamp startdate,
         String description,
         Double base_price,
         Double student_price,
         Double vip_price,
         String creator) throws SQLException {

        //set parameters
        String insertContactSql =
            "INSERT INTO events (name, startdate, description, base_price, student_price, vip_price, creator) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement insertContactStmt = con.prepareStatement(insertContactSql);
        insertContactStmt.setString(1, name);
        insertContactStmt.setTimestamp(2, startdate);
        insertContactStmt.setString(3, description);
        insertContactStmt.setDouble(4, base_price);
        if (student_price != null){
            insertContactStmt.setDouble(5, student_price);
        } else {
            insertContactStmt.setNull(5, Types.DOUBLE);
        }
        if (vip_price != null){
            insertContactStmt.setDouble(6, vip_price);
        } else {
            insertContactStmt.setNull(6, Types.DOUBLE);
        }
        insertContactStmt.setString(7, creator);

        insertContactStmt.executeUpdate();
    }

    /**
     * return all events
     * @param con connection to database
     * @throws SQLException sql query failed
     */
    public static ResultSet executeSelectEvents(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT name FROM users;";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }
}
