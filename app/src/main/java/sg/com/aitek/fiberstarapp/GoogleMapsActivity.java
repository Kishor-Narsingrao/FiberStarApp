package sg.com.aitek.fiberstarapp;

import android.app.ProgressDialog;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.cast.framework.zzn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;


public class GoogleMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Location Your_location;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LatLngBounds latLngBounds;
    Marker marker;

    EditText userInput;
    Button btnOpenPopup,btnViewNetworkIds;

    List<String> database_siteList;
    Connection conn;
    Statement statement;

    String user, user_role;
    boolean status = true;
    Double sele_lat;
    Double sele_long;
    int count;
    int radius;

    private Context context;
    Circle mapCircle=null;

    List<String> entity_list = new ArrayList<String>();

    private AppCompatDelegate mDelegate;

    SessionManager session;
    LoginActivity loginActivity;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

       Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);


        context = this;
        loginActivity = new LoginActivity();
        session = new SessionManager(getApplicationContext());


        if (ContextCompat.checkSelfPermission(GoogleMapsActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapsActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");

        btnOpenPopup = (Button) findViewById(R.id.openpopup);
        btnViewNetworkIds=(Button)findViewById(R.id.btViewNetworkId);

        session.checkLogin();
        session.createLoginSession(user);

        try {
            DbConncetion dbConncetion = new DbConncetion(context);
            conn = dbConncetion.getConnection();

            if (conn != null) {
                statement = conn.createStatement();
                String user_role_query = "SELECT MODULE_ROLE from user_modules WHERE USER_ID = (SELECT USER_ID FROM USER_MASTER WHERE USER_NAME = '" + user + "')";
                ResultSet resultSet1 = statement.executeQuery(user_role_query);
                if(resultSet1.next()) {
                    user_role = resultSet1.getString("MODULE_ROLE");
                }
            }
        }
        catch(SQLException sql){
            sql.printStackTrace();
        }

        btnOpenPopup.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (marker == null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                    builder1.setTitle("Choose Location");
                    builder1.setMessage("Please Select a Location on Map");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    getRadius();
                }
            }
        });

        btnViewNetworkIds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker == null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                    builder1.setTitle("Choose Location");
                    builder1.setMessage("Please Select a Location on Map");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    radius=50;
                    getEntityInfo();
                    if (entity_list.size() > 0) {
                        Intent network_id = new Intent(GoogleMapsActivity.this, Network_id_selection.class);
                        network_id.putExtra("user", user);
                        network_id.putExtra("user_role", user_role);
                        network_id.putExtra("radius", radius);
                        network_id.putExtra("selected_lat", sele_lat);
                        network_id.putExtra("selected_long", sele_long);
                        network_id.putStringArrayListExtra("entity_list", (ArrayList<String>) entity_list);
                        startActivity(network_id);
                    }
                }
            }
        });


        database_siteList = new ArrayList<>();

        if (!status) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
            builder1.setTitle("No Internet Connection");
            builder1.setMessage("Google map is not coming, please check your internet connection");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(GoogleMapsActivity.this);
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void addCircleToMap(int radius,double lat,double lng) {

        // circle settings
        int radiusM = radius;
        double latitude = lat;
        double longitude =lng;


        //to remove circle when refreshing the google map
        if(mapCircle!=null){
            mapCircle.remove();
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longitude))   //set center
                .radius(radiusM)   //set radius in meters
                .fillColor(0x55ff0000)  //default
                .strokeColor(Color.BLACK)
                .strokeWidth(5);

        mapCircle=mMap.addCircle(circleOptions);

    }

    //Search Location by name in google Map
    public void SearchLocation(String location) {

//        EditText etSearch=(EditText) findViewById(R.id.etSearch);

        String Location=location;//etSearch.getText().toString();
        List<android.location.Address> addressList = null;

        if(Location != null || !Location.equals(""))
        {
            Geocoder geocoder=new Geocoder(this);
            try
            {
                addressList=geocoder.getFromLocationName(Location,1);

                android.location.Address address=addressList.get(0);
                sele_lat=address.getLatitude();
                sele_long=address.getLongitude();
                LatLng latlng=new LatLng(sele_lat,sele_long);

                //Clearing the marker from map
                mMap.clear();

                marker=mMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

                addCircleToMap(Integer.parseInt(userInput.getText().toString()),address.getLatitude(),address.getLongitude());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    public void getEntityInfo() {

        addCircleToMap(radius,sele_lat,sele_long);

        database_siteList = new ArrayList<>();

        DbConncetion dbConncetion = new DbConncetion(context);
        conn = dbConncetion.getConnection();

        if (conn != null) {
            try {
                statement = conn.createStatement();

                String query = "select * from vw_att_details_site";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {

                    String entity_type = "SITE:";
                    String site_id = resultSet.getString("site_id");
                    //String site_name = resultSet.getString("site_name");
                    String site_lat = resultSet.getString("site_latitude");
                    String site_longi = resultSet.getString("site_longitude");

                    if(site_id!=null && site_lat!=null && site_longi!=null) {
                        String dataBase_latLong = entity_type + "," + site_id + "," + site_lat + "," + site_longi;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                //for adding customer entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_customer");
                while (resultSet.next()) {
                    String entity_type = "CUST:";
                    String customer_id = resultSet.getString("customer_id");
                    String customer_lat = resultSet.getString("customer_lat");
                    String customer_long = resultSet.getString("customer_long");

                    if(customer_id!=null && customer_lat!=null && customer_long!=null) {
                        String dataBase_latLong = entity_type + "," + customer_id + "," + customer_lat + "," + customer_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                //for adding pop entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_pop");
                while (resultSet.next()) {
                    String entity_type = "POP:";
                    String pop_id = resultSet.getString("pop_id");
                    String pop_lat = resultSet.getString("pop_latitude");
                    String pop_long = resultSet.getString("pop_longitude");

                    if(pop_id!=null && pop_lat!=null && pop_long!=null) {
                        String dataBase_latLong = entity_type + "," + pop_id + "," + pop_lat + "," + pop_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                //for adding chamber entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_chamber");
                while (resultSet.next()) {
                    String entity_type = "CHMBR:";
                    String chamber_id = resultSet.getString("chamber_id");
                    String chamber_lat = resultSet.getString("latitude");
                    String chamber_long = resultSet.getString("longitude");

                    if (chamber_id != null && chamber_lat != null && chamber_long != null){
                        String dataBase_latLong = entity_type + "," + chamber_id + "," + chamber_lat + "," + chamber_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                //for adding chamber entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_pole");
                while (resultSet.next()) {
                    String entity_type = "POLE:";
                    String pole_id = resultSet.getString("pole_id");
                    String pole_lat = resultSet.getString("latitude");
                    String pole_long = resultSet.getString("longitude");

                    if(pole_id!=null && pole_lat!=null && pole_long!=null) {
                        String dataBase_latLong = entity_type + "," + pole_id + "," + pole_lat + "," + pole_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                if (conn == null) {
                    conn = dbConncetion.getConnection();
                }

                //for adding splice closure entity latLongs
                if (conn != null) {
                    resultSet = statement.executeQuery("select * from vw_att_details_spliceclosure");
                    while (resultSet.next()) {
                        String chamber_system_id = resultSet.getString("spliceclosure_id");
                        String chamber_id = resultSet.getString("chamber_id");
                        Statement stmt = conn.createStatement();
                        ResultSet resultSet1 = stmt.executeQuery("select * from vw_att_details_chamber where chamber_id = \'" + chamber_id + "\'");

                        while (resultSet1.next()) {
                            String entity_type = "SPCL:";
                            String chamber_lat = resultSet1.getString("latitude");
                            String chamber_long = resultSet1.getString("longitude");

                            if (chamber_id != null && chamber_lat != null && chamber_long != null) {
                                String dataBase_latLong = entity_type + "," + chamber_system_id + "," + chamber_lat + "," + chamber_long;
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                if (conn != null) {
                    resultSet = statement.executeQuery("select * from vw_att_details_spliceclosure");
                    while (resultSet.next()) {
                        String chamber_system_id = resultSet.getString("spliceclosure_id");
                        String chamber_id = resultSet.getString("chamber_id");
                        Statement stmt = conn.createStatement();
                        ResultSet resultSet2 = stmt.executeQuery("select * from vw_att_details_pole where pole_id = \'" + chamber_id + "\'");
                        while (resultSet2.next()) {
                            String entity_type = "SPCL:";
                            String chamber_lat = resultSet2.getString("latitude");
                            String chamber_long = resultSet2.getString("longitude");

                            if (chamber_id != null && chamber_lat != null && chamber_long != null) {
                                String dataBase_latLong = entity_type + "," + chamber_system_id + "," + chamber_lat + "," + chamber_long;
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                //for adding odf entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_odf");
                while (resultSet.next()) {
                    String entity_type = "ODF:";
                    String odf_id = resultSet.getString("odf_id");
                    String odf_lat = resultSet.getString("odf_lat");
                    String odf_long = resultSet.getString("odf_long");

                    if(odf_id!=null && odf_lat!=null && odf_long!=null) {
                        String dataBase_latLong = entity_type + "," + odf_id + "," + odf_lat + "," + odf_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }

                resultSet = statement.executeQuery("SELECT *,ST_AsText(sp_geometry) as venkat  FROM vw_att_details_cable");
                while (resultSet.next()) {
                    String entity_type = "CABLE:";
                    String latLongs = resultSet.getString("venkat");
                    String cable_id = resultSet.getString("cable_id");
                    String cable_lat = null;
                    String cable_long= null;

                    if(latLongs.contains("LINESTRING") || latLongs.contains("(") ||  latLongs.contains(")"))
                    {
                        String onlyLatLongs =  latLongs.replaceAll("[^0-9,. -]", "");
                        String array_of_latLongs[] = onlyLatLongs.split(",");
                        for(int i=0;i<array_of_latLongs.length;i++){
                            String longLat[] = array_of_latLongs[i].split(" ");
                            cable_lat = longLat[1];
                            cable_long = longLat[0];

                            if(cable_id!=null && cable_lat!=null && cable_long!=null) {
                                String dataBase_latLong = entity_type + "," + cable_id + "," + cable_lat + "," + cable_long;
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                resultSet = statement.executeQuery("SELECT *,ST_AsText(sp_geometry) as venkat  FROM vw_att_details_circuit");
                while (resultSet.next()) {
                    String entity_type = "CIRCUIT:";
                    String latLongs = resultSet.getString("venkat");
                    String circuit_id = resultSet.getString("circuit_id");
                    String cable_lat = null;
                    String cable_long= null;

                    if(latLongs.contains("LINESTRING") || latLongs.contains("(") ||  latLongs.contains(")"))
                    {
                        String onlyLatLongs =  latLongs.replaceAll("[^0-9,. -]", "");
                        String array_of_latLongs[] = onlyLatLongs.split(",");
                        for(int i=0;i<array_of_latLongs.length;i++){
                            String longLat[] = array_of_latLongs[i].split(" ");
                            cable_lat = longLat[1];
                            cable_long = longLat[0];

                            if(circuit_id!=null && cable_lat!=null && cable_long!=null) {
                                String dataBase_latLong = entity_type + "," + circuit_id + "," + cable_lat + "," + cable_long;
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                if (conn != null) {
                    resultSet = statement.executeQuery("select * from vw_att_details_splitter");
                    while (resultSet.next()) {
                        String chamber_system_id = resultSet.getString("splitter_id");
                        String chamber_id = resultSet.getString("parent_id");
                        Statement stmt = conn.createStatement();
                        ResultSet resultSet1 = stmt.executeQuery("select * from vw_att_details_chamber where chamber_id = \'" + chamber_id + "\'");

                        while (resultSet1.next()) {
                            String entity_type = "SPLITTER:";
                            String chamber_lat = resultSet1.getString("latitude");
                            String chamber_long = resultSet1.getString("longitude");

                            if (chamber_id != null && chamber_lat != null && chamber_long != null) {
                                String dataBase_latLong = entity_type + "," + chamber_system_id + "," + chamber_lat + "," + chamber_long;
                                System.out.println("dataBase_latLong from chamber: " + dataBase_latLong);
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                if (conn != null) {
                    resultSet = statement.executeQuery("select * from vw_att_details_splitter");
                    while (resultSet.next()) {
                        String chamber_system_id = resultSet.getString("splitter_id");
                        String chamber_id = resultSet.getString("parent_id");
                        Statement stmt = conn.createStatement();
                        ResultSet resultSet2 = stmt.executeQuery("select * from vw_att_details_pole where pole_id = \'" + chamber_id + "\'");
                        while (resultSet2.next()) {
                            String entity_type = "SPLITTER:";
                            String chamber_lat = resultSet2.getString("latitude");
                            String chamber_long = resultSet2.getString("longitude");

                            if (chamber_id != null && chamber_lat != null && chamber_long != null) {
                                String dataBase_latLong = entity_type + "," + chamber_system_id + "," + chamber_lat + "," + chamber_long;
                                System.out.println("dataBase_latLong from pole: " + dataBase_latLong);
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                //for adding Logical Link entity latLongs
                resultSet = statement.executeQuery("select * from vw_att_details_link");
                while (resultSet.next()) {
                    String entity_type = "LGLK:";
                    String lglink_id = resultSet.getString("link_id");
                    String lglink_lat = resultSet.getString("site_lat");
                    String lglink_long = resultSet.getString("site_lng");

                    if(lglink_id!=null && lglink_lat!=null && lglink_long!=null) {
                        String dataBase_latLong = entity_type + "," + lglink_id + "," + lglink_lat + "," + lglink_long;
                        database_siteList.add(dataBase_latLong);
                    }
                }
            } catch (SQLException sql) {
                sql.printStackTrace();
            }

            //List<String> latList = database_siteList;
            entity_list = new ArrayList<String>();

            for (int i = 0; i < database_siteList.size(); i++) {
                //for (String list : latList) {
                String list = database_siteList.get(i);
                String arr[] = list.split(",");

                if(arr.length==4) {
                    String type  = arr[0];
                    String key   = arr[1];
                    String lats  = arr[2];
                    String longs = arr[3];

                    Double db_lat = Double.parseDouble(lats);
                    Double db_longs = Double.parseDouble(longs);
                    LatLng latLong = new LatLng(db_lat, db_longs);

                    LatLng southwest = SphericalUtil.computeOffset(marker.getPosition(), radius * Math.sqrt(2.0), 225);
                    LatLng northeast = SphericalUtil.computeOffset(marker.getPosition(), radius * Math.sqrt(2.0), 45);
                    latLngBounds = new LatLngBounds(southwest, northeast);

                    if (latLngBounds.contains(latLong)) {
                        System.out.println("count: " + count + " key Id: " + key+" type: "+type);
                        String keyType = type + key;

                        if (!entity_list.contains(keyType)) {
                            System.out.println("LatLong bound contains keyType: "+keyType);
                            entity_list.add(keyType);
                        }

                        if(database_siteList.get(i).contains("CUST"))
                        {
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.customer);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("POP")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.pop);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("CHMBR")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.chamber);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("POLE"))
                        {
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.pole);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("SPCL")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.spliceclosure);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("ODF")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.odf);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("SPLITTER")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.splitter);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }



                        /*else if(database_siteList.get(i).contains("CABLE")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.cable);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }


                        else if(database_siteList.get(i).contains("CIRCUIT")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_circute);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }

                        else if(database_siteList.get(i).contains("LGLK")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.logicallink);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }

                        else if(database_siteList.get(i).contains("SITE")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.site);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else {
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_circute);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }*/
                    }
                }
            }
        }

        if (entity_list.size() == 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
            builder1.setTitle("No Nearby Network Entities");
            builder1.setMessage("Sorry No Network Entities are available around selected Location");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    //displays Alert Dailog for radius in google map
    public void getRadius() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                String given_radius = userInput.getText().toString();

                                if(given_radius.length()<1){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                                    builder1.setTitle("Invalid syntax");
                                    builder1.setMessage("Please enter radius between 1 & 50 meters Only");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }

                                else if(given_radius.contains(".")){

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                                    builder1.setTitle("Invalid syntax");
                                    builder1.setMessage("Please enter radius between 1 & 50 meters Only(without decimal value)");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                                else if(given_radius.matches("[A-Za-z !@#$%^&*()]+")){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                                    builder1.setTitle("Invalid syntax");
                                    builder1.setMessage("Please enter radius between 1 & 50 meters Only");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }
                                else if(!Character.isDigit(given_radius.charAt(0)) ||
                                        !Character.isDigit(given_radius.charAt(given_radius.length()-1)) || !containsOnlyNumbers(given_radius)){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                                    builder1.setTitle("Invalid syntax");
                                    builder1.setMessage("Please enter radius between 1 & 50 meters Only");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                                else {
                                    radius = Integer.parseInt(userInput.getText().toString());
                                    entity_list = new ArrayList<String>();
                                    if(radius <1 || radius >50 ){
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                                        builder1.setTitle("Invalid syntax");
                                        builder1.setMessage("Please enter radius between 1 & 50 meters Only");
                                        builder1.setCancelable(true);
                                        builder1.setNeutralButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                    }
                                    else
                                    {
                                        getEntityInfo();
                                    }
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

   /* public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }*/

    @NonNull
    public AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(GoogleMapsActivity.this, loginActivity);
        }
        return mDelegate;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final ProgressDialog loading = ProgressDialog.show(this, "Loading...", "Please wait...", false, false);

        mMap = googleMap;

        mMap.setPadding(0,0,0,100);

        LatLng country_map = new LatLng(2.4759444,112.8602515); //3.6607456,112.2450171  -- another latlong for indonesia
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(country_map, 4));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        loading.dismiss();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            String db_id = null;

            @Override
            public void onMapClick(LatLng point) {
                if (marker != null) {
                    marker.remove();
                    mMap.clear();
                }
                if (mMap != null) {
                    mMap.clear();
                }

                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                sele_lat = point.latitude;
                sele_long = point.longitude;

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));
                marker.setDraggable(true);
                if (!marker.isDraggable()) {
                    marker.setDraggable(true);
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String key = null;
                String coordinates=(marker.getPosition().latitude)+","+ (marker.getPosition().longitude); //marker.getSnippet();

                for(int i =0;i<database_siteList.size();i++) {
                    if (database_siteList.get(i).contains(coordinates)) {

                        String list = database_siteList.get(i);
                        String arr[] = list.split(",");

                        if(arr.length==4) {
                            String types = arr[0];
                            String keys = arr[1];
                            key=types+keys;
                        }
                    }
                }

                marker.showInfoWindow();
                if (key != null) {
                    Intent intent = new Intent(GoogleMapsActivity.this, Popup_Menu.class);
                    intent.putExtra("key", key);
                    intent.putExtra("role", user_role);
                    intent.putExtra("userName", user);
                    startActivity(intent);
                }
                return true;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        Your_location = location;
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.clear();
                if (ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return true;
                }
                mMap.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(Your_location.getLatitude(), Your_location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                marker    = mCurrLocationMarker;
                marker.setDraggable(true);
                sele_lat  = Your_location.getLatitude();
                sele_long = Your_location.getLongitude();

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                return true;
            }
        });

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            //to read internal file Read_Externa_stoorage Permission
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(GoogleMapsActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            GoogleMapsActivity.this.finish();
        }
    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu,inflater);
        getMenuInflater().inflate(R.menu.google_maps, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();//findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchLocation(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GoogleMapsActivity.this);
            alertDialog.setTitle("Confirm Logout");
            alertDialog.setMessage("Are you sure you want Logout?");
            alertDialog.setIcon(R.drawable.fail);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    session.logoutUser();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("logout","yes");
                    startActivity(i);
                    finish();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
}
