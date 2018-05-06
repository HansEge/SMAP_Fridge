package smap_f18_24.smap_fridge;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smap_f18_24.smap_fridge.Adaptors.FridgeListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.fragment_details_tabs.DetailsActivity;

public class OverviewActivity extends AppCompatActivity {

    Button btn_addNewFridge, btn_addExistingFridge, btn_dirtyDetailsViewDetour;
    ListView lv_fridgesListView;
    TextView tv_welcomeUser;

    ServiceUpdater mService;
    private boolean mBound = false;
    private String broadcastResult;

    final public ArrayList<Fridge> debugList = new ArrayList<>();
    public FridgeListAdaptor adaptor1 = new FridgeListAdaptor(this, debugList);

    List<String> connectedUserEmailss;
    final public InventoryList inventoryList = new InventoryList();
    final public EssentialsList essentialList = new EssentialsList();

    List<ShoppingList> myShoppingLists = new ArrayList<ShoppingList>();
    List<IngredientList> myIngredientsLists = new ArrayList<IngredientList>();

    IngredientList myIngredientsList1 = new IngredientList("ingredientsListName","ingredientsListID");
    ShoppingList myShoppingList1 = new ShoppingList("shoppingListName","shoppingListID");

    Fridge testFridge = new Fridge("Tester", "testID", connectedUserEmailss, inventoryList, essentialList, myShoppingLists, myIngredientsLists);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        // INITIALIZING

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(this);


        //Start service
        Intent ServiceIntent = new Intent(OverviewActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        btn_addNewFridge = findViewById(R.id.overview_btn_addNewFridge);
        btn_addExistingFridge = findViewById(R.id.overview_btn_addExistingFridge);
        btn_dirtyDetailsViewDetour = findViewById(R.id.overview_btn_dirtyDetailsActivitydetour);


        lv_fridgesListView = findViewById(R.id.overview_lv_fridgesListView);


        tv_welcomeUser = findViewById(R.id.overview_tv_welcomeUser);

        debugList.add(testFridge);

        lv_fridgesListView.setAdapter(adaptor1);

        // POST-INITIALIZATION

        //A temporary solution to check detailsActivity - quick and dirty
        btn_dirtyDetailsViewDetour.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent detailsActivityIntent = new Intent(OverviewActivity.this,DetailsActivity.class);
                startActivity(detailsActivityIntent);
            }
        });

        //If the user wants to add a new fridge to the list
        btn_addNewFridge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){


                AlertDialog.Builder addNewFridgeDialogBox = new AlertDialog.Builder(OverviewActivity.this);
                addNewFridgeDialogBox.setTitle("Creating a new fridge");

                LinearLayout layout = new LinearLayout(OverviewActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText et_newFridgeName = new EditText(OverviewActivity.this);
                et_newFridgeName.setHint("Name:");
                et_newFridgeName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(et_newFridgeName);

                final EditText et_newFridgeID = new EditText(OverviewActivity.this);
                et_newFridgeID.setInputType(InputType.TYPE_CLASS_TEXT);
                et_newFridgeID.setHint("ID:");
                layout.addView(et_newFridgeID);

                addNewFridgeDialogBox.setView(layout);

                addNewFridgeDialogBox.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String tmp_name = et_newFridgeName.getText().toString();
                        String tmp_id = et_newFridgeID.getText().toString();

                        //User will get Toast message if the ID already exists.
                        mService.createNewFridge(tmp_id,tmp_name);

                        mService.SubscribeToFridge(tmp_id);

                        Fridge tmpFridge = mService.getFridge(tmp_id);

                        debugList.add(tmpFridge);

                        lv_fridgesListView.setAdapter(adaptor1);


                        //TODO - get data from database to Fridge listview - in other words --> global to local


                    }
                });

                addNewFridgeDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                addNewFridgeDialogBox.show();




            }
        });

        //If the user wants to add an existing fridge to the list - needs the UNIQUE code from another fridgeOwner
        btn_addExistingFridge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                final AlertDialog.Builder addExistingFridgeDialogBox = new AlertDialog.Builder(OverviewActivity.this);
                addExistingFridgeDialogBox.setTitle("Enter the UNIQUE code for the fridge you want to add");

                final EditText et_uniqueCodeUserInput = new EditText(OverviewActivity.this);

                et_uniqueCodeUserInput.setInputType(InputType.TYPE_CLASS_TEXT);
                addExistingFridgeDialogBox.setView(et_uniqueCodeUserInput);

                //Functionality of the right sided button - cancels the dialogbox
                addExistingFridgeDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //Functionality of the right sided button - cancels the dialogbox
                addExistingFridgeDialogBox.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //mService.SubscribeToFridge(et_uniqueCodeUserInput.getText().toString());

                        //TODO - subscribe to an existing fridge by using the UNIQUE ID

                    }
                });

                addExistingFridgeDialogBox.show();

            }
        });

        //User pressing a fridge on the listview --> go to DetailsActivity
        lv_fridgesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent detailsActivityIntent = new Intent(OverviewActivity.this, DetailsActivity.class);

                SharedPreferences.Editor sharedPrefsEditor = sharedData.edit();
                String tmpID = debugList.get(position).getName();
                sharedPrefsEditor.putString("clickedFridgeID",tmpID);
                sharedPrefsEditor.apply();

                detailsActivityIntent.putExtra("clickedFridgeID",tmpID);

                startActivity(detailsActivityIntent);


            }
        });

        //Ask user if they want to delete item if long-pressed
        lv_fridgesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final AlertDialog.Builder addExistingFridgeDialogBox = new AlertDialog.Builder(OverviewActivity.this);
                addExistingFridgeDialogBox.setTitle("Do you want to delete the fridge?");

                addExistingFridgeDialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO - unsubscribe the fridge from the users connected fridge list
                    }
                });

                addExistingFridgeDialogBox.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ServiceUpdater.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);

        //can use registerReceiver(...)
        //but using local broadcasts for this service:
        LocalBroadcastManager.getInstance(this).registerReceiver(ServiceUpdaterReceiver,filter);

    }

    private BroadcastReceiver ServiceUpdaterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast Receiver", "Broadcast reveiced from ServiceUpdater");
            String result = null;

            result = intent.getStringExtra(ServiceUpdater.EXTRA_TASK_RESULT);

            if (result == null) {
                Log.d("Broadcast Receiver", "Something went wrong in the broadcast receiver");
            }
            broadcastResult = result;
            Log.d("BROADCAST_RESULT",result);

        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            /*
            mService.setContext(getApplicationContext());

            mService.SubscribeToFridge("TestFridgeID");

            mService.SubscribeToFridge("TestFridge");
            */


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

}
