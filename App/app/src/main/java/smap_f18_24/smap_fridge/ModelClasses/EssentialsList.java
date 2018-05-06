package smap_f18_24.smap_fridge.ModelClasses;

public class EssentialsList extends ItemList {


    public void EditItemMinimum(String _Itemname, float _ItemNewValue)
    {
        EditItemQuantity(_Itemname, _ItemNewValue);
    }
}
