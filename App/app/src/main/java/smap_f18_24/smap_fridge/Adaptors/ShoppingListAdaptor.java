package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;


public class ShoppingListAdaptor extends BaseAdapter {
    private Context context;
    private ShoppingList shoppingList;
    private Item item;


    public ShoppingListAdaptor(Context c, ShoppingList shoppingLists){
        this.context = c;
        this.shoppingList = shoppingLists;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of items in the shoppinglist)
        if(shoppingList != null){
            return shoppingList.getItems().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (shoppinglist item) in our array list at the given position
        if(shoppingList != null){
            return shoppingList.getItems().get(position);
        } else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater fridgeInFlator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = fridgeInFlator.inflate(R.layout.shopping_list_item,null);
        }
        item = shoppingList.getItems().get(position);
        if(item != null){
            //set name of item
            TextView tv_ShoppingListItemName = (TextView) convertView.findViewById(R.id.shoppingListAdaptor_tv_itemName);
            tv_ShoppingListItemName.setText(String.valueOf(item.getName()));
            //set quantity of item
            TextView tv_ShoppingListItemQuantity = (TextView) convertView.findViewById(R.id.shoppingListAdaptor_tv_itemQuantity);
            tv_ShoppingListItemQuantity.setText(String.valueOf(item.getQuantity()));
            //set unit of item
            TextView tv_ShoppingListItemUnit = (TextView) convertView.findViewById(R.id.shoppingListAdaptor_tv_itemUnit);
            tv_ShoppingListItemUnit.setText(String.valueOf(item.getUnit()));
        }

        return convertView;
    }
}
