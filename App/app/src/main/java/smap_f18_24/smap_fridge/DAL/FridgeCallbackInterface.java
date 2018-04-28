package smap_f18_24.smap_fridge.DAL;

public interface FridgeCallbackInterface {
    void onInventoryChange();
    void onEssentialsChange();
    void onShoppingListsChange();
    void onIngredientListsChange();
}
