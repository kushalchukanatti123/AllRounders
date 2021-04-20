package com.albatross.allrounders.Util;

import java.util.Map;

public class StudentUtilClass {
    private String Timestamp;
    private Map<String,Object> Classes;

    public StudentUtilClass() {
    }

    public StudentUtilClass(String timestamp, Map<String, Object> classes) {
        Timestamp = timestamp;
        Classes = classes;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public Map<String, Object> getClasses() {
        return Classes;
    }

    public void setClasses(Map<String, Object> classes) {
        Classes = classes;
    }
}
