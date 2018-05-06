package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;

public class ShoppingListListAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<ShoppingList> shoppingLists;
    private ShoppingList shoppingList;


    public ShoppingListListAdaptor(Context c, ArrayList<ShoppingList> shoppingListItems){
        this.context = c;
        this.shoppingLists = shoppingListItems;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of items in the shoppinglistLIST)
        if(shoppingLists != null){
            return shoppingLists.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (shoppinglist item) in our array list at the given position
        if(shoppingLists != null){
            return shoppingLists.get(position);
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
            convertView = fridgeInFlator.inflate(R.layout.shopping_list_list_adaptor,null);
        }
        shoppingList = shoppingLists.get(position);
        if(shoppingList != null){
            //set name of the fridge
            TextView tv_ShoppingListListName = (TextView) convertView.findViewById(R.id.tv_adaptor_shoppingListList_name);
            tv_ShoppingListListName.setText(String.valueOf(shoppingList.getName()));

            TextView tv_ShoppingListListResponsibility = (TextView) convertView.findViewById(R.id.tv_adaptor_shoppingListList_responsibility);
            tv_ShoppingListListResponsibility.setText(String.valueOf(shoppingList.getResponsibility()));

        }

        return convertView;
    }

}
