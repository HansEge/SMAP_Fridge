package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import smap_f18_24.smap_fridge.Adaptors.ShoppingListListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.ModelClasses.ShoppingList;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import smap_f18_24.smap_fridge.ShoppingListActivity;


public class details_fragment_tab3_shoppinglists extends Fragment {

    ServiceUpdater mService;
    private Fridge currentFridge;
    private ShoppingListListAdaptor adaptor;
    private ListView lv_shoppingListList;
    Button btn_newList;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get copy of currrent fridge from DetailsActivity.
        currentFridge = ((DetailsActivity)getActivity()).currentFridge;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_details_tab3_shoppinglists, container, false);

        //subscribe to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver(serviceUpdaterReceiver,filter);

        //Get copy of mService from detailsActivity.
        mService = ((DetailsActivity)getActivity()).mService;
        lv_shoppingListList = v.findViewById(R.id.lv_shoppingListList_tab3);
        btn_newList = v.findViewById(R.id.details_tab3_shoppinglists_btn_newList);


        btn_newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenNewListDialogBox();
            }
        });

        lv_shoppingListList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Start new ShoppingListActivity with the clicked shopping list passed on.
                Intent intent = new Intent(getActivity().getBaseContext(), ShoppingListActivity.class);
                String tmpID = ((DetailsActivity)getActivity()).currentFridge.getID();

                intent.putExtra(getString(R.string.CURRENT_FRIDGE_ID),tmpID);
                intent.putExtra(getString(R.string.POSITION_OF_SHOPPING_LIST),i);

                startActivity(intent);

            }
        });

        lv_shoppingListList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openClaimResponsibilityDialogBox((ShoppingList)adaptor.getItem(position));
                return true;
            }
        });



        adaptor = new ShoppingListListAdaptor(getActivity().getBaseContext(),(ArrayList<ShoppingList>)((DetailsActivity)getActivity()).currentFridge.getShoppingLists());
        lv_shoppingListList.setAdapter(adaptor);

        return v;
    }

    //Dialog boxes inspired by https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    private void openClaimResponsibilityDialogBox(final ShoppingList shoppingList){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.WANT_TO_CLAIM_RESPONSIBILITY);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mService.setResponsibilityForShoppingList(currentFridge.getID(),shoppingList.getID(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            }
        });

        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setNeutralButton(R.string.DELETE_LIST, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove list locally
                ((DetailsActivity)getActivity()).currentFridge.getShoppingLists().remove(shoppingList);

                //remove in database.
                mService.deleteShoppingList(currentFridge.getID(),shoppingList.getID());

                updateData(getString(R.string.DATA_UPDATED));
            }
        });

        builder.show();
    };

    //Dialog boxes inspired by https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    private void OpenNewListDialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ADD_NEW_LIST);

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
                mService.createNewShoppingList(currentFridge.getID(),listName);

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
        if (updateString.equals(getString(R.string.DATA_UPDATED))) {
            //get new data
            ((DetailsActivity) getActivity()).currentFridge = ((DetailsActivity) getActivity()).mService.getFridge(currentFridge.getID());
            //reset adaptor
            adaptor = new ShoppingListListAdaptor(getActivity().getBaseContext(), (ArrayList<ShoppingList>) ((DetailsActivity) getActivity()).currentFridge.getShoppingLists());
            lv_shoppingListList.setAdapter(adaptor);
        }
    }
}
