package dob;

import common.Util;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by chaojiewang on 11/25/17.
 */
public class TestComplaintsSource {

    ComplaintsSource source;

    @Before
    public void setup() {
        try {
            Properties properties = new Properties();
            try (FileInputStream in = new FileInputStream(new File(System.getenv("CONF")))) {
                properties.load(in);
                source = new ComplaintsSource(properties.getProperty("COMPLAINTS_API"), properties.getProperty("OPEN_NYC_TOKEN"));
                source.getComplaintByBin("test");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            source = null;
        }
    }

    @Test
    public void testGetComplaints() throws Exception {
        Assume.assumeNotNull(source);

        List<Complaint> complaintList = source.getComplaintByBin("3340049");
        Assert.assertNotEquals(0, complaintList.size());
    }
}
