package com.albatross.allrounders.Util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class Constants {
    //KEYS
    public static final String GCLOUD_API_KEY = "AIzaSyDeuO2417qqY09niMHs5B8mWbejhab2EaM";

    //ONESIGNAL
    public static final String ONESIGNAL_ID = "1eefb43d-905e-4b20-8c31-529226727d47";
    public static final int N_NOWHERE = 0;
    public static final int N_CHAT_ACT = 1;
    public static final int N_PROFILE_ACT = 2;
    public static final int N_NOTIF_ACT = 3;
    public static final int N_CLASS_ACT = 4;
    public static final String Connection_Request = "Connection Request";

    public static final String You_have_been_blocked = "~You have been blocked~";
    public static final String You_are_now_connected = "~You are now connected~";
    public static final String You_are_now_disconnected = "~You are now disconnected~";
    public static final String You_have_been_unblocked = "~You have been unblocked~";
    public static final String You_blocked = "~You blocked~";
    public static final String You_unblocked = "~You unblocked~";

    public static final String ALL_USERS = "ALL_USERS";

    public static final String ADMIN_EMAIL = "kushalchukanatti123@gmail.com";
    public static final String Chats = "Chats";
    public static final String Demo = "Demo";
    public static final String ALL_CHATS = "ALL_CHATS";
    public static final String LS = "LS";
    public static final String ME = "ME";
    public static final String HE = "HE";
    public static final String NULL = "NULL";
    public static final String isBlocked = "isBlocked";
    public static final String BLOCK = "BLOCK";
    public static final String UNBLOCK = "UNBLOCK";
    public static final String Others = "Others";
    public static final String Amount = "Amount";
    public static final String Mode = "Mode";
    public static final String DOB = "DOB";
    public static final String S_Desc = "S_Desc";
    public static final String T_Desc = "T_Desc";
    public static final String Gender = "Gender";
    public static final String Mobile = "Mobile";
    public static final String Lat = "Lat";
    public static final String Lng = "Lng";
    public static final String Type = "Type";
    public static final String Connection = "C";
    public static final String DisConnection = "D";
    public static final String Online = "Online";
    public static final String Offline = "Offline";
    public static final String Any = "Any";
    public static final String NA = "NA";
    public static final String Male = "Male";
    public static final String Female = "Female";
    public static final String EMPTY_STR = "";

    public static final String Notif_Key = "Notif_Key";
    public static final String Address_L = "Address_L";
    public static final String Address_P = "Address_P";
    public static final String Address_C = "Address_C";


    public static final String Timestamp = "Timestamp";
    public static final String SUBJECTS = "Subjects";
    public static final String Students = "Students";
    public static final String PENDING = "Pending";
    public static final String DECLINED = "Declined";
    public static final String ACTIONS = "Actions";
    public static final String Notifications = "Notifications";
    public static final String General = "General";
    public static final String Requests = "Requests";

    public static final String My_Students = "My_Students";
    public static final String Classes = "Classes";
    public static final String My_Tutors = "My_Tutors";
    public static final String ALL_TUTORS = "ALL_TUTORS";
    public static final String Is_Tutor = "Is_Tutor";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/theallroundersedu.appspot.com/o/ALL_PROFILE%2Farnold.jpeg?alt=media&token=72170c04-1988-4672-92fb-554113a96f2d";

    public static String getdatetimeFromTimestamp(String timestampStr) {
        Long timestamp = Long.parseLong(timestampStr);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;

    }

    public static String getCurrentTimestamp() {
        return System.currentTimeMillis() + "";
    }


    //SETUP
    public static final String[] TEACHING_MODE_ARR = {"Any", "Online", "Offline"};
    public static final String[] GENDER_ARR = {"None", "Male", "Female", "NA"};

}
