package com.underconstruction.underconstruction;

/**
 * userId hardcoded in new Report object instantiation
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.underconstruction.underconstruction.Constants;
import com.underconstruction.underconstruction.DBHelper;
import com.underconstruction.underconstruction.FetchAddressIntentService;
import com.underconstruction.underconstruction.JSONParser;
import com.underconstruction.underconstruction.R;
import com.underconstruction.underconstruction.Report;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class AddReport extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

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
    private ArrayList<String>locationAtrributes=new ArrayList<String>();
    Bitmap imageBitmap;
    byte[] imageByteArray;
    SQLiteDatabase tempDatabase;
    HashSet<String> tagHashSet=new HashSet<String>();
    private RadioButton[] rbCatagory;
    private int rbSelected;
    private int calledFromInsertReport=1;
    private int calledFromSaveReport=2;
    private int whichButtonIsPressed;
    Button btnAddReport,btnSaveReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        populateHashSet();
        //initiateHazardTagsInSpinner();
        mImageView=(ImageView)findViewById(R.id.addReportImageImageView);
        btnAddReport=(Button)(findViewById(R.id.addReportNewReportButton));
        btnSaveReport=(Button)(findViewById(R.id.addReportSaveReportButton));

        mResultReceiver = new AddressResultReceiver(new Handler());
        // mResultReceiver.setReceiver(this);

        //buildGoogleApiClient();
        //radio grouping
        rbCatagory = new RadioButton[10];

        rbCatagory[0] = (RadioButton)findViewById(R.id.rbFootpath);
        rbCatagory[1] = (RadioButton)findViewById(R.id.rbDustbin);
        rbCatagory[2] = (RadioButton)findViewById(R.id.rbManhole);
        rbCatagory[3] = (RadioButton)findViewById(R.id.rbWire);
        rbCatagory[4] = (RadioButton)findViewById(R.id.rbWaterclog);
        rbCatagory[5] = (RadioButton)findViewById(R.id.rbRoadIntersection);
        rbCatagory[6] = (RadioButton)findViewById(R.id.rbStreetlight);
        rbCatagory[7] = (RadioButton)findViewById(R.id.rbCrime);
        rbCatagory[8] = (RadioButton)findViewById(R.id.rbBrokenRoad);
        rbCatagory[9] = (RadioButton)findViewById(R.id.rbTraffic);

        RadioButtonListener();
    }
    public class RadioButtonOnClickList implements View.OnClickListener{
        int selected;
        public RadioButtonOnClickList(int i)
        {
            this.selected = i;
        }
        public void onClick(View v)
        {
            for (int i = 0; i<10; i++)
                rbCatagory[i].setChecked(false);
            rbCatagory[selected].setChecked(true);
            rbSelected = selected;
        }
    }
    public void RadioButtonListener()
    {
        for (int i =0; i<10; i++)
        {
            rbCatagory[i].setOnClickListener(new RadioButtonOnClickList(i));
        }
    }
    private void populateHashSet() {
        tagHashSet.add("street_number");
        tagHashSet.add("street_number");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_report, menu);
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


    /*
        public void initiateHazardTagsInSpinner() {
            Spinner hazardTagsSpinner = (Spinner) findViewById(R.id.addReportProblemCategorySpinner);


           String[] hazardTags=Utility.HazardTags.getHazardTags();

            // Application of the Array to the Spinner
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item,hazardTags);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            hazardTagsSpinner.setAdapter(spinnerArrayAdapter);


        }
    */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addReportAddPhotoButton){
            Toast.makeText(this,"Add button pressed", Toast.LENGTH_LONG).show();
            dispatchTakePictureIntent();
        }

        if(v.getId()==R.id.addReportNewReportButton){
            Toast.makeText(this,"RB selected "+rbSelected,Toast.LENGTH_LONG).show();
            whichButtonIsPressed=calledFromInsertReport;
            new FetchLocation().execute();
//            Intent intent = new Intent(AddReport.this, Home.class);

            Intent intent = new Intent(AddReport.this, TabbedHome.class);
            startActivity(intent);
//            buildGoogleApiClient();
            //handleNewReport()
        }
        else if(v.getId()==R.id.addReportSaveReportButton){
            whichButtonIsPressed=calledFromSaveReport;
            new FetchLocation().execute();
//            Intent intent = new Intent(AddReport.this, Home.class);
            Intent intent = new Intent(AddReport.this, TabbedHome.class);
            startActivity(intent);
//            buildGoogleApiClient();
            //saveTheReportInDatabase();

        }

    }

    private void saveTheReportInDatabase(byte[] arr) {
        //tempDatabase = openOrCreateDatabase("tempReport",MODE_PRIVATE,null);
        //tempDatabase.execSQL("CREATE TABLE IF NOT EXISTS tempTable(userName VARCHAR,Password VARCHAR);");
        //mydatabase.execSQL("INSERT INTO TutorialsPoint VALUES('admin','admin');");

        DBHelper help=new DBHelper(this);
        Log.d("before insertion : ", help.getAllRecords().toString());
        Toast.makeText(this, "before: " + help.getAllRecords().toString(), Toast.LENGTH_LONG).show();
        Toast.makeText(this,"in internal database before saving" + locationAtrributes.toString(),Toast.LENGTH_LONG).show();
        help.insertRecord(locationAtrributes, arr);
        Log.d("after insertion : ", help.getAllRecords().toString());
        Toast.makeText(this, "before: " + help.getAllRecords().toString(), Toast.LENGTH_LONG).show();
        Log.d("numberInserted: ", "" + help.numberOfRows());
        Toast.makeText(this, "numberInserted: " + help.numberOfRows(), Toast.LENGTH_LONG).show();
        //getUserRecords("Onix");
        //help.deleteRecord(1);
        //getUserRecords("Onix");
        //help.getData()
        //sendStoredEntryToDatabase();
    }

    private void deleteWholeDatabase(){
        DBHelper help=new DBHelper(this);
        help.onUpgrade(help.getWritableDatabase(),0,1);
    }

    public ArrayList<Report> getUserRecords(String name){

        DBHelper help=new DBHelper(this);
        Cursor allRowsForName=help.getData(name);
        ArrayList<Report> reportsToBeSent=new ArrayList<Report>();
        allRowsForName.moveToFirst();
        while(allRowsForName.isAfterLast() == false){
            int tREPORT_COLUMN_ID = allRowsForName.getInt(allRowsForName.getColumnIndex(help.REPORT_COLUMN_ID));
            String tREPORT_COLUMN_NAME = allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_NAME));
            String tREPORT_COLUMN_CATEGORY = allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_CATEGORY));
            byte[] tREPORT_COLUMN_IMAGE = allRowsForName.getBlob(allRowsForName.getColumnIndex(help.REPORT_COLUMN_IMAGE));
            String tREPORT_COLUMN_TIME = allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_TIME));
            String tREPORT_COLUMN_INFORMALLOCATION=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_INFORMALLOCATION));
            String tREPORT_COLUMN_PROBDESCR=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_PROBDESCR));
            String tREPORT_COLUMN_STREETNO=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_STREETNO));
            String tREPORT_COLUMN_ROUTE=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_ROUTE));
            String tREPORT_COLUMN_NEIGHBORHOOD=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_NEIGHBORHOOD));
            String tREPORT_COLUMN_SUBLOCALITY=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_SUBLOCALITY));
            String tREPORT_COLUMN_LOCALITY=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_LOCALITY));
            String tREPORT_COLUMN_LATITUDE=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_LATITUDE));
            String tREPORT_COLUMN_LONGITUDE=allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_LONGITUDE));
            //Log.d("userentries:",allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_NAME))+" "+allRowsForName.getString(allRowsForName.getColumnIndex(help.REPORT_COLUMN_TIME)));
            Report objectToBeSent=new Report(tREPORT_COLUMN_ID, 1, tREPORT_COLUMN_NAME,tREPORT_COLUMN_CATEGORY,tREPORT_COLUMN_IMAGE,tREPORT_COLUMN_TIME,tREPORT_COLUMN_INFORMALLOCATION,tREPORT_COLUMN_PROBDESCR,tREPORT_COLUMN_STREETNO,tREPORT_COLUMN_ROUTE,tREPORT_COLUMN_NEIGHBORHOOD,tREPORT_COLUMN_SUBLOCALITY,tREPORT_COLUMN_LOCALITY,tREPORT_COLUMN_LATITUDE,tREPORT_COLUMN_LONGITUDE);
            reportsToBeSent.add(objectToBeSent);
            Log.d("userentries:",objectToBeSent.toString());

            allRowsForName.moveToNext();
        }
        return reportsToBeSent;
    }
    public void fetchAddressButtonHandler() {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            Toast.makeText(this,"before starting the intent service",Toast.LENGTH_LONG).show();
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        //updateUIWidgets();
    }


    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        Toast.makeText(this,"Just before calling intent service",Toast.LENGTH_LONG).show();
        //Log.d("inside service",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
        this.startService(intent);


    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            /*
            Toast.makeText(this,"activity resolved",Toast.LENGTH_LONG).show();
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            Toast.makeText(this,photoFile.toString(),Toast.LENGTH_LONG).show();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            */
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
            btnSaveReport.setClickable(true);
            btnAddReport.setClickable(true);
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.d("google map client", "returned");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation==null){
            Toast.makeText(this,"Google client has returned null",Toast.LENGTH_LONG).show();
        }
        else if (mLastLocation != null) {
            // Toast.makeText(this,"Google client has returned",Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this,"Google client has returned not null",Toast.LENGTH_LONG).show();
            Toast.makeText(this,mLastLocation.getLatitude()+" "+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();


            fetchAddressButtonHandler();
            // Determine whether a Geocoder is available.
            /*
                if (!Geocoder.isPresent()) {
                    /*
                    Toast.makeText(this, R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show();

                    return;
                }

               /*
                if (mAddressRequested) {
                    startIntentService();
                }

               else {
                    fetchAddressButtonHandler();
                }
        */
        }

    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
                FormatAndPopulateLocationTextView();
            }

        }
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


    private void sendStoredEntryToDatabase(){
        ArrayList<Report> records= getUserRecords("onix");

        if(records.size()>0) {
            Report tobeSent = records.get(0);
            Log.d("finaltask: ",tobeSent.toString());
            new AddReportFromInternalDBTask().execute(tobeSent);
        }


    }

    class AddReportFromInternalDBTask extends AsyncTask<Report, Void, String> {

        private JSONObject jsonAddReport;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(Report... args) {

            Report reportToBeSent=args[0];
            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("userId",reportToBeSent.getUserId()));
            params.add(new Pair("userName",reportToBeSent.getUserName()));
            params.add(new Pair("category",reportToBeSent.getCategory()));
            String encodedString = Base64.encodeToString(reportToBeSent.getImage(), 0);
            params.add(new Pair("image",encodedString));

            params.add(new Pair("time",reportToBeSent.getTime()));
            params.add(new Pair("informalLocation",reportToBeSent.getInformalLocation()));
            params.add(new Pair("problemDescription",reportToBeSent.getProblemDescription()));
            params.add(new Pair("streetNo",reportToBeSent.getStreetNo()));
            params.add(new Pair("route",reportToBeSent.getRoute()));
            params.add(new Pair("neighborhood",reportToBeSent.getNeighborhood()));
            params.add(new Pair("sublocality",reportToBeSent.getSublocality()));
            params.add(new Pair("locality",reportToBeSent.getLocality()));
            params.add(new Pair("latitude",reportToBeSent.getLatitude()));
            params.add(new Pair("longitude",reportToBeSent.getLongitude()));
            /*params.add(new Pair("fromLocationId", fromLocationId));
            params.add(new Pair("toLocationId", toLocationId));
            params.add(new Pair("estTimeToCross", estimatedTime));
            params.add(new Pair("situation", situation));
            params.add(new Pair("description", description));
            params.add(new Pair("timeOfSituation", timestamp));
            params.add(new Pair("updaterId", Utility.CurrentUser.getId()));
            params.add(new Pair("requestId", requestId));*/
            /*
            for(int i=0;i<locationAtrributes.size();i++){

                String tagAndValueString=locationAtrributes.get(i);

                String tag= tagAndValueString.split(":")[0];
                Log.d("timest: ",tagAndValueString);
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
            Log.d("image size", imageByteArray.length + "");*/

            // getting JSON string from URL
            jsonAddReport = jParser.makeHttpRequest("/insertPost", "POST", params);
//            jsonLocations = jParser.makeHttpRequest("/locations", "GET", null);

            try {
//                Boolean error = jsonLogin.getBoolean("error");
//                errorMessage = jsonLogin.getString("message");
//                if(error)loginError=true;
            }catch(Exception e){
                e.printStackTrace();
            }

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
    private void FormatAndPopulateLocationTextView() {
        Log.d("result_output",resultOutput);
        String[] locationPairs=resultOutput.split("~");

        for(int i=0;i<locationPairs.length;i++){
            locationAtrributes.add(locationPairs[i]);
        }

        locationAtrributes.add("latitude:" + mLastLocation.getLatitude() + "");
        locationAtrributes.add("longitude:" + mLastLocation.getLongitude());
        locationAtrributes.add("category:" + rbSelected);
        locationAtrributes.add("time:" + getCurrentTimestamp());
        String informalLocation=((EditText)findViewById(R.id.addInformalLocationEditText)).getText().toString();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=((EditText)findViewById(R.id.addInformalDescEditText)).getText().toString();
        locationAtrributes.add("problemDescription:" + informalDescription);

        imageByteArray=convertBitmapIntoByteArray(imageBitmap);
        //Log.d("byteArray", new String(imageByteArray));
        //locationAtrributes.add("image:"+new String(imageByteArray));



        Log.d("Formatted arraylist", locationAtrributes.toString());
        TextView locationTV=(TextView)findViewById(R.id.addReportLocationTextView);
        String resultToShow=new String(resultOutput);

        resultToShow=resultToShow.replaceAll("~",",");

        locationTV.setText(resultToShow);
        Log.d("resultToShow", resultToShow);

        if(whichButtonIsPressed==calledFromInsertReport){
            new AddReportTask().execute();
        }
        else{
            saveTheReportInDatabase(imageByteArray);
        }

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
                Log.d("timest: ",tagAndValueString);
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
}
