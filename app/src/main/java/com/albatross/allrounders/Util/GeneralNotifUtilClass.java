package com.albatross.allrounders.Util;

public class GeneralNotifUtilClass {
    private String Msg,Timestamp;

    public GeneralNotifUtilClass() {

    }

    public GeneralNotifUtilClass(String msg, String timestamp) {
        Msg = msg;
        Timestamp = timestamp;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
