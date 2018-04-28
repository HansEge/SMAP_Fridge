package smap_f18_24.smap_fridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import smap_f18_24.smap_fridge.Adaptors.FridgeListAdaptor;

public class OverviewActivity extends AppCompatActivity {

    Button btn_addNewFridge, btn_addExistingFridge;
    ListView lv_fridgesListView;
    TextView tv_welcomeUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        // INITIALIZING

        btn_addNewFridge = findViewById(R.id.overview_btn_addNewFridge);
        btn_addExistingFridge = findViewById(R.id.overview_btn_addExistingFridge);

        lv_fridgesListView = findViewById(R.id.overview_lv_fridgesListView);

        tv_welcomeUser = findViewById(R.id.overview_tv_welcomeUser);

        //lv_fridgesListView.setAdapter();

        // POST-INITIALIZATION

        //If the user wants to add a new fridge to the list
        btn_addNewFridge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(OverviewActivity.this,AddNewFridgeActivity.class);
                startActivity(i);
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





    }
}
