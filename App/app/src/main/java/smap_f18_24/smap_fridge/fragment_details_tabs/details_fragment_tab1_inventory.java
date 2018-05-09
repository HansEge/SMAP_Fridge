package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;


public class details_fragment_tab1_inventory extends Fragment {


    public Button btn_goBackToOverview;


    private Fridge fridge;

    List<String> connectedUserEmailss;
    InventoryList inventoryList = new InventoryList();
    EssentialsList essentialList = new EssentialsList();

    List<ShoppingList> myShoppingLists = new ArrayList<ShoppingList>();
    List<IngredientList> myIngredientsLists = new ArrayList<IngredientList>();

    Item kartoffel = new Item("katoffel", "kg", 1000, "hejmeddig123@dibidut.au", "Status");
    Item Tomat = new Item("Tomat", "kg", 100, "hejmeddig123@dibidut.au", "Status");
    Item Æg = new Item("Æg", "stk", 10, "hejmeddig123@dibidut.au", "Status");
    Item juice = new Item("Juice", "L", 2, "hejmeddig123@dibidut.au", "Status");

    public String clickedFridgeID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab1_inventory, container, false);

        // INITIALIZING

        btn_goBackToOverview = (Button) v.findViewById(R.id.details_tab1_inventory_btn_backToOverView);
        ListView lv_inventoryList = v.findViewById(R.id.lv_inventoryList_tap1);


        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");


        //fridge = ((DetailsActivity)getActivity()).mService.getFridge(((DetailsActivity)getActivity()).clickedFridgeID); //TODO fix ID
        //fridge = new Fridge("Tester", "testID", connectedUserEmailss, inventoryList, essentialList, myShoppingLists, myIngredientsLists);

        inventoryList.AddItem(kartoffel);
        inventoryList.AddItem(Tomat);
        inventoryList.AddItem(Æg);
        inventoryList.AddItem(juice);

        InventoryListAdaptor inventoryListAdaptor = new InventoryListAdaptor(getActivity().getApplicationContext(),inventoryList);

        lv_inventoryList.setAdapter(inventoryListAdaptor);


        btn_goBackToOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return v;
    }



}
