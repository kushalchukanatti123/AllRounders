package com.albatross.allrounders.Util;

public class RequestNotifUtilClass {
    private String Timestamp,Type;

    public RequestNotifUtilClass(String timestamp, String type) {
        Timestamp = timestamp;
        Type = type;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public RequestNotifUtilClass() {

    }
}
