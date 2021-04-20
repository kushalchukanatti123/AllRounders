package com.albatross.allrounders.Util;

public class ChatUtilClass {
    private String By;
    private String Timestamp;
    private String Msg;

    public ChatUtilClass(String by, String timestamp, String msg) {
        By = by;
        Timestamp = timestamp;
        Msg = msg;
    }

    public ChatUtilClass() {
    }

    public String getBy() {
        return By;
    }

    public void setBy(String by) {
        By = by;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
