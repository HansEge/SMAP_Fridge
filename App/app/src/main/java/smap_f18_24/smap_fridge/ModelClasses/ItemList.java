package smap_f18_24.smap_fridge.ModelClasses;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public abstract class ItemList {

    private static final String TAG="ItemList";

    List<Item> Items;
    Fridge connectedFridge;

    //constructors
    public ItemList()
    {
        //Empty constructor for use with firebase (i think..)
        Items=new ArrayList<>();
    }

    public ItemList(Fridge _connectedFridge)
    {
        this.connectedFridge = _connectedFridge;
    }

    //methods
    public void AddItem(Item toAdd)
    {
        Items.add(toAdd);
    }

    public void RemoveItem(String itemName)
    {
        Items.remove(getItem(itemName));
    }

    public void EditItemQuantity(String singleItemName, float newValue)
    {
        getItem(singleItemName).setQuantity(newValue);
    }

    //Search list for item with given name. Returns it if it exists, throws exception if not.
    public Item getItem(String singleItemName)
    {
        for (Item i: Items
                ) {
            if(i.getName().equals(singleItemName))
            {
                return i;
            }

        }
        throw new RuntimeException("Item " + singleItemName + " was not found on list");
    }

    public List<Item> getItems()
    {
        return Items;
    }
}
