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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;


public class details_fragment_tab1_inventory extends Fragment {

    ListView lv_inventoryList;
    Button btn_addItem;

    InventoryList inventoryList = new InventoryList();
    InventoryListAdaptor inventoryListAdaptor;

    public String clickedFridgeID;
    private Fridge currentFridge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab1_inventory, container, false);

        //bind to broadcastManager
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver(serviceUpdaterReceiver,filter);

        // INITIALIZING
        lv_inventoryList = v.findViewById(R.id.lv_inventoryList_tap1);
        btn_addItem = v.findViewById(R.id.details_tap1_inventory_btn_addItem);

        //Gets clickedFridgeID through shared pref.
        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        clickedFridgeID = sharedData.getString(getString(R.string.CLICKED_FRIDGE_ID),"errorNoValue");

        //Button to add Item to inventory
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        // Edit quantity dialogbox when a item is clicked
        lv_inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item i = inventoryList.getItems().get(position);
                openEditItemDialogBox(i);
            }
        });

        //Delete Item when a item is long clicked
        lv_inventoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemName = inventoryList.getItems().get(i).getName();
                openDeleteItemDialogBox(itemName);

                return true;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //On create. If no inventorylist is created in this fridge, it automatically creates one.
        //Otherwise it uses the already created one.
        currentFridge = ((DetailsActivity)getActivity()).currentFridge;
        if(currentFridge==null)
        {
            inventoryList=new InventoryList();
        }
        else
        {
            inventoryList = currentFridge.getInventory();
        }
        inventoryListAdaptor = new InventoryListAdaptor(getActivity().getBaseContext(),inventoryList);

        lv_inventoryList.setAdapter(inventoryListAdaptor);
    }

    private void addItemDialog(){
        //Creates a addItem dialogbox.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.add_item);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText et_newItemName = new EditText(getContext());
        et_newItemName.setHint(R.string.DIALOG_HINT_name);
        et_newItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemName);

        final EditText et_newItemQuantity = new EditText(getContext());
        et_newItemQuantity.setHint(R.string.DIALOG_HINT_quantity);
        //Set input type as positive decimal.
        //https://stackoverflow.com/questions/6919360/how-do-i-restrict-my-edittext-input-to-numerical-possibly-decimal-and-signed-i?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        et_newItemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(et_newItemQuantity);

        final EditText et_newItemUnit = new EditText(getContext());
        et_newItemUnit.setHint(R.string.DIALOG_HINT_unit);
        et_newItemUnit.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemUnit);

        builder.setView(layout);


        builder.setPositiveButton(R.string.DIALOG_add_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String inputName = et_newItemName.getText().toString();
                float inputQuantity = Float.parseFloat(et_newItemQuantity.getText().toString());
                String inputUnit = et_newItemUnit.getText().toString();

                Item newItem = new Item(inputName, inputUnit,inputQuantity , "hejmeddig123@dibidut.au", "Status");
                ((DetailsActivity)getActivity()).mService.addItemToInventory(newItem,currentFridge.getID());

            }
        });
        builder.setNegativeButton(R.string.DIALOG_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }

    private void openDeleteItemDialogBox(String itemName){

        //A function that creates a "are you sure you wanna delete item" dialogbox
        final String _itemName = itemName;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.areYouSureYouWantToDeleteThisItem);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton(R.string.DIALOG_yes_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((DetailsActivity)getActivity()).mService.removeItemFromInventory(_itemName,currentFridge.getID());
                Log.d("Broadcast Receiver", "Error in broadcast receiver");

            }
        });
        builder.setNegativeButton(R.string.DIALOG_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    };

    private void openEditItemDialogBox(final Item i)
    {
        //A dialogbox to change the quantity of an already existing item.
        AlertDialog.Builder ItemClickedDialog = new AlertDialog.Builder(getActivity());
        ItemClickedDialog.setTitle(i.getName());

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        //Quantity
        final EditText et_qty = new EditText(getActivity());
        et_qty.setHint(R.string.DIALOG_HINT_quantity);
        //Set input type as positive decimal.
        //https://stackoverflow.com/questions/6919360/how-do-i-restrict-my-edittext-input-to-numerical-possibly-decimal-and-signed-i?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        et_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        et_qty.setText(String.valueOf((i.getQuantity())));
        layout.addView(et_qty);

        ItemClickedDialog.setView(layout);

        ItemClickedDialog.setNegativeButton(R.string.DIALOG_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ItemClickedDialog.setPositiveButton(R.string.DIALOG_apply_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float quantity = Float.parseFloat(et_qty.getText().toString());
                Item overwriteItem = i;
                overwriteItem.setQuantity(quantity);
                ((DetailsActivity)getActivity()).mService.overwriteItemInInventory(overwriteItem,currentFridge.getID());
            }
        });

        ItemClickedDialog.show();
    }

    // creates a new broadcastreceiver, to notify about changes.
    private BroadcastReceiver serviceUpdaterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ASDASD", "Broadcast reveiced from ServiceUpdater in tab1");
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

    //Updates date when the broadcast is received
    public void updateData(String updateString)
    {
        Log.d("Inventory","Inventory updated");
        if(updateString.equals(getString(R.string.DATA_UPDATED)) && currentFridge != null)
        {
            Log.d("Inventory","Inventory updated after if");
            ((DetailsActivity)getActivity()).currentFridge = ((DetailsActivity)getActivity()).mService.getFridge(currentFridge.getID());
            inventoryList = ((DetailsActivity)getActivity()).currentFridge.getInventory();
            inventoryListAdaptor = new InventoryListAdaptor(getActivity().getBaseContext(),inventoryList);
            lv_inventoryList.setAdapter(inventoryListAdaptor);
        }
    }


}
