package cs601.project4.database;

import java.util.HashMap;

/**
 * Holds constants related to database
 * @author Wyatt Mumford
 */
public class DatabaseConstants {
    public static int NORMAL_TICKET = 0;
    public static int STUDENT_TICKET = 1;
    public static int VIP_TICKET = 2;
    public static HashMap<Integer, String> ticketType;

    static {
        ticketType = new HashMap<>();
        ticketType.put(NORMAL_TICKET, "Standard Ticket");
        ticketType.put(STUDENT_TICKET, "Student Ticket");
        ticketType.put(VIP_TICKET, "VIP Ticket");
    }
}
