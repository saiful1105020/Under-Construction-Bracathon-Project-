package com.underconstruction.underconstruction;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import java.util.List;

import static android.app.ActivityManager.*;

/**
 * Created by wasif on 4/17/16.
 * This class is created to send the reports stored in the SQLite database to the main database.
 */


public class MyBroadcastReceiver extends BroadcastReceiver {

    //A context object to capture the context of the calling
    Context context;

    /**
     * This method will be called when WIFI state will change, go online from offline o offline from onlie
     * @param context THe context in which the receiver is called
     * @param intent THe intent calling the receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("broadcast receiver ", "called");
        this.context = context;

        if(Utility.isOnline(context)){

            Log.d("device ", "online");


            /**
             *
             * Check if app is in foreground
             */
            DBHelper dbHelper = new DBHelper(context);
            int n = dbHelper.getDataForUser(Utility.CurrentUser.getUserId()).size();


            //device is online, so we will initiate a new intent to complete sending all the items in SQLite DB to main database
            if(!Utility.isAppIsInBackground(context) && n>0 && !TabbedHome.sendingSavedReports) {
                TabbedHome.sendingSavedReports = true;
                Log.d("Broadcast Receiver", "app in foreground, number of saved reports: " + n);
                Intent newIntent = new Intent(context, ReportAutoUploadActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
//                ReportAutoUploadActivity.bringDataFromInternalDb(context, new TabbedHome());

            }


        }
        else{
            Log.d("device ", "offline");
            //Device offline, nothing to do
        }
    }







}

