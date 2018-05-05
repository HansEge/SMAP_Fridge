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
import smap_f18_24.smap_fridge.R;

/**
 * Created by Liver on 01-05-2018.
 */

public class ShoppingListAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<Item> items;
    private Item item;


    ShoppingListAdaptor(Context c, ArrayList<Item> shoppingListItems){
        this.context = c;
        this.items = shoppingListItems;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of items in the shoppinglist)
        if(items != null){
            return items.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (shoppinglist item) in our array list at the given position
        if(items != null){
            return items.get(position);
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
        item = items.get(position);
        if(item != null){
            //set name of the fridge
            TextView tv_ShoppingListItemName = (TextView) convertView.findViewById(R.id.shoppingListAdaptor_tv_itemName);
            tv_ShoppingListItemName.setText(String.valueOf(item.getName()));

            TextView tv_ShoppingListItemQuantity = (TextView) convertView.findViewById(R.id.shoppingListAdaptor_tv_itemQuantity);
            tv_ShoppingListItemQuantity.setText(String.valueOf(item.getQuantity()));
        }

        return null;
    }
}
