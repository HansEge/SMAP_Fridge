package smap_f18_24.smap_fridge.ModelClasses;

import java.util.List;

public class Fridge {

    String Name;
    String ID;
    List<String> ConnectedUserEmails;
    InventoryList Inventory;
    EssentialsList Essentials;
    List<ShoppingList> ShoppingLists;
    List<IngredientList> IngredientLists;

    public Fridge() {
        //Empty constructor for use with firebase (i think..)
    }

    public Fridge(String name, String id, List<String> connectedUserEmails, InventoryList inventory, EssentialsList essentials, List<ShoppingList> shoppingLists, List<IngredientList> ingredientLists)
    {
        Name = name;
        ID = id;
        ConnectedUserEmails = connectedUserEmails;
        Inventory = inventory;
        Essentials = essentials;
        ShoppingLists = shoppingLists;
        IngredientLists = ingredientLists;
    }


    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<String> getConnectedUsers() {
        return ConnectedUserEmails;
    }

    public void setConnectedUsers(List<String> connectedUsers) {
        ConnectedUserEmails = connectedUsers;
    }

    public InventoryList getInventory() {
        return Inventory;
    }

    public void setInventory(InventoryList inventory) {
        Inventory = inventory;
    }

    public EssentialsList getEssentials() {
        return Essentials;
    }

    public void setEssentials(EssentialsList essentials) {
        Essentials = essentials;
    }

    public List<ShoppingList> getShoppingLists() {
        return ShoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        ShoppingLists = shoppingLists;
    }

    public List<IngredientList> getIngredientLists() {
        return IngredientLists;
    }

    public void setIngredientLists(List<IngredientList> ingredientLists) {
        IngredientLists = ingredientLists;
    }


    //Methods

    public void AddUser(String userEmail)
    {
        //Check if userEmail is already on list
        for (String s:ConnectedUserEmails
             ) {
            if(s.equals(userEmail))
            {
                return;
            }

        }

        //If userEmail is not on list yet:
        //TODO
        //Add fridge ID to USERS list of connectedFridges
        ConnectedUserEmails.add(userEmail);
    }

    /*
    //INVENTORY-RELATED
    public void AddItemToInventory(Item toAdd)
    {
        //Check current inventory to see if item already exists.
        //If it does, add to quantity.
        //If not, add item to list.
        InventoryList inventory = getInventory();
        for (Item i: inventory.getItems()
             ) {
            if(i.getName().equals(toAdd.getName()))
            {
                float oldQty = i.getQuantity();
                i.setQuantity(oldQty+toAdd.getQuantity());
            }
        }
    }
    */

    public void RemoveItemFromInventory(String itemName)
    {
        Inventory.getItems().remove(Inventory.getItem(itemName));
    }

    public void EditInventoryItemQuantity(String itemName, float quantity)
    {
        Inventory.getItem(itemName).setQuantity(quantity);
        //TODO: Check essential quantity of item. If too low, add to shopping list.
    }


    //ESSENTIALS LIST-RELATED
    public void AddItemToEssentials(Item toAdd)
    {
        Essentials.AddItem(toAdd);
    }

    public void RemoveItemFromEssentials(String itemName)
    {
        Essentials.getItems().remove(Inventory.getItem(itemName));
    }

    public void EditEssentialsItemQuantity(String itemName, float quantity)
    {
        Essentials.EditItemQuantity(itemName,quantity);
    }

    //SHOPPING LIST-RELATED
    public void CreateNewShoppingList(String name)
    {
        ID="";
        //TODO: Find a way to make ID - maybe fridgeID+name?
        ShoppingLists.add(new ShoppingList(name,ID));
    }

    public void AddItemToShoppingList(String ListID, Item toAdd)
    {
        ShoppingList targetList = getShoppingList(ListID);
        //TODO: Check if item is already on list - Maybe this should be done in ItemList?
        //If on list: Increment quantity properly
        //else: add entire item
    }

    public void RemoveItemFromShoppingList(String ListID, String itemName)
    {
        ShoppingList targetList = getShoppingList(ListID);
        targetList.RemoveItem(itemName);
    }



    //INGREDIENT LIST-RELATED
    public void CreateNewIngredientList(String name)
    {
        ID="";
        //TODO: Find a way to make ID - maybe fridgeID+name?
        IngredientLists.add(new IngredientList(name,ID));
    }

    public void AddItemToIngredientList(String ListID, Item toAdd)
    {
        IngredientList targetList = getIngredientList(ListID);
        //TODO: Check if item is already on list - Maybe this should be done in ItemList?
        //If on list: Increment quantity properly
        //else: add entire item
    }

    public void RemoveItemFromIngredientList(String ListID, String itemName)
    {
        IngredientList targetList = getIngredientList(ListID);
        targetList.RemoveItem(itemName);
    }



    public void UpdateShoppingListFromIngredientList(ShoppingList shoppingList ,InventoryList ingredientList)
    {
        //TODO: FOR EACH ITEM ON SHOPPING LIST
        //Check if item is in inventory
        //If it is:
        //  Check if quantity is high enough
        //  If not: Add Item to shopping list
        //If not:
        //  Add Item to shopping list
    }


    //HELPER FUNCTIONS
    private ShoppingList getShoppingList(String ListID)
    {
        for (ShoppingList s: ShoppingLists
             ) {
            if(s.ID.equals(ListID))
            {
                return s;
            }
        }
        throw new RuntimeException("ShoppingList " + ListID + " was not found on list");
    }

    private IngredientList getIngredientList(String ListID)
    {
        for (IngredientList s: IngredientLists
                ) {
            if(s.ID.equals(ListID))
            {
                return s;
            }
        }
        throw new RuntimeException("IngredientList " + ListID + " was not found on list");
    }

}
