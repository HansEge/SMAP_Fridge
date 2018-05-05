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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.fragment_details_tabs.DetailsActivity;

public class OverviewActivity extends AppCompatActivity {

    Button btn_addNewFridge, btn_addExistingFridge, btn_dirtyDetailsViewDetour;
    ListView lv_fridgesListView;
    TextView tv_welcomeUser;

    ServiceUpdater mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        // INITIALIZING

        //Start service
        Intent ServiceIntent = new Intent(OverviewActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        btn_addNewFridge = findViewById(R.id.overview_btn_addNewFridge);
        btn_addExistingFridge = findViewById(R.id.overview_btn_addExistingFridge);
        btn_dirtyDetailsViewDetour = findViewById(R.id.overview_btn_dirtyDetailsActivitydetour);


        lv_fridgesListView = findViewById(R.id.overview_lv_fridgesListView);

        tv_welcomeUser = findViewById(R.id.overview_tv_welcomeUser);

        //lv_fridgesListView.setAdapter();

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
                addExistingFridgeDialogBox.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //Functionality of the right sided button - cancels the dialogbox
                addExistingFridgeDialogBox.setNegativeButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*
                            TODO

                           Search database for unique code
                            If found, then add fridge and all information to the users list of fridges
                            If not, then present a errorMessage to the user

                            At this point the errorMessage doesn't work. This is because the dialog closes before code is being executed.
                            Try following work-around:
                            https://stackoverflow.com/questions/40261250/validation-on-edittext-in-alertdialog

                        */

                        if (et_uniqueCodeUserInput.getText().toString().trim().equalsIgnoreCase("")) {
                            //Tjek for den rigtige fejl og ikke bare tomt felt
                            et_uniqueCodeUserInput.setError("The unique code doesn't exist");
                        }



                    }
                });

                addExistingFridgeDialogBox.show();

            }
        });

        //User pressing a fridge on the listview --> go to DetailsActivity
        lv_fridgesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailsActivityIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
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
            mService.SubscribeToFridge("TestFridge");


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

}
