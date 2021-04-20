package com.albatross.allrounders.Util;

import java.io.Serializable;

public class GenUserUtilClass implements Serializable {
    private String Name,Profile_Url,Is_Tutor,DOB,S_Desc,Gender,Mobile,Lat,Lng,Address_L,Address_C,Address_P,Notif_Key;


    public GenUserUtilClass(String name, String profile_Url, String is_Tutor, String DOB, String s_Desc, String gender, String mobile, String lat, String lng, String address_L, String address_C, String address_P, String notif_Key) {
        Name = name;
        Profile_Url = profile_Url;
        Is_Tutor = is_Tutor;
        this.DOB = DOB;
        S_Desc = s_Desc;
        Gender = gender;
        Mobile = mobile;
        Lat = lat;
        Lng = lng;
        Address_L = address_L;
        Address_C = address_C;
        Address_P = address_P;
        Notif_Key = notif_Key;
    }

    public String getNotif_Key() {
        return Notif_Key;
    }

    public void setNotif_Key(String notif_Key) {
        Notif_Key = notif_Key;
    }

    public GenUserUtilClass() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfile_Url() {
        return Profile_Url;
    }

    public void setProfile_Url(String profile_Url) {
        Profile_Url = profile_Url;
    }

    public String getIs_Tutor() {
        return Is_Tutor;
    }

    public void setIs_Tutor(String is_Tutor) {
        Is_Tutor = is_Tutor;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getS_Desc() {
        return S_Desc;
    }

    public void setS_Desc(String s_Desc) {
        S_Desc = s_Desc;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getAddress_L() {
        return Address_L;
    }

    public void setAddress_L(String address_L) {
        Address_L = address_L;
    }

    public String getAddress_C() {
        return Address_C;
    }

    public void setAddress_C(String address_C) {
        Address_C = address_C;
    }

    public String getAddress_P() {
        return Address_P;
    }

    public void setAddress_P(String address_P) {
        Address_P = address_P;
    }
}
