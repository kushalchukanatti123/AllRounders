package com.albatross.allrounders.Util;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleObserver;

import com.albatross.allrounders.MAIN.SplashScreenAct;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONObject;



/*import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;*/

public class IntroApp extends Application implements LifecycleObserver, OSPermissionObserver {

    @Override
    public void onCreate() {
        super.onCreate();

       /* OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
               // .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        // OneSignal.startInit(MapsActivity.this).setNotificationOpenedHandler(new ExampleNotificationOpenedHandler()).init();
        OneSignal.setSubscription(true);

        // OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        OneSignal.addPermissionObserver(this);


    }
    public void onOSPermissionChanged(OSPermissionStateChanges stateChanges) {
        if (stateChanges.getFrom().getEnabled() &&
                !stateChanges.getTo().getEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage("Notifications Disabled!")
                    .show();
        }

        Log.i("Debug", "onOSPermissionChanged: " + stateChanges);
    }


    //ONESIGNAL
    public class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            int customKey = Constants.N_NOWHERE;
            String openURL = null;
            Object activityToLaunch = SplashScreenAct.class;

            if (data != null) {
                customKey = data.optInt("target", Constants.N_NOWHERE);
                // openURL = data.optString("openURL", null);

                if (customKey != Constants.N_NOWHERE) {
                    Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                    intent.putExtra("FROM_NOTIF",customKey);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.putExtra("openURL", openURL);
                    // Log.i("OneSignalExample", "openURL = " + openURL);
                    // startActivity(intent);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), SplashScreenAct.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                Log.e("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

          /*  if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = GreenActivity.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }*/

            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            // intent.putExtra("openURL", openURL);
            // Log.i("OneSignalExample", "openURL = " + openURL);
            // startActivity(intent);
            startActivity(intent);


        }

    }
}