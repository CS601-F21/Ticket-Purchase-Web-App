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
     * updates a user's name for given email
     * @param con connection to database
     * @param name new name
     * @param email email linked to slack
     * @throws SQLException error in insertion
     */
    public static void executeUpdateUser(Connection con, String name, String email)
            throws SQLException {

        //update columns
        String updateSql =
                "UPDATE users SET name=? WHERE email=?;";
        PreparedStatement updateStmt = con.prepareStatement(updateSql);
        updateStmt.setString(1, name);
        updateStmt.setString(2, email);

        updateStmt.executeUpdate();
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
     * deletes specified event from table
     * @param con connection to database
     * @param id id of event
     * @throws SQLException sql query failed
     */
    public static void executeDeleteEvent(Connection con, int id) throws SQLException{
        String sql = "DELETE FROM events WHERE id = " + id + ";";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(sql);
        selectAllContactsStmt.executeUpdate();
    }

    /**
     * Update event with new data
     * @param con connection to database
     * @param id id of event
     * @param name name of event
     * @param startdate start date of event
     * @param description description of event
     * @param base_price base price of ticket
     * @param student_price optional discounted price
     * @param vip_price optional premium price
     * @throws SQLException error in update
     */
    public static void executeUpdateEvent(Connection con,
            int id,
            String name,
            Timestamp startdate,
            String description,
            Double base_price,
            Double student_price,
            Double vip_price)
            throws SQLException {

        //reset optional parameters
        String resetSql = "UPDATE events SET vip_price=NULL, student_price=NULL WHERE id=?;";
        PreparedStatement resetStmt = con.prepareStatement(resetSql);
        resetStmt.setInt(1,id);
        resetStmt.executeUpdate();

        //update columns
        String updateSql =
                "UPDATE events SET name=?, startdate=?, description=?, base_price=?, student_price=?, vip_price=? " +
                        "WHERE id=?;";
        PreparedStatement updateStmt = con.prepareStatement(updateSql);
        updateStmt.setString(1, name);
        updateStmt.setTimestamp(2, startdate);
        updateStmt.setString(3, description);
        updateStmt.setDouble(4, base_price);
        if (student_price != null){
            updateStmt.setDouble(5, student_price);
        } else {
            updateStmt.setNull(5, Types.DOUBLE);
        }
        if (vip_price != null){
            updateStmt.setDouble(6, vip_price);
        } else {
            updateStmt.setNull(6, Types.DOUBLE);
        }
        updateStmt.setInt(7, id);

        updateStmt.executeUpdate();
    }

    /**
     * return all details about specified event
     * @param con connection to database
     * @param id identifier of event
     * @throws SQLException sql query failed
     */
    public static ResultSet executeSelectEvent(Connection con, int id) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM events WHERE id = '" + id + "';";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }
    /**
     * return all events
     * @param con connection to database
     * @throws SQLException sql query failed
     */
    public static ResultSet executeSelectAllEvents(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT name,id FROM events;";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }
    /**
     * return all events created by user
     * @param con connection to database
     * @param email user's email
     * @throws SQLException sql query failed
     */
    public static ResultSet executeSelectUsersEvents(Connection con, String email) throws SQLException {
        String selectAllContactsSql = "SELECT name,id FROM events WHERE creator = '" + email + "';";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }

    /**
     * insert ticket to database
     * @param con connection to database
     * @param clientInfo information about client
     * @param type ticket type
     * @param eventId id of event
     * @throws SQLException sql insert failed
     */
    public static void executeInsertTicket(Connection con, ClientInfo clientInfo, int type, int eventId)
            throws SQLException{
        String sql = "INSERT INTO tickets (owner, type, eventId) VALUES (?, ?, ?);";
        PreparedStatement insertContactStmt = con.prepareStatement(sql);
        insertContactStmt.setString(1, clientInfo.getEmail());
        insertContactStmt.setInt(2, type);
        insertContactStmt.setInt(3, eventId);
        insertContactStmt.executeUpdate();
    }
    /**
     * transfer ticket to new user
     * @param con connection to database
     * @param id ticket id
     * @param email email of new user
     * @throws SQLException error in insertion
     */
    public static void executeUpdateTicket(Connection con, int id, String email)
            throws SQLException {

        //update columns
        String updateSql =
                "UPDATE tickets SET owner=email WHERE id=?;";
        PreparedStatement updateStmt = con.prepareStatement(updateSql);
        updateStmt.setInt(2, id);
        updateStmt.setString(1, email);

        updateStmt.executeUpdate();
    }
    /**
     * return all tickets owned by user, as well as the event name
     * @param con connection to database
     * @param email user's email
     * @throws SQLException sql query failed
     */
    public static ResultSet executeSelectUsersTickets(Connection con, String email) throws SQLException {
        String selectAllContactsSql = "SELECT type, tickets.id, eventId, events.name FROM tickets " +
                "JOIN events ON tickets.eventId = events.id WHERE owner = '" + email + "';";
        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        return selectAllContactsStmt.executeQuery();
    }

}
