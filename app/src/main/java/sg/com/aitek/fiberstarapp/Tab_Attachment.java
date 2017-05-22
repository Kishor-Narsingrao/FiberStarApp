package sg.com.aitek.fiberstarapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import static android.R.attr.path;
import static android.R.id.list;


/**
 * Created by Venkat on 7/27/2016.
 */
public class Tab_Attachment extends Activity {

    //final String serverPath = "http://172.16.28.39:80/Image/";
    //final String serverPath = "http://192.168.1.41:808/Image/";
    String serverPath;
    Button select,upload;
    TextView file_info;
    String imgDecodableString;

    //database connection variables
    Connection connection;
    Statement statement;

    ArrayList<String> images_list = new ArrayList<>();
    ArrayList<String> size_list = new ArrayList<>();

    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String fileName,key;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;
    private String time_fileName,org_file;
    private String system_id;
    private String user_Name;
    private String user_id;
    private String user_role;
    private Context context;
    private String upload_url;
    private ArrayList<String> AttachmentFilesArray;
    int attchmentFileArraySize=5;
    TableRow tbrow;
    ListView lvFileDetails;
    ArrayAdapter<String> adapter;
    TextView tvSelectedFileName;
    LinearLayout llSelectedFileNames;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_attachment);

        context = this;
        DbConncetion dbConncetion = new DbConncetion(context);
        Properties properties     = dbConncetion.getProperties("FiberStar.properties");
        upload_url                = properties.getProperty("upload.url");
        serverPath                = properties.getProperty("serverPath");




        select      = (Button) findViewById(R.id.fileSelect);
        file_info   = (TextView) findViewById(R.id.files);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        AttachmentFilesArray=new ArrayList<>();
        lvFileDetails=(ListView) findViewById(R.id.lvFileDetails);
        tvSelectedFileName=(TextView)findViewById(R.id.tvSelectedFileName);
        llSelectedFileNames=(LinearLayout)findViewById(R.id.llSelectedFiles);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            user_Name = bundle.getString("user_Name");
            user_role = bundle.getString("role");
        }
        try {
            connection   = dbConncetion.getConnection();
            statement    = connection.createStatement();

            String queryu = "select * from user_master where user_name=\'"+user_Name+"\'";
            ResultSet resultSetu = statement.executeQuery(queryu);

            while (resultSetu.next()){
                user_id = resultSetu.getString("user_id");
            }

            String key_type = null;

            if(key!=null){
                String replace= key.substring(0,key.indexOf(':')+1);
                key_type      = key.substring(0,key.indexOf(':'));
                key           = key.replaceFirst(replace,"");
            }
            System.out.println("key id after: "+key);
            /*String query = "select * from vw_att_details_site where site_id=\'"+key+"\'";
            System.out.println("query for Image storing: "+query);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                system_id = resultSet.getString("system_id");
            }*/

            String query = "select * from vw_att_details_site where site_id=\'" + key + "\'";
            if(key_type.equalsIgnoreCase("site")) {
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("cust")) {
                query = "select * from vw_att_details_customer where customer_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("pop")) {
                query = "select * from vw_att_details_pop where pop_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("CHMBR")) {
                query = "select * from vw_att_details_chamber where chamber_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("pole")) {
                query = "select * from vw_att_details_pole where pole_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("SPCL")) {
                query = "select * from vw_att_details_spliceclosure where spliceclosure_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("odf")) {
                query = "select * from vw_att_details_odf where odf_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }
            else if(key_type.equalsIgnoreCase("CABLE")) {
                query = "select * from vw_att_details_cable where cable_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }


            else if(key_type.equalsIgnoreCase("CIRCUIT")) {
                query = "select * from vw_att_details_cable where circuit_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("splitter")) {
                query = "select * from vw_att_details_splitter where splitter_id=\'" + key + "\'";
                ResultSet resultSet1 = statement.executeQuery(query);
                while (resultSet1.next()) {
                    system_id = resultSet1.getString("system_id");
                    //System.out.println("system_id before: "+system_id);
                }
            }

            else if(key_type.equalsIgnoreCase("link")) {
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
            getFileDetails();

        }
        catch (SQLException cls){
            cls.printStackTrace();
        }



        lvFileDetails.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {


                android.support.v7.app.AlertDialog myQuittingDialogBox = new android.support.v7.app.AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete")

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                try {
                                    String DeleteQuery = "DELETE FROM attachment_details where file_name=\'" + images_list.get(position) + "\'";
                                    PreparedStatement pstmt = connection.prepareStatement(DeleteQuery);
                                    int i = pstmt.executeUpdate();

//                                    getFileDetails();
                                    images_list.remove(position);
                                    adapter.notifyDataSetChanged();
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

//                images_list.remove(position);
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        //todo: remove comment after testing
//        if(user_role.equalsIgnoreCase("Administrator")|| user_role.equalsIgnoreCase("Modifier")){
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFilefromMobile();
                }
            });

//        init();
    }


    public void getFileDetails()
    {
        try {
            images_list=new ArrayList<>();
            String query = "select * from attachment_details where system_id=\'" + system_id + "\'";
            //System.out.println("query for Image storing: "+query);
            ResultSet resultSet = statement.executeQuery(query);
            String image = null;
            int file_size = 0;
            while (resultSet.next()) {
                image = resultSet.getString("file_name");
                file_size = resultSet.getInt("file_size");
                images_list.add(image);
                int kb = (file_size / 1024);
                size_list.add(String.valueOf(kb));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, images_list);
        lvFileDetails.setAdapter(adapter);
    }

//Display the attached file in table view
    public void init() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);

        /*if(images_list.size()>0) {
            TextView tv1 = new TextView(this);
            tv1.setText(" Sl No. ");
            tv1.setTextColor(Color.RED);
            //tv1.setWidth(150);
            tv1.setTextSize(15);
            tbrow0.addView(tv1);

            TextView tv2 = new TextView(this);
            tv2.setText(" File Name ");
            tv2.setTextColor(Color.RED);
            tv2.setTextSize(15);
            tv2.setWidth(500);
            tbrow0.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText(" Size in Kb ");
            tv3.setTextColor(Color.RED);
            tv3.setTextSize(15);
            //tv3.setWidth(200);
            tbrow0.addView(tv3);

            tableLayout.addView(tbrow0);
        }
        else{
            file_info.setVisibility(View.VISIBLE);
//            ScrollView file_scScrollView = (ScrollView) findViewById(R.id.fileScroll);
//            file_scScrollView.setVisibility(View.GONE);
//            LinearLayout fileLinearLayout= (LinearLayout) findViewById(R.id.file_layout);
//            fileLinearLayout.setVisibility(View.VISIBLE);
            //select.setPadding(50,300,50,20);
        }*/
        for (int i = 0; i < images_list.size(); i++) {

            tbrow = new TableRow(this);
            //tbrow.setLayoutParams(new ViewGroup.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView t1v = new TextView(this);
            t1v.setText(""+(i+1));
            t1v.setTextColor(Color.RED);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(images_list.get(i));
            t2v.setTextColor(Color.RED);
            t2v.setGravity(Gravity.START);
            t2v.setWidth(500);
            t2v.setTextSize(10);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(size_list.get(i));
            t3v.setTextColor(Color.RED);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            tableLayout.addView(tbrow);
        }

        tbrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TableRow t = (TableRow) v;
                TextView FileName = (TextView) t.getChildAt(1);
                TextView FileSize = (TextView) t.getChildAt(2);
                String strFileName = FileName.getText().toString();
                String strFileSize = FileSize.getText().toString();
                return false;
            }
        });
    }

    public void loadFilefromMobile() {
        if(AttachmentFilesArray.size()<attchmentFileArraySize) {
            //Create intent to Open Image applications like Gallery, Google Photos
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, RESULT_LOAD_IMG);
        }
        else
        {
            Toast.makeText(this,"You can upload max "+attchmentFileArraySize+" files",Toast.LENGTH_SHORT).show();
        }
    }

