package bigbottleapps.fluffer.Fragments.RegisterOrLogInActivityFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.R;
import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;

public class LogInFragment extends Fragment{

    TextView forgotPasswordTV;
    Button registerB, loggingB;
    EditText loginOrEmailET, passwordET;
    View.OnClickListener regClickListener, logClickListener;
    String mServerUrl = "http://posovetu.vh100.hosterby.com/", answer;
    HttpURLConnection conn;
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
        forgotPasswordTV = (TextView) view.findViewById(R.id.forgot);
    }

    public void Init(){
        regClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterOrLogInActivity) getActivity()).setRegistrationFragment();
            }
        };
        logClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RegisterOrLogInActivity)getActivity()).hasConnection(getActivity()))
                    new LOGGING().execute();
                else
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        };
        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterOrLogInActivity)getActivity()).setForgetFragment();
            }
        });
    }

    public void UISet(){
        String htmlTaggedString  = getString(R.string.forgot_password);
        Spanned textSpan  =  android.text.Html.fromHtml(htmlTaggedString);
        forgotPasswordTV.setText(textSpan);
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
            dialog.setMessage(getResources().getString(R.string.wait_loading));
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
                jsonObject = jsonArray.getJSONObject(0);
                dialog.dismiss();
                switch (Integer.parseInt(jsonObject.getString("answer"))){
                    case 0:
                        setSnackBar(getResources().getString(R.string.logged_in));
                        ((RegisterOrLogInActivity)getActivity()).startApp(loginOrEmail, password, jsonObject.getString("_id"));
                        break;
                    case 1:
                        setSnackBar(getResources().getString(R.string.wrong_pass));
                        break;
                    case 2:
                        setSnackBar(getResources().getString(R.string.not_registered));
                        break;
                    case 3:
                        setSnackBar(getResources().getString(R.string.account_banned));
                        break;
                    default:
                        setSnackBar(getResources().getString(R.string.something_wrong));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSnackBar(getResources().getString(R.string.something_wrong));
                    }
                });
            } finally {
                conn.disconnect();
            }
            return res;
        }

        void setSnackBar(String text){
            Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}