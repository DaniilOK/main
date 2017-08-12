package bigbottleapps.fluffer1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    SharedPreferences mSettings;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        //region
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   setActionList();
                    return true;
                case R.id.navigation_map:
                    return true;
                case R.id.navigation_add:
                    startNew();
                    return true;
                case R.id.navigation_settings:
                    setFragment(new SettingsFragment());
                    return true;
            }
            return false;
        }

    };
    //endregion

    public void startNew(){
        if(hasConnection(this))
            adding();
        else
            setFragment(new RefreshFragment2());
    }

    public void setActionList(){
        if(hasConnection(this))
            setFragment(new ActionListFragment());
        else
            setFragment(new RefreshFragment());

    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setActionList();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mSettings!=null) {
            if (mSettings.contains("logged")) {
                if (mSettings.contains("logged") && mSettings.getBoolean("logged", false)) {
                    setFragment(new NewActionFragment());
                    navigation.setSelectedItemId(R.id.navigation_add);
                }
            }else {
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    public void adding() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_LOE)) {
            setFragment(new NewActionFragment());
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Registration");
            builder.setMessage("Only registered users can add events");
            builder.setCancelable(false);
            builder.setPositiveButton("Registration", new DialogInterface.OnClickListener() { // Кнопка ОК
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(), RegisterOrLogInActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navigation.setSelectedItemId(R.id.navigation_home);
                    setActionList();
                    dialog.dismiss();
                }
            });
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }
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




}
