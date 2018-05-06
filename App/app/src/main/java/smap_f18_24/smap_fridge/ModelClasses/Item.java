package smap_f18_24.smap_fridge.ModelClasses;

public class Item {


    private String Name;
    private String Unit;
    private float Quantity;
    private String ResponsibleUserEmail;
    //ItemStatus itemStatus;
    private String itemStatus;


    //Constructors
    public Item()
    {
        Unit="THIS_IS_NOT_AN_ITEM";
        //Empty constructor for use with firebase (i think..)
    }

    public Item(String Name, String _unit, float _quantity, String _responsibleUserEmail, String _itemStatus) {
        this.Name = Name;
        this.Unit = _unit;
        this.Quantity = _quantity;
        this.ResponsibleUserEmail = _responsibleUserEmail;
        this.itemStatus = _itemStatus;
    }

    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String _unit) {
        this.Unit = _unit;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float _quantity) {
        this.Quantity = _quantity;
    }

    public String getResponsibleUserEmail() {
        return ResponsibleUserEmail;
    }

    public void setResponsibleUserEmail(String _responsibleUserEmail) {
        this.ResponsibleUserEmail = _responsibleUserEmail;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String _itemStatus) {
        this.itemStatus = _itemStatus;
    }


    //Methods


}


