package cs601.project4.webserver;

import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DatabaseConstants;
import cs601.project4.database.DatabaseManager;
import cs601.project4.webserver.utilities.ClientInfo;
import cs601.project4.webserver.utilities.ServerConstants;
import cs601.project4.webserver.utilities.ServerUtils;
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

        switch (path) {
            default:
                //show all events
                resp.getWriter().println("<h2>Events:</h2>");
                try (Connection connection = DBCPDataSource.getConnection()) {
                    ResultSet result = DatabaseManager.executeSelectAllEvents(connection);

                    showEvents(result, req, resp);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                resp.getWriter().println("<p><a href='" +
                        ServerConstants.EVENT_CREATE_PATH + "'>Create new event</a></p>");

                break;
            case ServerConstants.EVENT_CREATE_PATH:
                if (verifyFormParameters(req)) {
                    //create new event
                    createEvent(req, clientInfo);
                    resp.getWriter().println("Event created.");
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View Events</a></p>");
                } else {
                    //give new event form to user
                    resp.getWriter().println(ServerConstants.EVENT_FORM);
                }
                break;
            case ServerConstants.EVENT_MODIFY_PATH:
                //id not given
                if (req.getParameter("id") == null) {
                    resp.getWriter().println("<p>Event not found.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                    break;
                }
                //verify parameters
                if (!verifyFormParameters(req)) {
                    //query event
                    try (Connection connection = DBCPDataSource.getConnection()) {
                        int eventId = Integer.parseInt(req.getParameter("id"));
                        ResultSet result = DatabaseManager.executeSelectEvent(connection, eventId);
                        //event not found
                        if (!result.next()) {
                            resp.getWriter().println("<p>Event does not exist.</p>");
                            resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                        } else {
                            //display edit form to user
                            String form = getModifyForm(result);
                            resp.getWriter().println(form);
                        }
                        break;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    //modify event and then display details
                    modifyEvent(req);
                    /* FALLTHROUGH*/
                }
            case ServerConstants.EVENT_DETAILS_PATH:
                //id not given
                if (!ServerUtils.verifyParameter(req, "id")) {
                    resp.getWriter().println("<p>Event not found.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                    break;
                }
                try (Connection connection = DBCPDataSource.getConnection()) {
                    int eventId = Integer.parseInt(req.getParameter("id"));
                    ResultSet result = DatabaseManager.executeSelectEvent(connection, eventId);
                    //event not found
                    if (!result.next()) {
                        resp.getWriter().println("<p>Event does not exist.</p>");
                        resp.getWriter().println("<p><a href='" +
                                ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                    } else {
                        //print details of event
                        showEventDetails(resp, clientInfo, result);
                    }
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case ServerConstants.EVENT_DELETE_PATH:
                //id not given
                if (!ServerUtils.verifyParameter(req, "id")) {
                    resp.getWriter().println("<p>Event not found.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                    break;
                }
                try (Connection connection = DBCPDataSource.getConnection()) {
                    int eventId = Integer.parseInt(req.getParameter("id"));
                    ResultSet result = DatabaseManager.executeSelectEvent(connection, eventId);
                    //event not found
                    if (!result.next()) {
                        resp.getWriter().println("<p>Event does not exist.</p>");
                        resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_PATH + "'>View All Events</a></p>");
                    } else if (!result.getString(8).equals(clientInfo.getEmail())) {
                        //event does not belong to user
                        resp.getWriter().println("<p>You cannot delete an event you did not create.</p>");
                        resp.getWriter().println("<p><a href='" +
                                ServerConstants.PROFILE_PATH + "'>View My Profile</a></p>");
                    } else {
                        //delete event
                        resp.getWriter().println("<p>Event deleted.</p>");
                        DatabaseManager.executeDeleteEvent(connection, eventId);
                        resp.getWriter().println("<p><a href='" +
                                ServerConstants.PROFILE_PATH + "'>View My Profile</a></p>");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
        }
        resp.getWriter().println(ServerConstants.HOME_PAGE_LINK);
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }

    /**
     * add new event to database
     *
     * @param req        server request containing information about event
     * @param clientInfo info on user
     */
    private void createEvent(HttpServletRequest req, ClientInfo clientInfo) {
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
    }

    /**
     * modify event in database
     *
     * @param req http request containing information about event
     */
    private void modifyEvent(HttpServletRequest req) {
        //get parameters from request
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        Double base_price = Double.parseDouble(req.getParameter("base_price"));
        int id = Integer.parseInt(req.getParameter("id"));
        String datetimeString = req.getParameter("datetime").replace('T', ' ');
        //when the datetime is resubmitted, the seconds are lost
        if (datetimeString.length() == 16) {
            datetimeString += ":00";
        }
        Timestamp datetime = Timestamp.valueOf(datetimeString);
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
            DatabaseManager.executeUpdateEvent(
                    connection,
                    id,
                    name,
                    datetime,
                    description,
                    base_price,
                    student_price,
                    vip_price);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Show event details
     *
     * @param resp       http server response that info is sent to
     * @param clientInfo info on user
     * @param result     sql result of single event
     * @throws SQLException sql select error
     * @throws IOException  server response error
     */
    private void showEventDetails(HttpServletResponse resp, ClientInfo clientInfo, ResultSet result)
            throws SQLException, IOException {

        int eventId = result.getInt(2);

        String name = result.getString(1);
        String description = result.getString(4);
        Timestamp datetime = result.getTimestamp(3);
        String date = datetime.toString().split(" ")[0];
        String time = datetime.toString().split(" ")[1];
        int hours = Integer.parseInt(time.substring(0, 2));
        String ampm;
        if (hours == 0) {
            ampm = "PM";
        } else if (hours > 12) {
            hours -= 12;
            ampm = "PM";
        } else {
            ampm = "AM";
        }
        int minutes = Integer.parseInt(time.substring(3, 5));
        Double price = result.getDouble(5);
        Double studentPrice = result.getDouble(6);
        if (result.wasNull()) {
            studentPrice = null;
        }
        Double vipPrice = result.getDouble(7);
        if (result.wasNull()) {
            vipPrice = null;
        }
        String creator = result.getString(8);

        resp.getWriter().println("<h2>" + name + "</h2>");
        resp.getWriter().println("<p>" + description + "</p>");
        resp.getWriter().println("<p>");
        resp.getWriter().println("Date: " + date + "<br/>");
        resp.getWriter().println("Time: " + hours + ":" + minutes + " " + ampm + "<br/>");

        String normalTicketHtml = "Price: $" + ServerUtils.df.format(price) +
                " <a href='" + ServerConstants.PROFILE_PURCHASE_PATH + "?id=" + eventId +
                "&type=" + DatabaseConstants.NORMAL_TICKET + "'>Purchase Ticket</a><br/>";
        resp.getWriter().println(normalTicketHtml);
        if (studentPrice != null) {
            String studentTicketHtml = "Student Price: $" + ServerUtils.df.format(studentPrice) +
                    " <a href='" + ServerConstants.PROFILE_PURCHASE_PATH + "?id=" + eventId +
                    "&type=" + DatabaseConstants.STUDENT_TICKET + "'>Purchase Ticket</a><br/>";
            resp.getWriter().println(studentTicketHtml);
        }
        if (vipPrice != null) {
            String vipTicketHtml = "VIP Price: $" + ServerUtils.df.format(vipPrice) +
                    " <a href='" + ServerConstants.PROFILE_PURCHASE_PATH + "?id=" + eventId +
                    "&type=" + DatabaseConstants.VIP_TICKET + "'>Purchase Ticket</a><br/>";
            resp.getWriter().println(vipTicketHtml);
        }
        resp.getWriter().println("</p>");
        //delete or modify event
        if (creator.equals(clientInfo.getEmail())) {
            resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_MODIFY_PATH +
                    "?id=" + eventId + "'>Modify Event</a></p>");
            resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_DELETE_PATH +
                    "?id=" + eventId + "'>Delete Event</a></p>");
        }
    }

    /**
     * Prints list of events according to page and page size
     * @param result
     * @param req
     * @param resp
     * @throws IOException
     * @throws SQLException - if a database access error occurs or this method is called on a closed result set
     */
    private void showEvents(ResultSet result, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        //get page number
        int page;
        int pageSize;
        if (!ServerUtils.verifyParameter(req, "page")) {
            page = 0;
        } else {
            page = Integer.parseInt(req.getParameter("page"));
            if (page < 0){
                page = 0;
            }
        }
        if (!ServerUtils.verifyParameter(req, "pageSize")) {
            pageSize = ServerConstants.PAGE_SIZE;
        } else {
            pageSize = Integer.parseInt(req.getParameter("pageSize"));
            if (pageSize <= 0){
                pageSize = 1;
            }
        }
        //skip rows in previous page
        for (int i = 0; i < page; i++) {
            for (int j = 0; j < pageSize; j++) {
                result.next();
            }
        }
        //print page of results
        resp.getWriter().print("<p>");
        for (int i = 0; i < pageSize; i++) {
            if (result.next()) {
                String name = result.getString(1);
                int id = result.getInt(2);
                String eventListing = "<a href='" + ServerConstants.EVENT_DETAILS_PATH
                        + "?id=" + id + "'>" + name + "</a><br/>";
                resp.getWriter().print(eventListing);
            }
        }
        resp.getWriter().println("</p><p>");
        if (page > 0){
            String previousPage = "<a href='" + ServerConstants.EVENT_PATH
                    + "?page=" + (page - 1) + "&pageSize=" + pageSize + "'>" + "Previous Page" + "</a> ";
            resp.getWriter().print(previousPage);
        }
        boolean moreResults = false;
        if (result.next()){
            moreResults = true;
            String nextPage = "<a href='" + ServerConstants.EVENT_PATH
                    + "?page=" + (page + 1) + "&pageSize=" + pageSize + "'>" + "Next Page" + "</a>";
            resp.getWriter().println(nextPage);
        }
        //set page size

        String pageForm = "<form action='" + ServerConstants.EVENT_PATH + "'>" +
                "<label for='pageSize'>Events Per Page:</label><br>" +
                "<input type='text' id='pageSize' name='pageSize' value ='" + pageSize + "'><br>" +
                "<input type='submit' value='Submit'>" +
                "</form>";
        resp.getWriter().println(pageForm);


        resp.getWriter().println("</p>");
    }
    /**
     * validates that get request parameters are valid
     * @param req http request containing parameters
     * @return true if valid, or false
     */
    private boolean verifyFormParameters(HttpServletRequest req){
        return ServerUtils.verifyParameter(req, "name") &&
                ServerUtils.verifyParameter(req, "datetime") &&
                ServerUtils.verifyParameter(req, "description") &&
                ServerUtils.verifyParameter(req, "base_price");
    }

    /**
     * creates html form of event populated with existing data
     * @param result sql result of event
     * @return html form
     */
    private String getModifyForm(ResultSet result){
        //get existing values
        int eventId = 0;
        String name = null;
        String description = null;
        String datetime = null;
        Double price;
        Double studentPrice;
        Double vipPrice;
        String priceFormatted = null;
        String studentPriceFormatted = null;
        String vipPriceFormatted = null;

        try {
            eventId = result.getInt(2);
            name = result.getString(1);
            description = result.getString(4);
            datetime = String.valueOf(result.getTimestamp(3)).replace(" ", "T");
            price = result.getDouble(5);
            priceFormatted = ServerUtils.df.format(price);
            studentPrice = result.getDouble(6);
            studentPriceFormatted = ServerUtils.df.format(studentPrice);
            if (result.wasNull()){
                studentPriceFormatted = "";
            }
            vipPrice = result.getDouble(7);
            vipPriceFormatted = ServerUtils.df.format(vipPrice);
            if (result.wasNull()){
                vipPriceFormatted = "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //create html form
        return "<form action='" + ServerConstants.EVENT_MODIFY_PATH + "?id=" + eventId + "'>" +
                "<label for='name'>Event name:</label><br/>" +
                "<input type='text' id='name' name='name' maxlength='255' required='true' value='" + name  + "'>" +
                "<br/>" +
                "<label for='description'>Description:</label><br/>" +
                "<textarea rows = '5' cols = '60' name = 'description' maxlength='512' required='true'>" +
                description +
                "</textarea>" +
                "<br/>" +
                "<label for='datetime'>Date and time:</label><br/>" +
                "<input type='datetime-local' id='datetime' name='datetime' " +
                "min='2021-01-01T00:00' required='true' value='" + datetime + "'>" +
                "<br/>" +
                "<label for='base_price'>Base Price:</label><br/>" +
                "<input type='text' id='base_price' name='base_price' pattern='[0-9]{1,4}([.][0-9]{2})?' " +
                "title='Price from 0.00-9999.99' required='true' value='" + priceFormatted  + "'>" +
                "<br/>" +
                "<label for='student_price'>Student Price (optional):</label><br/>" +
                "<input type='text' id='student_price' name='student_price'" +
                "pattern='[0-9]{1,4}([.][0-9]{2})?' title='Price from 0.00-9999.99' value='"
                + studentPriceFormatted  + "'>" +
                "<br/>" +
                "<label for='vip_price'>VIP Price (optional):</label><br/>" +
                "<input type='text' id='vip_price' name='vip_price'" +

                "pattern='[0-9]{1,4}([.][0-9]{2})?' title='Price from 0.00-9999.99' value='"
                + vipPriceFormatted  + "'>" +
                "<br/>" +
                "<input type='hidden' id='id' name='id' value='" + eventId + "'>" +
                "<input type='submit' value='Submit'>" +
                "</form>";
    }


}
