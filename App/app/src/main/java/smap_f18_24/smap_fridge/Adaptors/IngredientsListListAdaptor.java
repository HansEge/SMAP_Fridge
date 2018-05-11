package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;

public class IngredientsListListAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<IngredientList> ingredientLists;
    private IngredientList ingredientList;


    public IngredientsListListAdaptor(Context c, ArrayList<IngredientList> lars){
        this.context = c;
        this.ingredientLists = lars;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of items in the shoppinglistLIST)
        if(ingredientLists != null){
            return ingredientLists.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (shoppinglist item) in our array list at the given position
        if(ingredientLists != null){
            return ingredientLists.get(position);
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
            convertView = fridgeInFlator.inflate(R.layout.ingredients_list_list_adaptor,null);
        }
        ingredientList = ingredientLists.get(position);
        if(ingredientList != null){
            //set name of the fridge
            TextView tv_IngredientsListName = (TextView) convertView.findViewById(R.id.tv_ingredientsListListName);
            tv_IngredientsListName.setText(ingredientList.getName());
        }

        return convertView;
    }




}
