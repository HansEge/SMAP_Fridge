package smap_f18_24.smap_fridge.ModelClasses;

import java.util.ArrayList;
import java.util.List;

public class InventoryList extends ItemList {

    //methods
    void UpdateShoppingListFromIngredientList(ShoppingList shoppingList ,InventoryList ingredientList)
    {
        //TODO
        //For each item on ingredientlist:
        //Check if item is in inventory.
        //  If not: add to shoppingList.
        //  If is in inventory: Is there enough in inventory?
        //  If not: Add difference in quantity to shopping list.
    }

    public void AddItemToInventoryList(Item toAdd, float Quantity)
    {
        //TODO
        //Get shopping list(dependency injection?).
        //Add item to list.
    }
}
