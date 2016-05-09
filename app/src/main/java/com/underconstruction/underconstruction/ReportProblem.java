package com.underconstruction.underconstruction;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ReportProblem extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    ListView list;
    TextView txtCateDesc;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int camera =0;
    Button btnAddReport,btnSaveReport;
    private ArrayList<String>locationAtrributes=new ArrayList<String>();
    Bitmap imageBitmap;
    byte[] imageByteArray;
    SQLiteDatabase tempDatabase;
    HashSet<String> tagHashSet=new HashSet<String>();
    ImageView mImageView;
    String mCurrentPhotoPath;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    boolean mAddressRequested=true;
    //private String mAddressOutput;
    public String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private View view;
    private String resultOutput;
    private int categorySelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        if (camera == 0)
        {
            dispatchTakePictureIntent();
            camera++;
        }
        else
            finish();

        mResultReceiver = new AddressResultReceiver(new Handler());

        mImageView=(ImageView)findViewById(R.id.addReportImageImageView);
        btnAddReport=(Button)(findViewById(R.id.addReportNewReportButton));
//        btnSaveReport=(Button)(findViewById(R.id.addReportSaveReportButton));

        list = (ListView) findViewById(R.id.listView);
        txtCateDesc = (TextView) findViewById(R.id.txtCategoryDesc);


        //String[] values = new String[]{"Broken Road", "Manhole", "Risky Intersection", "Crime prone area", "Others"};
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, Utility.CategoryList.getArrayList());
        list.setAdapter(adapt);
        list.setItemChecked(0, true);
        categorySelected = 0;
        Log.d("Category Selected", categorySelected + "");

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = position;
                Log.d("Category Selected", position + "");
                if (list.getItemAtPosition(position).equals("Others")) {
                    txtCateDesc.setVisibility(View.VISIBLE);
                    txtCateDesc.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtCateDesc, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    txtCateDesc.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_problem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            //FormatAndPopulateLocationTextView();
            //setPic();
            Intent intent =new Intent(this, ReportProblem.class);
            startActivity(intent);
//            btnSaveReport.setClickable(true);
            btnAddReport.setClickable(true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class AddReportTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonAddReport;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            /*params.add(new Pair("fromLocationId", fromLocationId));
            params.add(new Pair("toLocationId", toLocationId));
            params.add(new Pair("estTimeToCross", estimatedTime));
            params.add(new Pair("situation", situation));
            params.add(new Pair("description", description));
            params.add(new Pair("timeOfSituation", timestamp));
            params.add(new Pair("updaterId", Utility.CurrentUser.getId()));
            params.add(new Pair("requestId", requestId));*/
            for(int i=0;i<locationAtrributes.size();i++){

                String tagAndValueString=locationAtrributes.get(i);

                String tag= tagAndValueString.split(":")[0];
                Log.d("timest: ", tagAndValueString);
                String value;
                if(!tag.equals("time"))
                    if(tagAndValueString.split(":").length==1){
                        value="";
                    }
                    else value=tagAndValueString.split(":")[1];
                else{
                    value=tagAndValueString.substring(tagAndValueString.indexOf(":")+1);
                }

                if(tag.equals("street_number"))
                    tag="streetNo";
                else if(tag.equals("sublocality_level_1"))
                    tag="sublocality";
                params.add(new Pair(tag, value));

                Log.d("string_test", tag + " " + value);
            }
            String encodedString = Base64.encodeToString(imageByteArray, 0);
            params.add(new Pair("image",encodedString));
//            params.add(new Pair("userName:", Utility.CurrentUser.getUsername()));
            params.add(new Pair("userId",Utility.CurrentUser.getUserId()));
            Log.d("image size", imageByteArray.length + "");

            // getting JSON string from URL
            jsonAddReport = jParser.makeHttpRequest("/insertPost", "POST", params);
//            jsonLocations = jParser.makeHttpRequest("/locations", "GET", null);


            // Check your log cat for JSON reponse
//            Log.e("All info: ", jsonLogin.toString());
            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String a){


            if(jsonAddReport==null)
                Log.d("report_database"," null");
            else Log.d("report_database",jsonAddReport.toString());



        }
    }

    class FetchLocation extends AsyncTask<Report, Void, String> {

        private JSONObject jsonAddReport;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(Report... args) {

            buildGoogleApiClient();
            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String a){


            if(jsonAddReport==null)
                Log.d("report_database"," null");
            else Log.d("report_database",jsonAddReport.toString());

        }
    }

    public void onUploadNowButtonClick(View v){

        new FetchLocation().execute();

        Intent intent =new Intent(this, TabbedHome.class);
        startActivity(intent);
//        dispatchTakePictureIntent();

    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Toast.makeText(this,"google map client",Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.d("google map client", "returned");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null) {
            Toast.makeText(this, "Google client has returned null", Toast.LENGTH_LONG).show();
            //buildGoogleApiClient();
        } else if (mLastLocation != null) {
            // Toast.makeText(this,"Google client has returned",Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this, "Google client has returned not null", Toast.LENGTH_LONG).show();
            Toast.makeText(this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

            sendDataToAppropriateDatabase();
        }
    }

    public void sendDataToAppropriateDatabase() {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            Toast.makeText(this,"before starting the intent service",Toast.LENGTH_LONG).show();
            //THis post has to be inserted in the main database
            if(Utility.isOnline(getApplicationContext())) {
                startIntentServiceForReverseGeoTagging();
            }
            //This post will be saved in the internal database.
            else {
                //saveTheReportInDatabase(imageByteArray);
                formatDataForSavingInTheInternalDB();
                Log.d("Internet Connection", "absent");

            }
        }
        else
            Toast.makeText(this,"Obtaining location failed. Please try after sometime",Toast.LENGTH_LONG).show();

        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        //mAddressRequested = true;
        //updateUIWidgets();
    }

    protected void startIntentServiceForReverseGeoTagging() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        Toast.makeText(this,"Just before calling intent service",Toast.LENGTH_LONG).show();
        //Log.d("inside service",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
        this.startService(intent);


    }

    @Override
    public void onConnectionSuspended(int i) {

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

        for(int i=0;i<locationPairs.length;i++){
            locationAtrributes.add(locationPairs[i]);
        }

        locationAtrributes.add("latitude:"+mLastLocation.getLatitude()+"");
        locationAtrributes.add("longitude:" + mLastLocation.getLongitude());
        locationAtrributes.add("category:"+categorySelected);
        locationAtrributes.add("time:" + getCurrentTimestamp());
        Log.d("time from normal report", getCurrentTimestamp());
        String informalLocation=((EditText)findViewById(R.id.addInformalLocationEditText)).getText().toString();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=((EditText)findViewById(R.id.addInformalDescEditText)).getText().toString();
        locationAtrributes.add("problemDescription:" + informalDescription);
        //locationAtrributes.add("userName:" + "Onix");

        imageByteArray=convertBitmapIntoByteArray(imageBitmap);
        //Log.d("byteArray", new String(imageByteArray));
        //locationAtrributes.add("image:"+new String(imageByteArray));



        //Log.d("Formatted arraylist", locationAtrributes.toString());
//        TextView locationTV=(TextView)findViewById(R.id.addReportLocationTextView);
        String resultToShow=new String(resultOutput);

        resultToShow=resultToShow.replaceAll("~",",");
        Log.d("finally outputed address",resultToShow+"");
//        locationTV.setText(resultToShow);
//        if(whichButtonIsPressed==calledFromInsertReport){
//            new AddReportTask().execute();
//        }
//        else {
//            //Log.d("storage", "internal storage");
//            //formatDataForSavingInTheInternalDB();
//        }

        new AddReportTask().execute();
    }

    private void formatDataForSavingInTheInternalDB(){

        locationAtrributes.clear();
        locationAtrributes.add("latitude:"+mLastLocation.getLatitude()+"");
        locationAtrributes.add("longitude:" + mLastLocation.getLongitude());
        locationAtrributes.add("category:"+categorySelected);
        locationAtrributes.add("time:" + getCurrentTimestamp());
        String informalLocation=((EditText)findViewById(R.id.addInformalLocationEditText)).getText().toString();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=((EditText)findViewById(R.id.addInformalDescEditText)).getText().toString();
        locationAtrributes.add("problemDescription:" + informalDescription);
        //locationAtrributes.add("userName:" + "Onix");
        //Log.d("before saving in the internal database: ",locationAtrributes.toString());
        imageByteArray=convertBitmapIntoByteArray(imageBitmap);
        saveTheReportInDatabase(imageByteArray);
    }



    private byte[] convertBitmapIntoByteArray(Bitmap imageBitmap){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return bArray;

    }

    private String getCurrentTimestamp(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        //DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        Date date = new Date();
//            dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //cal.add(Calendar.MINUTE, -1 * timeToSubtract);
        Date newDate = cal.getTime();
        //Log.d("timestamp", dateFormat.format(newDate));

        String timestamp = dateFormat.format(newDate);
        Log.d("timestamp: ", "" + timestamp);
        return timestamp;
    }

    private void saveTheReportInDatabase(byte[] imageByteArray) {
        //tempDatabase = openOrCreateDatabase("tempReport",MODE_PRIVATE,null);
        //tempDatabase.execSQL("CREATE TABLE IF NOT EXISTS tempTable(userName VARCHAR,Password VARCHAR);");
        //mydatabase.execSQL("INSERT INTO TutorialsPoint VALUES('admin','admin');");
        //Log.d("before inserting in database:", locationAtrributes.toString());

        // deleteWholeDatabase();
        DBHelper help=new DBHelper(this);
        Log.d("before new insertion : ",help.getAllRecords().toString());
        help.insertRecord(locationAtrributes, imageByteArray);
        Log.d("after new insertion : ", help.getAllRecords().toString());

        //testDelete();
        //fetchSavedDataOfaSingleUser("1");
        /*
        Log.d("before insertion : ",help.getAllRecords().toString());
        help.insertRecord(locationAtrributes, arr);
        Log.d("after insertion : ", help.getAllRecords().toString());
        Log.d("numberInserted: ", "" + help.numberOfRows());
        Toast.makeText(this,help.numberOfRows()+"",Toast.LENGTH_LONG).show();
        */
        //getUserRecords("Onix");
        //help.deleteRecord(1);
        //getUserRecords("Onix");
        //help.getData()
        //sendStoredEntryToDatabase();
    }


}
