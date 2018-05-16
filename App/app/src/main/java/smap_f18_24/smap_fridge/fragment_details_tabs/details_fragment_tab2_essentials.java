package smap_f18_24.smap_fridge.fragment_details_tabs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Fridge;
import smap_f18_24.smap_fridge.ModelClasses.Item;
import smap_f18_24.smap_fridge.OverviewActivity;
import smap_f18_24.smap_fridge.R;
import smap_f18_24.smap_fridge.Service.ServiceUpdater;
import android.widget.Button;


public class details_fragment_tab2_essentials extends Fragment {

    private static final String TAG = "details_fragment_tab2_e";

    private ListView essentialList;
    private EssentialsList EList = new EssentialsList();
    Button btn_addNewItem;

    private String clickedFridgeID;
    private Fridge currentFridge;

    public EssentialsListAdaptor adaptor;

    ServiceUpdater mService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mService = ((DetailsActivity)getActivity()).mService;

        //subscribe to broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceUpdater.BROADCAST_UPDATER_RESULT);
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver(serviceUpdaterReceiver,filter);

        View v = inflater.inflate(R.layout.fragment_details_tab2_essentials, container, false);

        final SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        clickedFridgeID = sharedData.getString(getString(R.string.CLICKED_FRIDGE_ID),"errorNoValue");

        essentialList = v.findViewById(R.id.lv_essential_tab2);

        //On click item
        essentialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item i  = (Item)adaptor.getItem(position);
                Log.d(TAG, "onClick: clicked Item " + i.getName());
                openEditItemDialogBox(i);
            }
        });

        essentialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Item i = (Item)adaptor.getItem(position);
                openDeleteItemDialogBox(i.getName());
                return true;
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try
        {
            EList = ((DetailsActivity)getActivity()).currentFridge.getEssentials();
        }
        catch (RuntimeException e)
        {
            EList = new EssentialsList();
        }
        adaptor = new EssentialsListAdaptor(getActivity().getBaseContext(),EList);

        essentialList.setAdapter(adaptor);

        currentFridge = ((DetailsActivity)getActivity()).currentFridge;
        btn_addNewItem= getView().findViewById(R.id.essentials_btn_newItem);
        btn_addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewItemDialogBox();
            }
        });
    }

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

   public void updateData(String updateString)
   {
       if(updateString.equals(getString(R.string.DATA_UPDATED)) && ((DetailsActivity) getActivity()).currentFridge != null)
       {
           ((DetailsActivity)getActivity()).currentFridge = ((DetailsActivity)getActivity()).mService.getFridge(currentFridge.getID());
           EList = ((DetailsActivity)getActivity()).currentFridge.getEssentials();
           adaptor = new EssentialsListAdaptor(getActivity().getBaseContext(),EList);
           essentialList.setAdapter(adaptor);
       }
   }

   private void openEditItemDialogBox(final Item i)
   {
       AlertDialog.Builder ItemClickedDialog = new AlertDialog.Builder(getActivity());
       ItemClickedDialog.setTitle(i.getName());

       LinearLayout layout = new LinearLayout(getActivity());
       layout.setOrientation(LinearLayout.VERTICAL);

       //Quantity
       final EditText et_qty = new EditText(getActivity());
       et_qty.setHint(getString(R.string.DIALOG_HINT_quantity));
       //Set input type as positive decimal.
       //https://stackoverflow.com/questions/6919360/how-do-i-restrict-my-edittext-input-to-numerical-possibly-decimal-and-signed-i?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
       et_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
       et_qty.setText(String.valueOf((i.getQuantity())));
       layout.addView(et_qty);

       ItemClickedDialog.setView(layout);

       ItemClickedDialog.setNegativeButton(getString(R.string.DIALOG_cancel_button), new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           }
       });

       ItemClickedDialog.setPositiveButton(getString(R.string.DIALOG_apply_button), new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               float quantity = Float.parseFloat(et_qty.getText().toString());
               Item overwriteItem = i;
               overwriteItem.setQuantity(quantity);
               mService.overwriteItemInEssentials(overwriteItem,currentFridge.getID());
           }
       });

       ItemClickedDialog.show();
   }

   private void openNewItemDialogBox()
   {
       AlertDialog.Builder newItemDialog = new AlertDialog.Builder(getActivity());
       newItemDialog.setTitle(getString(R.string.DIALOG_TITLE_newItem));

       LinearLayout layout = new LinearLayout(getActivity());
       layout.setOrientation(LinearLayout.VERTICAL);

       //item name
       final EditText et_ItemName = new EditText(getActivity());
       et_ItemName.setHint(getString(R.string.DIALOG_HINT_name));
       et_ItemName.setInputType(InputType.TYPE_CLASS_TEXT);
       layout.addView(et_ItemName);

       //Quantity
       final EditText et_qty = new EditText(getActivity());
       et_qty.setHint(getString(R.string.DIALOG_HINT_quantity));
       //Set input type as positive decimal.
       //https://stackoverflow.com/questions/6919360/how-do-i-restrict-my-edittext-input-to-numerical-possibly-decimal-and-signed-i?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
       et_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
       layout.addView(et_qty);

       //Unit
       final EditText et_Unit = new EditText(getActivity());
       et_Unit.setHint(getString(R.string.DIALOG_HINT_unit));
       et_Unit.setInputType(InputType.TYPE_CLASS_TEXT);
       layout.addView(et_Unit);

       newItemDialog.setView(layout);

       newItemDialog.setPositiveButton((getString(R.string.DIALOG_add_button)), new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {

               //Get info from editTexts
               String itemName=et_ItemName.getText().toString();
               float Quantity = Float.parseFloat(et_qty.getText().toString());
               String unit = et_Unit.getText().toString();

               Item i = new Item(itemName,unit,Quantity,"N/A","N/A");
               mService.addItemToEssentials(i,currentFridge.getID());
               mService.updateShoppingListToMatchEssentials(currentFridge.getID());
           }
       });

       newItemDialog.setNegativeButton((getString(R.string.DIALOG_cancel_button)), new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {

           }
       });

       newItemDialog.show();
   }

    private void openDeleteItemDialogBox(String itemName){
        final String _itemName = itemName;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.DIALOG_TITLE_delete_yes_no));

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);

        builder.setPositiveButton((getString(R.string.DIALOG_yes_button)), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((DetailsActivity)getActivity()).mService.removeItemFromEssentials(_itemName,currentFridge.getID());
                Log.d("Broadcast Receiver", "Error in broadcast receiver");

            }
        });
        builder.setNegativeButton((getString(R.string.DIALOG_cancel_button)), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    };
}

