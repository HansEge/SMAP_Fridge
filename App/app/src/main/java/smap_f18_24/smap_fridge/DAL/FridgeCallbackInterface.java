package smap_f18_24.smap_fridge.DAL;

import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;

public interface FridgeCallbackInterface {
    void onInventoryChange(String fridge_ID, InventoryList list);
    void onEssentialsChange(String fridge_ID, EssentialsList list);
    void onShoppingListsChange(String fridge_ID, ShoppingList list);
    void onShoppingListDelete(String fridge_ID, ShoppingList list);
    void onIngredientListsChange(String fridge_ID, IngredientList list);
    void onIngredientListDelete(String id, IngredientList ingredientList);
}
