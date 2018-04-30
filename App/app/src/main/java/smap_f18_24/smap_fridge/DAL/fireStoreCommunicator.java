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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import smap_f18_24.smap_fridge.ModelClasses.List_ID;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;

public class fireStoreCommunicator {

    public fireStoreCommunicator(Context context, FridgeCallbackInterface callbackInterface)
    {
        this.context=context;
        this.callbackInterface=callbackInterface;
    }

    FridgeCallbackInterface callbackInterface;
    Context context;
    private static final String TAG = "fireStoreCommunicator";

public void addItem(final CollectionReference destination, final Item itemToAdd)
{


    //Check whether item exists on list already or not.
    destination.whereEqualTo("Name",itemToAdd.getName()).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    //If old data exists, remove it.
                    if(!queryDocumentSnapshots.isEmpty())
                    {
                        Log.d(TAG, "onSuccess: Removing old data for item with name: " + itemToAdd.getName());
                        //removing old data.
                        String snapshotID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Log.d(TAG, "SNapshotID: " + snapshotID);
                        destination.document(snapshotID).delete();
                    }

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
        CollectionReference listRef = fridge.document("IngredientLists").collection(listName);
        addListInfo(listRef,listName,listID);
        for (Item i:listToAdd.getItems()
                ) {
            addItem(listRef,i);
        }

        //Add list ID to IngredientList_IDs
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
                    Log.d(TAG, "onSuccess: INVENTORY LIST EMPTY");
                    return;
                } else {
                    //Convert documentShapshots to list of items.
                    List<Item> itemList = documentSnapshots.toObjects(Item.class);

                    Log.d(TAG, "Broadcasting Inventory list");
                    for (Item i: itemList
                         ) {
                        //TODO: broadcast new list?
                        //Do something with items.
                        Log.d(TAG, "Item in inventory list: " + i.getName());
                    }
                    callbackInterface.onInventoryChange();

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
                            Log.d(TAG, "onSuccess: ESSENTIALS LIST EMPTY");
                            return;
                        } else {

                            //Convert documentShapshots to list of items.
                            List<Item> itemList = documentSnapshots.toObjects(Item.class);


                            Log.d(TAG, "Broadcasting Essentials list");
                            for (Item i: itemList
                                    ) {
                                //Do something with items.
                                Log.d(TAG, "Item on Essentials list: " + i.getName());
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

        final ShoppingList shoppingList = new ShoppingList("NO_NAME_YET",ID);

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
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        shoppingList.AddItem(i);
                                    }
                                }
                                if(shoppingList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                else
                                {
                                    //TODO: Return list through callback interface/broadcast new list.
                                    Log.d(TAG, "Broadcasting Shopping list: " + shoppingList.getName());
                                    for (Item i: shoppingList.getItems()
                                         ) {
                                        Log.d(TAG, "Item on Shopping list: " + i.getName());
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

        shoppingListReference.document("Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "getShoppingList - got Info object.");

                        String name = documentSnapshot.get("Name").toString();
                        String ID = documentSnapshot.get("ID").toString();

                        Log.d(TAG, "onSuccess: Name="+name + ", ID="+ID);

                        shoppingList.setName(name);
                        shoppingList.setID(ID);

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

    public void getIngredientList(CollectionReference fridge, String ID)
    {

        final IngredientList ingredientList = new IngredientList("NO_NAME_YET",ID);

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
                                    }
                                    else
                                    {
                                        //Do something with Item.
                                        Log.d(TAG, "onSuccess: Item: " + i.getName());
                                        ingredientList.AddItem(i);
                                    }
                                }
                                if(ingredientList.getName().equals("NO_NAME_YET"))
                                {
                                    Log.d(TAG, "onSuccess: List name has not been set yet, and thus list is not returned yet.");
                                }
                                else
                                {
                                    //TODO: Return list through callback interface/broadcast new list.
                                    Log.d(TAG, "Broadcasting Ingredient list: " + ingredientList.getName());
                                    for (Item i: ingredientList.getItems()
                                            ) {
                                        Log.d(TAG, "Item on Ingredient list: " + i.getName());
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
                Log.d(TAG, "onFailure: Failed to get IngredientList");
            }
        });

        ingredientListReference.document("Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "getIngredientList - got Info object.");

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
                            Log.d(TAG, "Broadcasting Shopping list: " + ingredientList.getName());
                            for (Item i: ingredientList.getItems()
                                    ) {
                                Log.d(TAG, "Item on Shopping list: " + i.getName());
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

    public void SubscribeToFridge(final CollectionReference fridge)
    {
        fridge.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Toast.makeText(context, "Fridge: " + fridge.getId() + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Fridge: " + fridge.getId() + " updated.");
            }
        });

        SubscribeToInventory(fridge);
        SubscribeToEssentials(fridge);
        SubscribeToShoppingLists(fridge);
        SubscribeToIngredientLists(fridge);
    }


    private void SubscribeToInventory(final CollectionReference fridge)
    {
        //Subscribe to receive notifications every time there's a change in the Inventory list.
        fridge.document("Inventory").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Toast.makeText(context, "Inventory of Fridge: " + fridge.getId() + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Inventory of Fridge: " + fridge.getId() + " updated.");

                //get new data and broadcast changes
                getInventoryList(fridge);
            }
        });
    }

    private void SubscribeToEssentials(final CollectionReference fridge)
    {
        //Subscribe to receive notifications every time there's a change in the Essentials list.
        fridge.document("Essentials").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                Toast.makeText(context, "Essentials of Fridge: " + fridge.getId() + " updated.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SubscribeToFridge - Essentials of Fridge: " + fridge.getId() + " updated.");

                //get new data and broadcast changes
                getEssentialsList(fridge);
            }
        });
    }

    private void SubscribeToShoppingLists(final CollectionReference fridge)
    {
        //get IDs for all shopping lists.
        fridge.document("ShoppingList_IDs").collection("IDs").get()
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
                                Log.d(TAG, "getShoppingListIDs: Subscribing to shopping list: " + id.getID());

                                //Subscribe to receive notifications every time there's a change in the Shopping list.
                                fridge.document("ShoppingLists").collection(id.getID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        Toast.makeText(context, "Shopping list " +id.getID() + " of Fridge: " + fridge.getId() + " updated.", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "SubscribeToFridge - Shopping List " + id.getID() + " of Fridge: " + fridge.getId() + " updated.");

                                        //get new data and broadcast changes
                                        getShoppingList(fridge,id.getID());
                                    }
                                });

                            }
                        }
                    }
                });
    }

    private void SubscribeToIngredientLists(final CollectionReference fridge)
    {
//get IDs for all ingredient lists.
        fridge.document("IngredientList_IDs").collection("IDs").get()
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
                                Log.d(TAG, "getShoppingListIDs: Subscribing to Ingredient list: " + id.getID());

                                //Subscribe to receive notifications every time there's a change in the Shopping list.
                                fridge.document("IngredientLists").collection(id.getID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        Toast.makeText(context, "Ingredient list of Fridge: " + fridge.getId() + " updated.", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "SubscribeToFridge - Ingredient List of Fridge: " + fridge.getId() + " updated.");

                                        //get new data and broadcast changes
                                        getIngredientList(fridge,id.getID());
                                    }
                                });

                            }
                        }
                    }
                });
    }

}
