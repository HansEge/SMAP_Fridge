package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import smap_f18_24.smap_fridge.ModelClasses.InventoryList;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.R;

public class InventoryListAdaptor extends BaseAdapter {
    private Context context;
    private InventoryList inventoryList;
    private Item item;


    public InventoryListAdaptor(Context context, InventoryList inventory)
    {
        this.context = context;
        this.inventoryList = inventory;
    }


    @Override
    public int getCount() {
        //return size of the array list (number of items in the essentialList)
        if(inventoryList != null){
            return inventoryList.getItems().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        //return the item (shoppinglist item) in our array list at the given position
        if(inventoryList != null){
            return inventoryList.getItems().get(i);
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
            LayoutInflater inventoryInflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inventoryInflator.inflate(R.layout.inventory_list_adaptor,null);
        }
        item = inventoryList.getItems().get(position);
        if(item != null){
            //set name of item
            TextView tv_inventoryListItemName = (TextView) convertView.findViewById(R.id.InventoryList_adaptor_itemName);
            tv_inventoryListItemName.setText(String.valueOf(item.getName()));
            //set quantity of item
            TextView tv_inventoryListItemQuantity = (TextView) convertView.findViewById(R.id.InventoryList_adaptor_qty);
            tv_inventoryListItemQuantity.setText(String.valueOf(item.getQuantity()));
            //set unit of item
            TextView tv_inventoryListItemUnit = (TextView) convertView.findViewById(R.id.InventoryList_adaptor_unit);
            tv_inventoryListItemUnit.setText(String.valueOf(item.getUnit()));
        }

        return convertView;
    }
}
