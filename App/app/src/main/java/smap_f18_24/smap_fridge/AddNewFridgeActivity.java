package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import smap_f18_24.smap_fridge.Service.ServiceUpdater;

public class AddNewFridgeActivity extends AppCompatActivity {

    ImageView im_fridge;
    TextView tv_fridgeName;
    EditText et_fridgeName;
    Button btn_addFridge, btn_cancel;

    private boolean mBound = false;
    ServiceUpdater mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_fridge);

        //INITIALIZATION

        im_fridge = findViewById(R.id.addNewFridge_im_fridge);
        tv_fridgeName = findViewById(R.id.addNewFridge_tv_fridgename);
        et_fridgeName = findViewById(R.id.addNewFridge_et_fridgename);
        btn_addFridge = findViewById(R.id.addNewFridge_btn_addFridge);
        btn_cancel = findViewById(R.id.addNewFridge_btn_cancel);

        et_fridgeName.setInputType(InputType.TYPE_CLASS_TEXT);

        //POST-INITIALIZATION

        //Start service
        Intent ServiceIntent = new Intent(AddNewFridgeActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        //If the user wants to add a new fridge to the list
        btn_addFridge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String fridgeID = et_fridgeName.getText().toString();
                mService.createNewFridge(fridgeID,"Generic fridge name");
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });


    }

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

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ServiceUpdater.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    };
}
