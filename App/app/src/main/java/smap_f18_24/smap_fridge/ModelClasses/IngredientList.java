package smap_f18_24.smap_fridge.ModelClasses;

public class IngredientList extends ItemList {


    String Name;
    String ID;


    public IngredientList(String name, String ID) {
        Name = name;
        this.ID = ID;
    }

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
}
