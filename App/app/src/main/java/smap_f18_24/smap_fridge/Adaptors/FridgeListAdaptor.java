package smap_f18_24.smap_fridge.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.R;

/**
 * Created by Liver on 27-04-2018.
 */

public class FridgeListAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<Fridge> fridges;
    private Fridge fridge;

    FridgeListAdaptor(Context c, ArrayList<Fridge> fridgeList){
        this.context = c;
        this.fridges = fridgeList;
    }

    @Override
    public int getCount() {
        //return size of the array list (number of fridges)
        if(fridges != null){
            return fridges.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (fridge object) in our array list at the given position
        if(fridges != null){
            return fridges.get(position);
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
            convertView = fridgeInFlator.inflate(R.layout.fridge_list_item,null);
        }
        fridge = fridges.get(position);
        if(fridge != null){
            //set name of the fridge
            TextView tv_fridgeName = (TextView) convertView.findViewById(R.id.fridgeListAdaptor_tv_fridgeName);
            tv_fridgeName.setText(String.valueOf(fridge.getName()));
        }



        return null;
    }
}
