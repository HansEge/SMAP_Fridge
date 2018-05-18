package smap_f18_24.smap_fridge.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import smap_f18_24.smap_fridge.DAL.FridgeCallbackInterface;
import smap_f18_24.smap_fridge.DAL.fireStoreCommunicator;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;


public class ServiceUpdater extends Service {

    //This service handles all functionality of communication with the database ona a higher level of abstraction.
    //It is through this service that all activities and fragments make changes to the database.

    //database reference
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "ServiceUpdater";
    private static final String ON_CHANGE_DEBUG_TAG = "onChangeDebug";
    
    //Used for binding service to activity
    private final IBinder mBinder = new ServiceBinder();

    public static final String BROADCAST_UPDATER_RESULT = "smap_f18_24.smap_fridge.Service.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";

    //context stuff
    Context context;
    FirebaseUser currentUser;

    //Object to communicate with database
    fireStoreCommunicator dbComm;

    //Local list of fridges. This is the list that the activities/fragments can fetch information from.
    ArrayList<Fridge> fridges = new ArrayList<Fridge>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");

        //Create new instance of the database communicator
        dbComm=new fireStoreCommunicator(getApplicationContext(),callbackInterface);

        //Receive updates when fridges that the user has subscribed to change.
        currentUser = getCurrentUserInformation();
        dbComm.SubscribeToSavedFridges(getCurrentUserEmail(),callbackInterface);
    }

    public void setContext(Context c)
    {
        context = c;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    //Sends a broadcast that new data is availabe.
    public void broadcastResult(String result)
    {
        Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(BROADCAST_UPDATER_RESULT);
        broadcastIntent.putExtra(EXTRA_TASK_RESULT,result);

        LocalBroadcastManager BCManager = LocalBroadcastManager.getInstance(context);

        if(BCManager.sendBroadcast(broadcastIntent))
        {
            Log.d("BROADCAST_SEND","Success on sending broadcast");
        }
    }



    //Used for binding service
    public class ServiceBinder extends Binder {
        public ServiceUpdater getService(){
            return ServiceUpdater.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Returns current user-object.
    public FirebaseUser getCurrentUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

    //returns name of current user.
    public String getCurrentUserName(){

        String userName = getCurrentUserInformation().getDisplayName();
        return userName;

    }

    //returns email of current user.
    public String getCurrentUserEmail(){
        String userEmail = getCurrentUserInformation().getEmail();
        return userEmail;
    }


    //Used for communicating back results from database queries.
    FridgeCallbackInterface callbackInterface = new FridgeCallbackInterface() {

        //gets called when there are changes to an inventory list of a subscribed fridge.
        @Override
        public void onInventoryChange(String fridge_ID, InventoryList list) {
            Log.d(TAG, "Inventory of fridge " + fridge_ID + " updated.");
            Log.d(ON_CHANGE_DEBUG_TAG, "onInventoryChange called ");
            //Update list for fridge with matching fridge ID

            //check for fridge with matching ID.
            for (Fridge f: fridges
                    ) {
                if(f.getID().equals(fridge_ID))
                {
                    //if ID matches, update inventory and broadcast that new data is available
                    f.setInventory(list);
                    broadcastResult(getString(R.string.DATA_UPDATED));

                    //Make sure that whatever is on Essentials is either in Inventory or on "Essentials Shopping List"
                    updateShoppingListToMatchEssentials(fridge_ID);
                    return;
                }
            }
            //If no matching fridge, create new fridge with list
            Fridge placeholderFridge = new Fridge();
            placeholderFridge.setInventory(list);
            placeholderFridge.setID(fridge_ID);

            fridges.add(placeholderFridge);
            //Broadcast that new data is available.
            broadcastResult(getString(R.string.DATA_UPDATED));
            //Make sure that whatever is on Essentials is either in Inventory or on "Essentials Shopping List"
            updateShoppingListToMatchEssentials(fridge_ID);
        }

        //gets called when there are changes to an essentials list of a subscribed fridge.
        @Override
        public void onEssentialsChange(String fridge_ID, EssentialsList list) {
            Log.d(TAG, "Essential of fridge " + fridge_ID + " updated.");
            Log.d(ON_CHANGE_DEBUG_TAG, "onEssentialsChange called ");
            //Update list for fridge with matching fridge ID

            //check for fridge with matching ID.
            for (Fridge f: fridges
                    ) {
                if(f.getID().equals(fridge_ID))
                {
                    //if ID matches, update essentials and broadcast that new data is availabe
                    f.setEssentials(list);

                    Log.d("BROADCASTFROMSERVICE", "BROADCAST!");
                    broadcastResult(getString(R.string.DATA_UPDATED));
                    updateShoppingListToMatchEssentials(fridge_ID);

                    return;
                }
            }
            //If no matching fridge, create new fridge with list
            Fridge placeholderFridge = new Fridge();
            placeholderFridge.setEssentials(list);
            placeholderFridge.setID(fridge_ID);

            fridges.add(placeholderFridge);
            //Broadcast that new data is available.
            broadcastResult(getString(R.string.DATA_UPDATED));

            //Make sure that whatever is on Essentials is either in Inventory or on "Essentials Shopping List"
            updateShoppingListToMatchEssentials(fridge_ID);

        }

        //gets called when there are changes to an shopping list of a subscribed fridge.
        @Override
        public void onShoppingListsChange(String fridge_ID, ShoppingList list) {
            Log.d(TAG, "Shopping list " + list.getID() + " of fridge " + fridge_ID + " updated.");
            Log.d(ON_CHANGE_DEBUG_TAG, "onShoppingListsChange called ");

            ArrayList<ShoppingList> shoppingLists = null;

            //Update list for fridge with matching fridge ID and matching list ID.

            //check for fridge with matching ID.
            try
            {
                for (Fridge f: fridges
                        ) {
                    if(f.getID().equals(fridge_ID))
                    {
                        //get current list of ShoppingLists
                        shoppingLists = (ArrayList<ShoppingList>) f.getShoppingLists();

                        //Check if list is initiliazed yet.
                        if(shoppingLists!=null)
                        {
                            //If initialized, check for shopping list with matching ID.
                            for (ShoppingList s: shoppingLists
                                    ) {
                                //If it exists, overwrite with updated list.
                                if(s.getID().equals(list.getID()))
                                {
                                    int index=shoppingLists.indexOf(s);
                                    shoppingLists.remove(s);
                                    shoppingLists.add(index,list);
                                    broadcastResult(getString(R.string.DATA_UPDATED));
                                    return;
                                }
                            }
                            //If Shopping list is not on list of ShoppingLists yet, add it.
                            shoppingLists.add(list);
                            broadcastResult(getString(R.string.DATA_UPDATED));
                        }

                        //If list of shopping lists is not initialized yet, do so.
                        else
                        {
                            shoppingLists=new ArrayList<ShoppingList>();
                            shoppingLists.add(list);
                            f.setShoppingLists(shoppingLists);
                            broadcastResult(getString(R.string.DATA_UPDATED));
                        }
                    }
                }
            }
            catch(RuntimeException e)
            {
                //If no matching fridge, create new fridge with list
                Fridge placeholderFridge = new Fridge();
                shoppingLists = new ArrayList<ShoppingList>();
                shoppingLists.add(list);
                placeholderFridge.setShoppingLists(shoppingLists);
                placeholderFridge.setID(fridge_ID);

                //Broadcast that new data is available.
                broadcastResult(getString(R.string.DATA_UPDATED));
            }
        }

        //Not used in current implementation
        @Override
        public void onShoppingListDelete(String fridge_ID, ShoppingList list) {

        }

        //gets called when there are changes to an ingredient list of a subscribed fridge.
        @Override
        public void onIngredientListsChange(String fridge_ID, IngredientList list) {
            Log.d(TAG, "Ingredient list " + list.getID() + " of fridge " + fridge_ID + " updated.");
            Log.d(ON_CHANGE_DEBUG_TAG, "onIngredientListsChange called ");
            ArrayList<IngredientList> ingredientLists = null;

            //Update list for fridge with matching fridge IDF and matching list ID.

            //check for fridge with matching ID.
            try {
                for (Fridge f : fridges
                        ) {
                    if (f.getID().equals(fridge_ID)) {
                        //get current list of ShoppingLists
                        ingredientLists = (ArrayList<IngredientList>) f.getIngredientLists();

                        //Check if list is initiliazed yet.
                        if (ingredientLists != null) {
                            //If initialized, check for ingredient list with matching ID.
                            for (IngredientList s : ingredientLists
                                    ) {
                                //If it exists, overwrite with updated list.
                                if (s.getID().equals(list.getID())) {
                                    int index = ingredientLists.indexOf(s);
                                    ingredientLists.remove(s);
                                    ingredientLists.add(index, list);
                                    //Broadcast that new data is available.
                                    broadcastResult(getString(R.string.DATA_UPDATED));
                                    return;
                                }
                            }
                            //If Ingredient list is not on list of IngredientLists yet, add it.
                            ingredientLists.add(list);
                            broadcastResult(getString(R.string.DATA_UPDATED));
                        }

                        //If list of ingredient lists is not initialized yet, do so.
                        else {
                            ingredientLists = new ArrayList<IngredientList>();
                            ingredientLists.add(list);
                            f.setIngredientLists(ingredientLists);
                            broadcastResult(getString(R.string.DATA_UPDATED));
                        }
                    }
                }
            } catch (RuntimeException e) {

                //If no matching fridge, create new fridge with list
                Fridge placeholderFridge = new Fridge();
                ingredientLists = new ArrayList<IngredientList>();
                ingredientLists.add(list);
                placeholderFridge.setIngredientLists(ingredientLists);
                placeholderFridge.setID(fridge_ID);

                broadcastResult(getString(R.string.DATA_UPDATED));
            }

        }

        //Not used in current implementaion
        @Override
        public void onIngredientListDelete(String fridge_ID, IngredientList list) {


        }

        //gets called when fridge name has been fetched from database.
        //Makes sure to add the correct name to the correct fridge.
        @Override
        public void onFridgeName(String id, String name) {
            Log.d(TAG, "onFridgeName: ID="+id + ", name="+name);
            try
            {
                getFridge(id).setName(name);
            }
            catch (RuntimeException e)
            {
                Log.d(TAG, "onFridgeName: fridge does not exist on list.");
            }

            broadcastResult(getString(R.string.DATA_UPDATED));
        }

        //When subscribing to a fridge, we want a placeholder fridge with the correct ID.
        @Override
        public void onSubscribingToFridge(String id) {
            //Add new placeholder fridge.
            Fridge fridgeToAdd = new Fridge();
            fridgeToAdd.setID(id);
            fridges.add(fridgeToAdd);
        }
    };

    //Returns all fridges currently in service
    public ArrayList<Fridge> getAllFridges()
    {
        return fridges;
    }

    //Subscribe to a fridge with the given ID.
    public void SubscribeToFridge(String ID)
    {
        //create Local placeholder fridge
        Fridge fridgeSubscribedTo = new Fridge();
        fridgeSubscribedTo.setID(ID);
        fridges.add(fridgeSubscribedTo);

        //add eventListeners.
        dbComm.SubscribeToFridge(ID);
    }

    //Return item with matching name from list.
    public Item getItem(String name, List<Item> listToSearch)
    {
        //For each item, check if name matches, and return if so. If no match, null is returned.
        for (Item i: listToSearch
             ) {
            if(i.getName().equals(name))
            {
                return i;
            }
        }
        return null;
    }

    //Return fridge with ID matching parameter from local list "fridges".
    public Fridge getFridge(String ID)
    {
        //For each fridge, check if ID matches. If no match, null is returned.
        for (Fridge f: fridges
             ) {
            if(f.getID().equals(ID))
            {
                return f;
            }
        }
        return null;
    }

    //Return Shopping List with ID matching parameter from passed list of ShoppingLists
    private ShoppingList getShoppingList(String ID, List<ShoppingList> lists)
    {
        //For each fridge, check if ID matches. If no match, null is returned.
        for (ShoppingList sl: lists
                ) {
            if(sl.getID().equals(ID))
            {
                return sl;
            }
        }
        return null;
    }

    //Return Ingredient List with ID matching parameter from passed list of IngredientLists
    private IngredientList getIngredientList(String ID, List<IngredientList> lists)
    {
        //For each fridge, check if ID matches. If no match, null is returned.
        for (IngredientList sl: lists
                ) {
            if(sl.getID().equals(ID))
            {
                return sl;
            }
        }
        return null;
    }

    //Add item to inventory - increments quantity, if item with matching name exists, else just add.
    public void addItemToInventory(Item item, String fridge_ID)
    {
        CollectionReference InventoryRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INVENTORY)).collection(getString(R.string.ITEMS));
        InventoryList inventory=getFridge(fridge_ID).getInventory();

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        if(inventory!=null)
        {
            for (Item i: inventory.getItems()
                    ) {
                if(i.getName().equals(item.getName()))
                {
                    //If item already exists, increase quantity.
                    float oldQty = i.getQuantity();
                    i.setQuantity(oldQty+item.getQuantity());
                    dbComm.addItem(InventoryRef, i);
                    Log.d(TAG, "addItemToInventory: Item already in inventory - Increasing qty " + oldQty+"->"+i.getQuantity());
                    return;
                }
            }
        }
        //If item was not in inventory yet, just add it to list.
        dbComm.addItem(InventoryRef, item);
        Log.d(TAG, "addItemToInventory: Item was not in inventory yet, and has thus been added.");
    }

    //removes item from inventory
    public void removeItemFromInventory(String itemName, String fridge_ID)
    {
        CollectionReference InventoryRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INVENTORY)).collection(getString(R.string.ITEMS));

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        InventoryList inventory=getFridge(fridge_ID).getInventory();
        for (Item i: inventory.getItems()
                ) {
            if(i.getName().equals(itemName))
            {
                //If name matches, remove item.
                dbComm.removeItem(InventoryRef,itemName);
                if(inventory.getItems().size()==1)
                {
                    //Manually remove item from services local list, if las item. This is not optimal, since it may break syncronization with database. If no internet connection was available at the time of removing the item, the item will be removed locally, but not in database.
                    inventory.getItems().remove(i);
                    callbackInterface.onInventoryChange(fridge_ID,inventory);
                }
                return;
            }
        }
        //If no item on list with name <itemName>
        Log.d(TAG, "removeItemFromInventory: Item: " + itemName + " was not on list, and thus cannot be removed");
    }

    //adds item to inventory and overwrites old data if any exists.
    public void overwriteItemInInventory(Item item, String fridge_ID)
    {
        Log.d(TAG, "overwriteItemInInventory: Overwriting old data(if any) for item: " + item.getName());
        CollectionReference InventoryRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INVENTORY)).collection(getString(R.string.ITEMS));
        dbComm.addItem(InventoryRef, item);
    }

    //Add item to essentials - increments quantity, if item with matching name exists.
    public void addItemToEssentials(Item item, String fridge_ID)
    {
        CollectionReference EssentialsRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.ESSENTIALS)).collection(getString(R.string.ITEMS));
        EssentialsList essentials=getFridge(fridge_ID).getEssentials();

        //Check current essentials to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        if(essentials!=null)
        {
            for (Item i: essentials.getItems()
                    ) {
                if(i.getName().equals(item.getName()))
                {
                    //If item already exists, increase quantity.
                    float oldQty = i.getQuantity();
                    i.setQuantity(oldQty+item.getQuantity());
                    dbComm.addItem(EssentialsRef, i);
                    Log.d(TAG, "addItemToEssentials: Item already in essentials - Increasing qty " + oldQty+"->"+i.getQuantity());
                    return;
                }
            }
        }
        //If item was not in inventory yet, just add it to list.
        dbComm.addItem(EssentialsRef, item);
        Log.d(TAG, "addItemToEssentials: Item was not in inventory yet, and has thus been added.");
    }

    //removes item from essentials
    public void removeItemFromEssentials(String itemName, String fridge_ID)
    {
        CollectionReference EssentialsRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.ESSENTIALS)).collection(getString(R.string.ITEMS));

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        EssentialsList essentials=getFridge(fridge_ID).getEssentials();
        for (Item i: essentials.getItems()
                ) {
            if(i.getName().equals(itemName))
            {
                //if name matches
                dbComm.removeItem(EssentialsRef,itemName);
                if(essentials.getItems().size()==1)
                {
                    //Manually remove item from services local list, if las item. This is not optimal, since it may break syncronization with database. If no internet connection was available at the time of removing the item, the item will be removed locally, but not in database.
                    essentials.getItems().remove(i);
                    callbackInterface.onEssentialsChange(fridge_ID,essentials);
                }
                return;

            }
        }
        //If no item on list with name <itemName>
        Log.d(TAG, "removeItemFromEssentials: Item: " + itemName + " was not on list, and thus cannot be removed");
    }

    //adds item to essentials and overwrites old data if any exists.
    public void overwriteItemInEssentials(Item item, String fridge_ID)
    {
        Log.d(TAG, "overwriteItemInEssentials: Overwriting old data(if any) for item: " + item.getName());
        CollectionReference EssentialsRef=db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.ESSENTIALS)).collection(getString(R.string.ITEMS));
        dbComm.addItem(EssentialsRef, item);
    }

    //Create a new shopping list with the given attributes, but with no items on it.
    public void createNewShoppingList(String fridge_ID, String list_name)
    {
        //generate ID from fridge ID and list name.
        String list_ID=fridge_ID+"_"+list_name;
        CollectionReference fridgeRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT));

        //create new Shopping List object, and set ID and Name.
        ShoppingList emptySL=new ShoppingList();
        emptySL.setID(list_ID);
        emptySL.setName(list_name);

        //Add shopping list in database.
        dbComm.addShoppingList(fridgeRef,emptySL,list_name,list_ID);
        CollectionReference IDs_ref=fridgeRef.document(getString(R.string.SHOPPING_LIST_IDS)).collection(getString(R.string.IDS));

        //Add List info to database.
        dbComm.addListInfo(IDs_ref,list_name,list_ID,getString(R.string.NONE));

        //Add List ID to list of lists to subscribe to.
        dbComm.addID2listofShoppingListIDs(fridgeRef,list_ID);

        //subscribe to list.
        dbComm.SubscribeToShoppingList(fridgeRef,list_ID,fridge_ID);
    }

    //add Item to Shopping List. Increments quantity, if item with matching name exists.
    // Shopping list must exist.
    public void addItemToShoppingList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.SHOPPING_LISTS)).collection(list_ID);

       //Find list with matching name
        ArrayList<ShoppingList> shoppingLists=(ArrayList<ShoppingList>)getFridge(fridge_ID).getShoppingLists();
        //If list exists, check for item with matching name.
        if(shoppingLists!=null)
        {
            for (ShoppingList s: shoppingLists
                    ) {
                if(s.getID().equals(list_ID))
                {
                    //Check current list to see if item already exists.
                    //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
                    for (Item i: s.getItems()
                            ) {
                        if(i.getName().equals(item.getName()))
                        {
                            //If item with same name exists, increase quantity.
                            float oldQty = i.getQuantity();
                            i.setQuantity(oldQty+item.getQuantity());
                            dbComm.addItem(listRef, i);
                            Log.d(TAG, "addItemToShoppingList: Item already on shopping list - Increasing qty " + oldQty+"->"+i.getQuantity());
                            return;
                        }
                    }
                    //If item was not in shopping list yet, just add it to list.
                    dbComm.addItem(listRef,item);
                    return;
                }
            }
        }
        //If list doesn't exist yet, just add item (list will be generated).
        ShoppingList newSL = new ShoppingList();
        newSL.setName(list_name);
        newSL.setID(list_ID);
        newSL.AddItem(item);
        CollectionReference fridgeRef=listRef.getParent().getParent();
        dbComm.addShoppingList(fridgeRef,newSL,list_name,list_ID);
        dbComm.SubscribeToShoppingList(fridgeRef,list_ID,fridge_ID);
    }

    //adds item to shopping list and overwrites old data if any exists.
    public void overWriteItemInShoppingList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.SHOPPING_LISTS)).collection(list_ID);

        Log.d(TAG, "overWriteItemInShoppingList: Overwriting old data(if any) for item:" + item.getName());
        //If list doesn't exist yet, just add item (list will be generated).
        if(getFridge(fridge_ID).getShoppingLists()==null)
        {
            ShoppingList newSL = new ShoppingList();
            newSL.setName(list_name);
            newSL.setID(list_ID);
            newSL.AddItem(item);
            CollectionReference fridgeRef=listRef.getParent().getParent();
            dbComm.addShoppingList(fridgeRef,newSL,list_name,list_ID);
            dbComm.SubscribeToShoppingList(fridgeRef,list_ID,fridge_ID);
        }
        else
        {
            dbComm.addItem(listRef, item);
        }
    }

    //Removes item with matching name from list in fridge.
    public void removeItemFromShoppingList(String itemName, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.SHOPPING_LISTS)).collection(list_ID);

        //Find list with matching name
        ArrayList<ShoppingList> shoppingLists=(ArrayList<ShoppingList>)getFridge(fridge_ID).getShoppingLists();
        if(shoppingLists!=null)
        {
            for (ShoppingList s: shoppingLists
                    ) {
                if(s.getID().equals(list_ID))
                {
                    //Check current list to see if item already exists.
                    //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
                    for (Item i: s.getItems()
                            ) {
                        if(i.getName().equals(itemName))
                        {
                            //If name matches.
                            Log.d(TAG, "removeItemFromShoppingList: Removing data for item: " + itemName);
                            dbComm.removeItem(listRef, itemName);
                            return;
                        }
                        Log.d(TAG, "removeItemFromShoppingList: Item: " + itemName + " was not on list, and thus cannot be removed");
                    }
                }
            }
        }
    }

    //Create a new ingredient list with the given attributes, but with no items on it.
    public void createNewIngredientList(String fridge_ID, String list_name)
    {
        String list_ID=fridge_ID+"_"+list_name;
        CollectionReference fridgeRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT));

        //Create new IngredientList object and set ID and name.
        IngredientList emptyIL=new IngredientList();
        emptyIL.setID(list_ID);
        emptyIL.setName(list_name);

        //Add list to database.
        dbComm.addIngredientList(fridgeRef,emptyIL,list_name,list_ID);
        CollectionReference IDs_ref=fridgeRef.document(getString(R.string.INGREDIENT_LIST_IDS)).collection(getString(R.string.IDS));

        //Add list info.
        dbComm.addListInfo(IDs_ref,list_name,list_ID,getString(R.string.NONE));

        //Add ID to list of lists to subscribe to.
        dbComm.addID2listofIngredientListIDs(fridgeRef,list_ID);

        //Subscribe to list.
        dbComm.SubscribeToIngredientList(fridgeRef,list_ID,fridge_ID);
    }

    //add Item to Ingredient List. Increments quantity, if item with matching name exists.
    // Ingredient list must exist.
    public void addItemToIngredientList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INGREDIENT_LISTS)).collection(list_ID);

        //Find list with matching name
        ArrayList<IngredientList> ingredientLists=(ArrayList<IngredientList>)getFridge(fridge_ID).getIngredientLists();
        //If list exists, check for item with matching name.
        if(ingredientLists!=null)
        {
            for (IngredientList s: ingredientLists
                    ) {
                if(s.getID().equals(list_ID))
                {
                    //Check current list to see if item already exists.
                    //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
                    for (Item i: s.getItems()
                            ) {
                        if(i.getName().equals(item.getName()))
                        {
                            float oldQty = i.getQuantity();
                            i.setQuantity(oldQty+item.getQuantity());
                            dbComm.addItem(listRef, i);
                            Log.d(TAG, "addItemToIngredientList: Item already on ingredient list - Increasing qty " + oldQty+"->"+i.getQuantity());
                            return;
                        }
                    }
                    //If item was not in ingredient list yet, just add it to list.
                    dbComm.addItem(listRef,item);
                    return;
                }
            }
        }
        //If list doesn't exist yet, just add item (list will be generated).
        IngredientList newIL = new IngredientList();
        newIL.setName(list_name);
        newIL.setID(list_ID);
        newIL.AddItem(item);
        CollectionReference fridgeRef=listRef.getParent().getParent();
        dbComm.addIngredientList(fridgeRef,newIL,list_name,list_ID);
        dbComm.SubscribeToIngredientList(fridgeRef,list_ID,fridge_ID);
    }

    //adds item to ingredient list and overwrites old data if any exists.
    public void overWriteItemInIngredientList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INGREDIENT_LISTS)).collection(list_ID);

        Log.d(TAG, "overWriteItemInIngredientList: Overwriting old data(if any) for item:" + item.getName());
        //If list doesn't exist yet, just add item (list will be generated).
        if(getFridge(fridge_ID).getIngredientLists()==null)
        {
            IngredientList newIL = new IngredientList();
            newIL.setName(list_name);
            newIL.setID(list_ID);
            newIL.AddItem(item);
            CollectionReference fridgeRef=listRef.getParent().getParent();
            dbComm.addIngredientList(fridgeRef,newIL,list_name,list_ID);
            dbComm.SubscribeToIngredientList(fridgeRef,list_ID,fridge_ID);
        }
        else
        {
            dbComm.addItem(listRef, item);
        }
    }

    public void removeItemFromIngredientList(String itemName, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(getString(R.string.FRIDGES)).document(fridge_ID).collection(getString(R.string.CONTENT)).document(getString(R.string.INGREDIENT_LISTS)).collection(list_ID);

        //Find list with matching name
        ArrayList<IngredientList> ingredientLists=(ArrayList<IngredientList>)getFridge(fridge_ID).getIngredientLists();
        if(ingredientLists!=null)
        {
            for (IngredientList s: ingredientLists
                    ) {
                if(s.getID().equals(list_ID))
                {
                    //Check current list to see if item already exists.
                    //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
                    for (Item i: s.getItems()
                            ) {
                        if(i.getName().equals(itemName))
                        {
                            Log.d(TAG, "removeItemFromIngredientList: Removing data for item: " + itemName);
                            dbComm.removeItem(listRef, itemName);
                            return;
                        }
                        Log.d(TAG, "removeItemFromIngredientList: Item: " + itemName + " was not on list, and thus cannot be removed");
                    }
                }
            }
        }
    }

    //Creates a new user in the database.
    public void createNewUser(String userName, String userEmail)
    {
        dbComm.createNewUserInDatabase(userName,userEmail);
    }

    //Adds fridge ID to list of subscribed fridges for given user.
    public void addFridgeIDtoListOfSubscribedFridges(String userEmail, String fridgeID)
    {
        dbComm.addFridgeID2listOfFridgeSubscriptions(fridgeID,userEmail);
    }
    //Removes fridge ID from list of subscribed fridges for given user.
    public void removeFridgeIDfromListOfSubscribedFridges(String userEmail, String fridgeID)
    {
        dbComm.removeFridgeIDFromListOfFridgeSubscriptions(fridgeID,userEmail);
    }

    //Sets responsibility for a shopping list to the given user.
    public void setResponsibilityForShoppingList(String Fridge_ID, String List_ID, String User_ID)
    {
        dbComm.setResponsibilityForListShoppingList(Fridge_ID,List_ID, User_ID);
    }


    //Creates a new fridge with the given ID and Name
    public void createNewFridge(String ID, String Name)
    {
        dbComm.createNewFridge(ID, Name);
    }

    //Checks if items in essentials are in inventory or on EssentialsShoppingList.
    //If not, or if quantity is too low, add items to EssentialsShoppingList.
    public void updateShoppingListToMatchEssentials(String fridgeID) {
        Fridge curFridge = getFridge(fridgeID);
        if (curFridge != null) {
            //Placeholders.
            ArrayList<Item> curInventory = null;
            ArrayList<Item> curEssentials = null;
            ArrayList<ShoppingList> curShoppingLists = null;
            ShoppingList curEssShoppingList = null;
            ArrayList<Item> curEssShoppingListItems = null;

            //get Inventory, Essenntials and EssentialsShoppingList for fridge.
            try {
                curInventory = (ArrayList<Item>) curFridge.getInventory().getItems();
                curEssentials = (ArrayList<Item>) curFridge.getEssentials().getItems();
                curShoppingLists = (ArrayList<ShoppingList>) curFridge.getShoppingLists();
            } catch (RuntimeException e) {
                Log.e(TAG, "updateShoppingListToMatchEssentials: Failed to get some list from curFridge", e);
            }

            //Get EssentialsShoppingList
            if (curShoppingLists != null) {
                curEssShoppingList = getShoppingList(getString(R.string.ESSENTIALS_SHOPPING_LIST), curShoppingLists);
            }

            if (curInventory != null && curEssentials != null) {
                try {
                    //Get list of items in Essentials Shopping List
                    curEssShoppingListItems = (ArrayList<Item>) curEssShoppingList.getItems();
                } catch (RuntimeException e) {
                    Log.e(TAG, "updateShoppingListToMatchEssentials: curEssShoppingListItems=null", e);
                }

                //For each item
                for (Item i : curEssentials
                        ) {

                    //Check if quantity in Inventory + quantity in EssentialsShoppingList is greater than desired quantity in essentials.
                    float totalQuantity = 0;
                    Item itemInInventory = getItem(i.getName(), curInventory);
                    Item itemInEssShoppingList = null;
                    if (curEssShoppingListItems != null) {
                        itemInEssShoppingList = getItem(i.getName(), curEssShoppingListItems);
                    }

                    if (itemInInventory != null) {
                        totalQuantity += itemInInventory.getQuantity();
                    }
                    if (itemInEssShoppingList != null) {
                        totalQuantity += itemInEssShoppingList.getQuantity();
                    }
                    //If total quantity is less than desired quantity.
                    if (totalQuantity < i.getQuantity()) {
                        //Make matching item, whose quantity is the difference of desired quantity and total quantity.
                        Item itemToAdd = new Item(i.getName(),i.getUnit(),i.getQuantity(),i.getResponsibleUserEmail(),i.getItemStatus());
                        itemToAdd.setQuantity(i.getQuantity() - totalQuantity);
                        addItemToShoppingList(itemToAdd, fridgeID, getString(R.string.ESSENTIALS_SHOPPING_LIST_NAME), getString(R.string.ESSENTIALS_SHOPPING_LIST));
                    }
                }
            }
        }
    }

    //Unsubscribe the user with the given email from the fridge with the given ID.
    public void UnsubscribeFromFridge(String fridge_ID, String userEmail)
    {
        //Unsubscribe eventListeners.
        dbComm.UnSubscribeToFridge(fridge_ID);

        //remove from local list
        fridges.remove(getFridge(fridge_ID));

        //remove fridge ID from list in database.
        removeFridgeIDfromListOfSubscribedFridges(userEmail, fridge_ID);
    }

    //Checks if items in given Ingredient List are in inventory or on given ShoppingList.
    //If not, or if quantity is too low, add items to Shopping List.
    public void UpdateShoppingListFromIngredientList(String fridgeID, IngredientList ingredientList, ShoppingList targetShoppingList)
    {
        Fridge curFridge = getFridge(fridgeID);
        if (curFridge != null) {
            //Placeholders.
            ArrayList<Item> curInventory = new ArrayList<>();
            ArrayList<ShoppingList> curShoppingLists = new ArrayList<>();
            ArrayList<Item> currentIngredientListItems = (ArrayList<Item>)ingredientList.getItems();

            //get Inventory and Shopping Lists for fridge.
            try {
                curInventory = (ArrayList<Item>) curFridge.getInventory().getItems();
                curShoppingLists = (ArrayList<ShoppingList>) curFridge.getShoppingLists();
            } catch (RuntimeException e) {
                Log.e(TAG, "updateShoppingListToMatchEssentials: Failed to get some list from curFridge", e);
            }

            //For each item
            for (Item i : currentIngredientListItems
                    ) {

                //Check if quantity in Inventory + quantity in Shopping List is greater than desired quantity in the ingredient list.
                float totalQuantity = 0;

                //Get item with matching name from inventory.
                Item itemInInventory = getItem(i.getName(), curInventory);
                if(itemInInventory!=null)
                {
                    totalQuantity+=itemInInventory.getQuantity();
                }

                //Get item with matching name from shopping list.
                Item item = getItem(i.getName(),targetShoppingList.getItems());
                if(item!=null)
                {
                    totalQuantity+=item.getQuantity();
                }

                //If total quantity is less than desired quantity.
                if (totalQuantity < i.getQuantity()) {
                    Log.d(TAG, "UpdateShoppingListFromIngredientList: totalQuantity="+totalQuantity+", desired quantity="+i.getQuantity());
                    Log.d(TAG, "UpdateShoppingListFromIngredientList: Adding " + (i.getQuantity()-totalQuantity)+" to ShoppingList " + targetShoppingList.getName());
                    //Make matching item, whose quantity is the difference of desired quantity and total quantity.
                    Item itemToAdd = i;
                    itemToAdd.setQuantity(i.getQuantity() - totalQuantity);
                    addItemToShoppingList(itemToAdd, fridgeID, targetShoppingList.getName(), targetShoppingList.getID());
                }
            }

        }
    }

    //Deletes a shopping list with the given list ID from fridge with the given fridge ID
    public void deleteShoppingList(String fridge_ID, String list_ID)
    {
        //Delete local copy
        Fridge currentFridge = getFridge(fridge_ID);
        ShoppingList listToDelete = getShoppingList(list_ID,currentFridge.getShoppingLists());
        currentFridge.getShoppingLists().remove(listToDelete);

        //Delete from database
        dbComm.deleteShoppingListFromDatabase(fridge_ID,list_ID);
        broadcastResult(getString(R.string.DATA_UPDATED));
    }

    public void deleteIngredientsList(String fridge_ID, String list_ID)
    {
        //Delete local copy
        Fridge currentFridge = getFridge(fridge_ID);
        IngredientList listToDelete = getIngredientList(list_ID,currentFridge.getIngredientLists());
        currentFridge.getIngredientLists().remove(listToDelete);

        //Delete from database
        dbComm.deleteIngredientListFromDatabase(fridge_ID,list_ID);
        broadcastResult(getString(R.string.DATA_UPDATED));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
    }
}


