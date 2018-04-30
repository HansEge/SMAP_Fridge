package smap_f18_24.smap_fridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddNewFridgeActivity extends AppCompatActivity {

    ImageView im_fridge;
    TextView tv_fridgeName;
    EditText et_fridgeName;
    Button btn_addFridge, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_fridge);

        //INITIALIZATION

        im_fridge = findViewById(R.id.addNewFridge_im_fridge);
        tv_fridgeName = findViewById(R.id.addNewFridge_tv_fridgename);
        et_fridgeName = findViewById(R.id.addNewFridge_et_fridgename);
        btn_addFridge = findViewById(R.id.addNewFridge_btn_addFridge);
        btn_cancel = findViewById(R.id.addNewFridge_btn_cancel);

        et_fridgeName.setInputType(InputType.TYPE_CLASS_TEXT);

        //POST-INITIALIZATION

        //If the user wants to add a new fridge to the list
        btn_addFridge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                finish();

                /*
                    TODO
                    Save the data
                    Upload to the database
                    Broadcast all new information
                */
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });


    }
}
