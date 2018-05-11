package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.Resource;

import org.w3c.dom.Text;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.IngredientsListListAdaptor;
import smap_f18_24.smap_fridge.Adaptors.InventoryListAdaptor;
import smap_f18_24.smap_fridge.IngredientsListActivity;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.ShoppingListActivity;

// TODO - Missing link to context in order to findView by R.id. for example

public class details_fragment_tab4_ingredients extends Fragment {


    ListView lv_ingredientsList;

    IngredientsListListAdaptor adaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab4_ingredients, container, false);

        lv_ingredientsList = v.findViewById(R.id.lv_ingredients_tab4);

        lv_ingredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity().getApplicationContext(), IngredientsListActivity.class);

                String tmpID = ((DetailsActivity)getActivity()).currentFridge.getID();

                intent.putExtra("CurrentFridgeID",tmpID);
                intent.putExtra("PositionOfShoppingList",i);

                startActivity(intent);


            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adaptor = new IngredientsListListAdaptor(getActivity().getApplicationContext(), (ArrayList<IngredientList>)((DetailsActivity)getActivity()).currentFridge.getIngredientLists());

        lv_ingredientsList.setAdapter(adaptor);

    }
}
