package smap_f18_24.smap_fridge.fragment_details_tabs;

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

import smap_f18_24.smap_fridge.R;

// TODO - Missing link to context in order to findView by R.id. for example


public class details_fragment_tab2_essentials extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab2_essentials, container, false);

        TextView test = (TextView) v.findViewById(R.id.details_tab2_essentials_tv_sectionLabel);



        return v;
    }
}
