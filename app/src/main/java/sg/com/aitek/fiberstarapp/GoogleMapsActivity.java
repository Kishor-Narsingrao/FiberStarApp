package sg.com.aitek.fiberstarapp;

import android.app.ProgressDialog;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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


public class GoogleMapsActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //AppCompatActivity
    Location Your_location;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LatLngBounds latLngBounds;
    EditText userInput;
    Button btnOpenPopup;

    List<String> database_siteList;
    Connection conn;
    Statement statement;

    String user, user_role;
    boolean status = true;
    private Context context;

    int count;
    int radius;
    List<String> entity_list = new ArrayList<String>();
    Marker marker;

    Double sele_lat;
    Double sele_long;

    private AppCompatDelegate mDelegate;

    SessionManager session;
    LoginActivity loginActivity;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        context = this;
        loginActivity = new LoginActivity();
        session = new SessionManager(getApplicationContext());


        if (ContextCompat.checkSelfPermission(GoogleMapsActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapsActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //status = LoginActivity.isConnected();
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");

        btnOpenPopup = (Button) findViewById(R.id.openpopup);

        session.checkLogin();
        session.createLoginSession(user);

        try {
            DbConncetion dbConncetion = new DbConncetion(context);
            conn = dbConncetion.getConnection();
            //String user_role_query = "SELECT MODULE_ROLE from user_modules WHERE USER_ID = (SELECT USER_ID FROM USER_MASTER WHERE USER_NAME = \'" + user + "\')";
            if (conn != null) {
                //Toast.makeText(getApplicationContext(), "connected successfully", Toast.LENGTH_SHORT).show();
                statement = conn.createStatement();
                String user_role_query = "SELECT MODULE_ROLE from user_modules WHERE USER_ID = (SELECT USER_ID FROM USER_MASTER WHERE USER_NAME = '" + user + "')";
                ResultSet resultSet1 = statement.executeQuery(user_role_query);
                if(resultSet1.next()) {
                    user_role = resultSet1.getString("MODULE_ROLE");
                }
                //Toast.makeText(getApplicationContext(), "user role is: " + user_role, Toast.LENGTH_SHORT).show();
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        Circle mapCircle=null;

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
    public void SearchLocation(View v) {
        EditText etSearch=(EditText) findViewById(R.id.etSearch);
        String Location=etSearch.getText().toString();
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

                Bitmap bitmap = getBitmap(getApplicationContext(),R.drawable.ic_circute);

                //Clearing the marker from map
                mMap.clear();
                //adding new marker to the map
//                marker=mMap.addMarker(new MarkerOptions().position(latlng).draggable(true));
//                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));

                marker=mMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));
                //changing camera view to the selected area of marker in map
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

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
        //Toast.makeText(GoogleMapsActivity.this, "Radius: " + radius, Toast.LENGTH_SHORT).show();
        database_siteList = new ArrayList<>();
        DbConncetion dbConncetion = new DbConncetion(context);
        conn = dbConncetion.getConnection();
        if (conn != null) {
            //Toast.makeText(getApplicationContext(), "connected successfully", Toast.LENGTH_SHORT).show();
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
                        //System.out.println("From Pole-- pole_lat: " + pole_lat + " pole_long: " + pole_long);
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
                                //System.out.println("dataBase_latLong from chamber: " + dataBase_latLong);
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
                                //System.out.println("dataBase_latLong from pole: " + dataBase_latLong);
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

                //resultSet = statement.executeQuery("SELECT ST_AsText(sp_geometry) as venkat  FROM vw_att_details_cable;");
                resultSet = statement.executeQuery("SELECT *,ST_AsText(sp_geometry) as venkat  FROM vw_att_details_cable");
                while (resultSet.next()) {
                    String entity_type = "CABLE:";
                    String latLongs = resultSet.getString("venkat");
                    String cable_id = resultSet.getString("cable_id");
                    String cable_lat = null;
                    String cable_long= null;

                    //Toast.makeText(getApplicationContext(),"Results from cable: "+latLongs,Toast.LENGTH_LONG).show();
                    //System.out.println("Results from cable with near NetworkIds: "+latLongs);

                    if(latLongs.contains("LINESTRING") || latLongs.contains("(") ||  latLongs.contains(")"))
                    {
                        String onlyLatLongs =  latLongs.replaceAll("[^0-9,. -]", "");
                        String array_of_latLongs[] = onlyLatLongs.split(",");
                        for(int i=0;i<array_of_latLongs.length;i++){
                            //Toast.makeText(getApplicationContext(),"latlongs from db: "+i+ array_of_latLongs[i],Toast.LENGTH_LONG).show();
                            //System.out.println("latlongs from db: "+i+": "+ array_of_latLongs[i]);
                            String longLat[] = array_of_latLongs[i].split(" ");
                            cable_lat = longLat[1];
                            cable_long = longLat[0];

                            if(cable_id!=null && cable_lat!=null && cable_long!=null) {
                                String dataBase_latLong = entity_type + "," + cable_id + "," + cable_lat + "," + cable_long;
                                //System.out.println("latlongs from db: "+dataBase_latLong);
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

                //resultSet = statement.executeQuery("SELECT *,ST_AsText(sp_geometry) as venkat  FROM vw_att_details_circuit");
                resultSet = statement.executeQuery("SELECT *,ST_AsText(sp_geometry) as venkat  FROM vw_att_details_circuit");
                while (resultSet.next()) {
                    String entity_type = "CIRCUIT:";
                    String latLongs = resultSet.getString("venkat");
                    String circuit_id = resultSet.getString("circuit_id");
                    String cable_lat = null;
                    String cable_long= null;

                    //Toast.makeText(getApplicationContext(),"Results from cable: "+latLongs,Toast.LENGTH_LONG).show();
                    //System.out.println("Results from cable with near NetworkIds: "+latLongs);

                    if(latLongs.contains("LINESTRING") || latLongs.contains("(") ||  latLongs.contains(")"))
                    {
                        String onlyLatLongs =  latLongs.replaceAll("[^0-9,. -]", "");
                        String array_of_latLongs[] = onlyLatLongs.split(",");
                        for(int i=0;i<array_of_latLongs.length;i++){
                            //Toast.makeText(getApplicationContext(),"latlongs from db: "+i+ array_of_latLongs[i],Toast.LENGTH_LONG).show();
                            //System.out.println("latlongs from db: "+i+": "+ array_of_latLongs[i]);
                            String longLat[] = array_of_latLongs[i].split(" ");
                            cable_lat = longLat[1];
                            cable_long = longLat[0];

                            if(circuit_id!=null && cable_lat!=null && cable_long!=null) {
                                String dataBase_latLong = entity_type + "," + circuit_id + "," + cable_lat + "," + cable_long;
                                //System.out.println("latlongs from db: "+dataBase_latLong);
                                database_siteList.add(dataBase_latLong);
                            }
                        }
                    }
                }

//                                                            //for adding splitter entity latLongs
//                                                            resultSet = statement.executeQuery("select * from vw_att_details_splitter");
//                                                            while (resultSet.next()) {
//                                                                String entity_type = "SPLITTER:";
//                                                                String splitter_id = resultSet.getString("splitter_id");
//                                                                String splitter_lat = resultSet.getString("latitude");
//                                                                String splitter_long = resultSet.getString("longitude");
//
//                                                                if(splitter_id!=null && splitter_lat!=null && splitter_long!=null) {
//                                                                    String dataBase_latLong = entity_type + "," + splitter_id + "," + splitter_lat + "," + splitter_long;
//                                                                    database_siteList.add(dataBase_latLong);
//                                                                }
//                                                            }

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
                                                                /*System.out.println("arr[0] FROM BUTTON: " + arr[0]);
                                                                System.out.println("arr[1] FROM BUTTON: " + arr[1]);
                                                                System.out.println("arr[2] FROM BUTTON: " + arr[2]);
                                                                System.out.println("arr[3] FROM BUTTON: " + arr[3]);*/

                    String type  = arr[0];
                    String key   = arr[1];
                    String lats  = arr[2];
                    String longs = arr[3];
                                                            /*
                                                            if(arr[0]!=null && !arr[0].isEmpty() && arr[0].length()>0 && arr[1]!=null && !arr[1].isEmpty() && arr[1].length()>0
                                                                        && arr[2]!=null && !arr[2].isEmpty() && arr[2].length()>0
                                                                        && arr[3]!=null && !arr[3].isEmpty() && arr[3].length()>0 ) {
                                                                    System.out.println("list size: " + arr.length);
                                                                    String type = arr[0];
                                                                    System.out.println("arr[0] FROM BUTTON: " + arr[0]);
                                                                    String key = arr[1];
                                                                    System.out.println("arr[1] FROM BUTTON: " + arr[1]);
                                                                    String lats = arr[2];
                                                                    System.out.println("arr[2] FROM BUTTON: " + arr[2]);
                                                                    String longs = arr[3];
                                                                    System.out.println("arr[3] FROM BUTTON: " + arr[3]);
                                                                    */

                    Double db_lat = Double.parseDouble(lats);
                    Double db_longs = Double.parseDouble(longs);
                    LatLng latLong = new LatLng(db_lat, db_longs);

//                    addCircleToMap(radius,db_lat,db_longs);

                    LatLng southwest = SphericalUtil.computeOffset(marker.getPosition(), radius * Math.sqrt(2.0), 225);
                    LatLng northeast = SphericalUtil.computeOffset(marker.getPosition(), radius * Math.sqrt(2.0), 45);
                    latLngBounds = new LatLngBounds(southwest, northeast);

                    //System.out.println("latLongs from db for cable: "+db_lat + " "+db_longs+" "+latLong);
                    //System.out.println("latLongs from db for cable: ");
                    if (latLngBounds.contains(latLong)) {
                        // System.out.println("latlong bounds contained latlong is: " + latLong);
                        //count++;
                        System.out.println("count: " + count + " key Id: " + key+" type: "+type);
                        String keyType = type + key;
                        if (!entity_list.contains(keyType)) {
                            System.out.println("LatLong bound contains keyType: "+keyType);
                            entity_list.add(keyType);
                        }

                        if(database_siteList.get(i).contains("POLE"))
                        {
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_pole);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("CABLE")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_cable);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("SPCL")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_splice_closer);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("ODF")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_odf);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("CIRCUIT")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_circute);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("SPLITTER")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_splitter);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else if(database_siteList.get(i).contains("LGLK")){
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_logical_link);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                        else {
                            Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_circute);
                            marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLong).draggable(true));
                        }
                    }

                    // }
                }
            }
        }

        for(String list:entity_list){
            System.out.println("list : " + list);
        }

        if (entity_list.size() > 0) {

            /*Intent network_id = new Intent(GoogleMapsActivity.this, Network_id_selection.class);
            network_id.putExtra("user", user);
            network_id.putExtra("user_role", user_role);
            network_id.putExtra("radius", radius);
            network_id.putExtra("selected_lat", sele_lat);
            network_id.putExtra("selected_long", sele_long);
            network_id.putStringArrayListExtra("entity_list", (ArrayList<String>) entity_list);
            startActivity(network_id);*/
/*
            for(String list:entity_list) {
                LatLng latlng = new LatLng(sele_lat, sele_long);

                Bitmap bitmap = getBitmap(getApplicationContext(), R.drawable.ic_circuit);

//                mMap.clear();
                //adding new marker to the map
                marker = mMap.addMarker(new MarkerOptions().position(latlng).snippet(list).draggable(true));
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }*/
        } else {
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
                                // get user input and set it to result
                                // edit text
                                //result.setText(userInput.getText());
                                //Toast.makeText(GoogleMapsActivity.this, "userinput: "+userInput.getText(), Toast.LENGTH_SHORT).show();
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

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

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

        mMap.setPadding(0,100,0,0);

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

              /*
               removed 17/11/2016

               if (conn != null) {
                    try {
                        String query_keyId = "select * from vw_att_details_site " +
                                "where site_latitude=\'" + sele_lat + "\' and site_longitude=\'" + sele_long + "\'";
                        System.out.println("query for site id: " + query_keyId);

                        ResultSet resultSet = statement.executeQuery(query_keyId);
                        if (resultSet.next()) {
                            db_id = resultSet.getString("site_id");
                        }
                        resultSet = statement.executeQuery("select * from vw_att_details_customer where customer_lat=\'" + sele_lat +
                                "\' and customer_long=\'" + sele_long + "\'");
                        if (resultSet.next()) {
                            db_id = resultSet.getString("system_id");
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }


                */

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));
                marker.setDraggable(true);
                if (!marker.isDraggable()) {
                    marker.setDraggable(true);
                }


                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, mMap.getMinZoomLevel()));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

