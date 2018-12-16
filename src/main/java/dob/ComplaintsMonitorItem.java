package dob;

/**
 * Created by chaojiewang on 11/25/17.
 */
public class ComplaintsMonitorItem {
    private int id;
    private String bin;
    private String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComplaintsMonitorItem that = (ComplaintsMonitorItem) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    /////////////////////////////////////////
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
