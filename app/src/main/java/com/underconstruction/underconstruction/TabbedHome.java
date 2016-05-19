package com.underconstruction.underconstruction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the main activity of the app. Holds the three main fragments : HomeFragment,DashBoardFragment,PostsFragemnt.
 * THis is also used to initate the job of adding a report
 */
public class TabbedHome extends AppCompatActivity implements PostsSectionFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, DashboardFragment.OnFragmentInteractionListener {

    //A layout for arranging the fragments in TAB
    private TabLayout tabLayout;
    private ViewPager viewPager;


    //

    //all the posts in the surrounding area
    private ArrayList<Post> postsList;
    //all the posts in the users own profile
    private ArrayList<YourPosts> profilePostsList;
    //the object holding users current rating graph and rating point
    private UserRating userRating = null;

    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");

        context = getApplicationContext();
        DBHelper dbHelper = new DBHelper(context);
        int n = dbHelper.getDataForUser(Utility.CurrentUser.getUserId()).size();


        //device is online, so we will initiate a new intent to complete sending all the items in SQLite DB to main database
        if(!Utility.isAppIsInBackground(context) && n>0 && Utility.isOnline(context)) {
            Log.d("MainActivity", "internet available, app in foreground, number of saved reports: " + n);
            Intent newIntent = new Intent(context, ReportAutoUploadActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_home);


        context = getApplicationContext();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_home, menu);
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

    /**
     * Adds the three fragments on the currrent activity
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "HOME");
        adapter.addFragment(new PostsSectionFragment(), "FEED");
        adapter.addFragment(new DashboardFragment(), "PROFILE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     *
     * @return The current rating point and rating graph
     */
    @Override
    public UserRating getUserRating() {
        return userRating;
    }

    /**
     * Updates the rating of a user
     * @param userRating teh latest rating of a user fatched form database
     */

    @Override
    public void setUserRating(UserRating userRating) {
            this.userRating = userRating;
    }

    /**
     * stores the latest YourPosts of a users profile
     * @param postsList the lists of the lastest YourPosts
     */
    @Override
    public void storeLatestProfilePosts(ArrayList<YourPosts> postsList) {
        this.profilePostsList = postsList;
    }

    /**
     * Gets the most recent your posts
     * @return An arraylist containing the most recentYourPosts object
     */

    @Override
    public ArrayList<YourPosts> retrieveLatestProfilePosts() {
        return profilePostsList;
    }

    /**
     * Stores the latest feed fetched from database
     * @param postsList the list of all teh posts in the surrounding area
     */
    @Override
    public void storeLatestFeed(ArrayList<Post> postsList) {
        this.postsList = postsList;
    }

    /**
     *
     * @return An Arraylist of all the posts fetched from the surrounding area
     */
    @Override
    public ArrayList<Post> retrieveLatestFeed() {
        return postsList;
    }


    /**
     * A class to manage the easy transition between tabs
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    /**
     * This method is called when the user wants to add a new report
     * @param v the view of the context
     */

    public void onReportButtonClick(View v){
        //A new intent is opened to handle the job of adding report
        Intent intent =new Intent(this, ReportProblem.class);
        ReportProblem.camera=0;
        startActivity(intent);

    }


    /**
     * When the user wants to refresh the profile. Not implemented. Ma be done in future
     * @param v
     */

    public void onProfileRefreshButtonClick(View v) {

    }

}
