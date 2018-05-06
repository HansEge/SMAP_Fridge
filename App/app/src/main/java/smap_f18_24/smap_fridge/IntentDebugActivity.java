package smap_f18_24.smap_fridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class IntentDebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_debug);


        String clickedFridgeID = getIntent().getStringExtra("clickedFridgeID");

        TextView tv = findViewById(R.id.textView);

        tv.setText("This is the data from the intent: " + clickedFridgeID);


    }
}
