package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.FridgeListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.IngredientsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;

public class DebugShoppingListActivity extends AppCompatActivity {

    ListView Lv_Shoppinglist;
    final public ArrayList<Item>  debugList = new ArrayList<>();
    final public EssentialsList essentialList = new EssentialsList();
    final public InventoryList inventoryList = new InventoryList();
    final public IngredientList debugList1 = new IngredientList();
    final public ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
    //public ShoppingListAdaptor adaptor1 = new ShoppingListAdaptor(this, debugList);
    public EssentialsListAdaptor adaptor2 = new EssentialsListAdaptor(this, essentialList);
    public InventoryListAdaptor adaptor3 = new InventoryListAdaptor(this, inventoryList);
    public IngredientsListAdaptor adaptor4 = new IngredientsListAdaptor(this, debugList1);
    public ShoppingListListAdaptor adaptor5 = new ShoppingListListAdaptor(this,shoppingLists);

    Item kartoffel = new Item("katoffel", "kg", 1000, "hejmeddig123@dibidut.au", "Status");
    Item Tomat = new Item("Tomat", "kg", 100, "hejmeddig123@dibidut.au", "Status");
    Item Æg = new Item("Æg", "stk", 10, "hejmeddig123@dibidut.au", "Status");
    Item juice = new Item("Juice", "L", 2, "hejmeddig123@dibidut.au", "Status");

    ServiceUpdater mConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_shopping_list);


        debugList.add(kartoffel);
        debugList.add(Tomat);
        debugList.add(Æg);
        debugList.add(juice);

        essentialList.AddItem(kartoffel);
        essentialList.AddItem(Tomat);
        essentialList.AddItem(Æg);
        essentialList.AddItem(juice);

        inventoryList.AddItem(kartoffel);
        inventoryList.AddItem(Tomat);
        inventoryList.AddItem(Æg);
        inventoryList.AddItem(juice);

        debugList1.AddItem(kartoffel);
        debugList1.AddItem(Tomat);
        debugList1.AddItem(Æg);
        debugList1.AddItem(juice);

        shoppingLists.add(new ShoppingList("hej","e"));
        shoppingLists.add(new ShoppingList("123","ee"));
        shoppingLists.add(new ShoppingList("sef","eee"));


        Lv_Shoppinglist = findViewById(R.id.debug_Lv_Shoppinglist);

        //Lv_Shoppinglist.setAdapter(adaptor1);

        Lv_Shoppinglist.setAdapter(adaptor5);

        Lv_Shoppinglist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                setQuantityDialog();
                return true;
            }
        });
    }

//quantity
    private void setQuantityDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set quantity");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String inputResult = input.getText().toString();


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


//Connecting to service

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mConnection = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    protected void onStart(){
        super.onStart();

        Intent intent = new Intent(this,ServiceUpdater.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onStop(){
        super.onStop();
        unbindService(serviceConnection);
    }


}
