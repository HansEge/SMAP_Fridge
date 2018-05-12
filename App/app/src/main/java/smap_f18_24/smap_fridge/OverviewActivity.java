package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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

    Button btn_addNewFridge, btn_addExistingFridge;
    ListView lv_fridgesListView;
    TextView tv_welcomeUser;

    ServiceUpdater mService;
    private boolean mBound = false;

    final public ArrayList<Fridge> debugList = new ArrayList<>();
    public FridgeListAdaptor adaptor1 = new FridgeListAdaptor(this, debugList);

    List<String> connectedUserEmailss;
    final public InventoryList inventoryList = new InventoryList();
    final public EssentialsList essentialList = new EssentialsList();

    List<ShoppingList> myShoppingLists = new ArrayList<ShoppingList>();
    List<IngredientList> myIngredientsLists = new ArrayList<IngredientList>();

    IngredientList myIngredientsList1 = new IngredientList("ingredientsListName","ingredientsListID");
    ShoppingList myShoppingList1 = new ShoppingList("shoppingListName","shoppingListID");


    public Fridge testFridge = new Fridge("Tester", "testID", connectedUserEmailss, inventoryList, essentialList, myShoppingLists, myIngredientsLists);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // INITIALIZING
        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(this);


        ShoppingList s = new ShoppingList("hh","123");

        myShoppingLists.add(s);


        //Start service
        Intent ServiceIntent = new Intent(OverviewActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        btn_addNewFridge = findViewById(R.id.overview_btn_addNewFridge);
        btn_addExistingFridge = findViewById(R.id.overview_btn_addExistingFridge);

        lv_fridgesListView = findViewById(R.id.overview_lv_fridgesListView);

        tv_welcomeUser = findViewById(R.id.overview_tv_welcomeUser);
        tv_welcomeUser.setText("Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        debugList.add(testFridge);

        lv_fridgesListView.setAdapter(adaptor1);

        // POST-INITIALIZATION

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

                        //Test with local data fridge
                        mService.createNewFridge(tmp_id,tmp_name);
                        mService.SubscribeToFridge(tmp_id);
                        //TODO: Add fridge to list of subscribed fridges for user.
                        // mService.addFridgeIDtoListOfSubscribedFridges("USER_EMAIL",tmp_id);

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

                        //Trying to find the database fridge and put it into the adaptor which presents it to the user
                        String existingFridgeID = et_uniqueCodeUserInput.getText().toString();

                        mService.SubscribeToFridge(existingFridgeID);

                        Fridge existingFridge = mService.getFridge(existingFridgeID);

                        debugList.add(existingFridge);

                        lv_fridgesListView.setAdapter(adaptor1);



                    }
                });

                addExistingFridgeDialogBox.show();

            }
        });

        //User pressing a fridge on the listview --> go to DetailsActivity
        lv_fridgesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent DetailsActivityIntent = new Intent(OverviewActivity.this, DetailsActivity.class);

                String tmpID = debugList.get(position).getID();


                // Umiddelbart skal dette ikke længere bruges, hvis Intent passer ID videre til detailsActivity, hvorefter det håndteres af fragmentManager
                SharedPreferences.Editor sharedPrefsEditor = sharedData.edit();
                sharedPrefsEditor.putString("clickedFridgeID",tmpID);
                sharedPrefsEditor.apply();

                DetailsActivityIntent.putExtra("clickedFridgeID",tmpID);

                startActivity(DetailsActivityIntent);


            }
        });

        //Ask user if they want to delete item if long-pressed
        lv_fridgesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String tmpFridgeID = debugList.get(i).getID();

                AlertDialog.Builder dialogB = new AlertDialog.Builder(OverviewActivity.this);
                dialogB.setTitle("Do you want to Share or Delete the fridge?");

                LinearLayout layout = new LinearLayout(OverviewActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                dialogB.setView(layout);

                dialogB.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Show user the fridgeID in order so the user can share it to his/hers friend/family


                        //Current way of displaying the user the fridgeID - should we do this in another way?
                            //User should properly be aple to copy that ID - to make it easy to send
                        Toast toast = Toast.makeText(OverviewActivity.this,"Fridge ID: " + tmpFridgeID, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });

                dialogB.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        String tmpUserEmail = mService.getCurrentUserEmail();

                        //Deleting the fridge from eventlisteners, locally and userID from database to the correspondant fridge
                        mService.UnsubscribeFromFridge(tmpFridgeID,tmpUserEmail);

                        //TODO
                        //Viewing current added fridges

                        lv_fridgesListView.setAdapter(adaptor1);



                    }
                });

                dialogB.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });


                dialogB.show();

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ServiceUpdater.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            mService.setContext(getApplicationContext());


            //mService.SubscribeToFridge("TestFridgeID");


            //mService.SubscribeToFridge("TestFridge");
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("SYSTEM","Shutting down - onStop() in MainActivity");
        unbindService(mConnection);
    }

}
