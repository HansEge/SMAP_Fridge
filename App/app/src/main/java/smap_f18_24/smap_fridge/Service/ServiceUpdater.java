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

    //database reference
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "ServiceUpdater";
    private static final String ON_CHANGE_DEBUG_TAG = "onChangeDebug";
    
    //Used for binding service to activity
    private final IBinder mBinder = new ServiceBinder();

    public static final String BROADCAST_UPDATER_RESULT = "smap_f18_24.smap_fridge.Service.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";

    //context shit
    Context context;

    fireStoreCommunicator dbComm;

    ArrayList<Fridge> fridges = new ArrayList<Fridge>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");

        dbComm=new fireStoreCommunicator(getApplicationContext(),callbackInterface);
        SubscribeToFridge("TestFridgeID");
    }

    public void setContext(Context c)
    {
        context = c;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Initialize list
        //fridges=new ArrayList<Fridge>();

        //Get instance of database-communicator
        //dbComm= new fireStoreCommunicator(context,callbackInterface);

        /*

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    notificationBuilder();

                    try {
                        Thread.sleep(2000); //Update stuff every xx ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        */

        Log.d("NOTI_FROM_SERVICE", "Notification from service");
        //TODO Update stuff

        return START_NOT_STICKY;
    }



    //Make notification displaying the time when updating.
    @TargetApi(26)
    void notificationBuilder()
    {
        //For API version < 26
        if (Build.VERSION.SDK_INT < 26) {
            //Log.d("API<26","bobby olsen");
            notificationBuilder_PRE26();
            return;
        }

        LocalDateTime time2 = LocalDateTime.now();

        NotificationChannel channel_1 = new NotificationChannel("CHANNEL_1","Fridge Stuff", NotificationManager.IMPORTANCE_HIGH);
        channel_1.setDescription("Notification for alerting user of changes");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.createNotificationChannel(channel_1);

        Notification updateNotification =
                new Notification.Builder(this,"CHANNEL_1")
                .setContentTitle("Stuff was updated!")
                .setContentText("Mathias lugtede kl " + time2.format(DateTimeFormatter.ISO_LOCAL_TIME))
                .setSmallIcon(R.drawable.stinus_face)
                .build();
        startForeground(123,updateNotification);
    }


    //For API < 26
    void notificationBuilder_PRE26()
    {
        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Notification updateNotification =
                new NotificationCompat.Builder(this,"Channel_X")
                .setContentTitle("Stuff was Updated")
                .setContentText("Mathias lugtede kl " + sdf.format(time.getTime()))
                .setSmallIcon(R.drawable.stinus_face)
                .build();
        startForeground(123,updateNotification);
    }


    //Not used atm
    public void broadcastResult(String result)
    {
        Intent broadcastIntent = new Intent();

        //TODO what needs to be broadcasted??

        broadcastIntent.setAction(BROADCAST_UPDATER_RESULT);
        broadcastIntent.putExtra(EXTRA_TASK_RESULT,result);

        LocalBroadcastManager BCManager = LocalBroadcastManager.getInstance(context);

        if(BCManager.sendBroadcast(broadcastIntent))
        {
            Log.d("BROADCAST_SEND","Succes on sending broadcast");
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

    public void getUserSubscribedFridges(String userEmail){

    }


    //Used for communicating back results from database queries.
    FridgeCallbackInterface callbackInterface = new FridgeCallbackInterface() {
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
                    //if ID matches
                    f.setInventory(list);
                    broadcastResult("DataUpdated");
                    return;
                }
            }
            //If no matching fridge, create new fridge with list
            Fridge placeholderFridge = new Fridge();
            placeholderFridge.setInventory(list);
            placeholderFridge.setID(fridge_ID);

            fridges.add(placeholderFridge);
            //TODO: Broadcast that new data is available.
            broadcastResult("DataUpdated");
        }

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
                    //if ID matches
                    f.setEssentials(list);

                    Log.d("BROADCASTFROMSERVICE", "BROADCAST!");
                    broadcastResult("DataUpdated");

                    return;
                }
            }
            //If no matching fridge, create new fridge with list
            Fridge placeholderFridge = new Fridge();
            placeholderFridge.setEssentials(list);
            placeholderFridge.setID(fridge_ID);

            fridges.add(placeholderFridge);
            //TODO: Broadcast that new data is available.
            broadcastResult("DataUpdated");

        }

        @Override
        public void onShoppingListsChange(String fridge_ID, ShoppingList list) {
            Log.d(TAG, "Shopping list " + list.getID() + " of fridge " + fridge_ID + " updated.");
            Log.d(ON_CHANGE_DEBUG_TAG, "onShoppingListsChange called ");

            ArrayList<ShoppingList> shoppingLists = null;

            //Update list for fridge with matching fridge IDF and matching list ID.

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
                                    //TODO: Broadcast that new data is available.
                                    broadcastResult("DataUpdated");
                                    return;
                                }
                            }
                            //If Shopping list is not on list of ShoppingLists yet, add it.
                            shoppingLists.add(list);
                        }

                        //If list of shopping lists is not initialized yet, do so.
                        else
                        {
                            shoppingLists=new ArrayList<ShoppingList>();
                            shoppingLists.add(list);
                            f.setShoppingLists(shoppingLists);
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

                //TODO: Broadcast that new data is available.
                broadcastResult("DataUpdated");
            }
        }

        @Override
        public void onShoppingListDelete(String fridge_ID, ShoppingList list) {
            Toast.makeText(context, "Gotta delete the list " + list.getID(), Toast.LENGTH_SHORT).show();
            List<ShoppingList> shoppingLists =  getFridge(fridge_ID).getShoppingLists();
            ShoppingList list2remove = getShoppingList(list.getID(),shoppingLists);
            shoppingLists.remove(list2remove);
        }

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
                                    //TODO: Broadcast that new data is available.
                                    broadcastResult("DataUpdated");
                                    return;
                                }
                            }
                            //If Shopping list is not on list of ShoppingLists yet, add it.
                            ingredientLists.add(list);
                        }

                        //If list of shopping lists is not initialized yet, do so.
                        else {
                            ingredientLists = new ArrayList<IngredientList>();
                            ingredientLists.add(list);
                            f.setIngredientLists(ingredientLists);
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

                //TODO: Broadcast that new data is available.
                broadcastResult("DataUpdated");
            }

        }

        @Override
        public void onIngredientListDelete(String fridge_ID, IngredientList list) {
            Toast.makeText(context, "Gotta delete the list " + list.getID(), Toast.LENGTH_SHORT).show();
            List<IngredientList> ingredientLists =  getFridge(fridge_ID).getIngredientLists();
            IngredientList list2remove = getIngredientList(list.getID(),ingredientLists);
            ingredientLists.remove(list2remove);

        }

        @Override
        public void onFridgeName(String id, String name) {
            Log.d(TAG, "onFridgeName: ID="+id + ", name="+name);
            getFridge(id).setName(name);
            //TODO: Broadcast that there's new data.
        }
    };

    public void SubscribeToFridge(String ID)
    {
        Fridge fridgeSubscribedTo = new Fridge();
        fridgeSubscribedTo.setID(ID);
        fridges.add(fridgeSubscribedTo);
        dbComm.SubscribeToFridge(ID);
    }

    //Return fridge with ID matching parameter.
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

    //Return Shopping List with ID matching parameter.
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

    //Return Shopping List with ID matching parameter.
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

    //Add item to inventory - increments quantity, if item with matching name exists.
    public void addItemToInventory(Item item, String fridge_ID)
    {
        //CollectionReference InventoryRef=db.collection(fridge_ID).document("Inventory").collection("Items");
        CollectionReference InventoryRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Inventory").collection("Items");
        InventoryList inventory=getFridge(fridge_ID).getInventory();

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        if(inventory!=null)
        {
            for (Item i: inventory.getItems()
                    ) {
                if(i.getName().equals(item.getName()))
                {
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
        CollectionReference InventoryRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Inventory").collection("Items");

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        InventoryList inventory=getFridge(fridge_ID).getInventory();
        for (Item i: inventory.getItems()
                ) {
            if(i.getName().equals(itemName))
            {
                dbComm.removeItem(InventoryRef,itemName);
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
        CollectionReference InventoryRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Inventory").collection("Items");
        dbComm.addItem(InventoryRef, item);
    }

    //Add item to essentials - increments quantity, if item with matching name exists.
    public void addItemToEssentials(Item item, String fridge_ID)
    {
        //CollectionReference InventoryRef=db.collection(fridge_ID).document("Inventory").collection("Items");
        CollectionReference EssentialsRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Essentials").collection("Items");
        EssentialsList essentials=getFridge(fridge_ID).getEssentials();

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        if(essentials!=null)
        {
            for (Item i: essentials.getItems()
                    ) {
                if(i.getName().equals(item.getName()))
                {
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

    //removes item from inventory
    public void removeItemFromEssentials(String itemName, String fridge_ID)
    {
        CollectionReference EssentialsRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Essentials").collection("Items");

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        EssentialsList essentials=getFridge(fridge_ID).getEssentials();
        for (Item i: essentials.getItems()
                ) {
            if(i.getName().equals(itemName))
            {
                dbComm.removeItem(EssentialsRef,itemName);
                return;
            }
        }
        //If no item on list with name <itemName>
        Log.d(TAG, "removeItemFromEssentials: Item: " + itemName + " was not on list, and thus cannot be removed");
    }

    //adds item to inventory and overwrites old data if any exists.
    public void overwriteItemInEssentials(Item item, String fridge_ID)
    {
        Log.d(TAG, "overwriteItemInEssentials: Overwriting old data(if any) for item: " + item.getName());
        CollectionReference EssentialsRef=db.collection("Fridges").document(fridge_ID).collection("Content").document("Essentials").collection("Items");
        dbComm.addItem(EssentialsRef, item);
    }

    //Create a new shopping list with the given attributes, but with no items on it.
    public void createNewShoppingList(String fridge_ID, String list_name)
    {
        String list_ID=fridge_ID+"_"+list_name;
        CollectionReference fridgeRef = db.collection("Fridges").document(fridge_ID).collection("Content");
        ShoppingList emptySL=new ShoppingList();
        emptySL.setID(list_ID);
        emptySL.setName(list_name);
        dbComm.addShoppingList(fridgeRef,emptySL,list_name,list_ID);
        dbComm.addID2listofShoppingListIDs(fridgeRef,list_ID);
    }

    //add Item to Shopping List. Increments quantity, if item with matching name exists.
    // Shopping list must exist.
    public void addItemToShoppingList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("ShoppingLists").collection(list_ID);

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
                            float oldQty = i.getQuantity();
                            i.setQuantity(oldQty+item.getQuantity());
                            dbComm.addItem(listRef, i);
                            Log.d(TAG, "addItemToShoppingList: Item already on shopping list - Increasing qty " + oldQty+"->"+i.getQuantity());
                            return;
                        }
                    }
                    //If item was not in inventory yet, just add it to list.
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
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("ShoppingLists").collection(list_ID);

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

    public void removeItemFromShoppingList(String itemName, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("ShoppingLists").collection(list_ID);

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

    //Create a new shopping list with the given attributes, but with no items on it.
    public void createNewIngredientList(String fridge_ID, String list_name)
    {
        String list_ID=fridge_ID+"_"+list_name;
        CollectionReference fridgeRef = db.collection("Fridges").document(fridge_ID).collection("Content");
        IngredientList emptyIL=new IngredientList();
        emptyIL.setID(list_ID);
        emptyIL.setName(list_name);
        dbComm.addIngredientList(fridgeRef,emptyIL,list_name,list_ID);
        CollectionReference IDs_ref=fridgeRef.document("ShoppingList_IDs").collection("IDs");
        dbComm.addListInfo(IDs_ref,list_name,list_ID,"None");
        dbComm.addID2listofIngredientListIDs(fridgeRef,list_ID);

    }

    //add Item to Ingredient List. Increments quantity, if item with matching name exists.
    // Shopping list must exist.
    public void addItemToIngredientList(Item item, String fridge_ID, String list_name, String list_ID)
    {
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("IngredientLists").collection(list_ID);

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
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("IngredientLists").collection(list_ID);

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
        CollectionReference listRef = db.collection("Fridges").document(fridge_ID).collection("Content").document("IngredientLists").collection(list_ID);

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

    public void createNewUser(String userName, String userEmail)
    {
        dbComm.createNewUserInDatabase(userName,userEmail);
    }

    public void addFridgeIDtoListOfSubscribedFridges(String userEmail, String fridgeID)
    {
        dbComm.addFridgeID2listOfFridgeSubscriptions(fridgeID,userEmail);
    }

    public void removeFridgeIDfromListOfSubscribedFridges(String userEmail, String fridgeID)
    {
        dbComm.removeFridgeIDFromListOfFridgeSubscriptions(fridgeID,userEmail);
    }

    public void setResponsibilityForShoppingList(String Fridge_ID, String List_ID, String User_ID)
    {
        dbComm.setResponsibilityForListShoppingList(Fridge_ID,List_ID, User_ID);
    }

    /*
    public void addShoppingList(CollectionReference fridge, final ShoppingList listToAdd, String listName, String listID)
    {
        dbComm.addShoppingList(fridge, listToAdd, listName, listID);
    }
    */

    public void createNewFridge(String ID, String Name)
    {
        dbComm.createNewFridge(ID, Name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
    }

    public void UpdateShoppingListFromIngredientList(ShoppingList shoppingList ,IngredientList ingredientList, InventoryList inventoryList)
    {

        for (Item i: ingredientList.getItems())
        {
            for (Item k: inventoryList.getItems())
            {
                if(i.getName().equals(k.getName()))
                {
                    if(i.getQuantity() > k.getQuantity())
                    {
                        float tmp = i.getQuantity()-k.getQuantity();
                        tmp += k.getQuantity();
                        shoppingList.EditItemQuantity(i.getName(),tmp);
                    }
                }
                else
                {
                    shoppingList.AddItem(i);
                    shoppingList.EditItemQuantity(i.getName(),i.getQuantity());
                }
            }
        }
    }
}


