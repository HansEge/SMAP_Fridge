package smap_f18_24.smap_fridge.DAL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smap_f18_24.smap_fridge.DebugActivity;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;

public class fireStoreCommunicator {

    private static final String TAG = "fireStoreCommunicator";

public void addItem(CollectionReference destination, final Item itemToAdd)
{

    // Create a new user with a first and last name
    Map<String, Object>  item = new HashMap<>();
    item.put("Name",itemToAdd.getName());
    item.put("Unit",itemToAdd.getUnit());
    item.put("Quantity", itemToAdd.getQuantity());
    item.put("ResponsibleUserEmail",itemToAdd.getResponsibleUserEmail());
    item.put("itemStatus",itemToAdd.getResponsibleUserEmail());

    destination
            .add(item)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
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

    private void addListInfo(CollectionReference destination, String name, String ID)
    {
        Map<String, Object> info = new HashMap<>();
        info.put("Name", name);
        info.put("ID", ID);

        destination.document("Info").set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void addInventoryList(CollectionReference fridge, final InventoryList listToAdd)
    {
        Map<String, Object>  InventoryList = new HashMap<>();
        CollectionReference listRef = fridge.document("Inventory").collection("Items");
        for (Item i:listToAdd.getItems()
             ) {
            addItem(listRef,i);
        }
    }

    public void addShoppingList(CollectionReference fridge, final ShoppingList listToAdd, String listName, String listID)
    {
        Map<String, Object>  ShoppingList = new HashMap<>();
        CollectionReference listRef = fridge.document("ShoppingLists").collection(listName);
        addListInfo(listRef,listName,listID);
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }

    public void addIngredientList(CollectionReference fridge, final IngredientList listToAdd, String listName, String listID)
    {
        Map<String, Object>  IngredientList = new HashMap<>();
        CollectionReference listRef = fridge.document("IngredientLists").collection(listName);
        addListInfo(listRef,listName,listID);
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }

    public void addEssentialsList(CollectionReference fridge, final EssentialsList listToAdd)
    {
        Map<String, Object>  EssentialsList = new HashMap<>();
        CollectionReference listRef = fridge.document("Essentials").collection("Items");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }

    public void getInventoryList(CollectionReference fridge)
    {
        Log.d(TAG, "getInventoryList: entered function");

        final CollectionReference inventoryReference = fridge.document("Inventory").collection("Items");

       inventoryReference
                .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                    return;
                } else {
                    // Convert the whole Query Snapshot to a list
                    // of objects directly! No need to fetch each
                    // document.
                    List<Item> itemList = documentSnapshots.toObjects(Item.class);

                    for (Item i: itemList
                         ) {
                        Log.d(TAG, "onSuccess: Item: " + i.getName());

                    }

                }
            }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed to get InventoryList");
                }
            });
    }

}
