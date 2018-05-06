package smap_f18_24.smap_fridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import smap_f18_24.smap_fridge.DAL.FridgeCallbackInterface;
import smap_f18_24.smap_fridge.DAL.fireStoreCommunicator;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ItemStatus;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;


public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";

    private boolean mBound = false;
    ServiceUpdater mService;

    EditText etFridgeName, etItemName, etListName, etFridgeID, etListID;
    Button btn_NewFridge, btn_add2inv, btn_add2ess, btn_add2SL, btn_overWriteInv, btn_overWriteShoppingList , btn_rmvFrmInv, btn_rmvFrmSL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        //dbComm = new fireStoreCommunicator(this);

        //Start service
        Intent ServiceIntent = new Intent(DebugActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        etFridgeName=findViewById(R.id.debug_et_fridgeName);
        etItemName=findViewById(R.id.debug_et_itemName);
        etListName=findViewById(R.id.debug_et_listName);
        etFridgeID=findViewById(R.id.debug_et_fridgeID);
        etListID=findViewById(R.id.debug_et_listID);

        btn_add2inv=findViewById(R.id.debug_btn_addItemToInv);
        btn_add2ess=findViewById(R.id.debug_btn_addItemToEss);
        btn_add2SL=findViewById(R.id.debug_btn_addItemToShoppingList);
        btn_NewFridge=findViewById(R.id.debug_btn_newFridge);
        btn_rmvFrmInv=findViewById(R.id.debug_btn_removeItemFromInventory);
        btn_rmvFrmSL=findViewById(R.id.debug_btn_removeItemFromShoppinList);
        btn_overWriteInv=findViewById(R.id.debug_btn_overWriteInventory);
        btn_overWriteShoppingList=findViewById(R.id.debug_btn_removeItemFromShoppinList);

        btn_add2inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.addItemToInventory(i,fridgeID);
            }
        });

        btn_add2ess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.addItemToEssentials(i,fridgeID);
            }
        });

        btn_NewFridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.createNewFridge(fridgeID,fridgeName);
                mService.SubscribeToFridge(fridgeID);
            }
        });

        btn_add2SL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String listName = etListName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.addItemToShoppingList(i,fridgeID,listName,listID);
            }
        });

        btn_rmvFrmSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String listName = etListName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.removeItemFromShoppingList(itemName,fridgeID,listID);
            }
        });

        btn_rmvFrmInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String listName = etListName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.removeItemFromInventory(itemName,fridgeID);
            }
        });

        btn_overWriteInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String listName = etListName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.overwriteItemInInventory(i,fridgeID);
            }
        });

        btn_overWriteShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID=etListID.getText().toString();
                String itemName=etItemName.getText().toString();
                String listName = etListName.getText().toString();
                String fridgeName=etFridgeName.getText().toString();
                String fridgeID = etFridgeID.getText().toString();
                Item i = new Item(itemName,"grams",5,"","");
                mService.overWriteItemInShoppingList(i,fridgeID,listID);
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
            mService.SubscribeToFridge("TestFridgeID");


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
}
