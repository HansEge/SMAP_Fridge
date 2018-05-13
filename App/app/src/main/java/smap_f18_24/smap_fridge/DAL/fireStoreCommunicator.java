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

//This class is responsible for the lowest layer of communication with the database. It reads and writes objects and lists.
//In general, the functionality is based on the FireStore documentation on how to read and write data (https://firebase.google.com/docs/firestore/manage-data/add-data) and ()https://firebase.google.com/docs/firestore/query-data/get-data).
//Furthermore, the firestore functinlaity snapshotListener is being useed to get updates from the database in real time. (https://firebase.google.com/docs/firestore/query-data/queries)

public class fireStoreCommunicator {
    //database reference
    FirebaseFirestore db;

    //Constructor that takes a callback interface in order to make changes upon completion of asynchronous tasks in firestore.
    public fireStoreCommunicator(Context context, FridgeCallbackInterface callbackInterface)
    {
        this.context=context;
        this.callbackInterface=callbackInterface;
        db = FirebaseFirestore.getInstance();
    }

    FridgeCallbackInterface callbackInterface; //For making changes in the service upon completion of asynchronous tasks in firestore.
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
                        //create hashmap with new item.
                        Map<String, Object>  item = new HashMap<>();
                        item.put("Name",itemToAdd.getName());
                        item.put("Unit",itemToAdd.getUnit());
                        item.put("Quantity", itemToAdd.getQuantity());
                        item.put("ResponsibleUserEmail",itemToAdd.getResponsibleUserEmail());
                        item.put("itemStatus",itemToAdd.getResponsibleUserEmail());

