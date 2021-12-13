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

/**
 * @author Wyatt Mumford
 */
public class ProfileServlet extends HttpServlet {

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
            resp.getWriter().println("<p><a href=\"/\">Log in</a></p>");
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
            //create new ticket
            case ServerConstants.PROFILE_PURCHASE_PATH:
                if (!ServerUtils.verifyParameter(req, "id") || !ServerUtils.verifyParameter(req, "type")) {
                    resp.getWriter().println("<p>Purchase unsuccessful.</p>");
                } else {
                    int type = Integer.parseInt(req.getParameter("type"));
                    int eventId = Integer.parseInt(req.getParameter("id"));
                    try (Connection connection = DBCPDataSource.getConnection()) {
                        DatabaseManager.executeInsertTicket(connection, clientInfo, type, eventId);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    resp.getWriter().println("<p>Ticket purchased.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.EVENT_DETAILS_PATH + "?id=" + eventId
                            + "'>Return to Event</a></p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.PROFILE_PATH + "'>View Profile</a></p>");
                }
                break;
            //transfer ticket to new user
            case ServerConstants.PROFILE_TRANSFER_PATH:
                //validate parameters
                if(!ServerUtils.verifyParameter(req, "id")){
                    resp.getWriter().println("<p>Ticket unsuccessfully transferred.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.PROFILE_PATH + "'>View Profile</a></p>");

                } else if (!ServerUtils.verifyParameter(req, "email")){
                    int id = Integer.parseInt(req.getParameter("id"));

                    //verify user owns ticket
                    try (Connection connection = DBCPDataSource.getConnection()) {
                        if (!ServerUtils.verifyTicketOwner(connection, clientInfo.getEmail(), id)) {
                            resp.getWriter().println("<p>Ticket does not belong to you.</p>");
                            break;
                        }
                    }catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    //give transfer ticket form to user
                    String form = "<form action='" + ServerConstants.PROFILE_TRANSFER_PATH + "'>" +
                        "<label for='email'>Transfer Ticket to :</label><br>" +
                        "<input type='text' id='email' name='email'><br>" +
                        "<input type='hidden' id='id' name='id' value='" + id + "'>" +
                        "<input type='submit' value='Submit'>" +
                    "</form>";
                    resp.getWriter().println(form);
                } else {
                    String email = req.getParameter("email");
                    int id = Integer.parseInt(req.getParameter("id"));
                    //verify user exists
                    try (Connection connection = DBCPDataSource.getConnection()) {
                        ResultSet result = DatabaseManager.executeSelectUser(connection, email);
                        if (!result.next()) {
                            resp.getWriter().println("<p>User not found.</p>");
                        } else if (!ServerUtils.verifyTicketOwner(connection, clientInfo.getEmail(), id)){
                            resp.getWriter().println("<p>Ticket does not belong to you.</p>");
                        } else {
                            DatabaseManager.executeUpdateTicket(connection,
                                    Integer.parseInt(req.getParameter("id")), req.getParameter("email"));
                            resp.getWriter().println("<p>Ticket transferred.</p>");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    resp.getWriter().println("<p><a href='" + ServerConstants.PROFILE_PATH + "'>View Profile</a></p>");
                }
                break;
            case ServerConstants.PROFILE_UPDATE_PATH:
                if (!ServerUtils.verifyParameter(req, "name")){
                    resp.getWriter().println("<p>Update unsuccessful.</p>");
                    resp.getWriter().println("<p><a href='" + ServerConstants.PROFILE_PATH + "'>View Profile</a></p>");
                    break;
                } else {
                    String name = req.getParameter("name");
                    clientInfo.setName(name);
                    try (Connection connection = DBCPDataSource.getConnection()) {
                        DatabaseManager.executeUpdateUser(connection, name, clientInfo.getEmail());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    /* FALLTHROUGH */
                }
            default:
                //profile
                String nameLine = """
                        <form action='""" + ServerConstants.PROFILE_UPDATE_PATH + """
                          '>
                          <label for='name'>
                          <p>Name: """ +
                        clientInfo.getName() + """
                          </label>
                          <input type='text' id='name' name='name' maxlength='255' required='true'>
                          <input type='submit' value='Update name'>
                        </form></p>
                        """;
                resp.getWriter().println(nameLine);
                String emailLine = "<p>Email: " + clientInfo.getEmail() + "</p>";
                resp.getWriter().println(emailLine);

                //display tickets owned by user
                resp.getWriter().println("<h2>My tickets:</h2>");
                try (Connection connection = DBCPDataSource.getConnection()) {
                    ResultSet result = DatabaseManager.executeSelectUsersTickets(connection, clientInfo.getEmail());
                    resp.getWriter().print("<p>");
                    while (result.next()){
                        String type = DatabaseConstants.ticketType.get(result.getInt(1));
                        int eventId = result.getInt(3);
                        int ticketId = result.getInt(2);
                        String name = result.getString(4);
                        String eventListing = "<a href='" + ServerConstants.EVENT_DETAILS_PATH +
                                "?id=" + eventId + "'>" + name + "</a> " + type +
                                " <a href='" + ServerConstants.PROFILE_TRANSFER_PATH +
                                "?id=" + ticketId + "'>Transfer</a><br/>";
                        resp.getWriter().print(eventListing);
                    }
                    resp.getWriter().println("</p>");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                //display events created by user
                resp.getWriter().println("<h2>My events:</h2>");
                try (Connection connection = DBCPDataSource.getConnection()) {
                    ResultSet result = DatabaseManager.executeSelectUsersEvents(connection, clientInfo.getEmail());
                    resp.getWriter().print("<p>");
                    while (result.next()){
                        String eventName = result.getString(1);
                        int id = result.getInt(2);
                        String eventListing = "<a href='" + ServerConstants.EVENT_DETAILS_PATH +
                                "?id=" + id + "'>" + eventName + "</a><br/>";
                        resp.getWriter().print(eventListing);
                    }
                    resp.getWriter().println("</p>");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        }
        resp.getWriter().println(ServerConstants.HOME_PAGE_LINK);
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }


}