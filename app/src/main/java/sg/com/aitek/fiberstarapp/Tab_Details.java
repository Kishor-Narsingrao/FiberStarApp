package sg.com.aitek.fiberstarapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Venkat on 7/27/2016.
 */

public class Tab_Details extends Activity {

    EditText activation_stage,construction_stage,keyId,site_latitude,site_longitude,site_name;

    Connection conn;
    Statement statement;
    ResultSet resultSet;
    String key, user_role;
    private Context context;
    TextView tvKeyId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_details);

        context = this;

        activation_stage   = (EditText) findViewById(R.id.act_stage);
        construction_stage = (EditText) findViewById(R.id.con_stage);
        keyId              = (EditText) findViewById(R.id.site_id);
        site_latitude      = (EditText) findViewById(R.id.site_lat);
        site_longitude     = (EditText) findViewById(R.id.site_long);
        site_name          = (EditText) findViewById(R.id.site_name);
        tvKeyId            = (TextView) findViewById(R.id.tvId);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key       = bundle.getString("key");
            user_role = bundle.getString("role");
        }

        if(key!=null) {
            String replace= key.substring(0,key.indexOf(':')+1);
            key          = key.replaceFirst(replace,"");

          /*  if(key.contains("CUST")||key.contains("cust"))
            {
                tvKeyId.setText("Customer Id");
            }
            else if(key.contains("POP")||key.contains("pop"))
            {
                tvKeyId.setText("PoP Id");
            }
            else if(key.contains("CHMBR")||key.contains("chmbr"))
            {
                tvKeyId.setText("Chamber Id");
            }
            else if(key.contains("POLE")||key.contains("pole"))
            {
                tvKeyId.setText("Pole Id");
            }
            else if(key.contains("SPC")||key.contains("spc"))
            {
                tvKeyId.setText("Splice Closure Id");
            }
            else if(key.contains("ODF")||key.contains("odf"))
            {
                tvKeyId.setText("ODF Id");
            }
            else if(key.contains("SPLT")||key.contains("splt"))
            {
                tvKeyId.setText("Splitter Id");
            }
            else if(key.contains("CABL")||key.contains("cabl"))
            {
                tvKeyId.setText("Cable Id");
            }
            else if(key.contains("LINK")||key.contains("link"))
            {
                tvKeyId.setText("Logical Link Id");
            }
            else if(key.contains("SITE")||key.contains("site"))
            {
                tvKeyId.setText("Site Id");
            }
            else if(key.contains("CIRCUIT")||key.contains("circuit"))
            {
                tvKeyId.setText("Circuit Id");
            }
            else{
                tvKeyId.setText("Id");
            }*/

            String dt_con_stage = null, dt_act_stage = null,dt_lati = null, dt_longi = null, dt_name = null;
          try {
                DbConncetion dbConncetion = new DbConncetion(context);
                conn = dbConncetion.getConnection();
                statement = conn.createStatement();
                if (conn != null) {
                    String query_keyId = "select * from vw_att_details_site where site_id=\'" + key + "\'";
                    resultSet = statement.executeQuery(query_keyId);


                    if (resultSet.next()) {
                        resultSet.first();
                        dt_act_stage = resultSet.getString("activation_stage");
                        dt_con_stage = resultSet.getString("construction_stage");
                        dt_lati      = resultSet.getString("site_latitude");
                        dt_longi     = resultSet.getString("site_longitude");
                        dt_name      = resultSet.getString("site_name");

                       System.out.println("dt_act_stage: "+dt_act_stage +" \ndt_construction_stage: "+dt_con_stage+" \ndb Latitude: " + dt_lati+" \ndb Longitude: " + dt_longi+" \ndt_name"+dt_name);
                    }


                    //Todo: remove comment after testing
//                    if(user_role.equalsIgnoreCase("Administrator") || user_role.equalsIgnoreCase("Modifier")){
                        activation_stage.setText(dt_act_stage);
                        construction_stage.setText(dt_con_stage);
                        keyId.setText(key);
                        site_latitude.setText(dt_lati);
                        site_longitude.setText(dt_longi);
                        site_name.setText(dt_name);

                        activation_stage.setFocusable(false);
                        activation_stage.setEnabled(false);
                        activation_stage.setCursorVisible(false);
                        activation_stage.setClickable(false);

                        construction_stage.setFocusable(false);
                        construction_stage.setEnabled(false);
                        construction_stage.setCursorVisible(false);
                        construction_stage.setClickable(false);

                        keyId.setFocusable(false);
                        keyId.setEnabled(false);
                        keyId.setCursorVisible(false);
                        keyId.setClickable(false);

                        site_latitude.setFocusable(false);
                        site_latitude.setEnabled(false);
                        site_latitude.setCursorVisible(false);
                        site_latitude.setClickable(false);

                        site_longitude.setFocusable(false);
                        site_longitude.setEnabled(false);
                        site_longitude.setCursorVisible(false);
                        site_longitude.setClickable(false);

                        site_name.setFocusable(false);
                        site_name.setEnabled(false);
                        site_name.setCursorVisible(false);
                        site_name.setClickable(false);
                    /*}
                else{

                    activation_stage.setText(dt_act_stage);
                    construction_stage.setText(dt_con_stage);
                    keyId.setText(key);
                    site_latitude.setText(dt_lati);
                    site_longitude.setText(dt_longi);
                    site_name.setText(dt_name);

                    activation_stage.setFocusable(false);
                    activation_stage.setEnabled(false);
                    activation_stage.setCursorVisible(false);
                    activation_stage.setClickable(false);

                    construction_stage.setFocusable(false);
                    construction_stage.setEnabled(false);
                    construction_stage.setCursorVisible(false);
                    construction_stage.setClickable(false);

                    keyId.setFocusable(false);
                    keyId.setEnabled(false);
                    keyId.setCursorVisible(false);
                    keyId.setClickable(false);

                    site_latitude.setFocusable(false);
                    site_latitude.setEnabled(false);
                    site_latitude.setCursorVisible(false);
                    site_latitude.setClickable(false);

                    site_longitude.setFocusable(false);
                    site_longitude.setEnabled(false);
                    site_longitude.setCursorVisible(false);
                    site_longitude.setClickable(false);

                    site_name.setFocusable(false);
                    site_name.setEnabled(false);
                    site_name.setCursorVisible(false);
                    site_name.setClickable(false);

                }*/
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
