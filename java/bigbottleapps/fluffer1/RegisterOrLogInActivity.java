package bigbottleapps.fluffer1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class RegisterOrLogInActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public static final String APP_PREFERENCES_PASSWORD = "password";
    SharedPreferences mSettings;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_log_in);
        setFragment(new LogInFragment());
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setReg(){
        setFragment(new RegistrationFragment());
    }

    public void setLog(String loe, String password){
        Fragment fragment = new LogInFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APP_PREFERENCES_LOE, loe);
        bundle.putString(APP_PREFERENCES_PASSWORD, password);
        fragment.setArguments(bundle);
        setFragment(fragment);
    }


    public void inputToSP(String loginOrEmail, String password){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOE, loginOrEmail);
        editor.putString(APP_PREFERENCES_PASSWORD, password);
        editor.apply();
    }

    public void startApp(String loginOrEmail, String password){
        inputToSP(loginOrEmail, password);
        finish();
    }
    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void setLogged(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("logged", true);
        editor.apply();
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
        if (wifiInfo != null && wifiInfo.isConnected()){
            return true;
        }
        return false;
    }
}
