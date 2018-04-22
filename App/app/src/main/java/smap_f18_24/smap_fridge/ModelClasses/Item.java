package smap_f18_24.smap_fridge.ModelClasses;

public class Item {


    String Name;
    String Unit;
    float Quantity;
    String ResponsibleUserEmail;
    //ItemStatus itemStatus;
    String itemStatus;


    //Constructors
    public Item()
    {
        Unit="THIS_IS_NOT_AN_ITEM";
        //Empty constructor for use with firebase (i think..)
    }

    public Item(String name, String unit, float quantity, String responsibleUserEmail, String itemStatus) {
        Name = name;
        Unit = unit;
        Quantity = quantity;
        ResponsibleUserEmail = responsibleUserEmail;
        this.itemStatus = itemStatus;
    }

    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getResponsibleUserEmail() {
        return ResponsibleUserEmail;
    }

    public void setResponsibleUserEmail(String responsibleUserEmail) {
        ResponsibleUserEmail = responsibleUserEmail;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }


    //Methods


}


