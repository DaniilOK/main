package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.R;

public class SettingsFragment extends Fragment {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    private SharedPreferences mSettings;


    private HttpURLConnection conn;
    private int res;
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private String answer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        Button button = (Button)view.findViewById(R.id.log_out);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettings.contains(APP_PREFERENCES_LOE)) {
                    mSettings.edit().clear().commit();
                }
            }
        });

        Button send = (Button) view.findViewById(R.id.message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MESSAGE_SEND().execute();
            }
        });
        return view;
    }

    private class MESSAGE_SEND extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "mail_developers.php?text="+ URLEncoder.encode("pipiska", "UTF-8"));
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