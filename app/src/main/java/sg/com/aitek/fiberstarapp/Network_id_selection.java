package sg.com.aitek.fiberstarapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Network_id_selection extends AppCompatActivity {

    EditText circle_radius;
    Button radius_ok, btnDismiss ,show_info;
    Spinner network_ids;

    String user_role = null, user_Name;
    int pre_radius;
    private int curr_radius;
    Double selected_lat, selected_long;

    ArrayList<String> entity_list;
    List<String> database_siteList;

    Connection conn;

    private Context context;
    private Statement statement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_id_selection);

        //circle_radius = (EditText) findViewById(R.id.radius);
        //radius_ok  = (Button) findViewById(R.id.radius_ok);
        //btnDismiss = (Button) findViewById(R.id.cancel);

        show_info  = (Button) findViewById(R.id.ok);
        network_ids= (Spinner) findViewById(R.id.popupspinner);

        context = this;

        Bundle bundle = getIntent().getExtras();

        entity_list = new ArrayList<String>();
        entity_list = getIntent().getStringArrayListExtra("entity_list");
        database_siteList = new ArrayList<>();

        if (bundle != null) {
            user_role = bundle.getString("user_role");
            user_Name = bundle.getString("user");
            pre_radius = bundle.getInt("radius");
            selected_lat = bundle.getDouble("selected_lat");
            selected_long = bundle.getDouble("selected_long");
        }
        System.out.println("---Network_id_selection---\nusername: " + user_Name + "\nuser_role: " + user_role + "\nRadius: " + pre_radius
                +" \nentityList size from network Id selection: "+entity_list.size());

        if(entity_list.size()>0) {

            LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
            total_spinner.setVisibility(View.VISIBLE);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Network_id_selection.this,
                    android.R.layout.simple_spinner_item, entity_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            network_ids.setAdapter(adapter);

            show_info.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //popupWindow.dismiss();
                    //Toast.makeText(getApplicationContext(), "you just clicked ok" + adapter.getItem(network_ids.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
                    String selected_near_id = adapter.getItem(network_ids.getSelectedItemPosition());
                    Intent intent = new Intent(Network_id_selection.this, Popup_Menu.class);
                    intent.putExtra("key", selected_near_id);
                    intent.putExtra("role", user_role);
                    intent.putExtra("userName", user_Name);
                    startActivity(intent);
                }
            });
        }
        else{
            LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
            total_spinner.setVisibility(View.GONE);
        }
    }
}
