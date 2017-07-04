package sg.com.aitek.fiberstarapp;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DbConncetion {
    private Context context;
    private Properties properties;
    Connection conn;

    public DbConncetion(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getProperties(String FileName) {

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(FileName);
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Connection getConnection(){
        Properties prop = getProperties("FiberStar.properties");
        String driver   = prop.getProperty("database.DRIVER");
        String url      = prop.getProperty("database.URL");
        String userName = prop.getProperty("database.USERNAME");
        String passWord = prop.getProperty("database.PASSWORD");

        try {
            Class.forName(driver);
            System.out.println("from Db Connection: url- "+url);
            System.out.println("from Db Connection: userName- "+userName);
            System.out.println("from Db Connection: passWord- "+passWord);
            conn = DriverManager.getConnection(url, userName, passWord);
        }
        catch (ClassNotFoundException cls){
            cls.printStackTrace();
        }
        catch(SQLException s){
            s.printStackTrace();
        }
        return conn;
    }
}