                        String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        //Update item in database
                        destination.document(snapshotID).update(item);
                        /*
                        Log.d(TAG, "onSuccess: Removing old data for item with name: " + itemToAdd.getName());
                        //removing old data.
                        String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Log.d(TAG, "SNapshotID: " + snapshotID);
                        destination.document(snapshotID).delete();
                        */
                    }
                    //If item didn't exist already, add it to database.
                    else
                    {
                        Log.d(TAG, "onSuccess: Adding new data for item with name: " + itemToAdd.getName());
                        //create hashmap with new item.
                        Map<String, Object>  item = new HashMap<>();
                        item.put("Name",itemToAdd.getName());
                        item.put("Unit",itemToAdd.getUnit());
                        item.put("Quantity", itemToAdd.getQuantity());
                        item.put("ResponsibleUserEmail",itemToAdd.getResponsibleUserEmail());
                        item.put("itemStatus",itemToAdd.getResponsibleUserEmail());

                        //push to database.
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

    //Removes an item at destination with a given name.
    public void removeItem(final CollectionReference destination, final String itemName)
    {
        //Check whether item exists on list or not.
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
                        //If item doesn't exist on list, don't do anything
                        else
                        {
                            Log.d(TAG, "onSuccess: Item: " + itemName + " was not on list, and thus cannot be removed");
                        }
                    }
                });
    }

    //Adds an pseudo-item to the given destination (an IngredientList or ShoppingList) with Name, ID and the email of the responsible user.
    //This is used in order to identify lists and retrieve this info later
    public void addListInfo(CollectionReference destination, String name, String ID, String responsibleUserEmail)
    {
        //Create new hashmap with info.
        Map<String, Object> info = new HashMap<>();
        info.put("Name", name);
        info.put("ID", ID);
        info.put("ResponsibleUserEmail",responsibleUserEmail);

        //Push hashmap to database.
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

    /*
    public void addInventoryList(CollectionReference fridge, final InventoryList listToAdd)
    {
        Map<String, Object>  InventoryList = new HashMap<>();
        CollectionReference listRef = fridge.document("Inventory").collection("Items");
        for (Item i:listToAdd.getItems()
             ) {
            addItem(listRef,i);
        }
    }
    */

    //Adds a new shopping list based on the passed ShoppingList to the fridge destination referenced in the parameters.
    public void addShoppingList(CollectionReference fridge, final ShoppingList listToAdd, String listName, String listID)
    {
        //Create new Hashmap.
        Map<String, Object>  ShoppingList = new HashMap<>();
        //Get database reference to where the list should be pushed to.
        CollectionReference listRef = fridge.document("ShoppingLists").collection(listID);
        //Add pseudo-item containing list information including name and ID.
        addListInfo(listRef,listName,listID,"None");
        //Add all items from passed list.
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
        //Add list ID to list of IDs in database.
        addID2listofShoppingListIDs(fridge,listID);
    }

    //Adds the passed list ID to a list in the fridge that keeps track of which shopping Lists must be subscribed to.
    //This was a necessary workaround to keep track of lists.
    public void addID2listofShoppingListIDs(CollectionReference fridge, String listID)
    {
        //Get database reference to list of ShoppingList IDs.
        CollectionReference ID_Ref = fridge.document("ShoppingList_IDs").collection("IDs");
       //Create a new hashmap containing the ID.
        Map<String, Object> info = new HashMap<>();
        info.put("ID", listID);

        //Push hashmap to database.
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

    //Adds a new ingredient list based on the passed IngredientList to the fridge destination referenced in the parameters.
    public void addIngredientList(CollectionReference fridge, final IngredientList listToAdd, String listName, String listID)
    {
        //Create new Hashmap.
        Map<String, Object>  IngredientList = new HashMap<>();
        //Get database reference to where the list should be pushed to.
        CollectionReference listRef = fridge.document("IngredientLists").collection(listID);
        //Add pseudo-item containing list information including name and ID.
        addListInfo(listRef,listName,listID,"None");
        //Add all items from passed list.
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
        //Add list ID to list of IDs in database.
        addID2listofShoppingListIDs(fridge,listID);
    }

    //Adds the passed list ID to a list in the fridge that keeps track of which ingredient Lists must be subscribed to.
    //This was a necessary workaround to keep track of lists.
    public void addID2listofIngredientListIDs(CollectionReference fridge, String listID)
    {
        //Get database reference to list of ShoppingList IDs.
        CollectionReference ID_Ref = fridge.document("IngredientList_IDs").collection("IDs");
        //Get database reference to where the list should be pushed to.
        Map<String, Object> info = new HashMap<>();
        info.put("ID", listID);

        //Push hashmap to database.
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

    /*
    public void addEssentialsList(CollectionReference fridge, final EssentialsList listToAdd)
    {
        Map<String, Object>  EssentialsList = new HashMap<>();
        CollectionReference listRef = fridge.document("Essentials").collection("Items");
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }
    }*/

    //Gets information from database and creates InventoryList object from this, which is passed back through callback interface.
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

                    //create new InventoryList object.
                    InventoryList inventoryList = new InventoryList();

                    //Add all items from database to InventoryList object.
                    for (Item i: itemList
                         ) {
                        inventoryList.AddItem(i);
                        Log.d(TAG, "Item in inventory list: " + i.getName());
                    }

                    //Notify through callback interface.
                    callbackInterface.onInventoryChange(fridge.getParent().getId(),inventoryList);

                }
            }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed to get InventoryList");
                }
            });
    }


    //Gets information from database and creates InventoryList object from this, which is passed back through callback interface.
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

                            //create new EssentialsList object.
                            EssentialsList essentialsList = new EssentialsList();

                            //Add all items from database to InventoryList object.
                            for (Item i: itemList
                                    ) {
                                //Do something with items.
                                essentialsList.AddItem(i);
                                Log.d(TAG, "Item on Essentials list: " + i.getName());
                            }

                            //Notify through callback interface.
                            callbackInterface.onEssentialsChange(fridge.getParent().getId(),essentialsList);

                        }
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to get EssentialsList");
            }
        });
    }

    //Gets information from database and creates ShoppingList object from this, which is passed back through callback interface.
    public void getShoppingList(final CollectionReference fridge, final String ID)
    {
        //Create placeholder list.
        final ShoppingList shoppingList = new ShoppingList("NO_NAME_YET",ID);

        //get ID for fridge.
        final String fridge_ID=fridge.getParent().getId();

        //reference to place in database where ShoppingLists for the given fridge is.
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
                                        //shoppingList.setName(i.getName());
                                    }
                                    else
                                    {
                                        //Add item to list.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        shoppingList.AddItem(i);
                                    }
                                }
                                //If only item in list was the Info-object, delete the list, both in database and locally.
                                if(itemList.size()<2)
                                {
                                    /*
                                    deleteShoppingListFromDatabase(fridge_ID,ID);
                                    //delete list locally.
                                    callbackInterface.onShoppingListDelete(fridge.getParent().getId(),shoppingList);
                                    */
                                }
                                //If list name has not been set yet, don't do anything.
                                if(shoppingList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                //Else, notyfi through callback interface.
                                else
                                {
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

        //Search for "Info"-document to set name, ID and responsible user for list.
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

                            //Set info for list.
                            shoppingList.setName(name);
                            shoppingList.setID(ID);
                            shoppingList.setResponsibility(responsibleUser);

                            //If items have not been added to list yet, do nothing.
                            if(shoppingList.getItems().size()==0)
                            {
                                Log.d(TAG, "onSuccess: List items have not been added yet, and thus list is not returned yet.");
                            }
                            //If items have been added to list, notify through callback interface.
                            else
                            {
                                callbackInterface.onShoppingListsChange(fridge.getParent().getId(),shoppingList);
                            }
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getShoppingList - failed to get Info Object");
            }
        });
    }

    //Gets information from database and creates ShoppingList object from this, which is passed back through callback interface.
    public void getIngredientList(final CollectionReference fridge, final String ID)
    {
        //Create placeholder list.
        final IngredientList ingredientList = new IngredientList("NO_NAME_YET",ID);

        //get ID for fridge.
        final String fridge_ID=fridge.getParent().getId();

        //reference to place in database where ShoppingLists for the given fridge is.
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
                                        //ingredientList.setName(i.getName());
                                    }
                                    else
                                    {
                                        //Add item to list.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        ingredientList.AddItem(i);
                                    }
                                }
                                //If only item in list was the Info-object, delete the list, both in database and locally.
                                if(itemList.size()<2)
                                {
                                    /*
                                    //TODO:delete list in database.
                                    deleteIngredientListFromDatabase(fridge_ID,ID);

                                    //delete list locally.
                                    callbackInterface.onIngredientListDelete(fridge.getParent().getId(),ingredientList);
                                    */
                                }
                                //If list name has not been set yet, don't do anything.
                                if(ingredientList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                ///Else, notify through callback interface.
                                else
                                {
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

        //Search for "Info"-document to set name, ID and responsible user for list.
        ingredientListReference.document("Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "getIngredientList - got Info object.");

                        if(documentSnapshot.exists())
                        {
                            //Get info from document
                            String name = documentSnapshot.get("Name").toString();
                            String ID = documentSnapshot.get("ID").toString();

                            Log.d(TAG, "onSuccess: Name="+name + ", ID="+ID);

                            //Set info for list.
                            ingredientList.setName(name);
                            ingredientList.setID(ID);

                            //If items have not been added yet, do nothing.
                            if(ingredientList.getItems().size()==0)
                            {
                                Log.d(TAG, "onSuccess: List items have not been added yet, and thus list is not returned yet.");
                            }
                            //Else, notify through callback interface.
                            else
                            {
                                callbackInterface.onIngredientListsChange(fridge.getParent().getId(),ingredientList);
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

    //Deletes a shopping list in the database with the given list ID from the fridge with the given fridge ID
    private void deleteShoppingListFromDatabase(String fridge_id, final String list_ID)
    {
        //reference to the fridge in the database.
        DocumentReference fridgeRef = db.collection("Fridges").document(fridge_id);
        //Reference to the list of ShoppingList IDs for the given fridge.
        final CollectionReference IDsRef = fridgeRef.collection("Content").document("ShoppingList_IDs").collection("IDs");

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

    //Deletes an ingredient list in the database with the given list ID from the fridge with the given fridge ID
    private void deleteIngredientListFromDatabase(String fridge_id, final String list_ID)
    {
        //reference to the fridge in the database.
        DocumentReference fridgeRef = db.collection("Fridges").document(fridge_id);
        //Reference to the list of IngredientList IDs for the given fridge.
        final CollectionReference IDsRef = fridgeRef.collection("Content").document("IngredientList_IDs").collection("IDs");

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

    //get name for fridge with given ID.
    public void getFridgeName(final String fridgeID){

        //reference to fridge in database.
        DocumentReference docRef = db.collection("Fridges").document(fridgeID);

        //get document for given fridge.
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                        if (document != null){
                            String name = document.getString("Name");
                            Log.d(TAG, "The name of fridgeID: " + fridgeID + " is: " + name);
                            //notify through callback interface.
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

    //Subscribe to be notified through callback interface when there are changes in fridge with given ID.
    public void SubscribeToFridge(final String fridgeID)
    {
        //Reference to fridge in database.
        DocumentReference fridgeRef = db.collection("Fridges").document(fridgeID);
        Log.d(TAG, "SubscribeToFridge: Subscribing to fridge with ID " + fridgeID);
        //Reference to lists in fridge.
        CollectionReference fridgeListRef=fridgeRef.collection("Content");

        //Add snapshotListener to lists.
        fridgeListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Toast.makeText(context, "Fridge with ID: " + fridgeID + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Fridge: " + fridgeID + " updated.");
            }
        });

        getFridgeName(fridgeID);

        //Subscribe to all lists of fridge.
        SubscribeToInventory(fridgeRef, fridgeID);
        SubscribeToEssentials(fridgeRef, fridgeID);
        SubscribeToShoppingLists(fridgeRef, fridgeID);
        SubscribeToIngredientLists(fridgeRef, fridgeID);
    }

    //Subscribe to be notified through callback interface whenever there are changes in the inventory in database for given fridge.
    private void SubscribeToInventory(final DocumentReference fridge, final String fridgeID)
    {
        final CollectionReference fridgeListRef=fridge.collection("Content");
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridgeListRef.document("Inventory").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "SubscribeToFridge - Inventory of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getInventoryList(fridgeListRef);
            }
        });
    }

    //Subscribe to be notified through callback interface whenever there are changes in the essentials in database for given fridge.
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

    //Subscribe to be notified through callback interface whenever there are changes in the any of the shopping lists in database for given fridge.
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

    //Subscribe to be notified through callback interface whenever there are changes to the given list.
    public void SubscribeToShoppingList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getShoppingListIDs: Subscribing to shopping list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("ShoppingLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "SubscribefToFridge - Shopping List " +listID + " of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getShoppingList(fridgeListRef,listID);
            }
        });
    }

    //Subscribe to be notified through callback interface whenever there are changes in the any of the ingredient lists in database for given fridge.
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

    //Subscribe to be notified through callback interface whenever there are changes to the given list.
    public void SubscribeToIngredientList(final CollectionReference fridgeListRef, final String listID, final String fridgeID)
    {
        Log.d(TAG, "getIngredientListIDs: Subscribing to ingredient list: " + listID);

        //Subscribe to receive notifications every time there's a change in the Shopping list.
        fridgeListRef.document("IngredientLists").collection(listID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "SubscribefToFridge - Ingredient List " +listID + " of Fridge: " + fridgeID + " updated.");

                //get new data and broadcast changes
                getIngredientList(fridgeListRef,listID);
            }
        });
    }



    //Unsubscribe from a fridge to stop getting new data when there are changes in database.
    public void UnSubscribeToFridge(final String fridgeID)
    {
        DocumentReference fridgeRef = db.collection("Fridges").document(fridgeID);
        Log.d(TAG, "SubscribeToFridge: UnSubscribing to fridge with ID " + fridgeID);
        CollectionReference fridgeListRef=fridgeRef.collection("Content");

        //Add new empty snapshotlistener.
        fridgeListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                //Do nothing
            }
        });

        getFridgeName(fridgeID);

        //Unsubscribe from all lists.
        UnSubscribeToInventory(fridgeRef, fridgeID);
        UnSubscribeToEssentials(fridgeRef, fridgeID);
        UnSubscribeToShoppingLists(fridgeRef, fridgeID);
        UnSubscribeToIngredientLists(fridgeRef, fridgeID);
    }

    //Unsubscribe from inventory to stop receiving new data on change in database for the given fridge.
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

    //Unsubscribe from inventory to stop receiving new data on change in database for the given fridge.
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

    //Unsubscribe from shopping lists to stop receiving new data on change in database for the given fridge.
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

    //Unsubscribe from shopping list to stop receiving new data on change in database for the given fridge.
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

    //Unsubscribe from ingredient lists to stop receiving new data on change in database for the given fridge.
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

    //Unsubscribe from ingredient lsit to stop receiving new data on change in database for the given fridge.
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

    //Create a new fridge in database.
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

    //Sets the responsibility for a shopping list in the database.
    public void setResponsibilityForListShoppingList(String fridge_ID, String list_ID, String ResponsibleUser)
    {
        final DocumentReference fridgesRef = db.collection("Fridges").document(fridge_ID);
        final CollectionReference listRef = fridgesRef.collection("Content").document("ShoppingLists").collection(list_ID);

        Map<String, Object>  newInfo = new HashMap<>();
        newInfo.put("ResponsibleUserEmail",ResponsibleUser);
        listRef.document("Info").update(newInfo);
    }

    //Create a new user in the database.
    public void createNewUserInDatabase(String Name, String email)
    {
        Map<String, Object>  UserInfo = new HashMap<>();
        UserInfo.put("Name",Name);
        UserInfo.put("email",email);

        db.collection("Users").document(email).set(UserInfo);
    }

    //Add fridge ID to list of Fridge subscribtions for the given user.
    public void addFridgeID2listOfFridgeSubscriptions(String fridge_ID, String userEmail)
    {
        //Create new hashmap with ID.
        Map<String, Object> info = new HashMap<>();
        info.put("ID", fridge_ID);

        //Push hashmap to database
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

    //Removes fridge id from the list of subscribed fridges for the user with the given userEmail.
    public void removeFridgeIDFromListOfFridgeSubscriptions(final String fridge_ID, String userEmail)
    {
        //Reference to list of fridgeSubscribtions.
        final CollectionReference listRef = db.collection("Users").document(userEmail).collection("FridgeSubscribtions");

        //Check if ID is on list.
        listRef.whereEqualTo("ID",fridge_ID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //If not on list, do nothing.
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            Log.d(TAG, "removeFridgeIDFromListOfFridgeSubscriptions - onSuccess: fridge with ID " + fridge_ID + " was not subscribed to in the first place");
                        }
                        //If on list, remove it.
                        else
                        {
                            Log.d(TAG, "removeFridgeIDFromListOfFridgeSubscriptions - onSuccess: Removing fridge ID " + fridge_ID + " from list of subscribed fridges.");
                            String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            listRef.document(snapshotID).delete();
                        }
                    }
                });
    }

    //Adds a user to the database, if a user with the given email adress does not exist yet.
    public void addUserToDatabaseIfNotThereAlready(final FirebaseUser user)
    {
        //Query for user with matching email-adress.
        db.collection("Users").document(user.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //If there is already a user with the given email, do nothing.
                        if (documentSnapshot.exists())
                        {
                            Log.d(TAG, "onSuccess:  addUserToDatabaseIfNotThereAlready - User with email " + user.getEmail() + " already exists in database, and this is not created");
                        }
                        //If no user with given email exists, create a new user.
                        else
                        {
                            createNewUserInDatabase(user.getDisplayName(),user.getEmail());
                            Log.d(TAG, "onSuccess: addUserToDatabaseIfNotThereAlready - User with email " + user.getEmail() + " created in database.");
                        }
                    }
                });
    }
}
