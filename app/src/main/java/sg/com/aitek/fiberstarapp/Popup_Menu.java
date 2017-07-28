package sg.com.aitek.fiberstarapp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Popup_Menu extends TabActivity {

    private String user_Name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup__menu);

        Resources ressources = getResources();
        TabHost tabHost = getTabHost();

        String key=null,user_role=null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key       = bundle.getString("key");
            user_role = bundle.getString("role");
            user_Name = bundle.getString("userName");
//            System.out.println("key values from Popup_Menu activity: "+key +" \nUser_Role is: "+user_role+" \nUser Name:"+user_Name);
        }

        // Android tab
        Intent intentDetails = new Intent().putExtra("user_Name",user_Name).putExtra("key",key).putExtra("role",user_role).setClass(this, Tab_Details.class);
        TabHost.TabSpec tabSpecDetails = tabHost
                .newTabSpec("Details")
                .setIndicator("Details")  //ressources.getDrawable(R.drawable.icon_android_config)
                .setContent(intentDetails);

        // Apple tab
        Intent intentPhoto = new Intent().putExtra("user_Name",user_Name).putExtra("key",key).putExtra("role",user_role).setClass(this, Tab_Photo.class);
        TabHost.TabSpec tabSpecPhoto = tabHost
                .newTabSpec("Image")
                .setIndicator("Image")  //, ressources.getDrawable(R.drawable.icon_apple_config)
                .setContent(intentPhoto);

        // Windows tab
        Intent intentAttachment = new Intent().putExtra("user_Name",user_Name).putExtra("key",key).putExtra("role",user_role).setClass(this, Tab_Attachment.class);
        TabHost.TabSpec tabSpecAttachment = tabHost
                .newTabSpec("Attachment")
                .setIndicator("Attachment") //, ressources.getDrawable(R.drawable.icon_windows_config)
                .setContent(intentAttachment);


        // add all tabs
        tabHost.addTab(tabSpecDetails);
        tabHost.addTab(tabSpecPhoto);
        tabHost.addTab(tabSpecAttachment);

        //set Windows tab as default (zero based)
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().width = 40;
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 50;
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 60;

        tabHost.setCurrentTab(0);
    }
}
