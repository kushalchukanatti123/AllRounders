package com.albatross.allrounders.Util;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.onesignal.OneSignal;

public class SendNotificationOnesignal {
    public SendNotificationOnesignal(String heading, String message, String notificationKey, int targetAct) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contents",new JSONObject().put("en",message));
            jsonObject.put("data",new JSONObject().put("target",targetAct));
            jsonObject.put("include_player_ids",new JSONArray().put(notificationKey));
            jsonObject.put("headings",new JSONObject().put("en",heading));

            Log.e("ONESIGNAL",jsonObject.toString());

//            JSONObject notificationObj = new JSONObject("{'contents':{'en':'"+message+"'},"+"'include_player_ids':['"+notificationKey+"'],"+
//                    "'headings':{'en':'"+heading+"'}}/*","+"'data': {'target':'"+targetAct+"'}"*/);

            OneSignal.postNotification(jsonObject,null);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ONESIGNAL",e.toString());
        }
    }
    public SendNotificationOnesignal(String heading, String message, ArrayList<String> notificationKeyList, int targetAct) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contents",new JSONObject().put("en",message));
            jsonObject.put("data",new JSONObject().put("target",targetAct));
            JSONArray array = new JSONArray(notificationKeyList);
            jsonObject.put("include_player_ids",array);
            jsonObject.put("headings",new JSONObject().put("en",heading));

            Log.e("ONESIGNAL",jsonObject.toString());

//            JSONObject notificationObj = new JSONObject("{'contents':{'en':'"+message+"'},"+"'include_player_ids':['"+notificationKey+"'],"+
//                    "'headings':{'en':'"+heading+"'}}/*","+"'data': {'target':'"+targetAct+"'}"*/);

            OneSignal.postNotification(jsonObject,null);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ONESIGNAL",e.toString());
        }
    }
}
