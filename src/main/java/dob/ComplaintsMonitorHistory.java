package dob;

import java.util.Date;

/**
 * Created by chaojiewang on 11/25/17.
 */
public class ComplaintsMonitorHistory {
    private Date dobRunDate;
    private String complaintNumber;
    private int complaintsMonitorId;
    private Date notificationTime;
    private Date dateEntered;

    /////////////////////////////////////////
    public Date getDobRunDate() {
        return dobRunDate;
    }

    public void setDobRunDate(Date dobRunDate) {
        this.dobRunDate = dobRunDate;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public int getComplaintsMonitorId() {
        return complaintsMonitorId;
    }

    public void setComplaintsMonitorId(int complaintsMonitorId) {
        this.complaintsMonitorId = complaintsMonitorId;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }
}
