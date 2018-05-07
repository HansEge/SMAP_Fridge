package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;


public class details_fragment_tab1_inventory extends Fragment {

    public TextView test;
    public Button btn_goBackToOverview;

    public String clickedFridgeID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab1_inventory, container, false);

        // INITIALIZING
        test = (TextView) v.findViewById(R.id.details_tab1_inventory_tv_sectionLabel);
        btn_goBackToOverview = (Button) v.findViewById(R.id.details_tab1_inventory_btn_backToOverView);

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");





        btn_goBackToOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return v;
    }



}
