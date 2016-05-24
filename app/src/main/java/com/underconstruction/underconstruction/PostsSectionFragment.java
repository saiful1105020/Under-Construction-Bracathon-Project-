package com.underconstruction.underconstruction;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostsSectionFragment.FteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostsSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


/**
 * Shows all the posts in the nearby location. The user can see the image captured, the address of the area, informal problem desciption
 * , informal location description, number of votes(up/down) in a post. The user can upvote or downvote a post also.
 */

public class PostsSectionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //color variables to draw colors
    private static final String GREY = "#c0bfac";
    private static final String BLUE = "#16586E";
    private static final String RED = "#D90D10";

    //holds all the posts in the surrounding area
    private ArrayList<Post> postArrayList = new ArrayList<Post>();
    //a listview to show the posts
    private ListView customPostListView;
    //an adapter to manage the listview
    ArrayAdapter<Post> adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //the currently active view
    private View v;
    //the contetx of the app
    private static Context context;

    // a variable to communicate with the parent Activity
    private OnFragmentInteractionListener mListener;

    //communicates with Google LOcation services
    static GoogleApiClient mGoogleApiClient;
    //the last known location of the device
    static Location mLastLocation;
    //the user can refresh the list through it
    ImageView feedRefresh;
    //progressbar for better user X.
    ProgressBar pbPosts;
    //Holds the custom list
    ListView lvwPosts;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsSectionFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static PostsSectionFragment newInstance(String param1, String param2) {
        PostsSectionFragment fragment = new PostsSectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PostsSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_posts_section, container, false);

        //initiating the necessary varaiables
        feedRefresh = (ImageView) v.findViewById(R.id.btnRefreshPostSection);
        pbPosts = (ProgressBar) v.findViewById(R.id.pbPosts);
        lvwPosts = (ListView) v.findViewById(R.id.lvwPosts);

        return v;
    }

    /**
     * if refresh button is pressed, hide listview and show progressbar
     * @param busy a flag to indicate an ongoing process
     */

    void busy_sessions(boolean busy)
    {
        if (pbPosts==null) return;
        if (busy == true)
        {
            pbPosts.setVisibility(View.VISIBLE);
            lvwPosts.setVisibility(View.GONE);
            feedRefresh.setEnabled(false);
        }
        else
        {
            pbPosts.setVisibility(View.GONE);
            lvwPosts.setVisibility(View.VISIBLE);
            feedRefresh.setEnabled(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        populatePostListView();

        //the user wants to reload the list
        feedRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnRefreshPostSection) {
                    //get the lat-long of the place and begin the poast of this place
                    getLatLong();
                }
            }
        });

        //the user is opening this fragment for the first time
        if(mListener.retrieveLatestFeed() == null) {
            getLatLong();
        }
        else {
            //we already have the lastest posts. So use them
            postArrayList = mListener.retrieveLatestFeed();
        }
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        public void storeLatestFeed(ArrayList<Post> postsList);
        public ArrayList<Post> retrieveLatestFeed();
    }

    /**
     * Converts the json into an arraylist of latest posts. Look at similar method in DashBoardFragment for more details
     * @param jsonPosts a json object that holds all the posts
     */
    private void populatePostList(JSONObject jsonPosts) {

        try {

            JSONArray postsJSONArray = jsonPosts.getJSONArray("posts");
            postArrayList.clear();

            int curIndex=0, N=postsJSONArray.length();

            while(curIndex<N) {
                JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);
                Log.d("jsonReturned", curObj.toString());
                Post curPost = Post.createPost(curObj);


                //which users have upvoted the post
                JSONArray upVotersJSONArray=curObj.getJSONArray("upVoters");

                //add all the voters in the posts voter list
                for(int i=0;i<upVotersJSONArray.length();i++){

                    Voter newVoter = new Voter(upVotersJSONArray.getJSONObject(i).getInt("userId"));
                    curPost.addVoterFromDB(newVoter, 1);
                }

                //which users have upvoted the post
                JSONArray downVotersJSONArray=curObj.getJSONArray("downVoters");
                //add all the voters in the posts voter list
                for(int i=0;i<downVotersJSONArray.length();i++){

                    Voter newVoter = new Voter(downVotersJSONArray.getJSONObject(i).getInt("userId"));
                    curPost.addVoterFromDB(newVoter, -1);
                }

                postArrayList.add(curPost);
            }

            //store latest post list in parent activity
            try {
                mListener.storeLatestFeed(postArrayList);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * redraws the listview by setting up the adapter and notifying it
     */
    private void populatePostListView(){
        adapter = new MyListAdapter();

        ListView list=(ListView)getView().findViewById(R.id.lvwPosts);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * An adapter to manage the listview
     */
    private class MyListAdapter extends ArrayAdapter<Post>{
        public MyListAdapter(){

            super(getActivity(), R.layout.home_post_item, postArrayList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.home_post_item, parent, false);
            }

            Voter curVoter = new Voter(Utility.CurrentUser.getId());


            //find the update to work with
            Post currentPost = postArrayList.get(position);

            //Get reference to all the variables and sets them up appropriately
            TextView problemType = (TextView) itemView.findViewById(R.id.lblPostProblemType);
            //problemType.setText(Utility.HazardTags.getHazardTags()[currentPost.getCategory()]);

            int categoryId = currentPost.getCategory();

            if(categoryId == -1)
                problemType.setText("Uncategorized Problem");
            else
                problemType.setText(Utility.CategoryList.get(categoryId));

            TextView postTime = (TextView) itemView.findViewById(R.id.lblPostTime);


            postTime.setText(Utility.CurrentUser.parsePostTime(currentPost.getTimeOfPost()));



            TextView formalLocation = (TextView) itemView.findViewById(R.id.lblPostProblemLocation);
            formalLocation.setText(currentPost.getFormalLocation());

            TextView informalLocation = (TextView) itemView.findViewById(R.id.lblPostProblemInformalLocation);
            informalLocation.setText(currentPost.getInformalLocation());

            TextView problemDescription = (TextView) itemView.findViewById(R.id.lblPostProblemDescription);
            problemDescription.setText(currentPost.getProblemDescription());

            TextView posterName = (TextView) itemView.findViewById(R.id.lblPostUsername);
            posterName.setText(currentPost.getUserName());

            TextView posterRating = (TextView) itemView.findViewById(R.id.lblPostUserRating);
            posterRating.setText(currentPost.getUserRating() + "");

            final TextView totalVote = (TextView) itemView.findViewById(R.id.lblPostTotalVote);

            showTotalVote(currentPost, totalVote);

            final ImageView voteUpView = (ImageView) itemView.findViewById(R.id.imgPostUp);
            final ImageView voteDownView = (ImageView) itemView.findViewById(R.id.imgPostDown);

            //If the user has already voted the post, change color accordingly
            checkIfAlreadyVotedAndChangeColorAccordingly(curVoter, currentPost, voteUpView, voteDownView);
            checkIfAlreadyVotedAndChangeColorAccordingly(curVoter, currentPost, voteUpView, voteDownView);

            //The user has upvoted the post
            voteUpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.imgPostUp) {
                        handleUpvoteView(position, voteUpView, voteDownView, totalVote);
                    }
                }
            });

            //the user has downvoted the post
            voteDownView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.imgPostDown) {
                        handleDownvoteView(position, voteUpView, voteDownView, totalVote);
                    }
                }
            });

            //sets up the image
            ImageView image = (ImageView) itemView.findViewById(R.id.lblPostProblemImage);
            byte[] imageBytes = currentPost.getImageBytes();
            Bitmap bMap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(bMap);

            return itemView;
        }
    }

    /**
     * Not used in this app
     * @param id
     * @return
     */

    private byte[] convertDrawableIntoByteArray(int id){

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return bArray;

    }


    /**
     * Not used in this app

     * @return
     */
    public void testHomePage() {
        postArrayList.clear();

        Post post1 = new Post(1,0, 2, "Gulshan", null, "BRAC University'r 3 goli pore", 1, "bishal gorto", 420, "Dec 4", 5, "Khan Shaheb", null);
        postArrayList.add(post1);

        Post post2 = new Post(1,0, 2, "Shantinagar", null, "Habibullah Bahar University'r 3 goli pore", 1, "majhari gorto, probably a majhari alien spaceship landed", 420, "Dec 4", 3, "Khan Shaheb", null);
        postArrayList.add(post2);

        Post post3 = new Post(1,0, 2, "Bashundhara", null, "North South University'r 3 goli pore", 1, "chhoto gorto, probably a chhoto alien spaceship landed", 420, "Dec 4", 10, "Khan Shaheb", null);
        postArrayList.add(post3);

        populatePostListView();
    }

    /**
     * sets up +/- sign and color depending on the number ofr votes a post has got
     * @param currentPost
     * @param totalVote
     */
    private void showTotalVote(Post currentPost, TextView totalVote) {
        int voteCount = (-1)*currentPost.getDownCount() + currentPost.getUpCount();

        if(voteCount>0)  {
            totalVote.setText("+" + voteCount);
            totalVote.setTextColor(Color.parseColor("#1E738F"));
        }
        else if(voteCount<0) {
            totalVote.setText("" + voteCount);
            totalVote.setTextColor(Color.RED);
        }
        else {
            totalVote.setText("" + voteCount);
            totalVote.setTextColor(Color.GRAY);
        }
    }


    /**
     * If the current user has voted the post, set color accordingly
     * @param curVoter
     * @param curPost
     * @param voteUpView
     * @param voteDownView
     */
    private void checkIfAlreadyVotedAndChangeColorAccordingly(Voter curVoter, Post curPost, ImageView voteUpView, ImageView voteDownView) {

        // Log.d("UpdateId-LikerId-LikeCount", curUpdate.getId() + "-" + curVoter.getLikerId() + "-" + curUpdate.getLikeCount());
//            Log.d("");
        if (curPost.hasTheUserUpvoted(curVoter)) {

            Log.d("Inside likeButton Color", "Yes");
            Log.d("Inside voteup Color", "Yes. User has voted up!");
//                voteUpView.setColorFilter(Color.parseColor(BLUE));
            voteUpView.setColorFilter(Color.parseColor(BLUE));
            voteDownView.setColorFilter(Color.parseColor(GREY));
//                voteDownView.setColorFilter(Color.parseColor("#000000"));
        }

        else if(curPost.hasTheUserDownvoted(curVoter)) {
            Log.d("Inside votedown Color", "Yes");                            //downvoted before, so change downView colour
            voteUpView.setColorFilter(Color.parseColor(GREY));
//                voteDownView.setColorFilter(Color.parseColor("#ffbfac"));
            voteDownView.setColorFilter(Color.parseColor(RED));
        }

        else {                                                              //user hasn't voted before
            Log.d("Inside default Color", "No votes from before");
            voteUpView.setColorFilter(Color.parseColor(GREY));
            voteDownView.setColorFilter(Color.parseColor(GREY));
        }
    }

    /**
     * Method is called when the user has pressed the upvote. It checks if the user has already voted. If yes, it does not
     * allow the user to vote again. If no, it adds up the user as a voter in the post and change the UI holding the vote number \
     * accordingly
     * @param index
     * @param voteUpView
     * @param voteDownView
     * @param totalVote
     */

    private void handleUpvoteView(int index, ImageView voteUpView, ImageView voteDownView, TextView totalVote) {
        Log.d("Upvoting", "Voter's id: " + Utility.CurrentUser.getId());

        Voter curVoter = new Voter(Utility.CurrentUser.getId());

        Post curPost = postArrayList.get(index);
        Log.d("Upvoting", "Poster id: " + curPost.getUserId());



        if(curPost.hasTheUserUpvoted(curVoter)) {           //if user has upvoted before... trying to upvote again
            //do nothing
        }
        else if(curPost.hasTheUserDownvoted(curVoter)) {          // if user has downvoted before...
            curPost.removeVoter(curVoter, -1);                   // remove previous vote
            voteDownView.setColorFilter(Color.parseColor(GREY));   // set voteDownView to default colour
            curPost.addVoter(curVoter, 1);                                  // add new upvote
            voteUpView.setColorFilter(Color.parseColor(BLUE));     // colour up voteUpView
            showTotalVote(curPost, totalVote);
            new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "1");
        }
        else {                                                              // if user has not voted before...
            curPost.addVoter(curVoter, 1);                                  // add new upvote
            voteUpView.setColorFilter(Color.parseColor(BLUE));              // colour up voteUpView
            showTotalVote(curPost, totalVote);
            new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "1");
        }
    }

    /**
     * Method is called when the user has pressed the upvote. It checks if the user has already voted. If yes, it does not
     * allow the user to vote again. If no, it adds up the user as a voter in the post and change the UI holding the vote number \
     * accordingly
     * @param index
     * @param voteUpView
     * @param voteDownView
     * @param totalVote
     */
    private void handleDownvoteView(int index, ImageView voteUpView, ImageView voteDownView, TextView totalVote) {
        Voter curVoter = new Voter(Utility.CurrentUser.getId());
        Post curPost = postArrayList.get(index);

        if(curPost.hasTheUserDownvoted(curVoter)) {           //if user has downvoted before... trying to downvote again
            //do nothing
        }
        else if(curPost.hasTheUserUpvoted(curVoter)) {          // if user has upvoted before...
            curPost.removeVoter(curVoter, 1);                   // remove previous vote
            voteDownView.setColorFilter(Color.parseColor(RED));   // colour up voteDownView
            curPost.addVoter(curVoter, -1);                                  // add new downvote
            voteUpView.setColorFilter(Color.parseColor(GREY));     // set voteUpView to default colour
            showTotalVote(curPost, totalVote);
            Log.d("totalVote", totalVote.getText().toString());
            new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "-1");
        }
        else {                                                              // if user has not voted before...
            curPost.addVoter(curVoter, -1);                                  // add new downvote
            voteUpView.setColorFilter(Color.parseColor(RED));                // colour up voteDownView
            showTotalVote(curPost, totalVote);
            new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "-1");
        }
    }


    /**
     * The user casts a vote through thsi task.Changes database accordingly
     */
    class SubmitVoteTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonVotingResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            params.add(new Pair("userId", args[0]));
            params.add(new Pair("postId", args[1]));
            params.add(new Pair("voteType", args[2]));

            jsonVotingResponse = jParser.makeHttpRequest("/submitvote", "POST", params);

            // Check your log cat for JSON reponse
            // Log.d("All info: ",jsonUpdates.toString());
            return null;

        }


        protected void onPostExecute (String a){

            if(jsonVotingResponse == null) {
//                Utility.CurrentUser.showConnectionError(UpdateFragment.context);
            }
        }
    }


    /**
     * Fetches all the posts surrounding  the current lat-long of the user
     */

    public class FetchHomePostsTask extends AsyncTask<String, Void, String> {

        public FetchHomePostsTask() {

        }

        private JSONObject jsonPosts;

        @Override
        protected void onPreExecute()
        {
//            progressLayout.setVisibility(View.VISIBLE);
//            customDiscussionListView.setVisibility(View.GONE);
            busy_sessions(true);
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

//            params.add(new Pair("locationId", 12));         //need to send lat and long
            params.add(new Pair("lat", mLastLocation.getLatitude()));
            params.add(new Pair("lon", mLastLocation.getLongitude()));
            // getting JSON string from URL
            jsonPosts = jParser.makeHttpRequest("/getallposts", "GET", params);

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
                busy_sessions(false);
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }

            Log.d("FetchPostsTask", "feed reloaded");

            //Then populate the arraylist again with latest posts
            try {
                populatePostList(jsonPosts);
            } catch (Exception e) {
                e.printStackTrace();
            }

            busy_sessions(false);

        }
//        private static ip ()
    }

    /**
     *  This method contacts the google location services and brings up the current lat-long
     */
    public synchronized void getLatLong() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Attempts to connect to the Google Services. onConnected is called upon success
        mGoogleApiClient.connect();
    }

    /**
     * Look at the ssame method in ReportProblem for more documentation
     * @param connectionHint
     */
    public void onConnected(Bundle connectionHint) {

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }catch(Exception e){
            e.printStackTrace();
        }

        //for debugging purposes
        if(mLastLocation==null){
            Toast.makeText(getActivity(), "Please check Location Service permission", Toast.LENGTH_LONG).show();
        }
        else if (mLastLocation != null) {
            // Toast.makeText(this,"Google client has returned",Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

//            Toast.makeText(getActivity(),"Google client has returned not null",Toast.LENGTH_LONG).show();
//            Toast.makeText(getActivity(),mLastLocation.getLatitude()+" "+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();


            //fetches up posts of the current location
            new FetchHomePostsTask().execute();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//    public void onFeedRefreshButtonClick(View v) {
//        getLatLong();
//    }

}
