package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.ShoppingListActivity;


public class details_fragment_tab3_shoppinglists extends Fragment {

    private String clickedFridgeID;
    ServiceUpdater mService;
    private Fridge currentFridge;
    private ShoppingListListAdaptor adaptor;
    private ListView lv_shoppingListList;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentFridge = ((DetailsActivity)getActivity()).currentFridge;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab3_shoppinglists, container, false);

        //subscribe to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(serviceUpdaterReceiver,filter);

        mService = ((DetailsActivity)getActivity()).mService;
        lv_shoppingListList = v.findViewById(R.id.lv_shoppingListList_tab3);
        Button btn_goBackToOverview = (Button) v.findViewById(R.id.details_tap3_shoppinglists_btn_backToOverView);


        btn_goBackToOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");


        lv_shoppingListList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity().getApplicationContext(), ShoppingListActivity.class);

                String tmpID = ((DetailsActivity)getActivity()).currentFridge.getID();

                intent.putExtra("CurrentFridgeID",tmpID);
                intent.putExtra("PositionOfShoppingList",i);

                startActivity(intent);

            }
        });

        lv_shoppingListList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openClaimResponsibilityDialogBox((ShoppingList)adaptor.getItem(position));
                return true;
            }
        });



        adaptor = new ShoppingListListAdaptor(getActivity().getApplicationContext(),(ArrayList<ShoppingList>)((DetailsActivity)getActivity()).currentFridge.getShoppingLists());
        lv_shoppingListList.setAdapter(adaptor);

        return v;
    }

    private void openClaimResponsibilityDialogBox(final ShoppingList shoppingList){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to claim responsibility for this shopping list?");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mService.setResponsibilityForShoppingList(currentFridge.getID(),shoppingList.getID(),"user_email");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    };

    private BroadcastReceiver serviceUpdaterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ASDASD", "Broadcast reveiced from ServiceUpdater in tab2");
            String result = null;

            result = intent.getStringExtra(ServiceUpdater.EXTRA_TASK_RESULT);
            Log.d("ASDASD", result);

            if (result == null) {
                Log.d("ASDASD", result);
            }

            if(result != null) {
                updateData(result);
            }

        }
    };

    public void updateData(String updateString) {
        if (updateString.equals("DataUpdated")) {
            //get new data
            ((DetailsActivity) getActivity()).currentFridge = ((DetailsActivity) getActivity()).mService.getFridge("TestFridgeID");
            //reset adaptor
            adaptor = new ShoppingListListAdaptor(getActivity().getApplicationContext(), (ArrayList<ShoppingList>) ((DetailsActivity) getActivity()).currentFridge.getShoppingLists());
            lv_shoppingListList.setAdapter(adaptor);
        }
    }
}
