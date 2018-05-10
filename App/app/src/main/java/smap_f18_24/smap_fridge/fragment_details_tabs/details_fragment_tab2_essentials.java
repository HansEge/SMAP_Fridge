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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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

import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;


public class details_fragment_tab2_essentials extends Fragment {

    private ListView essentialList;
    private EssentialsList EList = new EssentialsList();

    private String clickedFridgeID;
    private Fridge fridge;

    public EssentialsListAdaptor adaptor;

    Item kartoffel = new Item("katoffel", "kg", 1000, "hejmeddig123@dibidut.au", "Status");
    Item Tomat = new Item("Tomat", "kg", 100, "hejmeddig123@dibidut.au", "Status");
    Item Æg = new Item("Æg", "stk", 10, "hejmeddig123@dibidut.au", "Status");
    Item juice = new Item("Juice", "L", 2, "hejmeddig123@dibidut.au", "Status");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){


        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(serviceUpdaterReceiver,filter);

        View v = inflater.inflate(R.layout.fragment_details_tab2_essentials, container, false);

        EList.AddItem(kartoffel);
        EList.AddItem(Tomat);
        EList.AddItem(Æg);
        EList.AddItem(juice);

        fridge = ((DetailsActivity)getActivity()).mService.getFridge("TestFridgeID"); //TODO fix ID

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");

        essentialList = v.findViewById(R.id.lv_essential_tab2);

        adaptor = new EssentialsListAdaptor(getActivity().getApplicationContext(),((DetailsActivity)getActivity()).currentFridge.getEssentials());

        essentialList.setAdapter(adaptor);

        return v;
    }

    private BroadcastReceiver serviceUpdaterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ASDASD", "Broadcast reveiced from ServiceUpdater in tab2");
            String result = null;

            result = intent.getStringExtra(ServiceUpdater.EXTRA_TASK_RESULT);

            if (result == null) {
                Log.d("ASDASD", "Something went wrong in the broadcast receiver");
            }

            if(result != null) {
                updateData(result);
            }

        }
    };

   public void updateData(String updateString)
   {
       if(updateString.equals("DataUpdated"))
       {
           ((DetailsActivity)getActivity()).currentFridge = ((DetailsActivity)getActivity()).mService.getFridge("TestFridgeID");
           adaptor.notifyDataSetChanged();
       }
   }
}

