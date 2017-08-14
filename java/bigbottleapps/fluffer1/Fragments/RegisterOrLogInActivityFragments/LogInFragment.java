package bigbottleapps.fluffer1.Fragments.RegisterOrLogInActivityFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import bigbottleapps.fluffer1.Models.RecyclerItem;
import bigbottleapps.fluffer1.R;
import bigbottleapps.fluffer1.Controllers.RegisterOrLogInActivity;

public class LogInFragment extends Fragment{

    Button registerB, loggingB;
    EditText loginOrEmailET, passwordET;
    View.OnClickListener regClickListener, logClickListener;
    private String mServerUrl = "http://posovetu.vh100.hosterby.com/", answer;
    private HttpURLConnection conn;
    Integer res;
    ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_log_in, container, false);
        UIInit(view);
        Init();
        UISet();
        return view;
    }

    public void UIInit(View view){
        registerB = (Button)view.findViewById(R.id.regB);
        loginOrEmailET = (EditText)view.findViewById(R.id.loginoremailET);
        passwordET = (EditText)view.findViewById(R.id.passwordET2);
        loggingB = (Button)view.findViewById(R.id.logB);
    }

    public void Init(){
        regClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterOrLogInActivity) getActivity()).setReg();
            }
        };
        logClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RegisterOrLogInActivity)getActivity()).hasConnection(getActivity()))
                    new LOGGING().execute();
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

            }
        };
    }

    public void UISet(){
        registerB.setOnClickListener(regClickListener);
        loggingB.setOnClickListener(logClickListener);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            loginOrEmailET.setText(bundle.getString("loe", ""));
            passwordET.setText(bundle.getString("password", ""));
        }
    }

    private class LOGGING extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity()); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        protected Integer doInBackground(Void... params) {
            String loginOrEmail = loginOrEmailET.getText().toString();
            String password = passwordET.getText().toString();
            try {
                String user_url = mServerUrl + "login_service.php?"
                        + "loginoremail=" + URLEncoder.encode(loginOrEmail.trim(), "UTF-8")
                        + "&password=" + URLEncoder.encode(password.trim(), "UTF-8");
                Log.d("q11", user_url);
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
                JSONObject jsonObject;
                int code;
                String id;
                jsonObject = jsonArray.getJSONObject(0);
                code = Integer.parseInt(jsonObject.getString("answer"));
                id = jsonObject.getString("_id");
                Log.d("q11", id);
                dialog.dismiss();
                switch (code){
                    case 0:
                        Snackbar.make(getView(), "You were logged in", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        ((RegisterOrLogInActivity)getActivity()).setLogged();
                        ((RegisterOrLogInActivity)getActivity()).startApp(loginOrEmail, password, id);
                        break;
                    case 1:
                        Snackbar.make(getView(), "Wrong password", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        break;
                    case 2:
                        Snackbar.make(getView(), "You are not registered", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        break;
                    case 3:
                        Snackbar.make(getView(), "Your account is banned", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        break;
                    default:
                        Snackbar.make(getView(), "Something went wrong... try again", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "-Something went wrong... try again", Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                conn.disconnect();
            }
            return res;
        }

    }

}
