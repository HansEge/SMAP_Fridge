package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;


//Fragment dividing inspired by https://www.youtube.com/watch?v=00LLd7qr9sA
public class DetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static boolean mBound = false;

    public Fridge currentFridge;
    public ServiceUpdater mService;
    public String clickedFridgeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_details);

        // Bind to LocalService
        Intent intent = new Intent(this, ServiceUpdater.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        //Get the intent passed along from OverviewActivity
        String bob = getIntent().getStringExtra(getString(R.string.CLICKED_FRIDGE_ID));
        Toast.makeText(this, bob, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return false; //To remove bar and 3 dots
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    details_fragment_tab1_inventory tab1_inventory = new details_fragment_tab1_inventory();
                    return tab1_inventory;
                case 1:
                    details_fragment_tab2_essentials tab2_essentials = new details_fragment_tab2_essentials();
                    return tab2_essentials;
                case 2:
                    details_fragment_tab3_shoppinglists tab3_shoppinglists = new details_fragment_tab3_shoppinglists();
                    return tab3_shoppinglists;
                case 3:
                    details_fragment_tab4_ingredients tab4_ingredients = new details_fragment_tab4_ingredients();
                    return tab4_ingredients;

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return getString(R.string.PAGE_TITLE_INVENTORY);
                case 1:
                    return getString(R.string.PAGE_TITLE_ESSENTIALS);
                case 2:
                    return getString(R.string.PAGE_TITLE_SHOPPING_LISTS);
                case 3:
                    return getString(R.string.PAGE_TITLE_INGREDIENTS);
            }

            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            clickedFridgeID=getIntent().getStringExtra(getString(R.string.CLICKED_FRIDGE_ID));
            currentFridge = mService.getFridge(clickedFridgeID);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SYSTEM","Shutting down - onStop() in MainActivity");
        unbindService(mConnection);
    }



}
