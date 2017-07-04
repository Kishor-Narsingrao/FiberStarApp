package sg.com.aitek.fiberstarapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EntityDetails extends AppCompatActivity {

    String key=null,user_role=null,user_Name=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key       = bundle.getString("key");
            user_role = bundle.getString("role");
            user_Name = bundle.getString("userName");
        }
    }
}
