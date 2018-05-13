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

    Button btn_addNewFridge, btn_addExistingFridge, btn_updateUI;
    ListView lv_fridgesListView;
    TextView tv_welcomeUser;

    ServiceUpdater mService;
    private boolean mBound = false;

    private ArrayList<Fridge> localList = new ArrayList<>();
    public FridgeListAdaptor adaptor1 = new FridgeListAdaptor(this, localList);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // INITIALIZING
        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(this);

        //Start service
        Intent ServiceIntent = new Intent(OverviewActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        //register to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceUpdaterReceiver,filter);

        btn_addNewFridge = findViewById(R.id.overview_btn_addNewFridge);
        btn_addExistingFridge = findViewById(R.id.overview_btn_addExistingFridge);
        btn_updateUI = findViewById(R.id.overview_btn_updateUI);

        lv_fridgesListView = findViewById(R.id.overview_lv_fridgesListView);

        tv_welcomeUser = findViewById(R.id.overview_tv_welcomeUser);
        tv_welcomeUser.setText("Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        //localList.add(testFridge);

        lv_fridgesListView.setAdapter(adaptor1);
        UpdateUI();

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

                        mService.createNewFridge(tmp_id,tmp_name);
                        mService.SubscribeToFridge(tmp_id);

                        mService.addFridgeIDtoListOfSubscribedFridges(mService.getCurrentUserEmail(),tmp_id);

                        Fridge tmpFridge = mService.getFridge(tmp_id);

                        lv_fridgesListView.setAdapter(adaptor1);

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

                        mService.addFridgeIDtoListOfSubscribedFridges(mService.getCurrentUserEmail(),existingFridgeID);

                        mService.SubscribeToFridge(existingFridgeID);
                        
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

                String tmpID = localList.get(position).getID();


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

                final String tmpFridgeID = localList.get(i).getID();

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


        btn_updateUI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            UpdateUI();

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
            localList=mService.getAllFridges();
            UpdateUI();

        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void UpdateUI()
    {
        //reset adaptor to update UI.
        adaptor1 = new FridgeListAdaptor(this, localList);
        lv_fridgesListView.setAdapter(adaptor1);

    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("SYSTEM","Shutting down - onStop() in MainActivity");
        unbindService(mConnection);
    }


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
                UpdateUI();
            }
        }
    };
}
