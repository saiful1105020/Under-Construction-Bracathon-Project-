package com.underconstruction.underconstruction;

/**
 * userId hardcoded in new Report object instantiation
 */

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * This fragment holds all the recent activity of the user. And the situation of his posts. whether thery are pending, solved or rejected
 * The fragment data will be downloaded from the database only once. However, the user may click the refresh button and bring latest data
 *
 */

public class DashboardFragment extends Fragment {



    //The adapter to show all the YourPosts
    private ResultListAdaptor adapter;


    //The arraylist to hold all YourPosts
    private ArrayList<YourPosts> postArrayList = new ArrayList<YourPosts>();
    //This variable communicates with the parent actvity of the fragment
    private OnFragmentInteractionListener mListener;

    //Holds all the posts returned by the database
    JSONObject jsonPosts;
    DBHelper internalDb;
    Report theReportToBeSentToMainDB;

    //By clicking this button, user can refresh his dashboard
    ImageButton profileRefresh;
    //a progressbar to show while posts are being downloaded
    ProgressBar pbDash;
    //a custom listview to show all the YourPosts of a  user
    ListView lvwDash;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initiates the adapter
        adapter = new ResultListAdaptor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        //variables are initiated
        profileRefresh = (ImageButton) view.findViewById(R.id.btnRefreshDashboard);
        pbDash = (ProgressBar) view.findViewById(R.id.pbDashboard);
        lvwDash = (ListView) view.findViewById(R.id.lvwDashboard);

        return view;
    }

    /**
     * /if refresh button is pressed, hide listview and show progressbar
     * @param busy
     */
    void busy_sessions(boolean busy)
    {
        if (pbDash == null) return;
        if (busy == true)
        {
            pbDash.setVisibility(View.VISIBLE);
            lvwDash.setVisibility(View.GONE);
            profileRefresh.setEnabled(false);
        }
        else
        {
            pbDash.setVisibility(View.GONE);
            lvwDash.setVisibility(View.VISIBLE);
            profileRefresh.setEnabled(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //populate the custom listview
        try {
            populatePostListView();
        } catch(Exception e) {
            e.printStackTrace();
        }


        //Fetch latest data from database and set the listview again
        profileRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnRefreshDashboard) {
                    Log.d("Profile", "reload button clicked");

                    //bring all the YourPOst from database again
                    new FetchDashboardTask().execute();
                }
            }
        });


        //if the fragment is loaded for the first time, we bring data from the database. Otherwise, we just populate the arraylist with data stored in the parent activity
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void storeLatestProfilePosts(ArrayList<YourPosts> postsList);
        public ArrayList<YourPosts> retrieveLatestProfilePosts();
    }


    /**
     * Clears the existing Arraylist and relaods it with the latest posts
     * @param jsonPosts All the YourPosts in one json object
     */
    private void populatePostList(JSONObject jsonPosts) {

        try {
            //create a new JSONArray of posts
            JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("posts");

            //clear the Arraylist
            postArrayList.clear();

            //sets up variables to traverse through the entire JSON Array
            int curIndex=0, N=dashboardListJSONArray.length();

            if(N==0) {
                Toast.makeText(getActivity(), "You have not reported any problem yet!", Toast.LENGTH_LONG).show();
            }

            while(curIndex<N) {
                //get the json object at that index
                JSONObject curObj = dashboardListJSONArray.getJSONObject(curIndex++);

                //convert it into a YouPosts item
                YourPosts curPost = YourPosts.createPost(curObj);
//                int upCount = curPost.getUpVote();
//                int downCount = curPost.getDownVote();
//                Log.d("curPost", curPost.toString());

                postArrayList.add(curPost);
            }

            //reload the listView
            adapter.notifyDataSetChanged();
            //set the latest ArrayList in the parent Activity
            mListener.storeLatestProfilePosts(postArrayList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Populates the post listview with the latest ArrayList
     */

    private void populatePostListView(){
//        ArrayAdapter<YourPosts> adapter = new ResultListAdaptor();

        ListView list=(ListView)getView().findViewById(R.id.lvwDashboard);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //pd.hide();
//        lv.setItemsCanFocus(false);
    }

    /**
     * An adapter for holding and managing the listview
     */

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

            //getting reference of all the variables

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

            //not functional
            btnDelete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });

            //not functional
            btnUpdate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });


            //retrieve the post at this position
            YourPosts post_item = postArrayList.get(position);
            Log.d("Timestamp", post_item.getTimeStamp());

            txtTimeStamp.setText(Utility.CurrentUser.parsePostTime(post_item.getTimeStamp()));

            int categoryId = Integer.valueOf(post_item.getCategory());
            if(categoryId == -1)
                txtCatAtLoc.setText("Uncategorized Problem" + " at " + post_item.getExactLocation());
            else
                txtCatAtLoc.setText(Utility.CategoryList.get(categoryId) + " at " + post_item.getExactLocation());


            //sets up the location of the post
            if (!post_item.getLocationDescription().isEmpty() && !post_item.getLocationDescription().equals("null"))
                txtLocation_desc.setText("(" + post_item.getLocationDescription() + ")");
            else
                txtLocation_desc.setText("");

            //sets up the change in rating
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

            //setting up upvote downvote
            txtUpVote.setText(String.valueOf(post_item.getUpVote()));
            txtDownVote.setText(String.valueOf(post_item.getDownVote()));

            //sets up appropriate image depending on the state of the post(pending, verified etc)
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


    /**
     * Brings New data from server and reloads the content of the ArrayList
     */
    class FetchDashboardTask extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute()
        {
            busy_sessions(true);
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            params.add(new Pair("userId", Utility.CurrentUser.getUserId()));

            // getting JSON string from URL
            jsonPosts = jParser.makeHttpRequest("/getuserposts", "GET", params);

            return null;
        }



        protected void onPostExecute (String a){
//            progressLayout.setVisibility(View.GONE);
//            customDiscussionListView.setVisibility(View.VISIBLE);

            profileRefresh.setEnabled(true);
            if(jsonPosts == null) {
//                Utility.CurrentUser.showConnectionError(getActivity());
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                busy_sessions(false);
                return;
            }
            Log.d("FetchProfilePostsTask", "profile reloaded");

            try {
                //reloads the ArrayLIst with new item
                populatePostList(jsonPosts);
                //redraws the Custom listview
                populatePostListView();
            } catch (Exception e) {
                e.printStackTrace();
            }


            busy_sessions(false);

            Log.d("Profile", "End of Async");
        }
    }
}
