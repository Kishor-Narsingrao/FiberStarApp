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
    ArrayList<String> entity_list;
    Double selected_lat, selected_long;


    Connection conn;
    List<String> database_siteList;
    private Context context;
    private Statement statement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_id_selection);
        //circle_radius = (EditText) findViewById(R.id.radius);

        //radius_ok  = (Button) findViewById(R.id.radius_ok);
        show_info  = (Button) findViewById(R.id.ok);
        //btnDismiss = (Button) findViewById(R.id.cancel);

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

            /*btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                    total_spinner.setVisibility(View.GONE);
                }
            });*/

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

        /*radius_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String given_radius = circle_radius.getText().toString();
                if(given_radius.isEmpty() || given_radius.length()<1){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                    builder1.setTitle("Radius in meters");
                    builder1.setMessage("Please! enter radius to search");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                    total_spinner.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Please Enter Radius!", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), "Selected radius is: "+curr_radius, Toast.LENGTH_SHORT).show();
                *//*else if(pre_radius == curr_radius) {
                    Toast.makeText(getApplicationContext(), "You Selected previous radius only!", Toast.LENGTH_SHORT).show();
                }*//*else {
                        if(given_radius.contains(".")){
                            double radius = Double.parseDouble(given_radius);
                            curr_radius = (int) radius;
                        }
                        //else if(given_radius.matches("[a-zA-Z !@#$%^&*()-_+=]+")){
                          *//*  AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                            builder1.setTitle("Radius in meters");
                            builder1.setMessage("Please! enter radius between 1-50 in integers Only");
                            builder1.setCancelable(true);
                            builder1.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                            LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                            total_spinner.setVisibility(View.GONE);*//*
                       // }

                        else{
                            if(given_radius.matches("[A-Za-z !@#$%^&*()]+")){
                                *//*AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                                builder1.setTitle("Radius in meters");
                                builder1.setMessage("Please! enter radius between 1-50 in integers Only");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();*//*
                                LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                total_spinner.setVisibility(View.GONE);
                            }else {
                                curr_radius = Integer.parseInt(given_radius);
                            }
                        }

                        if(given_radius.contains(".") || given_radius.contains(",") || given_radius.matches("[A-Za-z !@#$%^&*()]+") ){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                            builder1.setTitle("Radius in meters");
                            builder1.setMessage("Please! enter radius between 1-50 in integers Only");
                            builder1.setCancelable(true);
                            builder1.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                            LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                            total_spinner.setVisibility(View.GONE);
                        }
                        else if(curr_radius < 1 || curr_radius >50){
                            //curr_radius = Integer.parseInt(given_radius);
                            //if{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                            builder1.setTitle("Radius in meters");
                            builder1.setMessage("Please! enter radius between 1-50");
                            builder1.setCancelable(true);
                            builder1.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                            LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                total_spinner.setVisibility(View.GONE);
                        }
                        else{
                            try {
                                entity_list = new ArrayList<String>();
                                LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                assert total_spinner != null;
                                total_spinner.setVisibility(View.GONE);

                                DbConncetion dbConncetion = new DbConncetion(context);
                                conn = dbConncetion.getConnection();
                                if (conn != null) {
                                    //Toast.makeText(getApplicationContext(), "connected successfully", Toast.LENGTH_SHORT).show();
                                    statement = conn.createStatement();
                                    String query = "select * from vw_att_details_site";
                                    ResultSet resultSet = statement.executeQuery(query);
                                    while (resultSet.next()) {
                                        String entity_type = "SITE:";
                                        String site_id = resultSet.getString("site_id");
                                        String site_name = resultSet.getString("site_name");
                                        String site_lat = resultSet.getString("site_latitude");
                                        String site_longi = resultSet.getString("site_longitude");

                                        String dataBase_latLong = entity_type + "," + site_id + "," + site_lat + "," + site_longi;
                                        database_siteList.add(dataBase_latLong);
                                    }
                                    //for adding customer entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_customer");
                                    while (resultSet.next()) {
                                        String entity_type   = "CUST:";
                                        String customer_id   = resultSet.getString("system_id");
                                        String customer_lat  = resultSet.getString("customer_lat");
                                        String customer_long = resultSet.getString("customer_long");

                                        String dataBase_latLong = entity_type + "," +customer_id + "," + customer_lat + "," + customer_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    //for adding pop entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_pop");
                                    while (resultSet.next()) {
                                        String entity_type = "POP:";
                                        String pop_id      = resultSet.getString("system_id");
                                        String pop_lat     = resultSet.getString("pop_latitude");
                                        String pop_long    = resultSet.getString("pop_longitude");

                                        String dataBase_latLong = entity_type + "," + pop_id + "," + pop_lat + "," + pop_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    //for adding chamber entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_chamber");
                                    while (resultSet.next()) {
                                        String entity_type  = "CHMB:";
                                        String chamber_id   = resultSet.getString("system_id");
                                        String chamber_lat  = resultSet.getString("latitude");
                                        String chamber_long = resultSet.getString("longitude");

                                        String dataBase_latLong = entity_type + "," + chamber_id + "," + chamber_lat + "," + chamber_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    //for adding chamber entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_pole");
                                    while (resultSet.next()) {
                                        String entity_type = "POLE:";
                                        String pole_id     = resultSet.getString("system_id");
                                        String pole_lat    = resultSet.getString("latitude");
                                        String pole_long   = resultSet.getString("longitude");

                                        String dataBase_latLong = entity_type + "," + pole_id + "," + pole_lat + "," + pole_long;
                                        database_siteList.add(dataBase_latLong);
                                    }
                                    //for adding splice closure entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_spliceclosure");
                                    while (resultSet.next()) {
                                        String chamber_system_id = resultSet.getString("system_id");
                                        String chamber_id = resultSet.getString("chamber_id");
                                        resultSet = statement.executeQuery("select * from vw_att_details_chamber where chamber_id = \'" + chamber_id + "\'");
                                        while (resultSet.next()) {
                                            String entity_type      = "SPCL:";
                                            String chamber_lat      = resultSet.getString("latitude");
                                            String chamber_long     = resultSet.getString("longitude");

                                            String dataBase_latLong = entity_type + "," + chamber_system_id + "," + chamber_lat + "," + chamber_long;
                                            database_siteList.add(dataBase_latLong);
                                        }
                                    }

                                    //for adding odf entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_odf");
                                    while (resultSet.next()) {
                                        String entity_type = "ODF:";
                                        String odf_id      = resultSet.getString("system_id");
                                        String odf_lat     = resultSet.getString("odf_lat");
                                        String odf_long    = resultSet.getString("odf_long");

                                        String dataBase_latLong = entity_type + "," + odf_id + "," + odf_lat + "," + odf_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    //for adding splitter entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_splitter");
                                    while (resultSet.next()) {
                                        String entity_type   = "SPLITTER:";
                                        String splitter_id   = resultSet.getString("system_id");
                                        String splitter_lat  = resultSet.getString("latitude");
                                        String splitter_long = resultSet.getString("longitude");

                                        String dataBase_latLong = entity_type + "," + splitter_id + "," + splitter_lat + "," + splitter_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    //for adding Logical Link entity latLongs
                                    resultSet = statement.executeQuery("select * from vw_att_details_link");
                                    while (resultSet.next()) {
                                        String entity_type  = "LGLK:";
                                        String lglink_id = resultSet.getString("system_id");
                                        String lglink_lat = resultSet.getString("site_lat");
                                        String lglink_long = resultSet.getString("site_lng");

                                        String dataBase_latLong = entity_type + "," + lglink_id + "," + lglink_lat + "," + lglink_long;
                                        database_siteList.add(dataBase_latLong);
                                    }

                                    int count = 0;
                                    LatLng point = new LatLng(selected_lat,selected_long);
                                    //LatLng point = new LatLng(-6.23105686799699, 106.561338971864);
                                    LatLng southwest = SphericalUtil.computeOffset(point, curr_radius * Math.sqrt(2.0), 225);
                                    LatLng northeast = SphericalUtil.computeOffset(point, curr_radius * Math.sqrt(2.0), 45);
                                    LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);

                                    //Toast.makeText(getApplicationContext(), "total database entities: "+database_siteList.size(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), "latlongs from previous: "+selected_lat+"\n"+selected_long, Toast.LENGTH_SHORT).show();
                                    //List<String> latList = database_siteList;
                                    entity_list = new ArrayList<String>();

                                    for (int i = 0; i < database_siteList.size(); i++) {
                                        //for (String list : latList) {
                                        String list = database_siteList.get(i);
                                        String arr[] = list.split(",");
                                        String type  = arr[0];
                                        String key   = arr[1];
                                        String lats  = arr[2];
                                        String longs = arr[3];
                                        //System.out.println("type, key, lat and longitude from db: " + type + key + " lats:" + lats + " longi:" + longs);

                                        Double db_lat = Double.parseDouble(lats);
                                        Double db_longs = Double.parseDouble(longs);
                                        LatLng latLong = new LatLng(db_lat, db_longs);
                                        if (latLngBounds.contains(latLong)) {
                                                 //System.out.println("latlong bounds contained latlong is: " + latLong);
                                                 count++;
                                                //System.out.println("count: " + count + " key Id: " + key+" type: "+key);
                                                String keyType = type + key;
                                                if (!entity_list.contains(keyType)) {
                                                entity_list.add(keyType);
                                                }
                                            }
                                        }

                                    if(entity_list.size()>0) {
                                        LinearLayout spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                        total_spinner.setVisibility(View.VISIBLE);
                                        //total_spinner.setVisibility(View.VISIBLE);

                                        Toast.makeText(getApplicationContext(), "entity list size after new radius: " + entity_list.size(), Toast.LENGTH_SHORT).show();
                                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Network_id_selection.this,
                                                android.R.layout.simple_spinner_item, entity_list);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        network_ids.setAdapter(adapter);

                                        btnDismiss.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                LinearLayout total_spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                                total_spinner.setVisibility(View.GONE);
                                            }

                                        });

                                        show_info.setOnClickListener(new Button.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //popupWindow.dismiss();
                                                //Toast.makeText(getApplicationContext(), "you just clicked ok" + adapter.getItem(network_ids.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
                                                String selected_near_id = adapter.getItem(network_ids.getSelectedItemPosition());
                                                Intent intent = new Intent(Network_id_selection.this, Popup_Menu.class);
                                                String akey[]    = selected_near_id.split(":");
                                                selected_near_id = akey[1];
                                                intent.putExtra("key", selected_near_id);
                                                intent.putExtra("role", user_role);
                                                intent.putExtra("userName", user_Name);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    else{
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Network_id_selection.this);
                                        builder1.setTitle("Network Ids");
                                        builder1.setMessage("Sorry! No network Ids are available in this radius!");
                                        builder1.setCancelable(true);
                                        builder1.setNeutralButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                        LinearLayout spinner = (LinearLayout) findViewById(R.id.total_spinner);
                                        assert total_spinner != null;
                                        total_spinner.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                catch (SQLException sql) {
                                    sql.printStackTrace();
                                }
                            }
                        }
                }
        });*/
    }


}


 /*submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = keyId.getText().toString();
                    System.out.println("keyId: " + key);

                    if (key.equalsIgnoreCase(null) || key.length() < 1) {
                        Toast.makeText(getApplicationContext(), "Please enter KeyId", Toast.LENGTH_LONG).show();
                        //System.out.println("----------  Key Id is null  ---------");
                    } else {
                        //System.out.println("----------  Key Id is not null  ---------");
                        if (conn != null) {
                            String query_keyId = "select * from vw_att_details_site where site_id=\'" + key + "\'";
                            // System.out.println("query for site id: " + query_keyId);
                            String query_cust_id = "select * from vw_att_details_customer where system_id =\'" + key + "\'";
                            ResultSet resultSet;
                            try {
                                if(key.startsWith("SITE")|| key.startsWith("site")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH SITE", Toast.LENGTH_LONG).show();
                                    resultSet = statement.executeQuery(query_keyId);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati = resultSet.getString("site_latitude");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("site_longitude");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid Site Id", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else if(key.startsWith("CUST") || key.startsWith("cust")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH CUST", Toast.LENGTH_LONG).show();
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati = resultSet.getString("customer_lat");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("customer_long");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid customer id", Toast.LENGTH_LONG).show();
                                    }
                                }

                                else if(key.startsWith("POP") || key.startsWith("pop")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH POP", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_pop where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati = resultSet.getString("pop_latitude");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("pop_longitude");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid POP id", Toast.LENGTH_LONG).show();
                                    }
                                }

                                else if(key.startsWith("CHAMBER") || key.startsWith("CHMB")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH chamber", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_chamber where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati = resultSet.getString("latitude");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("longitude");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid Chamber id", Toast.LENGTH_LONG).show();
                                    }
                                }


                                else if(key.startsWith("POLE") || key.startsWith("pole")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH Pole", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_pole where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati = resultSet.getString("latitude");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("longitude");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid Pole id", Toast.LENGTH_LONG).show();
                                    }
                                }

                                else if(key.startsWith("SPCL") || key.startsWith("spcl")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH splice closure", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_spliceclosure where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        String chamber_id = resultSet.getString("chamber_id");
                                        query_cust_id = "select * from vw_att_details_chamber where chamber_id =\'" + chamber_id + "\'";

                                        System.out.println("query id: "+query_cust_id);

                                        resultSet = statement.executeQuery(query_cust_id);
                                        while(resultSet.next()) {
                                            System.out.println("INSIDE RESULT NOT NULL");
                                            db_lati = resultSet.getString("latitude");
                                            //System.out.println("db Latitude: " + db_lati);
                                            db_longi = resultSet.getString("longitude");
                                            //System.out.println("db Longitude: " + db_longi);
                                            Double db_lat = Double.parseDouble(db_lati);
                                            Double db_long = Double.parseDouble(db_longi);
                                            searchAddress(key, db_lat, db_long);
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid splice closure id", Toast.LENGTH_LONG).show();
                                    }
                                }

                                else if(key.startsWith("ODF") || key.startsWith("odf")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH ODF", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_odf where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati  = resultSet.getString("odf_lat");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("odf_long");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat  = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid ODF id", Toast.LENGTH_LONG).show();
                                    }
                                }


                                else if(key.startsWith("SPL") || key.startsWith("SPLT") ||key.startsWith("splt")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH splitter", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_splitter where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati  = resultSet.getString("latitude");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("longitude");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat  = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid splitter id", Toast.LENGTH_LONG).show();
                                    }
                                }


                                else if(key.startsWith("LGLK") || key.startsWith("LGLN") ||key.startsWith("lglk")){
                                    Toast.makeText(getApplicationContext(), "SITE STARTS WITH logical link", Toast.LENGTH_LONG).show();
                                    query_cust_id = "select * from vw_att_details_link where system_id =\'" + key + "\'";
                                    System.out.println("query id: "+query_cust_id);
                                    resultSet = statement.executeQuery(query_cust_id);
                                    String db_lati = null, db_longi = null;
                                    if (resultSet.next()) {
                                        db_lati  = resultSet.getString("site_lat");
                                        //System.out.println("db Latitude: " + db_lati);
                                        db_longi = resultSet.getString("site_lng");
                                        //System.out.println("db Longitude: " + db_longi);
                                        Double db_lat  = Double.parseDouble(db_lati);
                                        Double db_long = Double.parseDouble(db_longi);
                                        searchAddress(key, db_lat, db_long);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid logical link id", Toast.LENGTH_LONG).show();
                                    }
                                }

                                else{
                                    Toast.makeText(getApplicationContext(), "Please Enter Valid id", Toast.LENGTH_LONG).show();
                                }



                            } catch (SQLException se) {
                                se.printStackTrace();
                            }
                        }
                    }
                }
            });*/


