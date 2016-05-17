package com.underconstruction.underconstruction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by wasif on 4/17/16.
 */


public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_POST_SUGGESTION = 1;
    Context context;
    //DBHelper helper;
    ArrayList<Report> allTheReportsOfIntDb;

//    private AddressResultReceiver mResultReceiver;
    private ArrayList<String>locationAtrributes;
    private String resultOutput;
    byte[] imageByteArray;
    DBHelper internalDb;
    Report theReportToBeSentToMainDB;
    int theIndexOfTheReportToBeSent;



//    ArrayList<CollectedData> allDatasToBeSent;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        Log.d("braodcast receiver ", "called");

//        mResultReceiver = new AddressResultReceiver(new Handler());
        this.context = context;

        if(isOnline(context)){
            //helper = new DBHelper(context);
//            if(allDatasToBeSent != null){
//                allDatasToBeSent.clear();
//            }
//            allDatasToBeSent = helper.getDataForUser(Utility.getUserId());
//
//            if(allDatasToBeSent.size()==0){
//                return;
//            }
            Log.d("device ", "online");
//            Log.d("the number of data that has come back", ""+allDatasToBeSent.size());
//            callNextData(0);


            /**
             *
             * Check if app is in foreground
             */


            Intent newIntent = new Intent(context, ReportAutoUploadActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
//            bringDataFromInternalDb();
//            if(allTheReportsOfIntDb.size()>0){
//                uploadTheReportToMainDatabase(0);
//                new PostSuggestionTask().execute(0 + "");
//            }
        }
        else{
            Log.d("device ", "offline");
//            Intent newIntent = new Intent(context, ReportAutoUploadActivity.class);
//            newIntent.putExtra("test", "for the first time in forever...");
//            context.startService(newIntent);
        }
    }



    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }



}

