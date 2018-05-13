package smap_f18_24.smap_fridge;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;

import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.fragment_details_tabs.DetailsActivity;

public class ShoppingListActivity extends AppCompatActivity {


    private ListView lv_shoppingList;
    private Fridge currentFridge;
    private ShoppingList currentList;
    private Button btn_newItem;

    String fridgeID;
    int position;

    private boolean mBound = false;
    public ServiceUpdater mService;

    public ShoppingListAdaptor adaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Intent i = getIntent();

        fridgeID = i.getStringExtra("CurrentFridgeID");
        position = i.getIntExtra("PositionOfShoppingList",0);

        lv_shoppingList = findViewById(R.id.shoppingList_lv_list);
        btn_newItem=findViewById(R.id.shoppingList_btn_newItem);

        //register to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceUpdaterReceiver,filter);

        btn_newItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewItemDialogBox();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ServiceUpdater.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("SYSTEM","Shutting down - onStop() in MainActivity");
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mService = binder.getService();
            Log.d("ServiceShopping","Service connected");
            mBound = true;


            currentFridge=mService.getFridge(fridgeID);
            currentList=currentFridge.getShoppingLists().get(position);

            adaptor = new ShoppingListAdaptor(getBaseContext(),currentList);
            lv_shoppingList.setAdapter(adaptor);
            lv_shoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Item i = (Item)adaptor.getItem(position);
                    openEditItemDialogBox(i);
                }
            });

            lv_shoppingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Item i = (Item)adaptor.getItem(position);
                    openDeleteItemDialogBox(i);
                    return true;
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void openEditItemDialogBox(final Item i)
    {
        AlertDialog.Builder ItemClickedDialog = new AlertDialog.Builder(this);
        ItemClickedDialog.setTitle(i.getName());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Quantity
        final EditText et_qty = new EditText(this);
        et_qty.setHint("Quantity");
        et_qty.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_qty.setText(String.valueOf((i.getQuantity())));
        layout.addView(et_qty);

        ItemClickedDialog.setView(layout);

        ItemClickedDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ItemClickedDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float quantity = Float.parseFloat(et_qty.getText().toString());
                Item overwriteItem = i;
                overwriteItem.setQuantity(quantity);
                mService.overWriteItemInShoppingList(overwriteItem,currentFridge.getID(),currentList.getName(),currentList.getID());
            }
        });

        ItemClickedDialog.show();
    }

    private void openNewItemDialogBox()
    {
        AlertDialog.Builder newItemDialog = new AlertDialog.Builder(this);
        newItemDialog.setTitle("New Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //item name
        final EditText et_ItemName = new EditText(this);
        et_ItemName.setHint("Name");
        et_ItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_ItemName);

        //Quantity
        final EditText et_qty = new EditText(this);
        et_qty.setHint("Quantity");
        et_qty.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(et_qty);

        //Unit
        final EditText et_Unit = new EditText(this);
        et_Unit.setHint("Unit");
        et_Unit.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_Unit);

        newItemDialog.setView(layout);

        newItemDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Get info from editTexts
                String itemName=et_ItemName.getText().toString();
                float Quantity = Float.parseFloat(et_qty.getText().toString());
                String unit = et_Unit.getText().toString();

                Item i = new Item(itemName,unit,Quantity,"None","Needed");
                mService.addItemToShoppingList(i,currentFridge.getID(),currentList.getName(),currentList.getID());
            }
        });

        newItemDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        newItemDialog.show();
    }

    private void openDeleteItemDialogBox(final Item item){
        final String _itemName = item.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you wanna delete this item?");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mService.removeItemFromShoppingList(_itemName,currentFridge.getID(),currentList.getID());
                Log.d("Broadcast Receiver", "Error in broadcast receiver");

            }
        });

        builder.setNeutralButton("Move to fridge", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove from shoppingList and add to inventory.
                mService.removeItemFromShoppingList(_itemName,currentFridge.getID(),currentList.getID());
                mService.addItemToInventory(item,fridgeID);
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

    //Updates UI by fetching new data from service, and resetting the adaptor.
    public void updateData(String updateString)
    {
        if(updateString.equals("DataUpdated"))
        {
            currentFridge = mService.getFridge(currentFridge.getID());
            currentList = currentFridge.getShoppingLists().get(position);
            adaptor = new ShoppingListAdaptor(this,currentList);
            lv_shoppingList.setAdapter(adaptor);
        }
    }
}
