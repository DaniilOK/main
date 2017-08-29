package bigbottleapps.fluffer.Controllers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.R;

public class SendTextActivity extends AppCompatActivity {

    private HttpURLConnection conn;
    private int res;
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private String message = "", login = "";

    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    private SharedPreferences mSettings;

    private boolean CheckMessage(String message){
        if (message.length() < 8)
            return false;
        return true;
    }

    private void ShowToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        final Button sendB = (Button)findViewById(R.id.send);
        final TextView textTV = (TextView) findViewById(R.id.text);

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = textTV.getText().toString();
                login ="null";
                if (mSettings.contains(APP_PREFERENCES_LOE))
                    login = mSettings.getString(APP_PREFERENCES_LOE, "");

                if (!CheckMessage(message)){
                    ShowToast(getResources().getString(R.string.short_msg));
                    return;
                }
                message += "\n\nUser login:" + login;
                new MESSAGE_SEND().execute();
            }
        });

    }

    private class MESSAGE_SEND extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            if (res == 200) {
                ShowToast(getResources().getString(R.string.msg_send_sucs));
                finish();
            }
            else
                ShowToast(getResources().getString(R.string.something_wrong));
        }

        protected Integer doInBackground(Void... params) {

            try {
                URL url = new URL(mServerUrl + "mail_developers.php?text="+ URLEncoder.encode(message, "UTF-8")+
                "&login="+URLEncoder.encode(login, "UTF-8"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setDoInput(true);
                conn.connect();
                res = conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }
}
