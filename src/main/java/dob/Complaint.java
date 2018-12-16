package dob;

import java.util.Date;

/**
 * Created by chaojiewang on 11/14/17.
 */
public class Complaint {
    private String complaintNumber;
    private String status;
    private Date dateEntered;
    private String houseNumber;
    private String houseStreet;
    private String zipcode;
    private String bin;
    private String communityBoard;
    private String specialDistrict;
    private String complaintCategory;
    private String unit;
    private Date dispositionDate;
    private String dispositionCode;
    private Date inspectionDate;
    private Date dobrundate;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("complaintNumber: ").append(complaintNumber).append(";");

        return stringBuilder.toString();
    }

    //////////////////////////////////////////////////
    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseStreet() {
        return houseStreet;
    }

    public void setHouseStreet(String houseStreet) {
        this.houseStreet = houseStreet;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getCommunityBoard() {
        return communityBoard;
    }

    public void setCommunityBoard(String communityBoard) {
        this.communityBoard = communityBoard;
    }

    public String getSpecialDistrict() {
        return specialDistrict;
    }

    public void setSpecialDistrict(String specialDistrict) {
        this.specialDistrict = specialDistrict;
    }

    public String getComplaintCategory() {
        return complaintCategory;
    }

    public void setComplaintCategory(String complaintCategory) {
        this.complaintCategory = complaintCategory;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getDispositionDate() {
        return dispositionDate;
    }

    public void setDispositionDate(Date dispositionDate) {
        this.dispositionDate = dispositionDate;
    }

    public String getDispositionCode() {
        return dispositionCode;
    }

    public void setDispositionCode(String dispositionCode) {
        this.dispositionCode = dispositionCode;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public Date getDobrundate() {
        return dobrundate;
    }

    public void setDobrundate(Date dobrundate) {
        this.dobrundate = dobrundate;
    }
}
