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
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;



public class details_fragment_tab3_shoppinglists extends Fragment {

    private String clickedFridgeID;
    private Fridge fridge;

    private ListView lv_shoppingList;


    List<String> connectedUserEmailss;
    InventoryList inventoryList = new InventoryList();
    EssentialsList essentialList = new EssentialsList();

    List<ShoppingList> myShoppingLists = new ArrayList<ShoppingList>();
    List<IngredientList> myIngredientsLists = new ArrayList<IngredientList>();


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


        ShoppingList s = new ShoppingList("hh","123");

        myShoppingLists.add(s);

        //fridge = ((DetailsActivity)getActivity()).mService.getFridge(((DetailsActivity)getActivity()).clickedFridgeID); //TODO fix ID
        fridge = new Fridge("Tester", "testID", connectedUserEmailss, inventoryList, essentialList, myShoppingLists, myIngredientsLists);


        ShoppingListListAdaptor adaptor = new ShoppingListListAdaptor(getActivity().getApplicationContext(),(ArrayList<ShoppingList>)fridge.getShoppingLists());

        lv_shoppingListList.setAdapter(adaptor);

        return v;
    }
}
