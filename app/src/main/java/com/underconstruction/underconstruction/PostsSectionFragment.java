package com.underconstruction.underconstruction;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
 * {@link PostsSectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostsSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsSectionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    private static final String GREY = "#c0bfac";
    private static final String BLUE = "#16586E";
    private static final String RED = "#D90D10";

    private ArrayList<Post> postArrayList = new ArrayList<Post>();
    private ListView customPostListView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View v;

    private OnFragmentInteractionListener mListener;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_posts_section, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        populatePostList(new JSONObject());
//        populatePostListView();
        getLatLong();
//        testHomePage();
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
    }

    private void populatePostList(JSONObject jsonPosts) {

//        Post post1 = new Post(1, 0, "Kamalapur, Dhaka", convertDrawableIntoByteArray(R.drawable.dustbin_image), "Behind the Railstation", 1, "Very smelly dustbin out in the open. Very unhealthy", 1510, "2:20pm Dec 16", 5, "Onix", null);
//        postArrayList.add(post1);
//        Post post2 = new Post(2, -2, "Mohakhali, Dhaka", convertDrawableIntoByteArray(R.drawable.manhole_image), "Under flyover", 2, "Risky!", 1200, "12:10pm Dec 16", 6, "Saif", null);
////        postArrayList.add(post2);
//        Post post3 = new Post(3, 3, "Baridhara, Dhaka", convertDrawableIntoByteArray(R.drawable.wire_image), "Beside rail crossing", 3, "God knows when the place catches fire...", 550, "10:45am Dec 16", 0, "Wasif", null);
//        postArrayList.add(post3);
        try {
            JSONArray postsJSONArray = jsonPosts.getJSONArray("posts");
            postArrayList.clear();

            int curIndex=0, N=postsJSONArray.length();

            while(curIndex<N) {
                JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);
                Log.d("jsonReturned", curObj.toString());
                Post curPost = Post.createPost(curObj);
//                int upCount = curPost.getUpCount();
//                int downCount = curPost.getDownCount();

                JSONArray upVotersJSONArray=curObj.getJSONArray("upVoters");

                for(int i=0;i<upVotersJSONArray.length();i++){

                    Voter newVoter = new Voter(upVotersJSONArray.getJSONObject(i).getInt("userId"));
                    curPost.addVoterFromDB(newVoter, 1);
                }

                JSONArray downVotersJSONArray=curObj.getJSONArray("downVoters");

                for(int i=0;i<downVotersJSONArray.length();i++){

                    Voter newVoter = new Voter(downVotersJSONArray.getJSONObject(i).getInt("userId"));
                    curPost.addVoterFromDB(newVoter, -1);
                }

                postArrayList.add(curPost);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populatePostListView(){
        ArrayAdapter<Post> adapter = new MyListAdapter();

        ListView list=(ListView)getView().findViewById(R.id.lvwPosts);
        list.setAdapter(adapter);
    }

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
            //fill the view

            TextView problemType = (TextView) itemView.findViewById(R.id.lblPostProblemType);
            problemType.setText(Utility.HazardTags.getHazardTags()[currentPost.getCategory()]);

            TextView postTime = (TextView) itemView.findViewById(R.id.lblPostTime);
//            String timeOfPost = Utility.CurrentUser.parsePostTime(currentPost.getTimeOfPost());
            postTime.setText(Utility.CurrentUser.parsePostTime(currentPost.getTimeOfPost()));
//            postTime.setText(currentPost.getTimeOfPost());

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

            checkIfAlreadyVotedAndChangeColorAccordingly(curVoter, currentPost, voteUpView, voteDownView);
            checkIfAlreadyVotedAndChangeColorAccordingly(curVoter, currentPost, voteUpView, voteDownView);

            voteUpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.imgPostUp) {
                        handleUpvoteView(position, voteUpView, voteDownView, totalVote);
                    }
                }
            });

            voteDownView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.imgPostDown) {
                        handleDownvoteView(position, voteUpView, voteDownView, totalVote);
                    }
                }
            });

            ImageView image = (ImageView) itemView.findViewById(R.id.lblPostProblemImage);
            byte[] imageBytes = currentPost.getImageBytes();
            Bitmap bMap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(bMap);

            return itemView;
        }
    }


    private byte[] convertDrawableIntoByteArray(int id){

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return bArray;

    }


    public void testHomePage() {
        postArrayList.clear();

        Post post1 = new Post(0, 2, "Gulshan", null, "BRAC University'r 3 goli pore", 1, "bishal gorto", 420, "Dec 4", 5, "Khan Shaheb", null);
        postArrayList.add(post1);

        Post post2 = new Post(0, 2, "Shantinagar", null, "Habibullah Bahar University'r 3 goli pore", 1, "majhari gorto, probably a majhari alien spaceship landed", 420, "Dec 4", 3, "Khan Shaheb", null);
        postArrayList.add(post2);

        Post post3 = new Post(0, 2, "Bashundhara", null, "North South University'r 3 goli pore", 1, "chhoto gorto, probably a chhoto alien spaceship landed", 420, "Dec 4", 10, "Khan Shaheb", null);
        postArrayList.add(post3);

        populatePostListView();
    }

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
        else
            totalVote.setText("" + voteCount);
    }


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

    private void handleUpvoteView(int index, ImageView voteUpView, ImageView voteDownView, TextView totalVote) {
        Voter curVoter = new Voter(Utility.CurrentUser.getId());
        Post curPost = postArrayList.get(index);

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

//    private void handleVoteView(int index, ImageView voteUpView, ImageView voteDownView, TextView totalVote, int voteType) {
//
//        Voter curVoter = new Voter(Utility.CurrentUser.getId());
//        Post curPost = postArrayList.get(index);
//
//        if(curPost.hasTheUserUpvoted(curVoter)) {              // if user has upvoted before...
//
//            if(voteType>0) {                                            // trying to upvote again
//                if (curPost.hasTheUserDownvoted(curVoter)) {             // if user has downvoted before
//                    curPost.removeVoter(curVoter, -1);                   // remove previous vote
//                    voteDownView.setColorFilter(Color.parseColor(GREY));   // set voteDownView to default colour
//                    curPost.addVoter(curVoter, 1);                                  // add new upvote
//                    voteUpView.setColorFilter(Color.parseColor(BLUE));     // colour up voteUpView
//                    showTotalVote(curPost, totalVote);
//                    new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "1");
//                }
//
//                else if(curPost.hasTheUserUpvoted(curVoter)) {             // user has upvoted before, yet trying to upvote
//                    //do nothing
//                }
//
//                else {                                                      // user has not ever voted this post
//                    curPost.addVoter(curVoter, 1);                                  // add new upvote
//                    voteUpView.setColorFilter(Color.parseColor(BLUE));     // colour up voteUpView
//                    showTotalVote(curPost, totalVote);
//                    new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "1");
//                }
//            }
//
//            else {                                                       //trying to downvote
//                if (curPost.hasTheUserUpvoted(curVoter)) {             // if user has upvoted before
//                    curPost.removeVoter(curVoter, 1);                   // remove previous vote
//                    voteDownView.setColorFilter(Color.parseColor(RED));   // colour up voteDownView
//                    curPost.addVoter(curVoter, -1);                                  // add new downvote
//                    voteUpView.setColorFilter(Color.parseColor(GREY));     // set voteUpView to default colour
//                    showTotalVote(curPost, totalVote);
//                    Log.d("totalVote", totalVote.getText().toString());
//                    new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "-1");
//                }
//
//                else if(curPost.hasTheUserDownvoted(curVoter)) {             // user has downvoted before, yet trying to downvote
//                    //do nothing
//                }
//
//                else {                                                      // user has not ever voted this post
//                    curPost.addVoter(curVoter, -1);                                  // add new downvote
//                    voteUpView.setColorFilter(Color.parseColor(RED));     // colour up voteDownView
//                    showTotalVote(curPost, totalVote);
//                    new SubmitVoteTask().execute(Utility.CurrentUser.getUserId(), "" + curPost.getPostId(), "-1");
//                }
//            }
//        }
//    }

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

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String a){
            if(jsonVotingResponse == null) {
//                Utility.CurrentUser.showConnectionError(UpdateFragment.context);
            }
        }
    }


    class FetchHomePostsTask extends AsyncTask<String, Void, String> {

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
                Log.d("Connection Error", "Probably couldn't connect to the internet");
                return;
            }
//            Home.removeIPEditText();

            //jsonUpdatesField=jsonPosts;
            populatePostList(jsonPosts);
            populatePostListView();


        }
//        private static ip ()
    }

    public synchronized void getLatLong() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Toast.makeText(this,"google map client",Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    public void onConnected(Bundle connectionHint) {
        //Log.d("google map client", "returned");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation==null){
            Toast.makeText(getActivity(), "Google client has returned null", Toast.LENGTH_LONG).show();
        }
        else if (mLastLocation != null) {
            // Toast.makeText(this,"Google client has returned",Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(getActivity(),"Google client has returned not null",Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),mLastLocation.getLatitude()+" "+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();
            new FetchHomePostsTask().execute();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
