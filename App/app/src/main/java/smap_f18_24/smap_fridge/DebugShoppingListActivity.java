package smap_f18_24.smap_fridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.FridgeListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.ShoppingListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;

public class DebugShoppingListActivity extends AppCompatActivity {

    ListView Lv_Shoppinglist;
    final public ArrayList<Item>  debugList = new ArrayList<>();
    public ShoppingListAdaptor adaptor = new ShoppingListAdaptor(this, debugList);


    Item katoffel = new Item("katoffel", "kg", 1000, "hejmeddig123@dibidut.au", "Status");
    Item Tomat = new Item("Tomat", "kg", 100, "hejmeddig123@dibidut.au", "Status");
    Item Æg = new Item("Æg", "stk", 10, "hejmeddig123@dibidut.au", "Status");
    Item juice = new Item("Juice", "L", 2, "hejmeddig123@dibidut.au", "Status");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_shopping_list);


        debugList.add(katoffel);
        debugList.add(Tomat);
        debugList.add(Æg);
        debugList.add(juice);

       Lv_Shoppinglist = findViewById(R.id.debug_Lv_Shoppinglist);

       Lv_Shoppinglist.setAdapter(adaptor);

    }
    protected void onStart(){
        super.onStart();
    }

    protected void onStop(){
        super.onStop();
    }
}
