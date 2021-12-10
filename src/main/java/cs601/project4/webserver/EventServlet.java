package cs601.project4.webserver;

import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DatabaseManager;
import cs601.project4.webserver.utilities.ClientInfo;
import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Implements logic for Event page.
 * Shows all events, allowing user to view details of each or create new ones
 */
public class EventServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();

        // determine whether the user is authenticated
        Object clientInfoObj = req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj == null) {
            // not authed, need to log in
            resp.getWriter().println(ServerConstants.PAGE_HEADER);
            resp.getWriter().println("<p>You are not authenticated.</p>");
            resp.getWriter().println("<p><a href='/'>Log in</a></p>");
            resp.getWriter().println(ServerConstants.PAGE_FOOTER);
            return;
        }

        //get ClientInfo
        ClientInfo clientInfo = (ClientInfo) req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);

        req.getSession().setAttribute(ServerConstants.CLIENT_INFO_KEY, clientInfo);
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(ServerConstants.PAGE_HEADER);


        String path = req.getServletPath();
        //create new event
        if (path.equals("/event/create")) {
            if (req.getParameter("name") != null) {
                //collect parameters
                String name = req.getParameter("name");
                String description = req.getParameter("description");
                String creator = clientInfo.getEmail();
                Double base_price = Double.parseDouble(req.getParameter("base_price"));

                Timestamp datetime = Timestamp.valueOf(req.getParameter("datetime").replace('T', ' ') + ":00");

                //optional parameters
                Double student_price = null;
                if (!req.getParameter("student_price").isEmpty()) {
                    student_price = Double.parseDouble(req.getParameter("student_price"));
                }
                Double vip_price = null;
                if (!req.getParameter("vip_price").isEmpty()) {
                    vip_price = Double.parseDouble(req.getParameter("vip_price"));
                }

                //add event to database
                try (Connection connection = DBCPDataSource.getConnection()) {
                    DatabaseManager.executeInsertEvent(
                            connection,
                            name,
                            datetime,
                            description,
                            base_price,
                            student_price,
                            vip_price,
                            creator);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                resp.getWriter().println("Event created.");
                resp.getWriter().println("<p><a href='/event'>View Events</a></p>");
            } else {
                //give new event form
                resp.getWriter().println(ServerConstants.EVENT_FORM);
            }
        } else if (path.equals("/event/delete")){
            try (Connection connection = DBCPDataSource.getConnection()) {
                int eventId = Integer.parseInt(req.getParameter("id"));
                ResultSet result = DatabaseManager.executeSelectEvent(connection, eventId);
                //event not found
                if (!result.next()) {
                    resp.getWriter().println("<p>Event does not exist.</p>");
                    resp.getWriter().println("<p><a href='/event'>View All Events</a></p>");
                } else if (!result.getString(8).equals(clientInfo.getEmail())){
                    //event does not belong to user
                    resp.getWriter().println("<p>You cannot delete an event you did not create.</p>");
                    resp.getWriter().println("<p><a href='/profile'>View My Profile</a></p>");
                } else {
                    resp.getWriter().println("<p>Event deleted.</p>");
                    DatabaseManager.executeDeleteEvent(connection, eventId);
                    resp.getWriter().println("<p><a href='/profile'>View My Profile</a></p>");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (path.equals("/event/details")){
            try (Connection connection = DBCPDataSource.getConnection()) {
                int eventId = Integer.parseInt(req.getParameter("id"));
                ResultSet result = DatabaseManager.executeSelectEvent(connection, eventId);
                //event not found
                if (!result.next()){
                    resp.getWriter().println("<p>Event does not exist.</p>");
                    resp.getWriter().println("<p><a href='/event'>View All Events</a></p>");
                } else {
                    //print details of event
                    String name = result.getString(1);
                    String description = result.getString(4);
                    Timestamp datetime = result.getTimestamp(3);
                    String date = datetime.toString().split(" ")[0];
                    String time = datetime.toString().split(" ")[1];
                    int hours = Integer.parseInt(time.substring(0,2));
                    String ampm = null;
                    if (hours == 0) {
                        ampm = "PM";
                    } else if (hours > 12){
                        hours -= 12;
                        ampm = "PM";
                    } else {
                        ampm = "AM";
                    }
                    int minutes = Integer.parseInt(time.substring(3,5));
                    Double price = result.getDouble(5);
                    Double studentPrice = result.getDouble(6);
                    if (result.wasNull()){
                        studentPrice = null;
                    }
                    Double vipPrice = result.getDouble(7);
                    if (result.wasNull()){
                        vipPrice = null;
                    }
                    String creator = result.getString(8);

                    resp.getWriter().println("<h2>" + name + "</h2>");
                    resp.getWriter().println("<p>" + description + "</p>");
                    resp.getWriter().println("<p>");
                    resp.getWriter().println("Date: " + date + "<br/>");
                    resp.getWriter().println("Time: " + hours + ":" + minutes + " " + ampm + "<br/>");
                    //TODO purchase
                    resp.getWriter().println("Price: $" + price + "<br/>");
                    if (studentPrice != null){
                        resp.getWriter().println("Student price: $" + studentPrice + "<br/>");
                    }
                    if (vipPrice != null){
                        resp.getWriter().println("Vip price: $" + vipPrice + "<br/>");
                    }
                    resp.getWriter().println("</p>");
                    //delete event
                    if (creator.equals(clientInfo.getEmail())){
                        resp.getWriter().println("<p><a href='/event/delete?id=" +  + eventId + "'>Delete Event</a></p>");
                    }
                }
                resp.getWriter().println("<p><a href='/event'>View All Events</a></p>");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            //show all events
            resp.getWriter().println("<h2>Events:</h2>");
            try (Connection connection = DBCPDataSource.getConnection()) {
                ResultSet result = DatabaseManager.executeSelectAllEvents(connection);
                resp.getWriter().print("<p>");
                while (result.next()){
                    String name = result.getString(1);
                    int id = result.getInt(2);
                    String eventListing = "<a href='/event/details?id=" + id + "'>" + name + "</a><br/>";
                    resp.getWriter().print(eventListing);
                }
                resp.getWriter().println("</p>");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            resp.getWriter().println("<p><a href='/event/create'>Create new event</a></p>");

        }
        resp.getWriter().println("<p><a href='/home'>Return to Home page</a></p>");
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }

}
