package dob;

import email.GmailProxy;
import email.IEmailProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by chaojiewang on 11/12/17.
 */
public class ComplaintsMonitor extends Thread {

    public enum BoroughCode {
        Manhattan(1), Bronx(2), Brooklyn(3), Queens(4), StatenIsland(5);

        private int code;
        BoroughCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static final Logger logger = LogManager.getLogger(ComplaintsMonitor.class);
    private static final IEmailProxy emailProxy = GmailProxy.getInstance();
    private DataSource source;
    private List<User> users;
    private long heartbeat;
    private ComplaintsSource complaintsSource;
    private Map<String, String> categoryCode;
    private Map<String, String> dispositionCode;
    private String complaintViewUrl;

    public ComplaintsMonitor(ComplaintsSource complaintsSource, DataSource source) throws Exception {
        this(complaintsSource, source, 60*60*1000);
    }

    /**
     *
     * @param source
     * @param heartbeat in milliseconds
     * @throws Exception
     */
    public ComplaintsMonitor(ComplaintsSource complaintsSource, DataSource source, long heartbeat) throws Exception {
        super();
        this.complaintsSource = complaintsSource;
        this.source = source;
        this.heartbeat = heartbeat;
    }

    private void loadContext() throws SQLException {
        this.users = new ArrayList<>();
        try (Connection con = source.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM Users WHERE Active = 1")) {
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String email = rs.getString("Email");
                        int active = rs.getInt("Active");
                        String username = rs.getString("Username");

                        User user = new User();
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setActive(active);

                        this.users.add(user);
                    }
                }
            }

            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM ComplaintCategories;")) {
                categoryCode = new HashMap<>();
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    categoryCode.put(rs.getString("Code").trim(), rs.getString("Description").trim());
                }
            }

            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM DispositionCodes;")) {
                dispositionCode = new HashMap<>();
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    dispositionCode.put(rs.getString("Code"), rs.getString("Description"));
                }
            }
        }
    }

    private void notify(User user, ComplaintsMonitorItem monitorItem, Complaint complaint) throws Exception {
        // notifying
        SimpleDateFormat showFormat = new SimpleDateFormat("yyyy-MM-dd mm:hha");
        StringBuilder content = new StringBuilder();
        content.append("Status: ").append(complaint.getStatus()).append("<br>");
        content.append("Date entered: ").append(showFormat.format(complaint.getDateEntered())).append("<br>");

        String borough = null;
        if (complaint.getCommunityBoard().length() > 0) {
            int code = complaint.getCommunityBoard().charAt(0) - '0';

            if (code == BoroughCode.Manhattan.getCode()) {
                borough = BoroughCode.Manhattan.name();
            } else if (code == BoroughCode.Queens.getCode()) {
                borough = BoroughCode.Queens.name();
            } else if (code == BoroughCode.Bronx.getCode()) {
                borough = BoroughCode.Bronx.name();
            } else if (code == BoroughCode.Brooklyn.getCode()) {
                borough = BoroughCode.Brooklyn.name();
            } else if (code == BoroughCode.StatenIsland.getCode()) {
                borough = BoroughCode.StatenIsland.name();
            }
        }

        content.append("Address: ").append(complaint.getHouseNumber() != null ? complaint.getHouseNumber() : "").append(" ")
                .append(complaint.getHouseStreet() != null ? complaint.getHouseStreet() : "").append(" ")
                .append(borough != null ? borough : "").append(", ")
                .append(complaint.getZipcode()).append("<br>");

        if (complaint.getComplaintCategory() != null) {
            String category = categoryCode.get(complaint.getComplaintCategory());
            content.append("Categorty: ").append(complaint.getComplaintCategory()).append("=")
                    .append(category != null ? category : "").append("<br>");
        }

        if (complaint.getInspectionDate() != null) {
            content.append("Inspection date: ")
                    .append(showFormat.format(complaint.getInspectionDate()))
                    .append("<br>");
        }

        if (complaint.getDispositionDate() != null) {
            content.append("Disposition date: ")
                    .append(showFormat.format(complaint.getDispositionDate()))
                    .append("<br>");

        }

        if (complaint.getDispositionCode() != null) {
            String disposition =  dispositionCode.get(complaint.getDispositionCode());
            content.append("Disposition Code: ").append(complaint.getDispositionCode())
                    .append("=").append(disposition).append("<br>");
        }

        if (complaintViewUrl != null) {
            content.append("<a href=\"").append(complaintViewUrl).append("?allbin=")
                    .append(monitorItem.getBin()).append("\"").append(">view complaints</a>");
        }

        emailProxy.sendEmail(user.getEmail(), "New DOB Complaint " + complaint.getComplaintNumber(), content.toString());
    }

    private void doMonitor(User user) {
        try (Connection con = source.getConnection()) {
            // get watch list
            List<ComplaintsMonitorItem> complaintsMonitorItems = new ArrayList<>();
            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM ComplaintsMonitors WHERE User = ?")) {
                preparedStatement.setString(1, user.getUsername());
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        ComplaintsMonitorItem item = new ComplaintsMonitorItem();
                        item.setId(rs.getInt("Id"));
                        item.setBin(rs.getString("Bin"));
                        complaintsMonitorItems.add(item);
                    }
                }
            }

            // get complaints that have been notified in the watch list

            // last date entered
            Map<String, Date> lastDateEnteredMap = new HashMap<>();
            for (ComplaintsMonitorItem item : complaintsMonitorItems) {
                try (PreparedStatement statement = con.prepareStatement("SELECT * FROM ComplaintsMonitorHistory WHERE ComplaintsMonitorId = ?")) {
                    statement.setInt(1, item.getId());
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            long monitorId = item.getId();
                            String complaintNumber = rs.getString("ComplaintNumber");
                            Date dateEntered = rs.getDate("DateEntered");

                            String key = monitorId + "-" + complaintNumber;
                            Date last = lastDateEnteredMap.get(key);
                            if (last == null || last.before(dateEntered))
                                lastDateEnteredMap.put(key, dateEntered);
                        }
                    }
                }
            }

            // get complaints received and notify new ones
            for (ComplaintsMonitorItem monitorItem : complaintsMonitorItems) {
                try {
                    List<Complaint> complaints = complaintsSource.getComplaintByBin(monitorItem.getBin());
                    Map<Date, List<Complaint>> complaintsToBeNotified = new HashMap<>();
                    for (Complaint complaint : complaints) {
                        Date dateEntered = complaint.getDateEntered();
                        Date lastDateEntered = lastDateEnteredMap.get(monitorItem.getId() + "-" + complaint.getComplaintNumber());
                        if (lastDateEntered == null || lastDateEntered.before(dateEntered)) {
                            try (PreparedStatement insertHistory = con.prepareStatement("INSERT INTO ComplaintsMonitorHistory" +
                                    "(DOBRunDate, ComplaintNumber, ComplaintsMonitorId, NotificationTime, DateEntered) VALUES (?,?,?,?,?)")) {
                                insertHistory.setTimestamp(1, new java.sql.Timestamp(complaint.getDobrundate().getTime()));
                                insertHistory.setString(2, complaint.getComplaintNumber());
                                insertHistory.setInt(3, monitorItem.getId());
                                insertHistory.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                                insertHistory.setDate(5, new java.sql.Date(complaint.getDateEntered().getTime()));
                                insertHistory.executeUpdate();
                            }

                            complaintsToBeNotified.putIfAbsent(complaint.getDobrundate(), new ArrayList<>());
                            complaintsToBeNotified.get(complaint.getDobrundate()).add(complaint);
                        }
                    }

                    // notify
                    List<Date> dobRunDates = new ArrayList<>(complaintsToBeNotified.keySet());
                    dobRunDates.sort((o1, o2) -> o2.compareTo(o1));
                    Set<String> complaintNumbersNotifed = new HashSet<>();
                    for (Date dobRunDate : dobRunDates) {
                        for (Complaint complaint : complaintsToBeNotified.get(dobRunDate)) {
                            if (!complaintNumbersNotifed.contains(complaint.getComplaintNumber())) {
                                notify(user, monitorItem, complaint);
                                complaintNumbersNotifed.add(complaint.getComplaintNumber());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn(e);
                }
            }

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                logger.debug("running Monitor");

                try {
                    loadContext();
                    for (User user : users)
                        this.doMonitor(user);

                } catch (SQLException e) {
                    logger.error(e);
                }

                Thread.sleep(heartbeat);
            }
        } catch (InterruptedException e) {
            logger.fatal(e);
        }
    }

    public String getComplaintViewUrl() {
        return complaintViewUrl;
    }

    public void setComplaintViewUrl(String complaintViewUrl) {
        this.complaintViewUrl = complaintViewUrl;
    }

    public void setHeartbeat(long heartbeat) {
        this.heartbeat = heartbeat;
    }
}
