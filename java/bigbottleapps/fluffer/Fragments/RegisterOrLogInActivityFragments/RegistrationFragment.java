package bigbottleapps.fluffer.Fragments.RegisterOrLogInActivityFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.R;
import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;

public class RegistrationFragment extends Fragment {

    private EditText loginET, emailET, passwordET, passwordAgainET;
    private TextView wrongTW;
    private Button continueB, logInB;
    private View.OnClickListener buttonClickListener, logInClickListener;
    private TextWatcher textWatcher;
    private HttpURLConnection conn;
    private Integer res;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registration, container, false);
        UIInit(view);
        Init();
        UISet();
        return view;
    }

    public void UIInit(View view){
        loginET = (EditText)view.findViewById(R.id.loginET);
        emailET = (EditText)view.findViewById(R.id.emailET);
        passwordET = (EditText)view.findViewById(R.id.passwordET);
        passwordAgainET = (EditText)view.findViewById(R.id.passwordagainET);
        continueB = (Button) view.findViewById(R.id.continueB);
        logInB = (Button)view.findViewById(R.id.logInB);
        wrongTW = (TextView)view.findViewById(R.id.wrongTW);
    }

    public void Init(){
        buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new INSERTtoChat().execute();
            }
        };
        logInClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterOrLogInActivity) getActivity()).setLogInFragment("", "");
            }
        };
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!passwordET.getText().toString().equals(passwordAgainET.getText().toString())) {
                    wrongTW.setVisibility(View.VISIBLE);
                    passwordAgainET.setBackgroundColor(getResources().getColor(R.color.red_wrong));
                    continueB.setEnabled(false);
                }else {
                    wrongTW.setVisibility(View.INVISIBLE);
                    passwordAgainET.setBackgroundColor(getResources().getColor(R.color.good));
                    continueB.setEnabled(true);
                }
            }
        };
    }

    public void UISet(){
        passwordET.addTextChangedListener(textWatcher);
        passwordAgainET.addTextChangedListener(textWatcher);
        continueB.setOnClickListener(buttonClickListener);
        logInB.setOnClickListener(logInClickListener);
    }

    private class INSERTtoChat extends AsyncTask<Void, Void, Integer> {
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

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String login = loginET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            try {
                String user_url = "http://posovetu.vh100.hosterby.com/" + "user_service.php?action=insert&"
                        + "login=" + URLEncoder.encode(login.trim(), "UTF-8")
                        + "&email=" + URLEncoder.encode(email.trim(), "UTF-8")
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
                String answer = stringBuilder.toString();
                int code = Integer.parseInt(answer.substring(1,2));
                dialog.dismiss();
                switch (code){
                    case 0:
                        setSnackBar(getResources().getString(R.string.y_wer_registr));
                        ((RegisterOrLogInActivity) getActivity()).setLogInFragment(login, password);
                        break;
                    case 1:
                        setSnackBar(getResources().getString(R.string.occupied_login));
                        break;
                    case 2:
                        setSnackBar(getResources().getString(R.string.occupied_email));
                        break;
                    default:
                        setSnackBar(getResources().getString(R.string.something_wrong));

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                setSnackBar(getContext().getString(R.string.something_wrong));
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

