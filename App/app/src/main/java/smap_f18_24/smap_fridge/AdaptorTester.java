package smap_f18_24.smap_fridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import smap_f18_24.smap_fridge.Adaptors.EssentialsListAdaptor;
import smap_f18_24.smap_fridge.ModelClasses.EssentialsList;
import smap_f18_24.smap_fridge.ModelClasses.Item;

public class AdaptorTester extends AppCompatActivity {

    private EssentialsList list = new EssentialsList();
    private EssentialsListAdaptor adaptor = new EssentialsListAdaptor(this,list);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaptor_tester);

        Item Pølser = new Item("Pølser","stk",10,"","");
        Item Nudler = new Item("Nudler","g",140,"","");
        Item Vin = new Item("Vin","Flasker",110,"","");
        Item Is = new Item("Is","stk",40,"","");
        Item Rasp = new Item("Rasp","g",56,"","");



        list.AddItem(Pølser);
        list.AddItem(Nudler);
        list.AddItem(Vin);
        list.AddItem(Is);
        list.AddItem(Rasp);

        ListView ListTester = findViewById(R.id.ListView_Adaptor_Tester);

        ListTester.setAdapter(adaptor);





    }
}
