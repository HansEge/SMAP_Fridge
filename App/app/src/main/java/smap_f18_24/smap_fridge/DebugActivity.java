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

    //fireStoreCommunicator dbComm;
    private static final String TAG = "DebugActivity";
    
    Button btn_write2db, btn_testItem2db, btn_addList, btn_addShoppingList, btn_addEssentialsList, btn_addIngredientList, btn_loadIngredientList, btn_loadShoppingList, btn_add2sl;
    EditText et_ShoppingListName, et_IngredientListName, et_add2sl;

    //database reference
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean mBound = false;
    ServiceUpdater mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        //dbComm = new fireStoreCommunicator(this);

        //Start service
        Intent ServiceIntent = new Intent(DebugActivity.this, ServiceUpdater.class);
        startService(ServiceIntent);

        final CollectionReference fridgePath = db.collection("TestFridge");

        //dbComm.SubscribeToFridge(fridgePath);

        btn_write2db = findViewById(R.id.debug_btn_write2db);

        btn_write2db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("name",currentUser.getDisplayName());
                user.put("email",currentUser.getEmail());

                // Add a new document with a generated ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(DebugActivity.this, "Created new user with name: " + currentUser.getDisplayName() + " and email: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                
            }
        });

        btn_testItem2db = findViewById(R.id.debug_btn_addItem);
        btn_testItem2db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get reference to database path.
                CollectionReference itemPath = db.collection("Items");

                //create test item.
                Item testItem = new Item("Malk","Liters",1,"null", "Needed");

                //Add item to db.
                //dbComm.addItem(itemPath,testItem);
            }
        });


        btn_addList = findViewById(R.id.debug_btn_addList);
        btn_addList.setOnClickListener(new View.OnClickListener() {

            CollectionReference itemPath = db.collection("TestFridge");

            @Override
            public void onClick(View v) {

                Item i1 = new Item("Malk", "Liters", 0.4f,"", "Bought");
                Item i2 = new Item("Potatoes", "Grams", 500,"", "Bought");
                Item i3 = new Item("Diapers", "Pcs", 3,"", "Bought");

                InventoryList inventoryList = new InventoryList();
                inventoryList.AddItem(i1);
                inventoryList.AddItem(i2);
                inventoryList.AddItem(i3);
                //dbComm.addInventoryList(itemPath,inventoryList);
            }
        });


        et_ShoppingListName = findViewById(R.id.debug_ET_ShoppingListName);

        btn_addShoppingList = findViewById(R.id.debug_btn_addShoppingList);
        btn_addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shoppingListName = et_ShoppingListName.getText().toString();

                CollectionReference fridgePath = db.collection("TestFridge");

                Item i1 = new Item("Malk", "Liters", 0.4f,"", "Bought");
                Item i2 = new Item("Potatoes", "Grams", 500,"", "Bought");
                Item i3 = new Item("Diapers", "Pcs", 3,"", "Bought");

                ShoppingList shoppingList = new ShoppingList();
                shoppingList.AddItem(i1);
                shoppingList.AddItem(i2);
                shoppingList.AddItem(i3);

                String ShoppingListID = et_ShoppingListName.getText().toString();

                mService.addShoppingList(fridgePath,shoppingList, shoppingListName,ShoppingListID);
                mService.SubscribeToFridge("TestFridge");
            }
        });

        btn_addEssentialsList = findViewById(R.id.debug_btn_addEssentialsList);
        btn_addEssentialsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference itemPath = db.collection("TestFridge");

                Item i1 = new Item("Malk", "Liters", 0.4f,"", "Bought");
                Item i2 = new Item("Potatoes", "Grams", 500,"", "Bought");
                Item i3 = new Item("Diapers", "Pcs", 3,"", "Bought");

                EssentialsList essentialsList = new EssentialsList();
                essentialsList.AddItem(i1);
                essentialsList.AddItem(i2);
                essentialsList.AddItem(i3);
                //dbComm.addEssentialsList(itemPath,essentialsList);
            }
        });

        et_IngredientListName = findViewById(R.id.debug_ET_ingredientListName);

        btn_addIngredientList = findViewById(R.id.debug_btn_addIngredientList);
        btn_addIngredientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference itemPath = db.collection("TestFridge");

                String listName = et_IngredientListName.getText().toString();

                Item i1 = new Item("Malk", "Liters", 0.4f,"", "Bought");
                Item i2 = new Item("Potatoes", "Grams", 500,"", "Bought");
                Item i3 = new Item("Diapers", "Pcs", 3,"", "Bought");

                String IngredientListID =et_IngredientListName.getText().toString();

                IngredientList ingredientList = new IngredientList("Cool thing list", IngredientListID);
                ingredientList.AddItem(i1);
                ingredientList.AddItem(i2);
                ingredientList.AddItem(i3);
                //dbComm.addIngredientList(itemPath,ingredientList,listName,ingredientList.getID());
                //dbComm.SubscribeToFridge(fridgePath);
            }
        });

        btn_loadIngredientList=findViewById(R.id.debug_btn_loadIngredientList);
        btn_loadIngredientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference fridgePath = db.collection("TestFridge");
                //dbComm.getInventoryList(fridgePath);
            }
        });

    btn_loadShoppingList = findViewById(R.id.debug_btn_loadShoppingList);
    btn_loadShoppingList.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            CollectionReference fridgePath = db.collection("TestFridge");
            //dbComm.getShoppingList(fridgePath,"TestFridge_SL1");
        }
    });

    et_add2sl=findViewById(R.id.debug_et_add2sl);

    btn_add2sl=findViewById(R.id.debug_btn_add2sl);
    btn_add2sl.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String itemName=et_add2sl.getText().toString();
            Item item = new Item(itemName,"asd",2,"","");

            mService.addItemToShoppingList(item,"TestFridge","Cool Shopping List");
            mService.SubscribeToFridge("TestFridge");
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