String strFileName=null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = { "_data"};  //MediaStore.Images.Media.DATA };

                String req_path = getPath(context,selectedImage);
                //System.out.println("file path from getpath method: "+req_path);

                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                if(cursor!=null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    //System.out.println("image column index: "+imgDecodableString);
                    //Toast.makeText(getApplicationContext(),"image path from cursor: "+imgDecodableString,Toast.LENGTH_LONG).show();

                    if(imgDecodableString==null) {
                        imgDecodableString = req_path;
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }

                if(AttachmentFilesArray.size()<attchmentFileArraySize) {
                    AttachmentFilesArray.add(imgDecodableString);
                    llSelectedFileNames.setVisibility(View.VISIBLE);
                    strFileName=strFileName+imgDecodableString+"\n";
                    tvSelectedFileName.setText(strFileName);
                }

            } else {
                Toast.makeText(this, "You haven't picked File",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public void InsertRecord(String Filepath) {
        try
        {
            File file = new File(Filepath);
            long image_size_in_byte = file.length();
            //System.out.println("image size in byte: "+image_size_in_byte);
            int image_size_in_bytes = (int)image_size_in_byte;
            int total_size = 0;
            if(connection!=null){
                String query = "select * from attachment_details where system_id= \'"+system_id+"\'";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()){
                    int size = resultSet.getInt("file_size");
                    //System.out.println("Total file limit: "+size);
                    total_size = total_size + size;
                    //System.out.println("Total fize adding: "+total_size);
                }
            }

            //System.out.println("Total file size from db: "+total_size + " current file size: "+ image_size_in_bytes);
            int db_toal_size = total_size + image_size_in_bytes;
            double kb =(db_toal_size / 1024);
            //double kb =db_toal_size;
            double mb = (kb / 1024);

            //System.out.println("length if image: "+image_size_in_bytes+" \ndb_total: "+db_toal_size+" \nkb total: "+kb+" \nmb total:"+mb);

            int file_limit = 0;
            if(connection!=null){
                String query = "select * from gs_attachmentsize where id= 0";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()){
                    file_limit = resultSet.getInt("size");
                }
            }

            //System.out.println("Total file limit: "+file_limit);
            //Toast.makeText(getApplicationContext(),"Total file limit: "+file_limit + " and your mb size is: "+mb,Toast.LENGTH_LONG).show();

            if(mb > file_limit ){
                    /*Toast.makeText(getApplicationContext(),"Can not upload as the size of attachment crosses threashold limit." +
                            " Please contact Super Administrator",Toast.LENGTH_LONG).show();*/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please contact Super Administrator");
                builder.setMessage("Can not upload as the size of attachment crosses threshold limit!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else{
                Date date= new Date();
                if(connection!=null){
                    String query = "insert into attachment_details(system_id,user_id,user_name,org_file_name,file_name,upload_date," +
                            "file_location,file_size,element_id,element_type) " +
                            "values(?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement st = connection.prepareStatement(query);
                    st.setString(1, system_id);
                    //Todo: change the userid to random, for testing it is static
//                    st.setInt(2, Integer.parseInt(user_id));
                    st.setInt(2, Integer.parseInt("101"));
                    st.setString(3, user_Name);
                    st.setString(4, org_file);
                    st.setString(5, time_fileName);
                    st.setTimestamp(6, new Timestamp(date.getTime()));
                    st.setString(7, serverPath);
                    st.setInt(8, image_size_in_bytes);
                    st.setString(9, key);
                    st.setString(10, "SITE");
                    int count = st.executeUpdate();
                    if (count > 0) {
                        //Toast.makeText(getApplicationContext(), "Data Inserted successfully", Toast.LENGTH_SHORT).show();
                        // uploadImage();
                    }
                    else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Tab_Attachment.this);
                        builder1.setTitle("Upload failed");
                        builder1.setMessage("Attachment uploaded failed");
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void uploadImage(View v) {

        if(AttachmentFilesArray.size()<=attchmentFileArraySize && AttachmentFilesArray.size() !=0) {

            for (int i=0;i<AttachmentFilesArray.size();i++) {

                String FilePath=AttachmentFilesArray.get(i);
                String fileNameSegments[] = FilePath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                //System.out.println("fileName "+fileName);

                String filenameArray[] = fileName.split("\\.");
                String file = filenameArray[0];
                String extension = filenameArray[1];
                // Put file name in Async Http Post Param which will used in Java web app
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss");
                String timestamp = simpleDateFormat.format(new Date());
                org_file = file + "." + extension;
                time_fileName = file + "_" + timestamp + "." + extension;

                //.out.println("image path url: "+serverPath+time_fileName);
                //Toast.makeText(getApplicationContext(),"image path url: "+serverPath+time_fileName,Toast.LENGTH_LONG).show();

                params.put("filename", time_fileName);

                InsertRecord(FilePath);

                // When Image is selected from Gallery
                if (FilePath != null && !FilePath.isEmpty()) {
                    prgDialog.setMessage("Converting Image to Binary Data");
                    prgDialog.show();
                    // Convert image to String using Base64

                    encodeImagetoString(FilePath);

                    getFileDetails();
                    adapter.notifyDataSetChanged();
                    llSelectedFileNames.setVisibility(View.GONE);

                    // When Image is not selected from Gallery
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "You must select image from gallery before you try to upload",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"You must select File from storage to upload",Toast.LENGTH_LONG).show();
        }
    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString(String imgDecodableString) {
       /* new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {*/
        BitmapFactory.Options options = null;
        options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        bitmap = BitmapFactory.decodeFile(imgDecodableString,options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        //bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        encodedString = Base64.encodeToString(byte_arr, 0);

try {
    FileInputStream fis = new FileInputStream(imgDecodableString);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] b = new byte[1024];

    for (int readNum; (readNum = fis.read(b)) != -1;) {
        bos.write(b, 0, readNum);
    }

    byte[] bytes = bos.toByteArray();
    encodedString = Base64.encodeToString(bytes, 0);


}catch (Exception e)
{
    e.printStackTrace();
}
        // Put converted Image string into Async Http Post param
        params.put("image", encodedString);
        // Trigger Image upload
        triggerImageUpload();
               /* return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.setMessage("Uploading file");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);*/
    }


    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // http://192.168.2.4:9000/imgupload/upload_image.php
    // http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
    // Make Http call to upload Image to Java server
    public void makeHTTPCall() {
        //prgDialog.setMessage("Invoking JSP");6
        prgDialog.setMessage("storing in server");
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        //client.post("http://172.16.28.39:8080/ImageUploadWebApp/",
        client.post(upload_url,
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();

                        /*Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG).show();*/
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Tab_Attachment.this);
                        builder1.setTitle("Upload Success");
                        builder1.setMessage("Attachment uploaded successfully");
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

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri)
    {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
