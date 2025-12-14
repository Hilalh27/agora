package database;

import java.util.Date;

public class Message {
    private String data;
    private Date date;
    private String ipSource;
    private String ipDest;

    public Message(String data, Date date, String ipSource, String ipDest) {
        this.data = data;
        this.date = date;
        this.ipSource = ipSource;
        this.ipDest = ipDest;
    }

    public String getData() {
        return data;
    }

    public Date getDate() {
        return date;
    }

    public String getIpSource() {
        return ipSource;
    }

    public String getIpDest() {
        return ipDest;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIpSource(String ipSource) {
        this.ipSource = ipSource;
    }

    public void setIpDest(String ipDest) {
        this.ipDest = ipDest;
    }
}
