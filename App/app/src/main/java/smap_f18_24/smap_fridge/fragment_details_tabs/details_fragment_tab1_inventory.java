package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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

import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;


public class details_fragment_tab1_inventory extends Fragment {


    private Fridge fridge;

    Button btn_addItem;

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
        ListView lv_inventoryList = v.findViewById(R.id.lv_inventoryList_tap1);


        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        clickedFridgeID = sharedData.getString("clickedFridgeID","errorNoValue");

        btn_addItem = v.findViewById(R.id.details_tap1_inventory_btn_addItem);
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        lv_inventoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                String itemName = inventoryList.getItems().get(i).getName();
                inventoryList.RemoveItem(itemName);

                //editItemDialog();

                return true;
            }
        });

        //fridge = ((DetailsActivity)getActivity()).mService.getFridge(((DetailsActivity)getActivity()).clickedFridgeID); //TODO fix ID
        //fridge = new Fridge("Tester", "testID", connectedUserEmailss, inventoryList, essentialList, myShoppingLists, myIngredientsLists);

        inventoryList.AddItem(kartoffel);
        inventoryList.AddItem(Tomat);
        inventoryList.AddItem(Æg);
        inventoryList.AddItem(juice);

        InventoryListAdaptor inventoryListAdaptor = new InventoryListAdaptor(getActivity().getApplicationContext(),inventoryList);

        lv_inventoryList.setAdapter(inventoryListAdaptor);


        return v;
    }

    private void addItemDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add new Item");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText et_newItemName = new EditText(getContext());
        et_newItemName.setHint("Name:");
        et_newItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemName);

        final EditText et_newItemQuantity = new EditText(getContext());
        et_newItemQuantity.setHint("Quantity:");
        et_newItemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(et_newItemQuantity);

        final EditText et_newItemUnit = new EditText(getContext());
        et_newItemUnit.setHint("Unit:");
        et_newItemUnit.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemUnit);

        builder.setView(layout);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String inputName = et_newItemName.getText().toString();
                float inputQuantity = Float.parseFloat(et_newItemQuantity.getText().toString());
                String inputUnit = et_newItemUnit.getText().toString();

                Item newItem = new Item(inputName, inputUnit,inputQuantity , "hejmeddig123@dibidut.au", "Status");
                inventoryList.AddItem(newItem);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }


    private void editItemDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Item Quantity or delete Item");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText et_newItemQuantity = new EditText(getContext());
        et_newItemQuantity.setHint("New quantity:");
        et_newItemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(et_newItemQuantity);


        builder.setView(layout);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String itemName = inventoryList.getItems().get(i).getName();
                //inventoryList.RemoveItem(itemName);
                //float inputQuantity = Float.parseFloat(et_newItemQuantity.getText().toString());
                Log.d("Broadcast Receiver", "Error in broadcast receiver");

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }


}
