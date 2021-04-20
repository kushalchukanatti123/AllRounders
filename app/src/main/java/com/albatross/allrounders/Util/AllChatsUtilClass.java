package com.albatross.allrounders.Util;

public class AllChatsUtilClass {
    private String LS;
    private Object Chats;
    private String isBlocked;

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public AllChatsUtilClass(String LS, Object chats, String isBlocked) {
        this.LS = LS;
        Chats = chats;
        this.isBlocked = isBlocked;
    }

    public AllChatsUtilClass() {
    }

    public String getLS() {
        return LS;
    }

    public void setLS(String LS) {
        this.LS = LS;
    }

    public Object getChats() {
        return Chats;
    }

    public void setChats(Object chats) {
        Chats = chats;
    }
}
