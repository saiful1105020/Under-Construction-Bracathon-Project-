package com.underconstruction.underconstruction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

        Log.d("braodcast receiver ", "called");


        this.context = context;

        if(isOnline(context)){

            Log.d("device ", "online");


            /**
             *
             * Check if app is in foreground
             */

            //device is online, so we will initiate a new intent to complete sending all the items in SQLIte DB to main database
            Intent newIntent = new Intent(context, ReportAutoUploadActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
//
        }
        else{
            Log.d("device ", "offline");
            //Device offline, nothing to do
        }
    }


    /**
     *
     * @param context The context of the application
     * @return true if device  is online, false otherwise
     */
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }



}

