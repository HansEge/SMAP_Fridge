package smap_f18_24.smap_fridge;

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

    private ListView lv_ingredientsList;
    private Button btn_addToShoppingList;
    private IngredientsListAdaptor ingredientsListAdaptor;

    public ListView lv_shoppingLists;
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

        btn_addToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mService.UpdateShoppingListFromIngredientList()); //TODO insert updateIngredients method
                OpenSelectShoppingListDialogBox();
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

    //inspired by https://stackoverflow.com/questions/24769733/android-listview-in-alertdialog-setting-adapter
    private void OpenSelectShoppingListDialogBox()
    {
        LayoutInflater shoppingListInflator = LayoutInflater.from(this);
        final View addToShoppingListView = shoppingListInflator.inflate(R.layout.shopping_list_dialog, null);
        //final AlertDialog AddToShoppingList = new AlertDialog.Builder(this).create();

        lv_shoppingLists = addToShoppingListView.findViewById(R.id.lv_dialog_shoppingList);

        shoppingListListAdaptor = new ShoppingListListAdaptor(getApplicationContext(),(ArrayList<ShoppingList>)fridge.getShoppingLists());

        lv_shoppingLists.setAdapter(shoppingListListAdaptor);

        AlertDialog.Builder listsDialog = new AlertDialog.Builder(this);

        listsDialog.setView(addToShoppingListView);

        listsDialog.setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertD = listsDialog.create();

        alertD.show();
    }




}
