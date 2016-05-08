package com.underconstruction.underconstruction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wasif on 4/17/16.
 */


public class MyBroadcastReceiver extends BroadcastReceiver {

    Context context;
    //DBHelper helper;
    ArrayList<Report> allTheReportsOfIntDb;

    private AddressResultReceiver mResultReceiver;
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

        mResultReceiver = new AddressResultReceiver(new Handler());
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
            bringDataFromInternalDb();
            if(allTheReportsOfIntDb.size()>0){
                uploadTheReportToMainDatabase(0);
            }
        }
        else{
            Log.d("device ", "offline");
        }
    }



    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }


    private void bringDataFromInternalDb() {
        internalDb = new DBHelper(context);
        allTheReportsOfIntDb = internalDb.getDataForUser(Utility.CurrentUser.getUserId());
       /* if(!allTheReportsOfIntDb.isEmpty()) {
          uploadTheReportToMainDatabase(allTheReportsOfIntDb.get(0));}
          */
        Log.d("bring back our report", allTheReportsOfIntDb.toString());

    }


    private void deleteAReportFromIntDb(Report deleteIt){
        String idOfTheRecord = deleteIt.getRecordID();
        //converting to int as the record id is in integer in sqlite database;
        internalDb.deleteRecord(idOfTheRecord);

    }

    private void uploadTheReportToMainDatabase(int index){
        //all the reports have been sent
        if(index>=allTheReportsOfIntDb.size()){
            return;
        }

        theIndexOfTheReportToBeSent = index;
        theReportToBeSentToMainDB = allTheReportsOfIntDb.get(index);
        Log.d("the selected report",theReportToBeSentToMainDB.toString());
        startIntentServiceForReverseGeoTagging(theReportToBeSentToMainDB);
    }

    protected void startIntentServiceForReverseGeoTagging(Report sendIt) {
        double lat = Double.parseDouble(sendIt.getLatitude());
        double lon = Double.parseDouble(sendIt.getLongitude());
        Location mLastLocation = new Location("");
        mLastLocation.setLatitude(lat);
        mLastLocation.setLongitude(lon);
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        Toast.makeText(context, "Just before calling intent service", Toast.LENGTH_LONG).show();
        //Log.d("inside service",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
        context.startService(intent);


    }

    class AddressResultReceiver extends ResultReceiver {


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            resultOutput= resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d("returned to destination","true");
            // Log.d("address location", resultOutput);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            Log.d("result code",resultCode+"");
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
                //Toast.makeText(AddReport.class,resultOutput,Toast.LENGTH_LONG).show();
                //Toast.makeText(this,resultOutput,Toast.LENGTH_LONG).show();
                Log.d("address location", resultOutput);
                formatAndSendDataToMainDB();
            }

        }
    }


    private void formatAndSendDataToMainDB() {
        Log.d("result_output",resultOutput);
        String[] locationPairs=resultOutput.split("~" +
                "");

        locationAtrributes = new ArrayList<String>();

        for(int i=0;i<locationPairs.length;i++){
            locationAtrributes.add(locationPairs[i]);
        }

        locationAtrributes.add("latitude:"+theReportToBeSentToMainDB.getLatitude());
        locationAtrributes.add("longitude:" + theReportToBeSentToMainDB.getLongitude());
        locationAtrributes.add("category:"+ theReportToBeSentToMainDB.getCategory());
        String time = theReportToBeSentToMainDB.getTime();
//        Log.d("Time before parsing", time);
//        time = time.substring(1);           //removing unwanted first character from the string
//        Log.d("Time after parsing", time);
        locationAtrributes.add("time:" + time);

        Log.d("time from internal db", theReportToBeSentToMainDB.getTime());

        String informalLocation= theReportToBeSentToMainDB.getInformalLocation();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=theReportToBeSentToMainDB.getProblemDescription();
        locationAtrributes.add("problemDescription:" + informalDescription);
        locationAtrributes.add("userId:"+theReportToBeSentToMainDB.getUserId());

        //locationAtrributes.add("userName:" + "Onix");

        imageByteArray=theReportToBeSentToMainDB.getImage();
        //Log.d("byteArray", new String(imageByteArray));
        //locationAtrributes.add("image:"+new String(imageByteArray));

        new AddReportTask().execute();

    }

    class AddReportTask extends AsyncTask<String, Void, String> {

        private JSONObject responseJSON;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();


            for (int i = 0; i < locationAtrributes.size(); i++) {

                String tagAndValueString = locationAtrributes.get(i);

                String tag = tagAndValueString.split(":")[0];
                Log.d("timest: ", tagAndValueString);
                String value;
                if (!tag.equals("time"))
                    if (tagAndValueString.split(":").length == 1) {
                        value = "";
                    } else value = tagAndValueString.split(":")[1];
                else {
                    value = tagAndValueString.substring(tagAndValueString.indexOf(":") + 1);
                }

                if (tag.equals("street_number"))
                    tag = "streetNo";
                else if (tag.equals("sublocality_level_1"))
                    tag = "sublocality";
                params.add(new Pair(tag, value));

                Log.d("string_test", tag + " " + value);
            }
            String encodedString = Base64.encodeToString(imageByteArray, 0);
            params.add(new Pair("image", encodedString));
            params.add(new Pair("userId", Utility.CurrentUser.getUserId()));
//            params.add(new Pair("userName:", Utility.CurrentUser.getUsername()));

            Log.d("image size", imageByteArray.length + "");

            // getting JSON string from URL
            responseJSON = jParser.makeHttpRequest("/insertPost", "POST", params);
//            jsonLocations = jParser.makeHttpRequest("/locations", "GET", null);


            // Check your log cat for JSON reponse
//            Log.e("All info: ", jsonLogin.toString());
            return null;

        }

        protected void onPostExecute (String a){


            if(responseJSON!=null){

                try {
                    if(responseJSON.getString("post_inserted").equals("OK")) {

                        Log.d("Size of internal db", internalDb.getAllRecords().size() + "");

                        int deletedRecordID = internalDb.deleteRecord(theReportToBeSentToMainDB.getRecordID() + "");
                        Log.d("Record ID to be deleted", theReportToBeSentToMainDB.getRecordID());
                        Log.d("Record ID actually deleted", deletedRecordID + "");

                        Log.d("the entry has been cleared from ","database");
                        uploadTheReportToMainDatabase(theIndexOfTheReportToBeSent+1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

