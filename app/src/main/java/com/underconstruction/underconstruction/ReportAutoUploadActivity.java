package com.underconstruction.underconstruction;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shabab on 5/9/2016.
 *
 * Reports may be uploaded in the main database from two places. If internet is available, the user can directly upload the data. However, in the absence of internet, data
 * is temporarily stored in the SQLite internal database. And when the net becomes available again, the reports are extracted one by one from the SQLIte database and checked if
 * there is a similar report already residing in the main database. If there are such reports, they are shown to to the user through PostSuggestion Activity. And the user may
 * decide to upload or discard the report.
 */
public class ReportAutoUploadActivity extends AppCompatActivity implements Utility.UploadDecision {

    // This id is used when the app invokes the POstSuggestion Activity
    public static final int REQUEST_POST_SUGGESTION = 1;

    //Used to store the contetx of the activity
    Context context;

    //holds all the reports fetched form the SQLite db
    ArrayList<Report> allTheReportsOfIntDb;
    Report reportToBeSent;

    //This object receives the result sent from Google reverse geotagging service
    private AddressResultReceiver mResultReceiver;
    //all the attributes of the location
    private ArrayList<String>locationAtrributes;
    //the address received from google in a string format
    private String resultOutput;
    //the image captured in a byte array form
    byte[] imageByteArray;
    //a reference to the internal SQlite db helper class
    DBHelper internalDb;
    //The report that is sent to the main db currently
    Report theReportToBeSentToMainDB;
    //the index of the current report in the arraylist
    int theIndexOfTheReportToBeSent=0;
    //progressbar for better user X.
    ProgressBar pbPosts;
    //Holds the custom list
    ListView lvwPosts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_verification);
        Log.d("in report auto upload activity", " done successfully");

        //instantiate the necessary variables
        context = getApplicationContext();
        mResultReceiver = new AddressResultReceiver(new Handler());


        bringDataFromInternalDb();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View v= super.onCreateView(name, context, attrs);
