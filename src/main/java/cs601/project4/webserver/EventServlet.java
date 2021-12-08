package cs601.project4.webserver;

import cs601.project4.webserver.utilities.ClientInfo;
import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
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
        //create new event
        if (path == "/event/create") {
            if(req.getParameter("name") != null){

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
                    student_price = Double.parseDouble(req.getParameter("vip_price"));
                }
/*
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
 */
                resp.getWriter().println("Event created.");
            } else {
                //give form
                String form = """
                        <form action="/event/create">
                          <label for="name">Event name:</label><br/>
                          <input type="text" id="name" name="name" maxlength="255" required="true">
                          <br/>                          
                          <label for="description">Description:</label><br/>
                          <input type="text" id="description" name="description" maxlength="512" required="true">
                          <br/>                         
                          <label for="datetime">Date and time:</label><br/>
                          <input type="datetime-local" id="datetime" name="datetime" min='2021-01-01T00:00' required="true">
                          <br/> 
                          <label for="base_price">Base Price:</label><br/>
                          <input type="text" id="base_price" name="base_price"
                            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99" required="true">
                            <br/>                            
                          <label for="student_price">Student Price (optional):</label><br/>
                          <input type="text" id="student_price" name="student_price"
                            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99">
                            <br/>                            
                          <label for="vip_price">VIP Price (optional):</label><br/>
                          <input type="text" id="vip_price" name="vip_price"
                            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99">
                            <br/> 
                          <input type="submit" value="Submit">
                        </form>
                        """;
                resp.getWriter().println(form);
            }
        }else if (path.startsWith("/event/create/")){
            //add event to database
            //TODO::
            resp.getWriter().println("Event created");
        } else {
            //show all events
            resp.getWriter().println("<h2>Events:</h2>");
            //TODO:

            resp.getWriter().println("<p><a href=\"/event/create\">Create new event</a></p>");

        }
        resp.getWriter().println("<p><a href=\"/home\">Return to home</a></p>");
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }

}
