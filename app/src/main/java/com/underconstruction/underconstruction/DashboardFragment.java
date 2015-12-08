package com.underconstruction.underconstruction;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.underconstruction.underconstruction.LineGraphPackage.Line;
import com.underconstruction.underconstruction.LineGraphPackage.LineGraph;
import com.underconstruction.underconstruction.LineGraphPackage.LinePoint;
import com.underconstruction.underconstruction.YourPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    public static ProgressDialog pd;
    private static ListView lv;
    private ResultListAdaptor rla;
    private List<YourPosts> lst = new ArrayList<YourPosts>();
    private List<YourPosts> lst_online;
    private String[] problemCatagory = {"Occupied Footpath", "Open Dustbin", "Exposed Manhole", "Dangerous Electric wire", "Waterlogging", "Risky Road Intersection", "No Street Light", "Crime Prone Area", "Broken Road", "Wrong Way Trafiic"};
    private OnFragmentInteractionListener mListener;
    private String userName = "Onix";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);

        Line l = new Line();
//        LinePoint p = new LinePoint();
//        p.setX(1);
//        p.setY(5);
//        p.setLabel_string("23 December, 2014");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(2);
//        p.setY(8);
//        p.setLabel_string("1 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(3);
//        p.setY(4);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(4);
//        p.setY(20);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(5);
//        p.setY(7);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(6);
//        p.setY(45);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(7);
//        p.setY(25);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(8);
//        p.setY(15);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);
//        p = new LinePoint();
//        p.setX(9);
//        p.setY(48);
//        p.setLabel_string("6 January, 2015");
//        l.addPoint(p);

        l.setColor(Color.parseColor("#FFBB33"));

//        LineGraph li = (LineGraph)findViewById(R.id.graph);

//        li.addLine(l);
//
//        li.setRangeY(0, 50);
//        li.setLineToFill(0);

//        lst.add(new YourPosts("27 Oct, 2015", "Gulshan", "Broken Road", "Near PQS", "Creates jam", -10, 2, 2, 5));
//        lst.add(new YourPosts("9 Nov, 2015", "Motijheel", "Narrow Footpath", "In front of VNS", "Occupied by hawkers", 10, 3, 5, 1));
//        lst.add(new YourPosts("24 Nov, 2015", "Azimpur", "Open Manhole", "At Palashi point", "Very Dangerous", 0, -1, 0, 0));
//        lst.add(new YourPosts("24 Nov, 2015", "Kamlapur", "Crime Prone Area", "", "", 0, 0, 2, 2));
//        lst.add(new YourPosts("24 Nov, 2015", "Mugda", "Crime Prone Area", "", "", 5, 1, 2, 2));
//

        rla = new ResultListAdaptor();
//        lv = (ListView)findViewById(R.id.lvwDashboard);
//        lv.setAdapter(rla);
//        lv.setItemsCanFocus(false);

