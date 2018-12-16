package dob;

import common.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chaojiewang on 11/24/17.
 */
public class ComplaintsSource extends OpenNycSource {
    private final Logger logger = LogManager.getLogger(ComplaintsSource.class);

    /**
     *
     * @param complaintsApi API url for fetching complaints
     * @param appToken can be null
     */
    public ComplaintsSource(String complaintsApi, String appToken) {
        super(complaintsApi, appToken);
    }

    /**
     *
     * @param bin
     * @return
     */
    public List<Complaint> getComplaintByBin(String bin) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("bin", bin);
        JSONArray complaintsJson;
        try (InputStream in = sendGetRequest(params)) {
            complaintsJson = new JSONArray(new JSONTokener(in));
        }

        SimpleDateFormat dobRundateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        SimpleDateFormat jsonDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<Complaint> complaints = new ArrayList<>();
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < complaintsJson.length(); i++) {
            try {
                JSONObject complaintJson = complaintsJson.getJSONObject(i);
                String complaintNumber = safeGetString(complaintJson, "complaint_number");
                String status = safeGetString(complaintJson, "status");
                String dateEnterStr = safeGetString(complaintJson, "date_entered");
                String houseNumber = safeGetString(complaintJson, "house_number");
                String zipcodeStr = safeGetString(complaintJson, "zip_code");
                String houseStreet = safeGetString(complaintJson, "house_street");
                String inBin = safeGetString(complaintJson, "bin");
                String communityBoard = safeGetString(complaintJson, "community_board");
                String specialDistrict = safeGetString(complaintJson, "special_district");
                String complaintCategory = safeGetString(complaintJson, "complaint_category");
                String unit = safeGetString(complaintJson, "unit");
                String dispositionDateStr = safeGetString(complaintJson, "disposition_date");
                String dispositionCode = safeGetString(complaintJson, "disposition_code");
                String inspectionDateStr = safeGetString(complaintJson, "inspection_date");
                Date dobrundate = dobRundateFormat.parse(complaintJson.getString("dobrundate"));

                Complaint complaint = new Complaint();
                complaint.setComplaintNumber(complaintNumber);
                complaint.setStatus(status);
                complaint.setHouseNumber(houseNumber);
                complaint.setHouseStreet(houseStreet);
                complaint.setCommunityBoard(communityBoard);
                complaint.setZipcode(zipcodeStr);
                complaint.setBin(inBin);
                complaint.setSpecialDistrict(specialDistrict);
                complaint.setComplaintCategory(complaintCategory);
                complaint.setDispositionCode(dispositionCode);
                complaint.setUnit(unit);
                complaint.setDobrundate(dobrundate);

                try {
                    if (dateEnterStr != null) {
                        Date date = jsonDateFormat.parse(dateEnterStr);
                        complaint.setDateEntered(date);
                    }
                } catch (ParseException e) {
                    logger.warn(e);
                }

                try {
                    if (dispositionDateStr != null) {
                        Date date = jsonDateFormat.parse(dispositionDateStr);
                        complaint.setDispositionDate(date);
                    }
                } catch (ParseException e) {
                    logger.warn(e);
                }

                try {
                    if (inspectionDateStr != null) {
                        Date date = jsonDateFormat.parse(inspectionDateStr);
                        complaint.setInspectionDate(date);
                    }
                } catch (ParseException e) {
                    logger.warn(e);
                }



                complaints.add(complaint);

            } catch (JSONException e) {
                errors.append(Util.exceptionStacks(e));
                logger.warn(Util.exceptionStacks(e));
            }
        }

        return complaints;
    }


    /**
     *
     * @param obj
     * @param field
     * @return null on error
     */
    private static String safeGetString(JSONObject obj, String field) {
        try {
            return obj.getString(field).trim();
        } catch (JSONException e) {
            return null;
        }
    }
}
