package com.underconstruction.underconstruction;

import android.app.Activity;
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
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    JSONObject jsonRating;

    private OnFragmentInteractionListener mListener;

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
    ImageButton btnSettings, btnLogout, btnRefresh;
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

        /*

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        */
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView lblGreeting = (TextView) getView().findViewById(R.id.lblDashboardHello);
        lblGreeting.setText("Hello " + Utility.CurrentUser.getUsername() + "!");
//        if(DashboardFragment.jsonPosts != null)
//            DashboardFragment.populateRatingGraph(DashboardFragment.jsonPosts);
        Line l = new Line();
        l.setColor(Color.parseColor("#FFBB33"));
        Log.d("onActivityCreated", "yes");
        new FetchRatingTask().execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    }

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
//            params.add(new Pair("userName", Utility.CurrentUser.getId()));

            // getting JSON string from URL
            jsonRating = jParser.makeHttpRequest("/getuserrating", "GET", params);

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String a) {
//            progressLayout.setVisibility(View.GONE);
//            customDiscussionListView.setVisibility(View.VISIBLE);

            if (jsonRating == null) {
//                Utility.CurrentUser.showConnectionError(getActivity());
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }
            //jsonUpdatesField=jsonPosts;
            populateRatingGraph(jsonRating);
            populateUserRating(jsonRating);

        }

    }

    void populateUserRating(JSONObject jsonPosts) {
        try {
            //JSONArray dashboardListJSONArray = jsonPosts.getJSONArray("userRating");
            int userR = jsonPosts.getInt("userRating");
            TextView lblUsrt = (TextView)getView().findViewById(R.id.lblDashboardCurrentRating);
            lblUsrt.setText("Your current rating is " + userR);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void populateRatingGraph(JSONObject jsonPosts) {
        JSONArray ratingJSONArray = new JSONArray();
        int userR = 0;
        try {
            ratingJSONArray = jsonPosts.getJSONArray("rating");
            userR = jsonPosts.getInt("userRating");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int curIndex=0, N=ratingJSONArray.length();

        Line l = new Line();

        int max, min, rating=0;

        //ArrayList<RatingGraphItem> graphItems = new ArrayList<RatingGraphItem>();
        int currentRating = userR;
        min = max = userR;

        Stack<RatingGraphItem> graphItemStack = new Stack<>();
        graphItemStack.push(new RatingGraphItem(currentRating, ""));
        while(curIndex<N) {
            try{
                JSONObject curObj = ratingJSONArray.getJSONObject(curIndex);
                int ratingChange = curObj.getInt("ratingChange");

                if (ratingChange >max) max = ratingChange;
                if (ratingChange <min) min = ratingChange;
                //voteCount += ratingChange;
                //if(voteCount>max) max=voteCount;
                //if(voteCount<min) min=voteCount;

                currentRating -=ratingChange;
                Log.d("RatingItems Loop", currentRating + " :" + N);
                if (currentRating >max) max = currentRating;
                if (currentRating <min) min = currentRating;

                graphItemStack.push(new RatingGraphItem(currentRating, curObj.getString("time")));

                //rating += ratingChange;
                //if(rating>max) max=rating;
                //if(rating<min) min=rating;

                /*
>>>>>>> a36a5b6958b2c440cfa8d32280c49f5ea9887921
                LinePoint p = new LinePoint();
                p.setX(curIndex);
                p.setY(ratingChange);
                p.setLabel_string(curObj.getString("time"));
                l.addPoint(p);*/

                curIndex++;
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }


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

        l.setColor(Color.parseColor("#FFBB33"));

        LineGraph li = (LineGraph)getView().findViewById(R.id.graph);

        li.addLine(l);

        li.setRangeY(min-2, max+2);
        li.setLineToFill(0);
    }
    class RatingGraphItem
    {
        int rating;
        String label;

        public RatingGraphItem(int rating, String label) {
            this.rating = rating;
            this.label = label;
        }

        @Override
        public String toString() {
            return "RatingGraphItem{" +
                    "rating=" + rating +
                    ", label='" + label + '\'' +
                    '}';
        }
    }
}