//        rla.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new FetchDashboardTask().execute();
        TextView lblGreeting = (TextView)getView().findViewById(R.id.lblDashboardHello);
        lblGreeting.setText("Hello " + userName + "!");

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void populateRatingGraph(JSONObject jsonPosts) {
        JSONArray ratingJSONArray = null;
        try {
            ratingJSONArray = jsonPosts.getJSONArray("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int curIndex=0, N=ratingJSONArray.length();

        Line l = new Line();
        int max=0, min=100000, rating=0;

        while(curIndex<N) {
            try{
                JSONObject curObj = ratingJSONArray.getJSONObject(curIndex);
                int ratingChange = curObj.getInt("ratingChange");
                if (ratingChange >max) max = ratingChange;
                if (ratingChange <min) min = ratingChange;
                //rating += ratingChange;
                //if(rating>max) max=rating;
                //if(rating<min) min=rating;
                LinePoint p = new LinePoint();
                p.setX(curIndex);
                p.setY(ratingChange);
                p.setLabel_string(curObj.getString("time"));
                l.addPoint(p);
                curIndex++;
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }

        l.setColor(Color.parseColor("#FFBB33"));

        LineGraph li = (LineGraph)getView().findViewById(R.id.graph);

        li.addLine(l);

        li.setRangeY(min-2, max+2);
        li.setLineToFill(0);
    }

    private void populateUserRating(JSONObject jsonPosts) {
        try {
            //JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("userRating");
            int userR = jsonPosts.getInt("userRating");
            TextView lblUsrt = (TextView)getView().findViewById(R.id.lblDashboardCurrentRating);
            lblUsrt.setText("Your current rating is " + userR);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void populatePostList(JSONObject jsonPosts) {

        try {
            JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("posts");
            lst.clear();

            int curIndex=0, N=dashboardListJSONArray.length();

            while(curIndex<N) {
                JSONObject curObj = dashboardListJSONArray.getJSONObject(curIndex++);
                YourPosts curPost = YourPosts.createPost(curObj);
//                int upCount = curPost.getUpVote();
//                int downCount = curPost.getDownVote();
//                Log.d("curPost", curPost.toString());
                lst.add(curPost);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lst_online = new ArrayList<YourPosts>(lst);
        Log.d("List", "Construction passed");
        ArrayList<Report> ar = new ArrayList<Report>(getUserRecords(userName));
        Log.d("List", getUserRecords("Onix").toString());
        for (int i = 0; i<ar.size(); i++)
        {
            lst.add(new YourPosts(ar.get(i)));
        }
        Log.d("List", "report converted to list");
        lst.add(new YourPosts("24 Nov, 2015", "Azimpur", "7", "At Palashi point", "Very Dangerous", 0, -1, 0, 0));
        //(new ArrayList<YourPosts>());
    }

    private void populatePostListView(){
//        ArrayAdapter<YourPosts> adapter = new ResultListAdaptor();

        ListView list=(ListView)getView().findViewById(R.id.lvwDashboard);
        list.setAdapter(rla);
        pd.hide();
//        lv.setItemsCanFocus(false);
    }

    class ResultListAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return lst.size();
        }

        @Override
        public Object getItem(int position) {
            return lst.get(position) ;
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
            btnDelete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
            YourPosts post_item = lst.get(position);

            txtTimeStamp.setText(post_item.getTimeStamp());
            txtCatAtLoc.setText(problemCatagory[Integer.valueOf(post_item.getCategory())] + " at " + post_item.getExactLocation());
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
            return lst.get(pos);
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

        private JSONObject jsonPosts;

        @Override
        protected void onPreExecute()
        {
//            progressLayout.setVisibility(View.VISIBLE);
//            customDiscussionListView.setVisibility(View.GONE);

            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            params.add(new Pair("userName", "Onix"));

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

            if(jsonPosts == null) {
//                Utility.CurrentUser.showConnectionError(getActivity());
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please wait, loading data...");
            pd.setCancelable(false);
            pd.setInverseBackgroundForced(false);
            pd.show();
            //jsonUpdatesField=jsonPosts;
            populateRatingGraph(jsonPosts);
            populatePostList(jsonPosts);
            populateUserRating(jsonPosts);


            populatePostListView();
        }
    }
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
            Report objectToBeSent=new Report(tREPORT_COLUMN_ID,tREPORT_COLUMN_NAME,tREPORT_COLUMN_CATEGORY,tREPORT_COLUMN_IMAGE,tREPORT_COLUMN_TIME,tREPORT_COLUMN_INFORMALLOCATION,tREPORT_COLUMN_PROBDESCR,tREPORT_COLUMN_STREETNO,tREPORT_COLUMN_ROUTE,tREPORT_COLUMN_NEIGHBORHOOD,tREPORT_COLUMN_SUBLOCALITY,tREPORT_COLUMN_LOCALITY,tREPORT_COLUMN_LATITUDE,tREPORT_COLUMN_LONGITUDE);
            reportsToBeSent.add(objectToBeSent);
            Log.d("userentries:",objectToBeSent.toString());

            allRowsForName.moveToNext();
        }
        return reportsToBeSent;
    }
}
