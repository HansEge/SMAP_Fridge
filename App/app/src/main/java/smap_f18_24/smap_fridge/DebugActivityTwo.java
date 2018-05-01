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

import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;

public class DebugActivityTwo extends AppCompatActivity {

    Button btn_incItemInSL;
    EditText et_incItemInSL;

    private boolean mBound = false;
    ServiceUpdater mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_two);

        //Start service
        Intent ServiceIntent = new Intent(DebugActivityTwo.this, ServiceUpdater.class);
        startService(ServiceIntent);

        et_incItemInSL=findViewById(R.id.debug2_et_incItemInSL);
        btn_incItemInSL=findViewById(R.id.debug2_btn_incItemInSL);
        btn_incItemInSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = et_incItemInSL.getText().toString();
                Item item = new Item(itemName,"g",1,"","");
                mService.addItemToShoppingList(item,"TestFridge","Cool Shopping List");
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
