package smap_f18_24.smap_fridge;

import android.app.Activity;
import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.IngredientsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.IngredientsListListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;

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

        Intent i = getIntent();

        fridgeID = i.getStringExtra("CurrentFridgeID");
        position = i.getIntExtra("PositionOfShoppingList",0);

        lv_ingredientsList = findViewById(R.id.ingredientsList_lv_list);
        btn_addToShoppingList = findViewById(R.id.ingredientsListActivty_btn_addIngredients);
        btn_addItem = findViewById(R.id.btn_addItem_ingredients);

        btn_addToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mService.UpdateShoppingListFromIngredientList()); //TODO insert updateIngredients method
                //openSelectShoppingListDialogBox();
                openDialog(view);
            }
        });

        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item i = new Item("Sukker", "g", 1000, "hejmeddig123@dibidut.au", "Status");
                mService.addItemToIngredientList(i,fridgeID,"kage","Math_ID_kage");
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

            ingredientsListAdaptor = new IngredientsListAdaptor(getApplicationContext(),mService.getFridge(fridgeID).getIngredientLists().get(position));
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
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
