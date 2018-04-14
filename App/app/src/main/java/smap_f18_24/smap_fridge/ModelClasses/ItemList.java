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
        Items=new ArrayList<Item>();
    }

    public ItemList(Fridge connectedFridge)
    {
        this.connectedFridge = connectedFridge;
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

    public void EditItemQuantity(String itemName, float newValue)
    {
        getItem(itemName).setQuantity(newValue);
    }

    //Search list for item with given name. Returns it if it exists, throws exception if not.
    public Item getItem(String itemName)
    {
        for (Item i: Items
                ) {
            if(i.Name.equals(itemName))
            {
                return i;
            }

        }
        throw new RuntimeException("Item " + itemName + " was not found on list");
    }

    public List<Item> getItems()
    {
        return Items;
    }
}
