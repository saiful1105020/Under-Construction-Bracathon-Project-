package com.underconstruction.underconstruction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Home extends AppCompatActivity implements PostsSectionFragment.OnFragmentInteractionListener{

    public String postFragmentTag = "POST_FRAGMENT";
    public String dashboardFragmentTag = "DASHBOARD_FRAGMENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        addPostFragment();
        addPostFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void removeAddedFragment(String exception){
        Fragment postsFragment = getFragmentManager().findFragmentByTag(postFragmentTag);
        Fragment dashboardFragment = getFragmentManager().findFragmentByTag(dashboardFragmentTag);

        FragmentManager fragmentManager= getFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        if(postsFragment!=null)fragmentTransaction.remove(postsFragment);
        if(dashboardFragment!=null)fragmentTransaction.remove(dashboardFragment);

        fragmentTransaction.commit();
    }

    public void onHomeButtonClick(View v){
        if(v.getId()==R.id.lblHome){
//            ((TextView)findViewById(R.id.lblHome)).setTextColor(Color.parseColor("#A5DF00"));

            removeAddedFragment(null);
            addPostFragment();
//            showSearchMenu(true);
        }
    }

    public void onProfileButtonClick(View v){
        if(v.getId()==R.id.lblHomeProfile){
//            ((TextView)findViewById(R.id.lblHomeProfile)).setTextColor(Color.parseColor("#A5DF00"));

            removeAddedFragment(null);
            addDashboardFragment();
//            showSearchMenu(true);
        }
    }

    public void onReportButtonClick(View v){
        Intent intent =new Intent(Home.this,AddReport.class);
        startActivity(intent);
    }

    public void onHelpButtonClick(View v){
        if(v.getId()==R.id.lblHomeHelp){
            return;
        }
    }


    private void addPostFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PostsSectionFragment postFragment = new PostsSectionFragment();

        fragmentTransaction.add(R.id.dataFragmentContainer, postFragment,postFragmentTag);
        fragmentTransaction.commit();
    }

    private void addDashboardFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DashboardFragment postFragment = new DashboardFragment();

        fragmentTransaction.add(R.id.dataFragmentContainer, postFragment,postFragmentTag);
        fragmentTransaction.commit();
    }
}
