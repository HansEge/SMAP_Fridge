package smap_f18_24.smap_fridge.DAL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.List_ID;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.ModelClasses.User;

public class fireStoreCommunicator {
    //database reference
    FirebaseFirestore db;



    public fireStoreCommunicator(Context context, FridgeCallbackInterface callbackInterface)
    {
        this.context=context;
        this.callbackInterface=callbackInterface;
        db = FirebaseFirestore.getInstance();
    }

    FridgeCallbackInterface callbackInterface;
    Context context;
    private static final String TAG = "fireStoreCommunicator";

    //Adds item to list. Overwrites old data, if an item with matching name exists.
public void addItem(final CollectionReference destination, final Item itemToAdd)
{
    //Check whether item exists on list already or not.
    destination.whereEqualTo("Name",itemToAdd.getName()).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    //If old data exists, overwrite it.
                    if(!queryDocumentSnapshots.isEmpty())
                    {
                        Map<String, Object>  item = new HashMap<>();
                        item.put("Name",itemToAdd.getName());
                        item.put("Unit",itemToAdd.getUnit());
                        item.put("Quantity", itemToAdd.getQuantity());
                        item.put("ResponsibleUserEmail",itemToAdd.getResponsibleUserEmail());
                        item.put("itemStatus",itemToAdd.getResponsibleUserEmail());

                        String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        destination.document(snapshotID).update(item);
                        /*
                        Log.d(TAG, "onSuccess: Removing old data for item with name: " + itemToAdd.getName());
                        //removing old data.
                        String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Log.d(TAG, "SNapshotID: " + snapshotID);
                        destination.document(snapshotID).delete();
                        */
                    }
                    else
                    {
                        Log.d(TAG, "onSuccess: Adding new data for item with name: " + itemToAdd.getName());
                        //Add new data to list.
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
                }
            });



    }

    public void removeItem(final CollectionReference destination, final String itemName)
    {
        //Check whether item exists on list already or not.
        destination.whereEqualTo("Name",itemName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //If data exists, remove it.
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: Removing data for item with name: " + itemName);
                            String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            //Log.d(TAG, "SNapshotID: " + snapshotID);
                            destination.document(snapshotID).delete();
                            if(queryDocumentSnapshots.getDocuments().size()==1)
                            {
                                Log.d(TAG, "onSuccess: DELETE SHOPPING LIST!");
                            }
                        }
                        else
                        {
                            Log.d(TAG, "onSuccess: Item: " + itemName + " was not on list, and thus cannot be removed");
                        }
                    }
                });
    }

    public void addListInfo(CollectionReference destination, String name, String ID, String responsibleUserEmail)
    {
        Map<String, Object> info = new HashMap<>();
        info.put("Name", name);
        info.put("ID", ID);
        info.put("ResponsibleUserEmail",responsibleUserEmail);

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
        addListInfo(listRef,listName,listID,"None");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }

        addID2listofShoppingListIDs(fridge,listID);
    }

    public void addID2listofShoppingListIDs(CollectionReference fridge, String listID)
    {
        //Add list ID to ShoppingList_IDs
        CollectionReference ID_Ref = fridge.document("ShoppingList_IDs").collection("IDs");
        Map<String, Object> info = new HashMap<>();
        info.put("ID", listID);
        ID_Ref.document(listID).set(info)
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

    public void addIngredientList(CollectionReference fridge, final IngredientList listToAdd, String listName, String listID)
    {
        Map<String, Object>  IngredientList = new HashMap<>();
        CollectionReference listRef = fridge.document("IngredientLists").collection(listID);
        addListInfo(listRef,listName,listID,"None");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }

        addID2listofShoppingListIDs(fridge,listID);
    }

    public void addID2listofIngredientListIDs(CollectionReference fridge, String listID)
    {
        //Add list ID to ShoppingList_IDs
        CollectionReference ID_Ref = fridge.document("IngredientList_IDs").collection("IDs");
        Map<String, Object> info = new HashMap<>();
        info.put("ID", listID);
        ID_Ref.document(listID).set(info)
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

    public void addEssentialsList(CollectionReference fridge, final EssentialsList listToAdd)
    {
        Map<String, Object>  EssentialsList = new HashMap<>();
        CollectionReference listRef = fridge.document("Essentials").collection("Items");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }

    public void getInventoryList(final CollectionReference fridge)
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
                    Log.d(TAG, "onSuccess: INVENTORY LIST EMPTY");
                    return;
                } else {
                    //Convert documentShapshots to list of items.
                    List<Item> itemList = documentSnapshots.toObjects(Item.class);
                    InventoryList inventoryList = new InventoryList();

                    Log.d(TAG, "Broadcasting Inventory list");
                    for (Item i: itemList
                         ) {
                        inventoryList.AddItem(i);
                        Log.d(TAG, "Item in inventory list: " + i.getName());
                    }

                    callbackInterface.onInventoryChange(fridge.getParent().getId(),inventoryList);

                }
            }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed to get InventoryList");
                }
            });
    }



    public void getEssentialsList(final CollectionReference fridge)
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
                            Log.d(TAG, "onSuccess: ESSENTIALS LIST EMPTY");
                            return;
                        } else {

                            //Convert documentShapshots to list of items.
                            List<Item> itemList = documentSnapshots.toObjects(Item.class);
                            EssentialsList essentialsList = new EssentialsList();

                            Log.d(TAG, "Broadcasting Essentials list");
                            for (Item i: itemList
                                    ) {
                                //Do something with items.
                                essentialsList.AddItem(i);
                                Log.d(TAG, "Item on Essentials list: " + i.getName());
                            }

                            callbackInterface.onEssentialsChange(fridge.getParent().getId(),essentialsList);

                        }
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to get EssentialsList");
            }
        });
    }

    public void getShoppingList(final CollectionReference fridge, final String ID)
    {

        final ShoppingList shoppingList = new ShoppingList("NO_NAME_YET",ID);
        final String fridge_ID=fridge.getParent().getId();

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
                            Log.d(TAG, "onSuccess: SHOPPING LIST EMPTY");
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
                                        shoppingList.setName(i.getName());
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        shoppingList.AddItem(i);
                                    }
                                }
                                //If only item in list was the Info-object, delete the list, both in database and locally.
                                if(itemList.size()<2)
                                {
                                    //TODO:delete list in database.
                                    deleteShoppingListFromDatabase(fridge_ID,ID);

                                    //delete list locally.
                                    callbackInterface.onShoppingListDelete(fridge.getParent().getId(),shoppingList);
                                }
                                if(shoppingList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                else
                                {
                                    //TODO: Return list through callback interface/broadcast new list.
                                    Log.d(TAG, "Broadcasting Shopping list: " + shoppingList.getName());
                                    callbackInterface.onShoppingListsChange(fridge.getParent().getId(),shoppingList);
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

        shoppingListReference.document("Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "getShoppingList - got Info object.");

                        if(documentSnapshot.exists())
                        {
                            String name = documentSnapshot.get("Name").toString();
                            String ID = documentSnapshot.get("ID").toString();
                            String responsibleUser = documentSnapshot.get("ResponsibleUserEmail").toString();

                            Log.d(TAG, "onSuccess: Name="+name + ", ID="+ID);

                            shoppingList.setName(name);
                            shoppingList.setID(ID);
                            shoppingList.setResponsibility(responsibleUser);

                            if(shoppingList.getItems().size()==0)
                            {
                                Log.d(TAG, "onSuccess: List items have not been added yet, and thus list is not returned yet.");
                            }
                            else
                            {
                                //TODO: Return list through callback interface/broadcast new list.
                                Log.d(TAG, "Broadcasting Shopping list: " + shoppingList.getName());
                                for (Item i: shoppingList.getItems()
                                        ) {
                                    Log.d(TAG, "Item on Shopping list: " + i.getName());
                                    callbackInterface.onShoppingListsChange(fridge.getParent().getId(),shoppingList);
                                }
                            }
                        }
                        //TODO: else - check if local copy of list exists. If it does, delete it. Do the same for Ingredient list.
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getShoppingList - failed to get Info Object");
            }
        });
    }

    public void getIngredientList(final CollectionReference fridge, final String ID)
    {

        final IngredientList ingredientList = new IngredientList("NO_NAME_YET",ID);
        final String fridge_ID=fridge.getParent().getId();

        //reference to fridge
        final CollectionReference ingredientListReference = fridge.document("IngredientLists").collection(ID);

        //make a query for all documents in collection
        ingredientListReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        //check if there are any documents
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: INGREDIENT LIST EMPTY");
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
                                        ingredientList.setName(i.getName());
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        ingredientList.AddItem(i);
                                    }
                                }
                                //If only item in list was the Info-object, delete the list, both in database and locally.
                                if(itemList.size()<2)
                                {
                                    //TODO:delete list in database.
                                    deleteIngredientListFromDatabase(fridge_ID,ID);

                                    //delete list locally.
                                    callbackInterface.onIngredientListDelete(fridge.getParent().getId(),ingredientList);
                                }
                                if(ingredientList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                else
                                {
                                    //TODO: Return list through callback interface/broadcast new list.
                                    Log.d(TAG, "Broadcasting Shopping list: " + ingredientList.getName());
                                    callbackInterface.onIngredientListsChange(fridge.getParent().getId(),ingredientList);
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
                Log.d(TAG, "onFailure: Failed to get IngredientList");
            }
        });

        ingredientListReference.document("Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "getIngredientList - got Info object.");

                        if(documentSnapshot.exists())
                        {
                            String name = documentSnapshot.get("Name").toString();
                            String ID = documentSnapshot.get("ID").toString();

                            Log.d(TAG, "onSuccess: Name="+name + ", ID="+ID);

                            ingredientList.setName(name);
                            ingredientList.setID(ID);

                            if(ingredientList.getItems().size()==0)
                            {
                                Log.d(TAG, "onSuccess: List items have not been added yet, and thus list is not returned yet.");
                            }
                            else
                            {
                                //TODO: Return list through callback interface/broadcast new list.
                                Log.d(TAG, "Broadcasting Ingredient list: " + ingredientList.getName());
                                for (Item i: ingredientList.getItems()
                                        ) {
                                    Log.d(TAG, "Item on Ingredient list: " + i.getName());
                                    callbackInterface.onIngredientListsChange(fridge.getParent().getId(),ingredientList);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "getIngredientList - failed to get Info Object");
                    }
                });
    }

    private void deleteShoppingListFromDatabase(String fridge_id, final String list_ID)
    {
        DocumentReference fridgeRef = db.collection("Fridges").document(fridge_id);
        final CollectionReference IDsRef = fridgeRef.collection("Content").document("ShoppingList_IDs").collection("IDs");
        DocumentReference ListsRef = fridgeRef.collection("Content").document("ShoppingLists");

        //Remove List ID from ID-list.
        IDsRef.whereEqualTo("ID",list_ID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //If data exists, remove it.
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: Deleting shopping list with ID: " + list_ID);
                            String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            IDsRef.document(snapshotID).delete();
                        }
                        else
                        {
                            Log.d(TAG, "onSuccess: List with ID: " + list_ID + " already didn't exist, and thus cannot be deleted.");
                        }
                    }
                });


    }

    private void deleteIngredientListFromDatabase(String fridge_id, final String list_ID)
    {
        DocumentReference fridgeRef = db.collection("Fridges").document(fridge_id);
        final CollectionReference IDsRef = fridgeRef.collection("Content").document("IngredientList_IDs").collection("IDs");
        DocumentReference ListsRef = fridgeRef.collection("Content").document("IngredientLists");

        //Remove List ID from ID-list.
        IDsRef.whereEqualTo("ID",list_ID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //If data exists, remove it.
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: Deleting ingredient list with ID: " + list_ID);
                            String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            IDsRef.document(snapshotID).delete();
                        }
                        else
                        {
                            Log.d(TAG, "onSuccess: List with ID: " + list_ID + " already didn't exist, and thus cannot be deleted.");
                        }
                    }
                });


    }

    public void getFridgeName(final String fridgeID){

        DocumentReference docRef = db.collection("Fridges").document(fridgeID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                        if (document != null){
                            String name = document.getString("Name");
                            Log.d(TAG, "The name of fridgeID: " + fridgeID + " is: " + name);
                            callbackInterface.onFridgeName(fridgeID,name);
                        }
                        else{
                            Log.d(TAG, "Error in finding fridgeID name");
                        }

                }
            }
        });


    }


    //Gets a list of fridges subscribed to by the user, and makes sure that updates in the subscribed fridges triggers callbacks to the provided callback interface.
    public void SubscribeToSavedFridges(String userEmail, final FridgeCallbackInterface callbackInterface)
    {
        //get list from database.
        db.collection("Users").document(userEmail).collection("FridgeSubscribtions").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: Failure getting QuerySnapshot in SubscribeToSavedFridges");
                        }
                        else
                        {
                            //Make list of ID-objects. (Yes, the class used is List_ID, but it only conatins a field ID, which is what we need here.)
                            ArrayList<List_ID> Fridge_IDs = (ArrayList<List_ID>)queryDocumentSnapshots.toObjects(List_ID.class);

                            //Subscribe to all fridges.
                            for (List_ID id:Fridge_IDs
                                 ) {
                                //create new placeholder fridge in service.
                                callbackInterface.onSubscribingToFridge(id.getID());
                                SubscribeToFridge(id.getID());
                            }
                        }
                    }
                });
    }

    public void SubscribeToFridge(final String fridgeID)
    {
        DocumentReference fridgeRef = db.collection("Fridges").document(fridgeID);
        Log.d(TAG, "SubscribeToFridge: Subscribing to fridge with ID " + fridgeID);
        CollectionReference fridgeListRef=fridgeRef.collection("Content");




        fridgeListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Toast.makeText(context, "Fridge with ID: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Fridge: " + fridgeID + " updated.");
            }
        });

        getFridgeName(fridgeID);

        SubscribeToInventory(fridgeRef, fridgeID);
        SubscribeToEssentials(fridgeRef, fridgeID);
        SubscribeToShoppingLists(fridgeRef, fridgeID);
        SubscribeToIngredientLists(fridgeRef, fridgeID);
    }


    private void SubscribeToInventory(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridgeListRef.document("Inventory").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Toast.makeText(context, "Essentials of Fridge: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Inventory of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getInventoryList(fridgeListRef);
            }
        });
    }

    private void SubscribeToEssentials(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridgeListRef.document("Essentials").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Toast.makeText(context, "Essentials of Fridge: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Essentials of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getEssentialsList(fridgeListRef);
            }
        });
    }

    private void SubscribeToShoppingLists(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //get IDs for all shopping lists.
        fridgeListRef.document("ShoppingList_IDs").collection("IDs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: No items on list of ShoppinList_IDs");
                        }
                        else
                        {
                            //Convert dataSnapshot to list of List_IDs(yes these only contain a string, but a model class is necessary for Firebase to create objects from datasnapshots).
                            ArrayList<List_ID> IDs = (ArrayList<List_ID>) queryDocumentSnapshots.toObjects(List_ID.class);

                            //Subscribe to each shopping list.
                            for (final List_ID id: IDs
                                    ) {
                                SubscribeToShoppingList(fridgeListRef, id.getID(), fridgeID);
                            }
                        }
                    }
                });
    }

    public void SubscribeToShoppingList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getShoppingListIDs: Subscribing to shopping list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("ShoppingLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                Toast.makeText(context, "Ingredient list " + listID + " of Fridge: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribefToFridge - Shopping List " +listID + " of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getShoppingList(fridgeListRef,listID);
            }
        });
    }

    private void SubscribeToIngredientLists(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //get IDs for all shopping lists.
        fridgeListRef.document("IngredientList_IDs").collection("IDs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: No items on list of IngredientList_IDs");
                        }
                        else
                        {
                            //Convert dataSnapshot to list of List_IDs(yes these only contain a string, but a model class is necessary for Firebase to create objects from datasnapshots).
                                ArrayList<List_ID> IDs = (ArrayList<List_ID>) queryDocumentSnapshots.toObjects(List_ID.class);

                            //Subscribe to each shopping list.
                            for (final List_ID id: IDs
                                    ) {
                               SubscribeToIngredientList(fridgeListRef, id.getID(), fridgeID);
                            }
                        }
                    }
                });

    }

    public void SubscribeToIngredientList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getIngredientListIDs: Subscribing to ingredient list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("IngredientLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                Toast.makeText(context, "Ingredient list " + listID + " of Fridge: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribefToFridge - Ingredient List " +listID + " of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getIngredientList(fridgeListRef,listID);
            }
        });
    }



    //UNSUBSCRIBE FROM FRIDGE
    public void UnSubscribeToFridge(final String fridgeID)
    {
        DocumentReference fridgeRef = db.collection("Fridges").document(fridgeID);
        Log.d(TAG, "SubscribeToFridge: UnSubscribing to fridge with ID " + fridgeID);
        CollectionReference fridgeListRef=fridgeRef.collection("Content");




        fridgeListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing
            }
        });

        getFridgeName(fridgeID);

        UnSubscribeToInventory(fridgeRef, fridgeID);
        UnSubscribeToEssentials(fridgeRef, fridgeID);
        UnSubscribeToShoppingLists(fridgeRef, fridgeID);
        UnSubscribeToIngredientLists(fridgeRef, fridgeID);
    }


    private void UnSubscribeToInventory(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridgeListRef.document("Inventory").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing.
            }
        });
    }

    private void UnSubscribeToEssentials(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridgeListRef.document("Essentials").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing.
            }
        });
    }

    private void UnSubscribeToShoppingLists(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //get IDs for all shopping lists.
        fridgeListRef.document("ShoppingList_IDs").collection("IDs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: No items on list of ShoppinList_IDs");
                        }
                        else
                        {
                            //Convert dataSnapshot to list of List_IDs(yes these only contain a string, but a model class is necessary for Firebase to create objects from datasnapshots).
                            ArrayList<List_ID> IDs = (ArrayList<List_ID>) queryDocumentSnapshots.toObjects(List_ID.class);

                            //Subscribe to each shopping list.
                            for (final List_ID id: IDs
                                    ) {
                                UnSubscribeToShoppingList(fridgeListRef, id.getID(), fridgeID);
                            }
                        }
                    }
                });
    }

    public void UnSubscribeToShoppingList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getShoppingListIDs: Subscribing to shopping list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("ShoppingLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing
            }
        });
    }

    private void UnSubscribeToIngredientLists(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //get IDs for all shopping lists.
        fridgeListRef.document("IngredientList_IDs").collection("IDs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "onSuccess: No items on list of IngredientList_IDs");
                        }
                        else
                        {
                            //Convert dataSnapshot to list of List_IDs(yes these only contain a string, but a model class is necessary for Firebase to create objects from datasnapshots).
                            ArrayList<List_ID> IDs = (ArrayList<List_ID>) queryDocumentSnapshots.toObjects(List_ID.class);

                            //Subscribe to each shopping list.
                            for (final List_ID id: IDs
                                    ) {
                                UnSubscribeToIngredientList(fridgeListRef, id.getID(), fridgeID);
                            }
                        }
                    }
                });

    }

    public void UnSubscribeToIngredientList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getIngredientListIDs: Subscribing to ingredient list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("IngredientLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing
            }
        });
    }





    public void createNewFridge(final String ID, final String Name) {
        final DocumentReference fridgesRef = db.collection("Fridges").document(ID);
        //Check whether fridge exists already or not.
        fridgesRef.get()
                //If it exists, notify user that he/she cannot create a fridge with that id.
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            Log.d(TAG, "onSuccess: Fridge with ID " + ID + " already exists. Name = " + Name);
                        }
                        else
                        {
                            Log.d(TAG, "onFailure: creating Fridge with ID " + ID + " Name = " + Name);

                            //Add ID and Name of fridge.
                            Map<String, Object> info = new HashMap<>();
                            info.put("ID", ID);
                            info.put("Name",Name);
                            fridgesRef.set(info)
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
                    }
                });
    }

    public void setResponsibilityForListShoppingList(String fridge_ID, String list_ID, String ResponsibleUser)
    {
        final DocumentReference fridgesRef = db.collection("Fridges").document(fridge_ID);
        final CollectionReference listRef = fridgesRef.collection("Content").document("ShoppingLists").collection(list_ID);

        Map<String, Object>  newInfo = new HashMap<>();
        newInfo.put("ResponsibleUserEmail",ResponsibleUser);
        listRef.document("Info").update(newInfo);
    }

    //Tested and working
    public void createNewUserInDatabase(String Name, String email)
    {
        Map<String, Object>  UserInfo = new HashMap<>();
        UserInfo.put("Name",Name);
        UserInfo.put("email",email);

        db.collection("Users").document(email).set(UserInfo);
    }

    //Tested and working
    public void addFridgeID2listOfFridgeSubscriptions(String fridge_ID, String userEmail)
    {
        Map<String, Object> info = new HashMap<>();
        info.put("ID", fridge_ID);
        db.collection("Users").document(userEmail).collection("FridgeSubscribtions").document(fridge_ID).set(info)
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

    //Tested and working
    //Removes fridge id from the list of subscribed fridges for the user with the given userEmail.
    public void removeFridgeIDFromListOfFridgeSubscriptions(final String fridge_ID, String userEmail)
    {
        final CollectionReference listRef = db.collection("Users").document(userEmail).collection("FridgeSubscribtions");
        listRef.whereEqualTo("ID",fridge_ID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "removeFridgeIDFromListOfFridgeSubscriptions - onSuccess: fridge with ID " + fridge_ID + " was not subscribed to in the first place");
                        }
                        else
                        {
                            Log.d(TAG, "removeFridgeIDFromListOfFridgeSubscriptions - onSuccess: Removing fridge ID " + fridge_ID + " from list of subscribed fridges.");
                            String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            listRef.document(snapshotID).delete();
                        }
                    }
                });
    }

    public void addUserToDatabaseIfNotThereAlready(final FirebaseUser user)
    {
        db.collection("Users").document(user.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            Log.d(TAG, "onSuccess:  addUserToDatabaseIfNotThereAlready - User with email " + user.getEmail() + " already exists in database, and this is not created");
                        }
                        else
                        {
                            createNewUserInDatabase(user.getDisplayName(),user.getEmail());
                            Log.d(TAG, "onSuccess: addUserToDatabaseIfNotThereAlready - User with email " + user.getEmail() + " created in database.");
                        }
                    }
                });
    }
}
