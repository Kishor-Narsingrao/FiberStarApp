package sg.com.aitek.fiberstarapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button btn_login, btn_switch_user;
    private Context context;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences1;
    public static final String preference = "FiberStarPreferences";
    public static final String preference1 = "FiberStarPreferences1";
    public static String Name;
    public static String PWord;

    String logout = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        username = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_switch_user = (Button) findViewById(R.id.btn_switch_user);

        sharedpreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
        sharedpreferences1 = getSharedPreferences(preference1, Context.MODE_PRIVATE);

        Name = sharedpreferences.getString(Name, "");
        //System.out.println("username from pref: "+Name + " \n "+sharedpreferences1.getString(Name, ""));

        PWord = sharedpreferences1.getString(PWord, "");
        //System.out.println("password from pref: "+ PWord + " \n"+sharedpreferences1.getString(PWord, ""));

        if (sharedpreferences.contains(Name)) {
            username.setText(sharedpreferences.getString(Name, ""));
        }

        if (sharedpreferences1.contains(PWord)) {
            password.setText(sharedpreferences1.getString(PWord, ""));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            logout = bundle.getString("logout");
        }
        if (logout != null && logout.equalsIgnoreCase("yes")) {
            password.setText("");
            if (sharedpreferences.contains(Name)) {
                username.setText(sharedpreferences.getString(Name, ""));
            }
            SharedPreferences.Editor editor2 = sharedpreferences1.edit();
            editor2.remove(PWord);
            editor2.apply();
            editor2.clear();
            editor2.commit();
            username.setText(Name);
        }

        if (username.length() > 0 && password.length() > 0) {
            login();
        }

        //reading properties file
        DbConncetion dbConncetion = new DbConncetion(context);
        Properties properties = dbConncetion.getProperties("FiberStar.properties");
        final String POST_URL = properties.getProperty("ldap.url.USERVALUES");

        btn_switch_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.length() > 0 || (!username.toString().isEmpty())) {
                    username.setText("");
                    username.requestFocus();
                    username.setFocusable(true);
                    username.setEnabled(true);
                } else {
                    username.setFocusable(true);
                    username.requestFocus();
                    username.setFocusable(true);
                    username.setEnabled(true);
                }
                if (password.length() > 0 || (!password.toString().isEmpty()) || username.length() < 1) {
                    password.setText("");
                    username.setFocusable(true);
                    username.requestFocus();
                    username.setFocusable(true);
                    username.setEnabled(true);
                } else {
                    username.setFocusable(true);
                    username.requestFocus();
                    username.setFocusable(true);
                    username.setEnabled(true);
                }
                if (username.length() > 0 && password.length() > 0) {
                    username.setText("");
                    password.setText("");
                    username.setFocusable(true);
                    username.setEnabled(true);
                    username.requestFocus();
                }
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (username.length() > 0) {
                    if (password.length() > 0) {
                        String userName = username.getText().toString();
                        System.out.println("username: " + userName);
                        String passWord = password.getText().toString();
                        System.out.println("password: " + passWord);

                        //Toast.makeText(getApplicationContext(), userName + " " + passWord, Toast.LENGTH_SHORT).show();

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        final String USER_AGENT = "Mozilla/5.0";
                        final String POST_PARAMS = "uname=" + userName + "&pw=" + passWord;
                        URL obj = null;
                        HttpURLConnection con = null;
                        try {
                            System.out.println("POST URL: " + POST_URL);
                            obj = new URL(POST_URL);
                            con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("User-Agent", USER_AGENT);
                            // For POST only - START
                            con.setDoOutput(true);
                            OutputStream os = con.getOutputStream();
                            os.write(POST_PARAMS.getBytes());
                            os.flush();
                            os.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        int responseCode = 0;
                        try {
                            responseCode = con.getResponseCode();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            //success
                            BufferedReader in = null;
                            try {
                                in = new BufferedReader(new InputStreamReader(
                                        con.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String inputLine;
                            StringBuffer response = new StringBuffer();
                            try {
                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // print result
                            String ldap_response = response.toString();
                            if (response.toString().equalsIgnoreCase("Connection with ldap server is failure") || ldap_response.equalsIgnoreCase("Connection with ldap server is failure")
                                    || ldap_response.length() > 30) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setTitle("Login Failed");
                                builder1.setMessage("LDAP/AD not authenticated  or failed");
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
                                //Toast.makeText(getApplicationContext(),"Connection With LDAP Server is Successful",Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                SharedPreferences.Editor editor1 = sharedpreferences1.edit();
                                editor.putString(Name, userName);
                                editor1.putString(PWord, passWord);
                                editor.apply();
                                editor.commit();

                                editor1.apply();

                                editor1.commit();

                                Intent intent = new Intent(LoginActivity.this, GoogleMapsActivity.class);
                                intent.putExtra("user", userName);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setTitle("Login Failed");
                            builder1.setMessage("LDAP/AD not authenticated  or failed");
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
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setTitle("Password");
                        builder1.setMessage("Please enter Password");
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
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setTitle("User Name");
                    builder1.setMessage("Please enter User Name");
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
        });
    }

    public void login() {
        if (username.length() > 0) {
            if (password.length() > 0) {
                //Toast.makeText(getApplicationContext(),"username and password are not null.. from login function",Toast.LENGTH_LONG).show();
                DbConncetion dbConncetion = new DbConncetion(context);
                Properties properties = dbConncetion.getProperties("FiberStar.properties");
                final String POST_URL = properties.getProperty("ldap.url.USERVALUES");

                String userName = username.getText().toString();
                //System.out.println("username: " + userName);
                String passWord = password.getText().toString();
                //System.out.println("password: " + passWord);
                //Toast.makeText(getApplicationContext(), userName + " " + passWord, Toast.LENGTH_SHORT).show();

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                final String USER_AGENT = "Mozilla/5.0";
                final String POST_PARAMS = "uname=" + userName + "&pw=" + passWord;
                URL obj = null;
                HttpURLConnection con = null;
                try {
                    obj = new URL(POST_URL);
                    con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    // For POST only - START
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    os.write(POST_PARAMS.getBytes());
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int responseCode = 0;
                try {
                    responseCode = con.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // System.out.println("POST Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //success
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(
                                con.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // print result
                    String ldap_response = response.toString();
                    if (response.toString().equalsIgnoreCase("Connection with ldap server is failure") || ldap_response.equalsIgnoreCase("Connection with ldap server is failure")
                            || ldap_response.length() > 30) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setTitle("Login Failed");
                        builder1.setMessage("LDAP/AD not authenticated  or failed");
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
                        //Toast.makeText(getApplicationContext(),"Connection With LDAP Server is Successful",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        SharedPreferences.Editor editor1 = sharedpreferences1.edit();
                        editor.putString(Name, userName);
                        editor1.putString(PWord, passWord);
                        editor.apply();
                        editor.commit();

                        editor1.apply();
                        editor1.commit();

                        //Toast.makeText(getApplicationContext(),"username and password are not null.. from login function. moved to maps activity",Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(LoginActivity.this, GoogleMapsActivity.class);
                        intent1.putExtra("user", userName);
                        startActivity(intent1);
                    }
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setTitle("Login Failed");
                    builder1.setMessage("LDAP/AD not authenticated  or failed");
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
        }
    }
}
