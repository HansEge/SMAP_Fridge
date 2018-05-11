package smap_f18_24.smap_fridge.ModelClasses;

import java.util.List;

public class ShoppingList extends ItemList {

    String Name;
    String ID;
    String responsibility = "Mathias Friis";

    public ShoppingList() {
        //Empty constructor for use with firebase (i think..)
    }

    public ShoppingList(String _name, String _id) {
        Name = _name;
        ID = _id;
    }

    public String getName() {
        return Name;
    }

    public String getID() {
        return ID;
    }


    public void EditItemQuantity(String _name, float _newValue) {
        getItem(_name).setQuantity(_newValue);
    }

    public void SetResponsibilityForItem(String _UserEmail, String _itemName) {
        getItem(_itemName).setResponsibleUserEmail(_UserEmail);
        getItem(_itemName).setItemStatus("Responsibilty Claimed");

    }

    public String getResponsibility()
    {
        return responsibility;
    }

    public void setResponsibility(String responsibleUser)
    {
        responsibility=responsibleUser;
    }

    public void MoveFromShoppingListToFridge( ShoppingList shoppingList, InventoryList inventoryList) {
        //TODO
        //Add toMove to InventoryList
        //Remove toMove from shopping list

        for (Item i: shoppingList.getItems()) {

            for (Item j: inventoryList.getItems()) {

                if (i.getName().equals(j.getName()))
                {
                    j.setQuantity(i.getQuantity()+j.getQuantity());
                    shoppingList.RemoveItem(i.getName());
                }
                else
                {
                    inventoryList.AddItem(shoppingList.getItem(i.getName()));
                    shoppingList.RemoveItem(i.getName());
                }
            }
        }
    }

    public void setName(String _name) {
        Name = _name;
    }

    public void setID(String _ID) {
        this.ID = _ID;
    }
}
