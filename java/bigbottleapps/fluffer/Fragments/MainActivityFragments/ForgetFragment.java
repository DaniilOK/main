package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;
import bigbottleapps.fluffer.R;

/**
 * Created by mikhail on 24.08.17.
 */

public class ForgetFragment extends Fragment {
    EditText loginOrEmailET, newPasswordET;
    Button continueB;
    View.OnClickListener btnClickListener;
    String mServerUrl = "http://posovetu.vh100.hosterby.com/", new_password, answer;
    HttpURLConnection conn;
    Integer res;
    ProgressDialog dialog;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    private SharedPreferences mSettings;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forget_activity, container, false);
        Initialization(view);
        return view;
    }

    public void Initialization(View view){
        loginOrEmailET = (EditText)view.findViewById(R.id.loginoremailETF);
        loginOrEmailET.setVisibility(View.INVISIBLE);
        newPasswordET = (EditText)view.findViewById(R.id.passwordETF);
        continueB = (Button)view.findViewById(R.id.continueBF);
        btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_password = newPasswordET.getText().toString();
                new NEWPASSWORD().execute();
            }
        };
        continueB.setOnClickListener(btnClickListener);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private class NEWPASSWORD extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity()); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.wait_loading));
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        protected Integer doInBackground(Void... params) {

            try {
                String user_url = mServerUrl + "mail.php?loginoremail=" + URLEncoder.encode(mSettings.getString(APP_PREFERENCES_LOE, "").trim(), "UTF-8")+
                        "&newpassword="+URLEncoder.encode(new_password.trim(), "UTF-8");
                conn = (HttpURLConnection) new URL(user_url).openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.connect();
                res = conn.getResponseCode();
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedString;
                while ((bufferedString = reader.readLine()) != null)
                    stringBuilder.append(bufferedString);
                answer = stringBuilder.toString();
                answer = answer.substring(0, answer.indexOf("]") + 1);
                inputStream.close();
                reader.close();
                JSONArray jsonArray = new JSONArray(answer);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                dialog.dismiss();
                if(jsonObject.getString("email").equals("email")){
                    setToast(getString(R.string.message_was_sent));
                }else{
                    setToast(getString(R.string.something_wrong));
                }
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
                setToast(getString(R.string.something_wrong));
            } finally {
                dialog.dismiss();
                conn.disconnect();
                try {
                    ((MainActivity)getActivity()).setSettingsFragment();
                }catch (Exception e){
                    Log.d("q11", "check");
                }
            }
            return res;
        }

        void setToast(final String toast){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
