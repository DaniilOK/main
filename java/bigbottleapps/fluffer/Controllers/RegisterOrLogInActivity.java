package bigbottleapps.fluffer.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import bigbottleapps.fluffer.Fragments.RegisterOrLogInActivityFragments.*;
import bigbottleapps.fluffer.R;

public class RegisterOrLogInActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public static final String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_LOGGED = "logged";
    public static final String APP_PREFERENCES_ID = "id";
    SharedPreferences mSettings;

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOGGED, "action_list");
        editor.apply();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_log_in);
        setFragment(new LogInFragment());
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setRegistrationFragment(){
        setFragment(new RegistrationFragment());
    }

    public void setLogInFragment(String loginOrEmail, String password){
        Fragment logInFragment = new LogInFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APP_PREFERENCES_LOE, loginOrEmail);
        bundle.putString(APP_PREFERENCES_PASSWORD, password);
        logInFragment.setArguments(bundle);
        setFragment(logInFragment);
    }

    public void inputToSharedPreferences(String loginOrEmail, String password, String id){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOE, loginOrEmail);
        editor.putString(APP_PREFERENCES_PASSWORD, password);
        editor.putString(APP_PREFERENCES_ID, id);
        editor.apply();
    }

    public void startApp(String loginOrEmail, String password, String id){
        inputToSharedPreferences(loginOrEmail, password, id);
        finish();
    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void setLogged(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOGGED, "new_action");
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
        return wifiInfo != null && wifiInfo.isConnected();
    }
}