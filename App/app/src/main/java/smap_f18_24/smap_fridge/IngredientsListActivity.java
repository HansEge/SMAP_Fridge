package smap_f18_24.smap_fridge;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.IngredientsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.IngredientsListListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.fragment_details_tabs.DetailsActivity;

public class IngredientsListActivity extends AppCompatActivity {

    private boolean mBound = false;
    public ServiceUpdater mService;

    private ListView lv_ingredientsList, lv_shoppingLists;
    private Button btn_addToShoppingList, btn_addItem;
    private IngredientsListAdaptor ingredientsListAdaptor;

    public ShoppingListListAdaptor shoppingListListAdaptor;

    Fridge fridge;

    public String fridgeID;
    public int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceUpdaterReceiver,filter);

        Intent i = getIntent();

        fridgeID = i.getStringExtra("CurrentFridgeID");
        position = i.getIntExtra("PositionOfIngredientsList",0);

        lv_ingredientsList = findViewById(R.id.ingredientsList_lv_list);
        btn_addToShoppingList = findViewById(R.id.ingredientsListActivty_btn_addIngredients);
        btn_addItem = findViewById(R.id.btn_addItem_ingredients);

        btn_addToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(view);
            }
        });

        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        lv_ingredientsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item)ingredientsListAdaptor.getItem(i);
                openDeleteItemDialogBox(item.getName());
                return true;
            }
        });

        lv_ingredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item)ingredientsListAdaptor.getItem(i);
                Log.d("Lars", "onClick: clicked Item " + item.getName());
                openEditItemDialogBox(item);
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

            ingredientsListAdaptor = new IngredientsListAdaptor(getBaseContext(),mService.getFridge(fridgeID).getIngredientLists().get(position));
            lv_ingredientsList.setAdapter(ingredientsListAdaptor);

            fridge = mService.getFridge(fridgeID);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public void openDialog(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.shopping_list_dialog,null);

        lv_shoppingLists = row.findViewById(R.id.lv_dialog_shoppingList);
        lv_shoppingLists.setAdapter(new ShoppingListListAdaptor(this,(ArrayList<ShoppingList>) mService.getFridge(fridgeID).getShoppingLists()));
        builder.setView(row);

        lv_shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mService.UpdateShoppingListFromIngredientList(fridgeID,fridge.getIngredientLists().get(position),fridge.getShoppingLists().get(i));
                Toast toast = Toast.makeText(getBaseContext(), "Ingredients added to: " + (String)fridge.getShoppingLists().get(i).getName(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addItemDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText et_newItemName = new EditText(this);
        et_newItemName.setHint("Name:");
        et_newItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemName);

        final EditText et_newItemQuantity = new EditText(this);
        et_newItemQuantity.setHint("Quantity:");
        et_newItemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(et_newItemQuantity);

        final EditText et_newItemUnit = new EditText(this);
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
                mService.addItemToIngredientList(newItem,fridgeID,fridge.getIngredientLists().get(position).getName(),fridge.getIngredientLists().get(position).getID());

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
                mService.overWriteItemInIngredientList(overwriteItem,fridgeID,fridge.getIngredientLists().get(position).getName(),fridge.getIngredientLists().get(position).getID());
            }
        });

        ItemClickedDialog.show();
    }

    private void openDeleteItemDialogBox(String itemName){
        final String _itemName = itemName;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you wanna delete this item?");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mService.removeItemFromIngredientList(_itemName,fridge.getID(),fridge.getIngredientLists().get(position).getID());
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

    public void updateData(String updateString)
    {
        if(updateString.equals("DataUpdated"))
        {
            fridge = mService.getFridge(fridgeID);
            ingredientsListAdaptor = new IngredientsListAdaptor(getBaseContext(),mService.getFridge(fridgeID).getIngredientLists().get(position));
            lv_ingredientsList.setAdapter(ingredientsListAdaptor);
        }
    }

}
