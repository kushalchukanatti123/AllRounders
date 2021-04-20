package com.albatross.allrounders.Util;

public class ActionsRequestsUtilCLass {
    private String Timestamp,Status,Type;

    public ActionsRequestsUtilCLass(String timestamp, String status, String type) {
        Timestamp = timestamp;
        Status = status;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public ActionsRequestsUtilCLass() {

    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
