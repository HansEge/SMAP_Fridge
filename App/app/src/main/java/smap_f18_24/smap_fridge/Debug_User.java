package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;

public class Debug_User extends AppCompatActivity {

    private boolean mBound = false;
    ServiceUpdater mService;

    Button btn_newUser, btn_subscribe, btn_unsubscribe, btn_getFridge,btn_subscribeAllFridges;
    EditText et_userName, et_userEmail, et_fridgeID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug__user);

        //Start service
        Intent ServiceIntent = new Intent(Debug_User.this, ServiceUpdater.class);
        startService(ServiceIntent);

        et_fridgeID=findViewById(R.id.debug_user_et_fridgeID);
        et_userEmail=findViewById(R.id.debug_user_et_useremail);
        et_userName=findViewById(R.id.debug_user_et_username);

        btn_newUser=findViewById(R.id.debug_user_btn_newUser);
        btn_subscribe=findViewById(R.id.debug_user_btn_subscribe);
        btn_unsubscribe=findViewById(R.id.debug_user_btn_unsubscribe);
        btn_getFridge=findViewById(R.id.debug_user_btn_getFridge);
        btn_subscribeAllFridges=findViewById(R.id.debug_user_btn_subscribeAllFridges);

        btn_newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et_userName.getText().toString();
                String userEmail = et_userEmail.getText().toString();
                String fridge_ID = et_fridgeID.getText().toString();
                mService.createNewUser(userName,userEmail);
            }
        });

        btn_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et_userName.getText().toString();
                String userEmail = et_userEmail.getText().toString();
                String fridge_ID = et_fridgeID.getText().toString();
                mService.addFridgeIDtoListOfSubscribedFridges(userEmail,fridge_ID);
            }
        });

        btn_unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = et_userEmail.getText().toString();
                String fridge_ID = et_fridgeID.getText().toString();
                mService.removeFridgeIDfromListOfSubscribedFridges(userEmail,fridge_ID);
            }
        });

        btn_getFridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fridge_ID = et_fridgeID.getText().toString();
                Fridge acquiredFridge = mService.getFridge(fridge_ID);
                //Creating fridge to debug its values
            }
        });

        btn_subscribeAllFridges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String userEmail = et_userEmail.getText().toString();
                mService.getUserSubscribedFridges(userEmail);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ServiceUpdater.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceUpdater.ServiceBinder binder = (ServiceUpdater.ServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            mService.setContext(getApplicationContext());
            //mService.SubscribeToFridge("TestFridgeID");


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
}
