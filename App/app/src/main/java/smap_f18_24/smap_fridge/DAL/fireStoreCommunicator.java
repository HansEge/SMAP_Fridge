package smap_f18_24.smap_fridge.DAL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smap_f18_24.smap_fridge.DebugActivity;
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

    public void addInventoryList(CollectionReference destination, final InventoryList listToAdd)
    {
        Map<String, Object>  InventoryList = new HashMap<>();
        CollectionReference listRef = destination.document("Inventory").collection("Items");
        for (Item i:listToAdd.getItems()
             ) {
            addItem(listRef,i);
        }
    }

    public void addShoppingList(CollectionReference destination, final ShoppingList listToAdd, String name)
    {
        Map<String, Object>  ShoppingList = new HashMap<>();
        CollectionReference listRef = destination.document(name).collection("Items");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }

}
