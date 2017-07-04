package sg.com.aitek.fiberstarapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Venkat on 7/27/2016.
 */

public class Tab_Photo extends Activity {


    ImageView image_view8;
    Button select,upload;
    GridView gridView;
    TextView image_info;
    Bitmap bitmap;
    ProgressDialog prgDialog;

    ArrayList<Bitmap> bitmap_array ;
    String imgDecodableString;
    String serverPath;
    String key,user_role;
    String filePath;
    String encodedString;
    String fileName;

    //database connection variables
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    DbConncetion dbConncetion;

    ImageAdapter adapter;

    ArrayList<String> images_list;

    RequestParams params = new RequestParams();

    private static int RESULT_LOAD_IMG = 1;
    private String time_fileName,org_file;
    private String system_id;
    private Context context;
    private String upload_url;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_photo);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        context     = this;
        dbConncetion = new DbConncetion(context);
        Properties properties     = dbConncetion.getProperties("FiberStar.properties");
        upload_url                = properties.getProperty("upload.url");
        serverPath                = properties.getProperty("serverPath");

        gridView    = (GridView) findViewById(R.id.gridview);
        //upload      = (Button) findViewById(R.id.upload);
        select      = (Button) findViewById(R.id.select);
        image_view8 = (ImageView) findViewById(R.id.image_view8);
        image_info  = (TextView) findViewById(R.id.images);


        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        init();
    }

    public  void initArraylist()
    {
        bitmap_array = new ArrayList<>();
        images_list = new ArrayList<>();
    }

    public void init() {

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                key = bundle.getString("key");
                user_role = bundle.getString("role");
                //System.out.println("key id after: "+key+user_role);
            }

            if(connection==null){
                connection   = dbConncetion.getConnection();
                statement    = connection.createStatement();
            }
            else{
                connection   = dbConncetion.getConnection();
                statement    = connection.createStatement();
            }
            //System.out.println("key id before: "+key);
            String key_type = null;
            if(key!=null){

                String replace= key.substring(0,key.indexOf(':')+1);
                key_type      = key.substring(0,key.indexOf(':'));
                key           = key.replaceFirst(replace,"");
            }
            //System.out.println("key id after: "+key);
            String query = "select * from vw_att_details_site where site_id=\'" + key + "\'";
            if(key_type!=null && key_type.equalsIgnoreCase("site")) {
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("cust")) {
                query = "select * from vw_att_details_customer where customer_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("pop")) {
                query = "select * from vw_att_details_pop where pop_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("CHMBR")) {
                query = "select * from vw_att_details_chamber where chamber_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("pole")) {
                query = "select * from vw_att_details_pole where pole_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("SPCL")) {
                query = "select * from vw_att_details_spliceclosure where spliceclosure_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("CABLE")) {
                query = "select * from vw_att_details_cable where cable_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("CIRCUIT")) {
                query = "select * from vw_att_details_cable where circuit_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }


            else if(key_type!=null && key_type.equalsIgnoreCase("odf")) {
                query = "select * from vw_att_details_odf where odf_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("splitter")) {
                query = "select * from vw_att_details_splitter where splitter_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type!=null && key_type.equalsIgnoreCase("link")) {
                query = "select * from vw_att_details_link where link_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else{
                Toast.makeText(getApplicationContext(),"For selected Entity, we did't find a Element Id",Toast.LENGTH_LONG).show();
            }

            initArraylist();
            getImages();

            //TODO: remove coment afer testing
//            if(user_role.equalsIgnoreCase("Administrator")|| user_role.equalsIgnoreCase("Modifier")){
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadImagefromGallery();
                }
            });
            /*}
            else{
                select.setVisibility(View.GONE);
            }*/

        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getImages() {


        String image = null;
        try {
            if (connection == null) {
                connection = dbConncetion.getConnection();
                statement = connection.createStatement();
            } else {
                statement = connection.createStatement();
            }
            String query = "select * from library_image where element_id=\'" + system_id + "\'";
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                image = resultSet.getString("image");
                System.out.println("image from db: " + image);
                images_list.add(image);
                String image1 = serverPath + image;
                URL url = new URL(image1);
                Bitmap bmp1 = null;
                try {
                    bmp1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (FileNotFoundException fne) {
                    fne.printStackTrace();
                }
                bitmap_array.add(bmp1);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(bitmap_array.size()>0) {
            image_info.setVisibility(View.GONE);
            ScrollView image_scScrollView = (ScrollView) findViewById(R.id.imageScroll);
            image_scScrollView.setVisibility(View.VISIBLE);


            adapter =new ImageAdapter(Tab_Photo.this,images_list);

            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent,
                                        View v, int position, long id)
                {
                    //Toast.makeText(getBaseContext(),"pic" + (position + 1) + " selected",Toast.LENGTH_SHORT).show();
                    image_view8.setImageBitmap(bitmap_array.get(position));
                }
            });
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getApplicationContext(),"item Deleted at "+position+" Position",Toast.LENGTH_SHORT).show();

                    final int deletePosition=position;

                    android.support.v7.app.AlertDialog myQuittingDialogBox = new android.support.v7.app.AlertDialog.Builder(context)
                            //set message, title, and icon
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")

                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //your deleting code
                                    try {
                                        connection   = dbConncetion.getConnection();
                                        statement    = connection.createStatement();

                                        String DeleteQuery = "DELETE FROM library_image where image=\'" + images_list.get(deletePosition) + "\'";
                                        PreparedStatement pstmt = connection.prepareStatement(DeleteQuery);
                                        int i = pstmt.executeUpdate();

                                        //Refreshing gridview
                                        initArraylist();
                                        getImages();
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            })

                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                    return false;
                }
            });
        }
        else{
            //Toast.makeText(Tab_Photo.this, "No Images Available For This ID: "+key, Toast.LENGTH_SHORT).show();
            image_info.setVisibility(View.VISIBLE);
            ScrollView image_scScrollView = (ScrollView) findViewById(R.id.imageScroll);
            image_scScrollView.setVisibility(View.GONE);
            //select.setPadding(50,250,50,20);
            //upload.setPadding(50,250,50,20);
        }
    }

    public void loadImagefromGallery()  {
        // Create intent to Open Image applications like Gallery, Google Photos

        //ImagePicker working fine previously.
        Intent intent = new Intent(this, ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 10);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        startActivityForResult(intent, RESULT_LOAD_IMG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
//                Toast.makeText(getApplicationContext(),"total images: "+images.size(),Toast.LENGTH_LONG).show();

                if(images.size()>0) {

                    for (int i = 0; i < images.size(); i++) {
                        //System.out.println("image path:" + i + " " + images.get(i).getPath());
                        //System.out.println("image Name:" + i + " " + images.get(i).getName());

                        imgDecodableString = images.get(i).getPath();

                        image_view8.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                        String fileNameSegments[] = imgDecodableString.split("/");
                        fileName = fileNameSegments[fileNameSegments.length - 1];
                        System.out.println("fileName " + fileName);
                        String filenameArray[] = fileName.split("\\.");
                        String file = filenameArray[0];
                        String extension = filenameArray[1];

                        // Put file name in Async Http Post Param which will used in Java web app
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss");
                        String timestamp = simpleDateFormat.format(new Date());
                        org_file = file + "." + extension;

                        File selectedFile = new File(org_file);

                        if (selectedFile.exists()) {
                            double bytes = selectedFile.length();
                        }
                        File selectedFile1 = new File(fileName);
                        if (selectedFile1.exists()) {
                            double bytes = selectedFile1.length();
                        }
                        time_fileName = file + "_" + timestamp + "." + extension;
                        System.out.println("file name with timestamp: " + i + " " + time_fileName);
                        params.put("filename", time_fileName);

                        File f = new File(images.get(i).getPath());
                        filePath = f.getPath();

                        //DbConncetion dbConncetion = new DbConncetion();
                        connection = dbConncetion.getConnection();
                        statement = connection.createStatement();
                        if (connection != null) {
                            String query_results = "select * from library_image where element_id =\'" + key + "\'";
                            ResultSet resultSet_images = statement.executeQuery(query_results);
                            int result = 0;
                            while (resultSet_images.next()) {
                                result++;
                                //System.out.println("result for getting images: " + result);
                            }
                            String query = "insert into library_image(element_id,image,org_file_name) values(" + "?,?,?)";
                            PreparedStatement st = connection.prepareStatement(query);
                            //System.out.println("system_id to insert into image: " + system_id);

                            st.setString(1, system_id);
                            st.setString(2, time_fileName);
                            st.setString(3, org_file);
                            int count = st.executeUpdate();
                            if (count > 0) {


                                uploadImage();
                                initArraylist();
                                getImages();

                                prgDialog.hide();

                            } else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(Tab_Photo.this);
                                builder1.setTitle("Image Selection");
                                builder1.setMessage("Image selection failed");
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
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Tab_Photo.this);
                    builder1.setTitle("Image Selection");
                    builder1.setMessage("Image selection failed");
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
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void uploadImage() {
        // When Image is selected from Gallery
        if (imgDecodableString != null && !imgDecodableString.isEmpty()) {

            System.out.println("imgDecodableString: "+imgDecodableString);
            // Convert image to String using Base64
            encodeImagetoString();
        }
    }

//    protected String stringEncoding() {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 3;
//        bitmap = BitmapFactory.decodeFile(imgDecodableString,
//                options);
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        // Must compress the Image to reduce image size to make upload easy
//        //bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
//       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byte_arr = stream.toByteArray();
//        // Encode Image to String
//        encodedString = Base64.encodeToString(byte_arr, 0);
//        prgDialog.setMessage("Uploading");
//        // Put converted Image string into Async Http Post param
//        //System.out.println("------------------------------------------------------------------------");
//        //System.out.println("encodedString:"+encodedString);
//        //System.out.println("------------------------------------------------------------------------");
//        params.put("image", encodedString);
//
//        return "";
//    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
       /* new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgDecodableString,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.setMessage("Uploading");
                // Put converted Image string into Async Http Post param
                // //System.out.println("encodedString: "+encodedString);

                if(encodedString!=null)
                    params.put("image", encodedString);
                else{*/
//       prgDialog=new ProgressDialog(this);
//        prgDialog.setMessage("Please wait... Converting Images");
//       prgDialog.show();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    bitmap = BitmapFactory.decodeFile(imgDecodableString,
                            options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Must compress the Image to reduce image size to make upload easy
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byte_arr = stream.toByteArray();
                    // Encode Image to String
                    encodedString = Base64.encodeToString(byte_arr, 0);
                    params.put("image", encodedString);
//                }

                // Trigger Image upload
                triggerImageUpload();

//        prgDialog.hide();
           /* }
        }.execute(null, null, null);*/
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // http://192.168.2.4:9000/imgupload/upload_image.php
    // http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
    // Make Http call to upload Image to Java server
    public void makeHTTPCall() {

        prgDialog.setMessage("please wait ...");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        //client.post("http://192.168.1.41:9090/ImageUploadWebApp/uploadimg.jsp",
        // client.post("http://172.16.28.39:8080/ImageUploadWebApp/",
        client.post(upload_url,
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'

                    //@Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
//                        prgDialog.dismiss();

                        Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
//                        prgDialog.dismiss();

                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

    //Listview adapter to display images
    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> filePaths = new ArrayList<>();

        public ImageAdapter(Context c,ArrayList filepath)
        {
            context = c;
            //String serverPath = "http://172.16.28.39/image/";
            for(int i=0;i<filepath.size();i++){
                String path = serverPath + filepath.get(i);
                filePaths.add(path);
            }
        }
        //---returns the number of images---
        public int getCount() {
            return filePaths.size();
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            // System.out.println("position from getItem: "+position);
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
                URL url;
                try{
                    url = new URL(filePaths.get(position));
                    Bitmap bmp1 =  BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    imageView.setImageBitmap(bmp1);
                }
                catch(IOException ioe){
                    ioe.printStackTrace();
                }


            }
            else {
                imageView = (ImageView) convertView;
            }
            return imageView;
        }
    }
}
