package com.underconstruction.underconstruction;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostsSectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostsSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsSectionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Post> postArrayList = new ArrayList<Post>();
    private ListView customPostListView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
        return inflater.inflate(R.layout.fragment_posts_section, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        testHomePage();
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

    private void populatePostList(JSONObject jsonPosts) {

        try {
            JSONArray postsJSONArray = jsonPosts.getJSONArray("posts");
            postArrayList.clear();

            int curIndex=0, N=postsJSONArray.length();

            while(curIndex<N) {
                JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);
                Post curPost = Post.createPost(curObj);
                int upCount = curPost.getUpCount();
                int downCount = curPost.getDownCount();

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

            View itemView=convertView;
            if(itemView==null){
                itemView=getActivity().getLayoutInflater().inflate(R.layout.home_post_item,parent,false);
            }


            //find the update to work with
            Post currentPost= postArrayList.get(position);
            //fill the view

            TextView problemType = (TextView)itemView.findViewById(R.id.lblPostProblemType);
            problemType.setText(currentPost.getCategory()+"");

            TextView postTime = (TextView)itemView.findViewById(R.id.lblPostTime);
//            String timeOfPost = Utility.CurrentUser.parsePostTime(currentPost.getTimeOfPost());
            postTime.setText(currentPost.getTimeOfPost());

            TextView formalLocation = (TextView)itemView.findViewById(R.id.lblPostProblemLocation);
            formalLocation.setText(currentPost.getFormalLocation());

            TextView informalLocation = (TextView)itemView.findViewById(R.id.lblPostProblemInformalLocation);
            informalLocation.setText(currentPost.getInformalLocation());

            TextView problemDescription = (TextView)itemView.findViewById(R.id.lblPostProblemDescription);
            problemDescription.setText(currentPost.getProblemDescription());

            TextView posterName = (TextView)itemView.findViewById(R.id.lblPostUsername);
            posterName.setText(currentPost.getUserName());

            TextView posterRating = (TextView)itemView.findViewById(R.id.lblPostUserRating);
            posterRating.setText(currentPost.getUserRating()+"");

            int voteCount = (-1)*currentPost.getDownCount() + currentPost.getUpCount();

            TextView totalVote = (TextView)itemView.findViewById(R.id.lblPostTotalVote);
            totalVote.setText("" + voteCount);

            return itemView;
        }
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

}
