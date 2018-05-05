package smap_f18_24.smap_fridge.ModelClasses;

import java.util.List;

public class ShoppingList extends ItemList {

    String Name;
    String ID;

    public ShoppingList() {
        //Empty constructor for use with firebase (i think..)
    }

    public ShoppingList(String name, String id) {
        Name = name;
        ID = id;
    }


    public String getName() {
        return Name;
    }

    public String getID() {
        return ID;
    }





    public void EditItemQuantity(String name, float newValue) {
        getItem(name).setQuantity(newValue);
    }

    public void SetResponsibilityForItem(String UserEmail, String itemName) {
        getItem(itemName).setResponsibleUserEmail(UserEmail);
        getItem(itemName).setItemStatus("Responsibilty Claimed");

    }

    public void MoveFromShoppingListToFridge(Item toMove) {
        //TODO
        //Add toMove to InventoryList
        //Remove toMove from shopping list
    }

    public void setName(String name) {
        Name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
