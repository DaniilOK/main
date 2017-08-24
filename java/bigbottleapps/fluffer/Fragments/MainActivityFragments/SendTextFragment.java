package bigbottleapps.fluffer.Fragments.MainActivityFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class SendTextFragment extends Fragment {

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

    private void ShowToast(String text, Context context){
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send_message, container, false);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        final Button sendB = (Button)view.findViewById(R.id.send);
        final TextView textTV = (TextView) view.findViewById(R.id.text);

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = textTV.getText().toString();
                login ="null";
                if (mSettings.contains(APP_PREFERENCES_LOE))
                    login = mSettings.getString(APP_PREFERENCES_LOE, "");

                if (!CheckMessage(message)){
                    ShowToast(getResources().getString(R.string.short_msg), getContext());
                    return;
                }
                message += "\n\nUser login:" + login;
                new MESSAGE_SEND().execute();
            }
        });

        return view;
    }

    private class MESSAGE_SEND extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            if (res == 200) {
                ShowToast(getResources().getString(R.string.msg_send_sucs), getContext());
                ((MainActivity)getActivity()).setSettingsFragment();
            }
            else
                ShowToast(getResources().getString(R.string.something_wrong), getContext());
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
