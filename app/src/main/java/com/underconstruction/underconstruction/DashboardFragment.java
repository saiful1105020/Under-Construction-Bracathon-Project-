package com.underconstruction.underconstruction;

/**
 * userId hardcoded in new Report object instantiation
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    public static ProgressDialog pd;
    public String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private ArrayList<String>locationAtrributes;
    private String resultOutput;
    byte[] imageByteArray;
    private static ListView lv;
    private ResultListAdaptor adapter;
    private List<YourPosts> lst_online;
    private String[] problemCategory = {"Occupied Footpath", "Open Dustbin", "Exposed Manhole", "Dangerous Electric wire", "Waterlogging", "Risky Road Intersection", "No Street Light", "Crime Prone Area", "Broken Road", "Wrong Way Trafiic"};
    private OnFragmentInteractionListener mListener;
//    private String userName = "Onix";
    ScrollView parentScroll, childScroll;
    ListView myPosts;
    JSONObject jsonPosts;
    DBHelper internalDb;
    Report theReportToBeSentToMainDB;
    ImageView profileRefresh;
    ArrayList<YourPosts> postArrayList = new ArrayList<YourPosts>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ResultListAdaptor();
        pd = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);
        profileRefresh = (ImageView) view.findViewById(R.id.btnRefreshDashboard);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationAtrributes = new ArrayList<String >();
        mResultReceiver = new AddressResultReceiver(new Handler());

        populatePostListView();

        profileRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnRefreshDashboard) {
                    Log.d("Profile", "reload button clicked");
                    new FetchDashboardTask().execute();
                }
            }
        });

        if(mListener.retrieveLatestProfilePosts() == null) {
            new FetchDashboardTask().execute();
        }
        else {
            postArrayList = mListener.retrieveLatestProfilePosts();
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void bringDataFromInternalDb() {
        internalDb = new DBHelper(getContext());
        ArrayList<Report> allTheReportsOfIntDb = internalDb.getDataForUser(Utility.CurrentUser.getUserId());
        if(!allTheReportsOfIntDb.isEmpty()) {
            uploadTheReportToMainDatabase(allTheReportsOfIntDb.get(0));
        }
        Log.d("bring back our report", allTheReportsOfIntDb.toString());

    }


    private void deleteAReportFromIntDb(Report deleteIt){
        String idOfTheRecord = deleteIt.getRecordID();
        //converting to int as the record id is in integer in sqlite database;
        internalDb.deleteRecord(idOfTheRecord);

    }

    private void uploadTheReportToMainDatabase(Report sendIt){
        theReportToBeSentToMainDB = sendIt;
        Log.d("the selected report",theReportToBeSentToMainDB.toString());
        startIntentServiceForReverseGeoTagging(sendIt);
    }

    protected void startIntentServiceForReverseGeoTagging(Report sendIt) {
        double lat = Double.parseDouble(sendIt.getLatitude());
        double lon = Double.parseDouble(sendIt.getLongitude());
        Location mLastLocation = new Location("");
        mLastLocation.setLatitude(lat);
        mLastLocation.setLongitude(lon);
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        Toast.makeText(getActivity(), "Just before calling intent service", Toast.LENGTH_LONG).show();
        //Log.d("inside service",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
        getActivity().startService(intent);


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

        locationAtrributes.add("latitude:"+theReportToBeSentToMainDB.getLatitude());
        locationAtrributes.add("longitude:" + theReportToBeSentToMainDB.getLongitude());
        locationAtrributes.add("category:"+ theReportToBeSentToMainDB.getCategory());
        locationAtrributes.add("time:" + theReportToBeSentToMainDB.getTime());
        String informalLocation= theReportToBeSentToMainDB.getInformalLocation();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=theReportToBeSentToMainDB.getProblemDescription();
        locationAtrributes.add("problemDescription:" + informalDescription);
        //locationAtrributes.add("userName:" + "Onix");

        imageByteArray=theReportToBeSentToMainDB.getImage();
        //Log.d("byteArray", new String(imageByteArray));
        //locationAtrributes.add("image:"+new String(imageByteArray));

        new AddReportTask().execute();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void storeLatestProfilePosts(ArrayList<YourPosts> postsList);
        public ArrayList<YourPosts> retrieveLatestProfilePosts();
    }




    private void populatePostList(JSONObject jsonPosts) {

        try {
            JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("posts");
            postArrayList.clear();

            int curIndex=0, N=dashboardListJSONArray.length();

            while(curIndex<N) {
                JSONObject curObj = dashboardListJSONArray.getJSONObject(curIndex++);
                YourPosts curPost = YourPosts.createPost(curObj);
//                int upCount = curPost.getUpVote();
//                int downCount = curPost.getDownVote();
//                Log.d("curPost", curPost.toString());
                postArrayList.add(curPost);
            }

            adapter.notifyDataSetChanged();
            mListener.storeLatestProfilePosts(postArrayList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        lst_online = new ArrayList<YourPosts>(postArrayList);
        Log.d("List", "Construction passed");
        //ArrayList<Report> ar = new ArrayList<Report>(getUserRecords(Utility.CurrentUser.getName()));             //CHANGE
        //ArrayList<Report> ar = internalDb.getDataForUser(Utility.CurrentUser.getUserId());
        //Log.d("List", ar.toString());
//        for (int i = 0; i<ar.size(); i++)
//        {
//            postArrayList.add(new YourPosts(ar.get(i)));
//        }
        //Log.d("List", "report converted to list");
        //postArrayList.add(new YourPosts("2015-12-05 09:25:30", "Azimpur", "7", "At Palashi point", "Very Dangerous", 0, -1, 0, 0));
        //(new ArrayList<YourPosts>());
    }

    private void populatePostListView(){
//        ArrayAdapter<YourPosts> adapter = new ResultListAdaptor();

        ListView list=(ListView)getView().findViewById(R.id.lvwDashboard);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pd.hide();
//        lv.setItemsCanFocus(false);
    }

    class ResultListAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return postArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return postArrayList.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
//                LayoutInflater inflater = (LayoutInflater) DashboardFragment.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.activity_dashboard_list_item, parent,false);
                convertView=getActivity().getLayoutInflater().inflate(R.layout.activity_dashboard_list_item,parent,false);
            }

            TextView txtTimeStamp = (TextView)convertView.findViewById(R.id.lblDashboardTimestamp);
            TextView txtCatAtLoc = (TextView)convertView.findViewById(R.id.lblDashboardCatAtLoc);
            TextView txtLocation_desc = (TextView)convertView.findViewById(R.id.lblDashboardInformalLocation);
            TextView txtUserComment = (TextView)convertView.findViewById(R.id.lblDashboardComment);

            TextView txtRatingChange = (TextView)convertView.findViewById(R.id.lblDashboardRatingchange);
            TextView txtUpVote = (TextView)convertView.findViewById(R.id.lblDashboardRatingUp);
            TextView txtDownVote = (TextView)convertView.findViewById(R.id.lblDashboardRatingDown);
            TextView txtStatus = (TextView)convertView.findViewById(R.id.lblDashboardStatus);
            Button btnUpdate = (Button)convertView.findViewById(R.id.btnDashboardUpload);
            Button btnDelete = (Button)convertView.findViewById(R.id.btnDashboardDelete);
            LinearLayout ratingLayout = (LinearLayout)convertView.findViewById(R.id.layoutRating);
            ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);

            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            YourPosts post_item = postArrayList.get(position);

            Log.d("Timestamp", post_item.getTimeStamp());
            txtTimeStamp.setText(Utility.CurrentUser.parsePostTime(post_item.getTimeStamp()));
            int categoryId = Integer.valueOf(post_item.getCategory());
            if(categoryId == -1)
                txtCatAtLoc.setText("Uncategorized Problem" + " at " + post_item.getExactLocation());
            else
                txtCatAtLoc.setText(Utility.CategoryList.get(categoryId) + " at " + post_item.getExactLocation());


            if (!post_item.getLocationDescription().isEmpty() && !post_item.getLocationDescription().equals("null"))
                txtLocation_desc.setText("(" + post_item.getLocationDescription() + ")");
            else
                txtLocation_desc.setText("");

            txtUserComment.setText(post_item.getProblemDescription());
            if (post_item.getRatingChanged()>0)
            {
                txtRatingChange.setText("Your rating has increased by " + post_item.getRatingChanged() + " points!");
                txtRatingChange.setTextColor(Color.parseColor("#ff669900"));
            }

            else if (post_item.getRatingChanged()<0)
            {
                txtRatingChange.setText("Your rating has decreased by " + (-1)*post_item.getRatingChanged() + " points!");
                txtRatingChange.setTextColor(Color.parseColor("#ffcc0000"));
            }
            else
            {
                txtRatingChange.setText("");
            }

            txtUpVote.setText(String.valueOf(post_item.getUpVote()));
            txtDownVote.setText(String.valueOf(post_item.getDownVote()));

            if (post_item.getState() == -1)
            {
                btnUpdate.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                txtStatus.setVisibility(View.INVISIBLE);
                ratingLayout.setVisibility(View.INVISIBLE);
            }
            else
            {
                btnDelete.setVisibility(View.INVISIBLE);
                btnUpdate.setVisibility(View.INVISIBLE);
                txtStatus.setVisibility(View.VISIBLE);
                ratingLayout.setVisibility(View.VISIBLE);
                switch (post_item.getState())
                {
                    case 0:
                    {
                        txtStatus.setText("PENDING");
                        break;
                    }
                    case 1:
                    {
                        txtStatus.setText("VERIFIED");
                        break;
                    }
                    case 2:
                    {
                        txtStatus.setText("REJECTED");
                        break;
                    }
                    case 3:
                    {
                        txtStatus.setText("SOLVED");
                        break;
                    }
                }
            }
            //String[] img = {"@drawable/pending", "@drawable/verified", "@drawable/rejected", "@drawable/solved", "@drawable/upload"};
            if (post_item.getState() == -1)
            {
                imgStatus.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.upload));
            }
            else if (post_item.getState() == 0)
            {
                imgStatus.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.pending));
            }
            else if (post_item.getState() == 1)
            {
                imgStatus.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.verified));
            }
            else if (post_item.getState() == 2)
            {
                imgStatus.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.rejected));
            }
            else if (post_item.getState() == 3)
            {
                imgStatus.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.solved));
            }
            return convertView;
        }

        public YourPosts getResultItem(int pos)
        {
            return postArrayList.get(pos);
        }
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

    class FetchDashboardTask extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute()
        {
            profileRefresh.setEnabled(false);
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            params.add(new Pair("userId", Utility.CurrentUser.getUserId()));
//            params.add(new Pair("userName", Utility.CurrentUser.getId()));

            // getting JSON string from URL
            jsonPosts = jParser.makeHttpRequest("/getuserposts", "GET", params);

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String a){
//            progressLayout.setVisibility(View.GONE);
//            customDiscussionListView.setVisibility(View.VISIBLE);

            profileRefresh.setEnabled(true);
            if(jsonPosts == null) {
//                Utility.CurrentUser.showConnectionError(getActivity());
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }
            Log.d("FetchProfilePostsTask", "profile reloaded");

            pd.setMessage("Please wait, loading data...");
            pd.setCancelable(true);
            pd.setInverseBackgroundForced(false);
            pd.show();
            //jsonUpdatesField=jsonPosts;
            populatePostList(jsonPosts);

            try {
                populatePostListView();
            } catch (Exception e) {
                e.printStackTrace();
                pd.cancel();
            }

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


        protected void onPostExecute (String a){


            if(jsonAddReport==null)
                Log.d("report_database"," null");
            else Log.d("report_database",jsonAddReport.toString());



        }
    }

    /*
    public ArrayList<Report> getUserRecords(String name){

        DBHelper help=new DBHelper(getActivity());
        Log.d("List", String.valueOf(help.numberOfRows()));
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
            Report objectToBeSent=new Report(tREPORT_COLUMN_ID,1,tREPORT_COLUMN_NAME,tREPORT_COLUMN_CATEGORY,tREPORT_COLUMN_IMAGE,tREPORT_COLUMN_TIME,tREPORT_COLUMN_INFORMALLOCATION,tREPORT_COLUMN_PROBDESCR,tREPORT_COLUMN_STREETNO,tREPORT_COLUMN_ROUTE,tREPORT_COLUMN_NEIGHBORHOOD,tREPORT_COLUMN_SUBLOCALITY,tREPORT_COLUMN_LOCALITY,tREPORT_COLUMN_LATITUDE,tREPORT_COLUMN_LONGITUDE);
            reportsToBeSent.add(objectToBeSent);
            Log.d("userentries:",objectToBeSent.toString());

            allRowsForName.moveToNext();
        }
        return reportsToBeSent;
    }
    */
}
