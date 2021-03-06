package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.R;

public class IngredientsListAdaptor extends BaseAdapter {
    private Context context;
    private IngredientList ingredientList;
    private Item item;

    public IngredientsListAdaptor(Context c, IngredientList ingredients ){
        this.context = c;
        this.ingredientList = ingredients;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of items in the ingredientslist)
        if(ingredientList != null){
            return ingredientList.getItems().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (ingredientslist item) in our array list at the given position
        if(ingredientList != null){
            return ingredientList.getItems().get(position);
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
            convertView = fridgeInFlator.inflate(R.layout.ingredients_list_item,null);
        }
        item = ingredientList.getItems().get(position);
        if(item != null){
            //set name of item name
            TextView tv_IngredientsListItemName = (TextView) convertView.findViewById(R.id.ingredientsListAdaptor_tv_itemName);
            tv_IngredientsListItemName.setText(String.valueOf(item.getName()));
            //set quantaty of item
            TextView tv_IngredientsListItemQuantity = (TextView) convertView.findViewById(R.id.ingredientsListAdaptor_tv_itemQuantity);
            tv_IngredientsListItemQuantity.setText(String.valueOf(item.getQuantity()));
            //set unit of item
            TextView tv_IngredientsListItemUnit = (TextView) convertView.findViewById(R.id.ingredientsListAdaptor_tv_itemUnit);
            tv_IngredientsListItemUnit.setText(String.valueOf(item.getUnit()));

        }

        return convertView;
    }
}
