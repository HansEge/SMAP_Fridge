package smap_f18_24.smap_fridge;

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

import smap_f18_24.smap_fridge.DAL.fireStoreCommunicator;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ItemStatus;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;


public class DebugActivity extends AppCompatActivity {

    fireStoreCommunicator dbComm;
    private static final String TAG = "DebugActivity";
    
    Button btn_write2db, btn_testItem2db, btn_addList, btn_addShoppingList;
    EditText et_ShoppingListName;

    //database reference
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        dbComm = new fireStoreCommunicator();

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
                Item testItem = new Item("Malk","Liters",1,"null", ItemStatus.NEEDED);

                //Add item to db.
                dbComm.addItem(itemPath,testItem);
            }
        });


        btn_addList = findViewById(R.id.debug_btn_addList);
        btn_addList.setOnClickListener(new View.OnClickListener() {

            CollectionReference itemPath = db.collection("Inventory");

            @Override
            public void onClick(View v) {

                Item i1 = new Item("Malk", "Liters", 0.4f,"", ItemStatus.BOUGHT);
                Item i2 = new Item("Potatoes", "Grams", 500,"", ItemStatus.BOUGHT);
                Item i3 = new Item("Diapers", "Pcs", 3,"", ItemStatus.BOUGHT);

                InventoryList inventoryList = new InventoryList();
                inventoryList.AddItem(i1);
                inventoryList.AddItem(i2);
                inventoryList.AddItem(i3);
                dbComm.addInventoryList(itemPath,inventoryList);
            }
        });


        et_ShoppingListName = findViewById(R.id.debug_ET_ShoppingListName);

        btn_addShoppingList = findViewById(R.id.debug_btn_addShoppingList);
        btn_addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shoppingListName = et_ShoppingListName.getText().toString();

                CollectionReference itemPath = db.collection("ShoppingList");

                Item i1 = new Item("Malk", "Liters", 0.4f,"", ItemStatus.BOUGHT);
                Item i2 = new Item("Potatoes", "Grams", 500,"", ItemStatus.BOUGHT);
                Item i3 = new Item("Diapers", "Pcs", 3,"", ItemStatus.BOUGHT);

                ShoppingList shoppingList = new ShoppingList();
                shoppingList.AddItem(i1);
                shoppingList.AddItem(i2);
                shoppingList.AddItem(i3);
                dbComm.addShoppingList(itemPath,shoppingList, shoppingListName);
            }
        });

    }
}
