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
    
    //Used for binding service to activity
    private final IBinder mBinder = new ServiceBinder();

    public static final String BROADCAST_UPDATER_RESULT = "smap_f18_24.smap_fridge.Service.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";

    //context shit
    Context context;

    fireStoreCommunicator dbComm;

    ArrayList<Fridge> fridges;


    public void setContext(Context c)
    {
        context = c;
        dbComm=new fireStoreCommunicator(context,callbackInterface);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Initialize list
        fridges=new ArrayList<Fridge>();

        //Get instance of database-communicator
        dbComm= new fireStoreCommunicator(context,callbackInterface);


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
            Log.d("API<26","bobby olsen");
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


    //Used for communicating back results from database queries.
    FridgeCallbackInterface callbackInterface = new FridgeCallbackInterface() {
        @Override
        public void onInventoryChange(String fridge_ID, InventoryList list) {
            Log.d(TAG, "Inventory of fridge " + fridge_ID + " updated.");

            //Update list for fridge with matching fridge ID

            //check for fridge with matching ID.
            for (Fridge f: fridges
                    ) {
                if(f.getID().equals(fridge_ID))
                {
                    //if ID matches
                    f.setInventory(list);
                    break;
                }
            }
            //If no matching fridge, create new fridge with list
            Fridge placeholderFridge = new Fridge();
            placeholderFridge.setInventory(list);
            placeholderFridge.setID(fridge_ID);

            fridges.add(placeholderFridge);
            //TODO: Broadcast that new data is available.
        }

        @Override
        public void onEssentialsChange(String fridge_ID, EssentialsList list) {
            Log.d(TAG, "Essentials of fridge " + fridge_ID + " updated.");

            //Update list for fridge with matching fridge ID

            try
            {
                //check for fridge with matching ID.
                for (Fridge f: fridges
                        ) {
                    if(f.getID().equals(fridge_ID))
                    {
                        //if ID matches
                        f.setEssentials(list);
                        //TODO: Broadcast that new data is available.
                        return;
                    }
                }
            }
            catch (RuntimeException e)
            {
                //If no matching fridge, create new fridge with list
                Fridge placeholderFridge = new Fridge();
                placeholderFridge.setEssentials(list);
                placeholderFridge.setID(fridge_ID);

                fridges.add(placeholderFridge);
                //TODO: Broadcast that new data is available.
            }


        }

        @Override
        public void onShoppingListsChange(String fridge_ID, ShoppingList list) {
            Log.d(TAG, "Shopping list " + list.getID() + " of fridge " + fridge_ID + " updated.");

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
                                    //TODO: Broadcast that new data is available.
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
            }

        }

        @Override
        public void onIngredientListsChange(String fridge_ID, IngredientList list) {
            Log.d(TAG, "Ingredient list " + list.getID() + " of fridge " + fridge_ID + " updated.");

            ArrayList<IngredientList> ingredientLists;

            //Update list for fridge with matching fridge ID and matching list ID.
            for (Fridge f: fridges
                    ) {
                if(f.getID().equals(fridge_ID))
                {
                    ingredientLists = (ArrayList<IngredientList>) f.getIngredientLists();
                    for (IngredientList s: ingredientLists
                            ) {
                        if(s.getID().equals(list.getID()))
                        {
                            s=list;
                        }

                    }
                }
            }

            //TODO: Broadcast that new data is available.
        }
    };

    public void SubscribeToFridge(String ID)
    {
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

    //Add item to inventory - increments quantity, if item with matching name exists.
    public void addItemToInventory(Item item, String fridge_ID)
    {
        CollectionReference InventoryRef=db.collection(fridge_ID).document("Inventory").collection("Items");

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        InventoryList inventory=getFridge(fridge_ID).getInventory();
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
        //If item was not in inventory yet, just add it to list.
        dbComm.addItem(InventoryRef, item);
        Log.d(TAG, "addItemToInventory: Item was not in inventory yet, and has thus been added.");
    }

    //removes item from inventory
    public void removeItemFromInventory(String itemName, String fridge_ID)
    {
        CollectionReference InventoryRef=db.collection(fridge_ID).document("Inventory").collection("Items");

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
        CollectionReference InventoryRef=db.collection(fridge_ID).document("Inventory").collection("Items");
        dbComm.addItem(InventoryRef, item);
    }

    //Add item to essentials - increments quantity, if item with matching name exists.
    public void addItemToEssentials(Item item, String fridge_ID)
    {
        CollectionReference InventoryRef=db.collection(fridge_ID).document("Essentials").collection("Items");

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        EssentialsList essentials=getFridge(fridge_ID).getEssentials();
        for (Item i: essentials.getItems()
                ) {
            if(i.getName().equals(item.getName()))
            {
                float oldQty = i.getQuantity();
                i.setQuantity(oldQty+item.getQuantity());
                dbComm.addItem(InventoryRef, i);
                Log.d(TAG, "addItemToEssentials: Item already in essentials - Increasing qty " + oldQty+"->"+i.getQuantity());
                return;
            }
        }
        //If item was not in inventory yet, just add it to list.
        dbComm.addItem(InventoryRef, item);
        Log.d(TAG, "addItemToInventory: Item was not in inventory yet, and has thus been added.");
    }

    //removes item from essentials
    public void removeItemFromEssentials(String itemName, String fridge_ID)
    {
        CollectionReference EssentialsRef=db.collection(fridge_ID).document("Essentials").collection("Items");

        //Check current inventory to see if item already exists.
        //If it does, add to quantity. (NOTE: OVERWRITES ALL OTHER DATA FOR THAT ITEM, EG: RESPONSIBLE USER, UNIT, STATUS, ETC)
        InventoryList essentials=getFridge(fridge_ID).getInventory();
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

    //adds item to essentials and overwrites old data if any exists.
    public void overwriteItemInEssentials(Item item, String fridge_ID)
    {
        Log.d(TAG, "overwriteItemInEssentials Overwriting old data(if any) for item: " + item.getName());
        CollectionReference InventoryRef=db.collection(fridge_ID).document("Essentials").collection("Items");
        dbComm.addItem(InventoryRef, item);
    }

    //add Item to Shopping List. Increments quantity, if item with matching name exists.
    // Shopping list must exist.
    public void addItemToShoppingList(Item item, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("ShoppingLists").collection(list_ID);

       //Find list with matching name
        ArrayList<ShoppingList> shoppingLists=(ArrayList<ShoppingList>)getFridge(fridge_ID).getShoppingLists();
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
            }
        }
    }

    //adds item to shopping list and overwrites old data if any exists.
    public void overWriteItemInShoppingList(Item item, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("ShoppingLists").collection(list_ID);

        //Find list with matching name
        ArrayList<ShoppingList> shoppingLists=(ArrayList<ShoppingList>)getFridge(fridge_ID).getShoppingLists();
        for (ShoppingList s: shoppingLists
                ) {
            if(s.getID().equals(list_ID))
            {
                Log.d(TAG, "overWriteItemInShoppingList: Overwriting old data(if any) for item:" + item.getName());
                dbComm.addItem(listRef, item);
            }
        }
    }

    public void removeItemFromShoppingList(String itemName, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("ShoppingLists").collection(list_ID);

        //Find list with matching name
        ArrayList<ShoppingList> shoppingLists=(ArrayList<ShoppingList>)getFridge(fridge_ID).getShoppingLists();
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

    //add Item to Ingredient List. Ingredient list must exist.
    public void addItemToIngredientList(Item item, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("IngredientLists").collection(list_ID);

        //Find list with matching name
        ArrayList<IngredientList> ingredientLists=(ArrayList<IngredientList>)getFridge(fridge_ID).getIngredientLists();
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
                        Log.d(TAG, "addItemToIngredientList: Item already in ingredientList - Increasing qty " + oldQty+"->"+i.getQuantity());
                        return;
                    }
                }
                //If item was not in inventory yet, just add it to list.
                dbComm.addItem(listRef,item);
            }
        }
    }

    //adds item to shopping list and overwrites old data if any exists.
    public void overWriteItemInIngredientList(Item item, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("IngredientLists").collection(list_ID);

        //Find list with matching name
        ArrayList<IngredientList> ingredientLists=(ArrayList<IngredientList>)getFridge(fridge_ID).getIngredientLists();
        for (IngredientList s: ingredientLists
                ) {
            if(s.getID().equals(list_ID))
            {
                Log.d(TAG, "overWriteItemInIngredientList: Overwriting old data(if any) for item:" + item.getName());
                dbComm.addItem(listRef, item);
            }
        }
    }

    public void removeItemFromIngredientList(String itemName, String fridge_ID, String list_ID)
    {
        CollectionReference listRef = db.collection(fridge_ID).document("IngredientLists").collection(list_ID);

        //Find list with matching name
        ArrayList<IngredientList> ingredientLists=(ArrayList<IngredientList>)getFridge(fridge_ID).getIngredientLists();
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

    public void addShoppingList(CollectionReference fridge, final ShoppingList listToAdd, String listName, String listID)
    {
        dbComm.addShoppingList(fridge, listToAdd, listName, listID);
    }

    public void createNewFridge(String ID, String Name)
    {
        dbComm.createNewFridge(ID, Name);
    }

}

