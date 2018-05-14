package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.IngredientsListListAdaptor;
import smap_f18_24.smap_fridge.IngredientsListActivity;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.IngredientList;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;


public class details_fragment_tab4_ingredients extends Fragment {


    ListView lv_ingredientsList;
    IngredientsListListAdaptor adaptor;
    private Fridge currentFridge;
    ServiceUpdater mService;
    Button btn_newList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab4_ingredients, container, false);

        //subscribe to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver(serviceUpdaterReceiver,filter);

        //Copy of mService from DetailsActivity.
        mService = ((DetailsActivity)getActivity()).mService;

        btn_newList=v.findViewById(R.id.details_tab4_ingredientlists_btn_newList);
        btn_newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenNewListDialogBox();
            }
        });

        lv_ingredientsList = v.findViewById(R.id.sdfds);
        lv_ingredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Start new IngredientListActivity with clicked Ingredient list passed on.
                Intent intent = new Intent(getActivity().getBaseContext(), IngredientsListActivity.class);
                String tmpID = ((DetailsActivity)getActivity()).currentFridge.getID();
                intent.putExtra("CurrentFridgeID",tmpID);
                intent.putExtra("PositionOfIngredientsList",i);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get cf currentFridge from DetailsActivity.
        currentFridge = ((DetailsActivity)getActivity()).currentFridge;
        //Set adaptor.
        adaptor = new IngredientsListListAdaptor(getActivity().getBaseContext(), (ArrayList<IngredientList>)((DetailsActivity)getActivity()).currentFridge.getIngredientLists());
        lv_ingredientsList.setAdapter(adaptor);

    }
    //BroadcastReceiver that updates UI when it receives a subscribed broadcast (currently, we're only subscribing to one broadcast, so we don't really check for the result other than null check)
    private BroadcastReceiver serviceUpdaterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ASDASD", "Broadcast reveiced from ServiceUpdater in tab2");
            String result = null;

            result = intent.getStringExtra(ServiceUpdater.EXTRA_TASK_RESULT);
            Log.d("ASDASD", result);

            if (result == null) {
                Log.d("ASDASD", result);
            }

            if(result != null) {
                updateData(result);
            }

        }
    };

    public void updateData(String updateString) {
        if (updateString.equals("DataUpdated")) {
            //get new data
            ((DetailsActivity) getActivity()).currentFridge = ((DetailsActivity) getActivity()).mService.getFridge(currentFridge.getID());
            //reset adaptor
            adaptor = new IngredientsListListAdaptor(getActivity().getBaseContext(), (ArrayList<IngredientList>) ((DetailsActivity) getActivity()).currentFridge.getIngredientLists());
            lv_ingredientsList.setAdapter(adaptor);
        }
    }

    //Dialog boxes inspired by https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    private void OpenNewListDialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.ADD_NEW_LIST));

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText et_newItemName = new EditText(getContext());
        et_newItemName.setHint(getString(R.string.NAME)+":");
        et_newItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_newItemName);


        builder.setView(layout);


        builder.setPositiveButton(getString(R.string.ADD), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String listName = et_newItemName.getText().toString();

                //create new list in Database and Subscribe to it.
                mService.createNewIngredientList(currentFridge.getID(),listName);

            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
