package smap_f18_24.smap_fridge.ModelClasses;

public class IngredientList extends ItemList {


    String itemName;
    String ID;


    public IngredientList(String _ItemName, String ID) {
        itemName = _ItemName;
        this.ID = ID;
    }

    public String getName() {
        return itemName;
    }

    public void setName(String _ItemName) {
        itemName = _ItemName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
