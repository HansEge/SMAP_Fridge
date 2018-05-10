package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;

public class ShoppingListActivity extends AppCompatActivity {


    private ListView lv_shoppingList;

    String fridgeID;
    int position;

    private boolean mBound = false;
    public ServiceUpdater mService;

    public ShoppingListAdaptor adaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Intent i = getIntent();

        fridgeID = i.getStringExtra("CurrentFridgeID");
        position = i.getIntExtra("PositionOfShoppingList",0);

        lv_shoppingList = findViewById(R.id.shoppingList_lv_list);



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

            adaptor = new ShoppingListAdaptor(getApplicationContext(),mService.getFridge(fridgeID).getShoppingLists().get(position));
            lv_shoppingList.setAdapter(adaptor);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
}
