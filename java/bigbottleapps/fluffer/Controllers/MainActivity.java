package bigbottleapps.fluffer.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import bigbottleapps.fluffer.Fragments.MainActivityFragments.*;
import bigbottleapps.fluffer.Fragments.MainActivityFragments.MapsFragment;
import bigbottleapps.fluffer.R;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_FROM = "from";
    SharedPreferences mSettings;
    public BottomNavigationView navigation;
    public static final String APP_PREFERENCES_MAP = "map";
    public static final String APP_PREFERENCES_LNG = "lng";
    public static final String APP_PREFERENCES_LTD = "ltd";


    @Override
    protected void onResume(){
        super.onResume();
        if(mSettings!=null){
            if(mSettings.contains(APP_PREFERENCES_MAP)&&mSettings.getString(APP_PREFERENCES_MAP, "false").equals("true")){

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if((mSettings!=null)&&(mSettings.contains(APP_PREFERENCES_FROM)))
            if (mSettings.getString(APP_PREFERENCES_FROM, "list").equals("list")) {
                navigation.setSelectedItemId(R.id.navigation_home);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setActionListFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            //region mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setActionListFragment();
                    return true;
                case R.id.navigation_map:
                    setMapFragment();
                    return true;
                case R.id.navigation_add:
                    mSettings.edit().putString(APP_PREFERENCES_FROM, "new").apply();
                    setNewActionFragment();
                    return true;
                case R.id.navigation_settings:
                    setFragment(new SettingsFragment());
                    return true;
            }
            return false;
        }

    };

    private void setMapFragment() {
        setFragment(new MapsFragment());
    }
    //endregion

    public void setNewActionFragment(){
        if(hasConnection(this))
            setFragment(new NewActionFragment());
        else
            setFragment(new RefreshFragment2());
    }

    public void setActionListFragment(){
        if(hasConnection(this))
            setFragment(new ActionListFragment());
        else
            setFragment(new RefreshFragment());

    }

    public void setSendTextFragment(){
        setFragment(new SendTextFragment());
    }

    public void setSettingsFragment(){
        setFragment(new SettingsFragment());
    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()){
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()){
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }

    public void setForgetFragment(){
        setFragment(new ForgetFragment());
    }
}