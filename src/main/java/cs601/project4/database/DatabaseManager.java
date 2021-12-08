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


    /**
     * A method to demonstrate using a PreparedStatement to execute a database insert.
     * @param con
     * @param name
     * @param startdate
     * //TODO
     * @throws SQLException
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
     * A method to demonstrate using a PrepareStatement to execute a database select
     * @param con
     * @throws SQLException
     */
    public static ResultSet executeSelectEvent(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM tickets;";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }

}
