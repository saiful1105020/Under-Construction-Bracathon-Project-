package com.underconstruction.underconstruction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.underconstruction.underconstruction.LineGraphPackage.Line;
import com.underconstruction.underconstruction.LineGraphPackage.LineGraph;
import com.underconstruction.underconstruction.LineGraphPackage.LinePoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


/**
 *
 * THis fragment shows teh user rating graph and the current rating point of the user.
 * THis fragment loads only once when the user first loads te fragment. AFter that, it does not load in that particular session
 */

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int REQUEST_SETTINGS_CHANGE = 1;

    String lastPreferredLanguage;

    //json object holding the rating object sent by the database
    JSONObject jsonRating;

    //the object to communicate with the activity holding the fragment
    private OnFragmentInteractionListener mListener;

    //Settings menu will open after the user clicks the button
    ImageButton btnSettings;

    //The user will log out from the app after clicking the button
    ImageButton btnLogout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //instantiating the variables
        btnSettings = (ImageButton)v.findViewById(R.id.btnSettings);
        btnLogout = (ImageButton)v.findViewById(R.id.btnLogout);

        //setting clicklistener for the settings button
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the settings Activity will be opened
                lastPreferredLanguage = Utility.Settings.get_language(getActivity());
                Intent k = new Intent(getContext(), SettingsActivity.class);
                startActivityForResult(k, REQUEST_SETTINGS_CHANGE);
            }
        });

        //setting clicklistener for the logout button

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disable autologin and clear login history
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("IsLoggedIn", false);
                editor.commit();

                //signOut
                getActivity().finish();
                Intent k = new Intent(getContext(), LoginActivity.class);
                startActivity(k);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //the user is greeted
        TextView lblGreeting = (TextView) getView().findViewById(R.id.lblDashboardHello);
        lblGreeting.setText(" " + Utility.CurrentUser.getUsername() + "!");


        //the user rating is fetched for the first time
        if(mListener.getUserRating()== null){
            Log.d("HomeFragment.java","Fetching rating for the first time");
            new FetchRatingTask().execute();
        }
        //redraw the rating graph and rating point from previuosly fetched data
        else{

            Log.d("HomeFragment.java","using previously fetched rating");
            UserRating userRating  = mListener.getUserRating();
            populateRatingGraph(userRating);
            populateUserRating(userRating);

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //reload if language has been changed
        if(requestCode == REQUEST_SETTINGS_CHANGE && !lastPreferredLanguage.equals(Utility.Settings.get_language(getActivity()))){
            Intent intent=new Intent(getActivity(), TabbedHome.class);
            startActivity(intent);
            getActivity().finish();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public UserRating getUserRating();
        public void setUserRating(UserRating userRating);
    }

    /**
     * Fetches the most recent rating from the database
     */

    class FetchRatingTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
//            progressLayout.setVisibility(View.VISIBLE);
//            customDiscussionListView.setVisibility(View.GONE);

            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            params.add(new Pair("userId", Utility.CurrentUser.getUserId()));

            jsonRating = jParser.makeHttpRequest("/getuserrating", "GET", params);

            return null;
        }



        protected void onPostExecute(String a) {
//            progressLayout.setVisibility(View.GONE);
//            customDiscussionListView.setVisibility(View.VISIBLE);

            if (jsonRating == null) {
//                Utility.CurrentUser.showConnectionError(getActivity());
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }


            try {
                formatRatingFields(jsonRating);
            } catch(Exception e){
                e.printStackTrace();
            }

            formatRatingFields(jsonRating);
        }

        /**
         * Creates a new UserRating object by parsing the returned json Object
         * @param jsonRating the database object returned that holds all the rating data
         */

        private void formatRatingFields(JSONObject jsonRating) {
            JSONArray ratingJSONArray = new JSONArray();

            //holds the rating point
            int userR = 0;

            try {
                //build a rating array
                ratingJSONArray = jsonRating.getJSONArray("rating");
                //get the rating point
                userR = jsonRating.getInt("userRating");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int curIndex = 0, N = ratingJSONArray.length();

            //holds the max and min rating points. Used for drawing the graph
            int max, min;

            int currentRating = userR;
            min = max = userR;

            //holds all the items for drawing the graph
            Stack<RatingGraphItem> graphItemStack = new Stack<>();

            graphItemStack.push(new RatingGraphItem(currentRating, ""));

            Log.d("HomeFragment","json array len "+N);

            //This loop processes the items one by one and pushes them into a stack
            while (curIndex < N) {
                try {
                    JSONObject curObj = ratingJSONArray.getJSONObject(curIndex);
                    int ratingChange = curObj.getInt("ratingChange");

                    currentRating -= ratingChange;
                    Log.d("RatingItems Loop", currentRating + " :" + N);
                    if (currentRating > max) max = currentRating;
                    if (currentRating < min) min = currentRating;

                    graphItemStack.push(new RatingGraphItem(currentRating, curObj.getString("time")));

                    curIndex++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Then a new UserRating object is created
            UserRating newUserRating = new UserRating(graphItemStack, userR, max, min);
            //and given to the parent activity for saving it
            mListener.setUserRating(newUserRating);
            //draw the rating graph
            populateRatingGraph(newUserRating);
            //drwa the rating point
            populateUserRating(newUserRating);
        }
    }


    /**
     * Populates user rating field
     * @param userRating The current rating of the user.Holds both rating point and info to draw the rating graph
     */
    void populateUserRating(UserRating userRating) {

            //JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("userRating");
            int userR = userRating.getUserRatingPoint();
            TextView lblUsrt = (TextView)getView().findViewById(R.id.lblDashboardCurrentRating);
            lblUsrt.setText(" " + userR);

    }

    /**
     * Draws the user rating graph
     * @param userRating The current rating of the user.Holds both rating point and info to draw the rating graph
     */
    void populateRatingGraph(UserRating userRating) {
        Stack<RatingGraphItem> graphItemStack = userRating.getAllRatingGraphItems();
        //to keep similarity with the json items
        int N =graphItemStack.size() -1 ;

        Log.d("HomeFragment","stack len "+N);

        int max = userRating.getMaxRating();
        int min = userRating.getMinRating();

        Line l = new Line();

        //saves a copy of the stack
        Stack<RatingGraphItem> savedCopy = new Stack<RatingGraphItem>();
        savedCopy.addAll(graphItemStack);


        //draws the line one by one
        for (int i = 0; i< N; i++)
        {

            RatingGraphItem g = graphItemStack.pop();
            Log.d("RatingItems", g.toString());
            LinePoint p = new LinePoint();
            p.setX(i);
            p.setY(g.rating);
            p.setLabel_string(g.label);
            l.addPoint(p);

        }

        //and restore the stack
        userRating.setAllRatingGraphItems(savedCopy);

        l.setColor(Color.parseColor("#FFBB33"));

        //then build the fraph
        LineGraph li = (LineGraph)getView().findViewById(R.id.graph);
        //and add the line to the graph
        li.addLine(l);
        li.setRangeY(min-2, max+2);
        li.setLineToFill(0);
    }



}