//                addCircleToMap(Integer.parseInt(userInput.getText().toString()),sele_lat,sele_long);

               /* Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(point.latitude, point.longitude))
                        .radius(10)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.TRANSPARENT).strokeWidth(5));*/

               /*

               removed 17/11/2016


                LatLng southwest = SphericalUtil.computeOffset(point, radius * Math.sqrt(2.0), 225);
                LatLng northeast = SphericalUtil.computeOffset(point, radius * Math.sqrt(2.0), 45);
                latLngBounds = new LatLngBounds(southwest, northeast);

                List<String> latList = database_siteList;
                for (int i = 0; i < database_siteList.size(); i++) {
                    //for (String list : latList) {
                    String list = database_siteList.get(i);

                    String arr[] = list.split(",");

                    if(arr.length==4) {

                        if(arr[0]!=null && !arr[0].isEmpty() && arr[0].length()>0 && arr[1]!=null && !arr[1].isEmpty() && arr[1].length()>0
                                && arr[2]!=null && !arr[2].isEmpty() && arr[2].length()>0 && arr[3]!=null && !arr[3].isEmpty() && arr[3].length()>0 ) {

                            System.out.println("list size: " + arr.length);
                            String type = arr[0];
                            System.out.println("arr[0]: " + arr[0]);
                            String key = arr[1];
                            System.out.println("arr[1]: " + arr[1]);
                            String lats = arr[2];
                            System.out.println("arr[2]: " + arr[2]);
                            String longs = arr[3];
                            System.out.println("arr[3]: " + arr[3]);

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
                    }
                }
                */
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

//                Toast.makeText(getApplicationContext(), "maker is clicked", Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(getApplicationContext(), "Key for selected marker is: " + key, Toast.LENGTH_SHORT).show();
                    //System.out.println("key values from Popup_Menu activity: "+key +" \nUser_Role is: "+user_role+" \nUser Name:"+user);
                    Intent intent = new Intent(GoogleMapsActivity.this, Popup_Menu.class);
                    //Intent intent = new Intent(Google_MapsActivity.this, Network_id_selection.class);
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
                // Toast.makeText(getApplicationContext(),"System out onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude,Toast.LENGTH_SHORT).show();
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                //Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                //Toast.makeText(getApplicationContext(),"System out onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude,Toast.LENGTH_SHORT).show();
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                //Log.i("System out", "onMarkerDrag...");
                sele_lat = arg0.getPosition().latitude;
                sele_long= arg0.getPosition().longitude;
                // Toast.makeText(getApplicationContext(),"System out onMarkerDrag...",Toast.LENGTH_SHORT).show();
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

        //Place current location marker
       /* LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));*/
        //mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                //Toast.makeText(getApplicationContext(),"rey enable your location",Toast.LENGTH_LONG).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

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

            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.google_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } */else if (id == R.id.nav_logout) {
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
