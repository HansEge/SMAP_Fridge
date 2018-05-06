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

    public Fridge(String _name, String _id, List<String> _connectedUserEmails, InventoryList _inventory, EssentialsList _essentials, List<ShoppingList> _shoppingLists, List<IngredientList> _ingredientLists)
    {
        Name = _name;
        ID = _id;
        ConnectedUserEmails = _connectedUserEmails;
        Inventory = _inventory;
        Essentials = _essentials;
        ShoppingLists = _shoppingLists;
        IngredientLists = _ingredientLists;
    }


    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String _name) {
        Name = _name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String _id) {
        this.ID = _id;
    }

    public List<String> getConnectedUsers() {
        return ConnectedUserEmails;
    }

    public void setConnectedUsers(List<String> _connectedUsers) {
        ConnectedUserEmails = _connectedUsers;
    }

    public InventoryList getInventory() {
        return Inventory;
    }

    public void setInventory(InventoryList _inventory) {
        Inventory = _inventory;
    }

    public EssentialsList getEssentials() {
        return Essentials;
    }

    public void setEssentials(EssentialsList _essentials) {
        Essentials = _essentials;
    }

    public List<ShoppingList> getShoppingLists() {
        return ShoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> _shoppingLists) {
        ShoppingLists = _shoppingLists;
    }

    public List<IngredientList> getIngredientLists() {
        return IngredientLists;
    }

    public void setIngredientLists(List<IngredientList> _ingredientLists) {
        IngredientLists = _ingredientLists;
    }


    //Methods

    public void AddUser(String _userEmail)
    {
        //Check if userEmail is already on list
        for (String s:ConnectedUserEmails
             ) {
            if(s.equals(_userEmail))
            {
                return;
            }

        }

        //If userEmail is not on list yet:
        //TODO
        //Add fridge ID to USERS list of connectedFridges
        ConnectedUserEmails.add(_userEmail);
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

    public void RemoveItemFromInventory(String _itemName)
    {
        Inventory.getItems().remove(Inventory.getItem(_itemName));
    }

    public void EditInventoryItemQuantity(String _itemName, float _quantity)
    {
        Inventory.getItem(_itemName).setQuantity(_quantity);
        //TODO: Check essential quantity of item. If too low, add to shopping list.
    }


    //ESSENTIALS LIST-RELATED
    public void AddItemToEssentials(Item _toAdd)
    {
        Essentials.AddItem(_toAdd);
    }

    public void RemoveItemFromEssentials(String _itemName)
    {
        Essentials.getItems().remove(Inventory.getItem(_itemName));
    }

    public void EditEssentialsItemQuantity(String _itemName, float _quantity)
    {
        Essentials.EditItemQuantity(_itemName,_quantity);
    }

    //SHOPPING LIST-RELATED
    public void CreateNewShoppingList(String _shoppingListName)
    {
        ID="";
        //TODO: Find a way to make ID - maybe fridgeID+name?
        ShoppingLists.add(new ShoppingList(_shoppingListName,ID));
    }

    public void AddItemToShoppingList(String _ListID, Item _toAdd)
    {
        ShoppingList targetList = getShoppingList(_ListID);
        //TODO: Check if item is already on list - Maybe this should be done in ItemList?
        //If on list: Increment quantity properly
        //else: add entire item
    }

    public void RemoveItemFromShoppingList(String _ListID, String _itemName)
    {
        ShoppingList targetList = getShoppingList(_ListID);
        targetList.RemoveItem(_itemName);
    }



    //INGREDIENT LIST-RELATED
    public void CreateNewIngredientList(String _name)
    {
        ID="";
        //TODO: Find a way to make ID - maybe fridgeID+name?
        IngredientLists.add(new IngredientList(_name,ID));
    }

    public void AddItemToIngredientList(String _ListID, Item _ingredientsItemtoAdd)
    {
        IngredientList targetList = getIngredientList(_ListID);
        //TODO: Check if item is already on list - Maybe this should be done in ItemList?
        //If on list: Increment quantity properly
        //else: add entire item
    }

    public void RemoveItemFromIngredientList(String ListID, String itemName)
    {
        IngredientList targetList = getIngredientList(ListID);
        targetList.RemoveItem(itemName);
    }



    public void UpdateShoppingListFromIngredientList(ShoppingList _shoppingList ,InventoryList _ingredientList)
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
    private ShoppingList getShoppingList(String _ListID)
    {
        for (ShoppingList s: ShoppingLists
             ) {
            if(s.ID.equals(_ListID))
            {
                return s;
            }
        }
        throw new RuntimeException("ShoppingList " + _ListID + " was not found on list");
    }

    private IngredientList getIngredientList(String _ListID)
    {
        for (IngredientList s: IngredientLists
                ) {
            if(s.ID.equals(_ListID))
            {
                return s;
            }
        }
        throw new RuntimeException("IngredientList " + _ListID + " was not found on list");
    }

}
