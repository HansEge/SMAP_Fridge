package smap_f18_24.smap_fridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.FridgeListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.IngredientsListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;

public class DebugShoppingListActivity extends AppCompatActivity {

    ListView Lv_Shoppinglist;
    final public ArrayList<Item>  debugList = new ArrayList<>();
    final public EssentialsList essentialList = new EssentialsList();
    final public InventoryList inventoryList = new InventoryList();
    final public ArrayList<Item>  debugList1 = new ArrayList<>();
    public ShoppingListAdaptor adaptor1 = new ShoppingListAdaptor(this, debugList);
    public EssentialsListAdaptor adaptor2 = new EssentialsListAdaptor(this, essentialList);
    public InventoryListAdaptor adaptor3 = new InventoryListAdaptor(this, inventoryList);
    public IngredientsListAdaptor adaptor4 = new IngredientsListAdaptor(this, debugList1);

    Item kartoffel = new Item("katoffel", "kg", 1000, "hejmeddig123@dibidut.au", "Status");
    Item Tomat = new Item("Tomat", "kg", 100, "hejmeddig123@dibidut.au", "Status");
    Item Æg = new Item("Æg", "stk", 10, "hejmeddig123@dibidut.au", "Status");
    Item juice = new Item("Juice", "L", 2, "hejmeddig123@dibidut.au", "Status");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_shopping_list);


        debugList.add(kartoffel);
        debugList.add(Tomat);
        debugList.add(Æg);
        debugList.add(juice);

        essentialList.AddItem(kartoffel);
        essentialList.AddItem(Tomat);
        essentialList.AddItem(Æg);
        essentialList.AddItem(juice);

        inventoryList.AddItem(kartoffel);
        inventoryList.AddItem(Tomat);
        inventoryList.AddItem(Æg);
        inventoryList.AddItem(juice);

        debugList1.add(kartoffel);
        debugList1.add(Tomat);
        debugList1.add(Æg);
        debugList1.add(juice);


       Lv_Shoppinglist = findViewById(R.id.debug_Lv_Shoppinglist);

       //Lv_Shoppinglist.setAdapter(adaptor1);

       Lv_Shoppinglist.setAdapter(adaptor4);


    }


    protected void onStart(){
        super.onStart();
    }

    protected void onStop(){
        super.onStop();
    }
}
