package smap_f18_24.smap_fridge.fragment_details_tabs;

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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;



public class details_fragment_tab3_shoppinglists extends Fragment {

    private String clickedFridgeID;
    private Fridge fridge;

    private ListView lv_shoppingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab3_shoppinglists, container, false);

        ListView lv_shoppingListList = v.findViewById(R.id.lv_shoppingListList_tab3);
        Button btn_goBackToOverview = (Button) v.findViewById(R.id.details_tap3_shoppinglists_btn_backToOverView);


        btn_goBackToOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");

        fridge = ((DetailsActivity)getActivity()).mService.getFridge(clickedFridgeID);

        ShoppingListListAdaptor adaptor = new ShoppingListListAdaptor(getActivity().getApplicationContext(),(ArrayList<ShoppingList>)fridge.getShoppingLists());

        lv_shoppingListList.setAdapter(adaptor);

        return v;
    }
}
