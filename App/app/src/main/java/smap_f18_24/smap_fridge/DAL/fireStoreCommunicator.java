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
        CollectionReference listRef = fridge.document("ShoppingLists").collection(listID);
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
        //Fridge reference.
        final CollectionReference inventoryReference = fridge.document("Inventory").collection("Items");

        //Query for Items.
       inventoryReference
                .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                //Check if list is empty.
                if (documentSnapshots.isEmpty()) {
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                    return;
                } else {
                    //Convert documentShapshots to list of items.
                    List<Item> itemList = documentSnapshots.toObjects(Item.class);

                    for (Item i: itemList
                         ) {
                        //Do something with items.
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



    public void getEssentialsList(CollectionReference fridge)
    {
        //Fridge reference.
        final CollectionReference inventoryReference = fridge.document("Essentials").collection("Items");

        //Query for Items.
        inventoryReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        //Check if list is empty.
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {

                            //Convert documentShapshots to list of items.
                            List<Item> itemList = documentSnapshots.toObjects(Item.class);

                            for (Item i: itemList
                                    ) {
                                //Do something with items.
                                Log.d(TAG, "onSuccess: Item: " + i.getName());

                            }

                        }
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to get EssentialsList");
            }
        });
    }

    public void getShoppingList(CollectionReference fridge, String ID)
    {

        //reference to fridge
        final CollectionReference shoppingListReference = fridge.document("ShoppingLists").collection(ID);

        //make a query for all documents in collection
        shoppingListReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        //check if there are any documents
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            try
                            {
                                //Make list of Items out of the documents
                                List<Item> itemList = documentSnapshots.toObjects(Item.class);
                                for (Item i: itemList
                                        ) {

                                    //hack to find the "INFO"-document, that we don't want to make into an Item-object.
                                    if(i.getUnit().equals("THIS_IS_NOT_AN_ITEM"))
                                    {
                                        Log.d(TAG, "getShoppingList: Item " + i.getName() + " is not an item.");
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                    }

                                }
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "getShoppingList: could not convert datasnapshot" + documentSnapshots.toString()+ " into Item");
                            }

                        }
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to get EssentialsList");
            }
        });
    }

    public void getIngredientList(CollectionReference fridge, String ID)
    {

        //reference to fridge
        final CollectionReference shoppingListReference = fridge.document("IngredientLists").collection(ID);

        //make a query for all documents in collection
        shoppingListReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        //check if there are any documents
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            try
                            {
                                //Make list of Items out of the documents
                                List<Item> itemList = documentSnapshots.toObjects(Item.class);
                                for (Item i: itemList
                                        ) {

                                    //hack to find the "INFO"-document, that we don't want to make into an Item-object.
                                    if(i.getUnit().equals("THIS_IS_NOT_AN_ITEM"))
                                    {
                                        Log.d(TAG, "getIngredientList: Item " + i.getName() + " is not an item.");
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                    }

                                }
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "getIngredientList: could not convert datasnapshot" + documentSnapshots.toString()+ " into Item");
                            }

                        }
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to get EssentialsList");
            }
        });
    }

}