//        pbPosts = (ProgressBar) v.findViewById(R.id.pbPosts);
//        lvwPosts = (ListView) v.findViewById(R.id.lvwPosts);
        return v;
    }

    /**
     * Brings all the data stored from the SQLite internal database.
     */
    private void bringDataFromInternalDb() {

        //a reference to the dbhelper
        internalDb = new DBHelper(context);

        //all the reports of the user stored are fetched
        allTheReportsOfIntDb = internalDb.getDataForUser(Utility.CurrentUser.getUserId());

        Log.d("bring back our report", allTheReportsOfIntDb.toString());

        //if one or more report is fetched, we will send suggestion for them one by one
        if(allTheReportsOfIntDb.size() > 0) {
            //the suggestion for the first report is sent now
            sendPostSuggestion(theIndexOfTheReportToBeSent);
        }

    }

    /**
     * sends post suggestion for all the stored post of SQLite db
     * @param index the index of the post in the arraylist
     */
    private void sendPostSuggestion(int index) {
        //if the index is within the bounds of the array, send a suggestion for it. Otherwise, go to the Home Activity
        if(index<allTheReportsOfIntDb.size()) {
            new PostSuggestionTask().execute(index + "");
        }
        else {
            goToHomeActivity();
        }
    }

    /**
     * Goes to the Home Activity of the App
     */
    private void goToHomeActivity() {
        Intent intent = new Intent(this, TabbedHome.class);
        startActivity(intent);
    }



    protected void startIntentServiceForReverseGeoTagging(Report sendIt) {
        theReportToBeSentToMainDB = sendIt;
        double lat = Double.parseDouble(sendIt.getLatitude());
        double lon = Double.parseDouble(sendIt.getLongitude());
        Location mLastLocation = new Location("");
        mLastLocation.setLatitude(lat);
        mLastLocation.setLongitude(lon);
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
//        Toast.makeText(context, "Just before calling intent service", Toast.LENGTH_LONG).show();
        //Log.d("inside service",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
        context.startService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG,"returned from intent");
        DBHelper dbHelper = new DBHelper(getApplicationContext());

        if(requestCode == REQUEST_POST_SUGGESTION  && resultCode == RESULT_OK){

            int chosenOption = data.getIntExtra("uploadDecision",-1);
            Log.d("ReportAutoUpload",chosenOption+"");

            //the user wants to upload the report
            if(chosenOption == UPLOAD_REPORT){
                //so start uploading the report in main database
                startIntentServiceForReverseGeoTagging(reportToBeSent);
                Log.d("ReportAutoUpload","upload");
            }
            else if(chosenOption == DONT_UPLOAD_REPORT){
                //don't upload, just remove from the SQLite db.
                Log.d("ReportAutoUpload", "dont upload");
                String reportIdToBeDeleted = allTheReportsOfIntDb.get(theIndexOfTheReportToBeSent).getRecordID();

                dbHelper.deleteRecord(reportIdToBeDeleted);
                sendPostSuggestion(++theIndexOfTheReportToBeSent);

            }

            Log.d("Reports in db", dbHelper.getAllRecords().toString());
        }
    }

    /**
     * THis class receives the data sent by Google Reverse geotagging api. Look at ReportProblem for more info.
     */

    class AddressResultReceiver extends ResultReceiver {


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            //extract the result
            resultOutput= resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d("result code",resultCode+"");

            //success. send to the main db
            if (resultCode == Constants.SUCCESS_RESULT) {

                Log.d("address location", resultOutput);
                formatAndSendDataToMainDB();
            }

        }
    }

    /**
     * Formats and sends data to main db. Look at the same method in ReportProblem Activity
     */

    private void formatAndSendDataToMainDB() {

        Log.d("result_output",resultOutput);

        //get all the different location attributes. They are separated by a ~
        String[] locationPairs=resultOutput.split("~" +
                "");

        locationAtrributes = new ArrayList<String>();

        //add all the attributes
        for(int i=0;i<locationPairs.length;i++){
            locationAtrributes.add(locationPairs[i]);
        }

        //get let, long, category, time from the stored report in SQLite
        //thsi method is all the same to its counterpart in ReportProblem. Just the data are fetched from SQLite mostly

        locationAtrributes.add("latitude:"+theReportToBeSentToMainDB.getLatitude());
        locationAtrributes.add("longitude:" + theReportToBeSentToMainDB.getLongitude());
        locationAtrributes.add("category:"+ theReportToBeSentToMainDB.getCategory());

        String time = theReportToBeSentToMainDB.getTime();
        locationAtrributes.add("time:" + time);

        Log.d("time from internal db", theReportToBeSentToMainDB.getTime());


        String informalLocation= theReportToBeSentToMainDB.getInformalLocation();
        locationAtrributes.add("informalLocation:" + informalLocation);

        String informalDescription=theReportToBeSentToMainDB.getProblemDescription();
        locationAtrributes.add("problemDescription:" + informalDescription);

        locationAtrributes.add("userId:"+theReportToBeSentToMainDB.getUserId());



        imageByteArray=theReportToBeSentToMainDB.getImage();


        //add the report to the main db
        new AddReportTask().execute();

    }

    //adds the report to the main db. Similar to the sam method in ReportProblem
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


            Log.d("image size", imageByteArray.length + "");

            // getting JSON string from URL
            responseJSON = jParser.makeHttpRequest("/insertPost", "POST", params);

            return null;

        }

        protected void onPostExecute (String a){


            if(responseJSON!=null){

                try {
                    if(responseJSON.getString("post_inserted").equals("OK")) {


                        //Log.d("Size of internal db", internalDb.getAllRecords().size() + "");

                        //this is the only difference from its counterpart in REportProblem
                        //we need to delete the record form SQLite
                        int deletedRecordID = internalDb.deleteRecord(theReportToBeSentToMainDB.getRecordID() + "");

                        //now send post suggestion for the next report
                        sendPostSuggestion(++theIndexOfTheReportToBeSent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    void busy_sessions(boolean busy)
    {
        if (pbPosts==null) return;
        if (busy == true)
        {
            pbPosts.setVisibility(View.VISIBLE);
            lvwPosts.setVisibility(View.GONE);
        }
        else
        {
            pbPosts.setVisibility(View.GONE);
            lvwPosts.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sends post suggestion for a post. Look at the same class in ReportProblem
     */

    class PostSuggestionTask extends AsyncTask<String, Void, String> {

        //holds the json objest returned from the database
        private JSONObject jsonPostSuggestion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Trying to upload previously saved report...", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Checking for duplicate reports...", Toast.LENGTH_LONG).show();
//            busy_sessions(true);
        }


        protected String doInBackground(String... args) {

            //retrieves the report from the arraylist
            int reportId = Integer.parseInt(args[0]);
            reportToBeSent = allTheReportsOfIntDb.get(reportId);

            JSONParser jParser = new JSONParser();

            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("lat",reportToBeSent.getLatitude()+""));
            params.add(new Pair("lon",reportToBeSent.getLongitude() + ""));
            params.add(new Pair("time", reportToBeSent.getTime()));
            params.add(new Pair("cat", reportToBeSent.getCategory()));

            // getting JSON from URL
            jsonPostSuggestion = jParser.makeHttpRequest("/getSuggestions", "GET", params);


            return null;
        }

        protected void onPostExecute (String file_url){
//            busy_sessions(false);
            if(jsonPostSuggestion == null) {
                Log.d("OnPostExecute", "jsonPostSuggestion == null");
                return;
            }

            Log.d("PostSuggest", jsonPostSuggestion.toString());

            try {
                //builds a json array of the all the matching reports
                JSONArray postsJSONArray = jsonPostSuggestion.getJSONArray("posts");
                //postArrayList.clear();

                int N=postsJSONArray.length();

                // No conflict with other posts, so just save it
                if (N==0) {
                    startIntentServiceForReverseGeoTagging(reportToBeSent);
                    return;
                }

                //otherwise, open a post suggestion activity for getting user feedback
                Intent intent = new Intent(context, PostSuggestion.class);
                intent.putExtra("jsonPostSuggestions", jsonPostSuggestion.toString());
                startActivityForResult(intent, REQUEST_POST_SUGGESTION);




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
