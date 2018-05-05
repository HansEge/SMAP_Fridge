package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.R;

public class EssentialsListAdaptor extends BaseAdapter {

    private Context context;
    private EssentialsList essentialList;
    private Item item;


    public EssentialsListAdaptor(Context context, EssentialsList essentials)
    {
        this.context = context;
        this.essentialList = essentials;
    }


    @Override
    public int getCount() {
        //return size of the array list (number of items in the essentialList)
        if(essentialList != null){
            return essentialList.getItems().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        //return the item (shoppinglist item) in our array list at the given position
        if(essentialList != null){
            return essentialList.getItems().get(i);
        } else{
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater essentialInflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = essentialInflator.inflate(R.layout.essentials_list_adaptor,null);
        }
        item = essentialList.getItems().get(position);
        if(item != null){
            //set name of the fridge
            TextView tv_EssentialListItemName = (TextView) convertView.findViewById(R.id.EssentialList_adaptor_itemName);
            tv_EssentialListItemName.setText(String.valueOf(item.getName()));

            TextView tv_EssentialListItemQuantity = (TextView) convertView.findViewById(R.id.EssentialList_adaptor_qty);
            tv_EssentialListItemQuantity.setText(String.valueOf(item.getQuantity()));

            TextView tv_EssentialListItemUnit = (TextView) convertView.findViewById(R.id.EssentialList_adaptor_unit);
            tv_EssentialListItemUnit.setText(String.valueOf(item.getUnit()));
        }

        return convertView;
    }
}
