package bigbottleapps.fluffer.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import bigbottleapps.fluffer.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddingNewActionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mPlaceB, mUploadBn;
    private EditText mTitle, mPlaceET, mDescription;
    private CircleImageView mImageView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap = null;
    private HttpURLConnection conn;
    private String mServerUrl = "http://posovetu.vh100.hosterby.com/", typeList[], type = null, unique;
    private Integer res;
    ProgressDialog dialog;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public final static String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_ID = "id";
    public static final String APP_PREFERENCES_FROM = "from";
    public static final String APP_PREFERENCES_MAP = "map";
    public static final String APP_PREFERENCES_LNG = "lng";
    public static final String APP_PREFERENCES_LTD = "ltd";
    private String lng, ltd;
    private boolean placeFlag = false;

    @Override
    protected void onResume(){
        super.onResume();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings != null){
            if(mSettings.contains(APP_PREFERENCES_FROM)&&mSettings.getString(APP_PREFERENCES_FROM, "").equals("new"))
                setFrom();
            if(mSettings.contains(APP_PREFERENCES_MAP)&&mSettings.getString(APP_PREFERENCES_MAP, "").equals("true")){
                placeFlag = true;
                lng = mSettings.getString(APP_PREFERENCES_LNG, "");
                ltd = mSettings.getString(APP_PREFERENCES_LTD, "");
                mPlaceET.setText(lng+"  "+ltd);
                mSettings.edit().putString(APP_PREFERENCES_MAP, "false").apply();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_action);
        InitializeUI();
        adding();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setFrom();
    }

    public void setFrom(){
        mSettings.edit().putString(APP_PREFERENCES_FROM, "list").apply();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                selectImage();
                break;
            case R.id.uploadBn:
                new LOGGING().execute();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mImageView.setImageBitmap(bitmap);
                mImageView.setSystemUiVisibility(View.VISIBLE);
                mImageView.setVisibility(View.VISIBLE);
                mUploadBn.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void InitializeUI(){
        unique = String.valueOf(System.currentTimeMillis());
        typeList = getResources().getStringArray(R.array.typelist);
        mUploadBn = (Button) findViewById(R.id.uploadBn);
        mImageView = (CircleImageView) findViewById(R.id.imageView);
        mTitle = (EditText)findViewById(R.id.action_title);
        mImageView.setOnClickListener(this);
        mUploadBn.setOnClickListener(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mDescription = (EditText)findViewById(R.id.descriptionET);
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        mPlaceET = (EditText) findViewById(R.id.place);
        mPlaceB = (Button) findViewById(R.id.place2);
        mPlaceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapForNewActionActivity.class));
            }
        });
        MyCustomAdapter adapter = new MyCustomAdapter(getApplicationContext(),
                R.layout.spinner_element, typeList);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                switch (pos){
                    case 0:
                        type = null;
                        break;
                    case 1:
                        type = "1";
                        break;
                    case 2:
                        type = "2";
                        break;
                    case 3:
                        type = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void adding(){
        if (!mSettings.contains(APP_PREFERENCES_LOE)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddingNewActionActivity.this);
            builder.setTitle(getString(R.string.registration));
            builder.setMessage(getString(R.string.only_registered_events));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.registration), new DialogInterface.OnClickListener() { // Кнопка ОК
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), RegisterOrLogInActivity.class);
                    intent.putExtra("from", "new_action");
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setFrom();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private void uploadImage(){
        String mUploadUrl = mServerUrl+"ImageUpload.php";
        if(bitmap == null) {
            Snackbar.make(getCurrentFocus(), getString(R.string.set_image), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            dialog.dismiss();
        }else if(mTitle.getText().toString().equals("")) {
            Snackbar.make(getCurrentFocus(), getString(R.string.set_title), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            dialog.dismiss();
        }else if(type == null){
            dialog.dismiss();
            Snackbar.make(getCurrentFocus(), getString(R.string.set_type), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }else if(mDescription.getText().toString().equals("")){
            dialog.dismiss();
            Snackbar.make(getCurrentFocus(), getString(R.string.set_desc), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }else if(!placeFlag){
            dialog.dismiss();
            Snackbar.make(getCurrentFocus(), getString(R.string.set_place), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, mUploadUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                Toast.makeText(getApplicationContext(), Response, Toast.LENGTH_SHORT).show();
                                new UPLOAD_TO_SERVER().execute();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", (mTitle.getText().toString() + unique).trim());
                    params.put("image", imageToString(bitmap));
                    return params;
                }
            };
            try {
                Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
            } catch (Exception e) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.intern_trouble), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        MyCustomAdapter(Context context, int textViewResourceId,
                        String[] objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        View getCustomView(int position, View convertView,
                           ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_element, parent, false);
            TextView label = (TextView) row.findViewById(R.id.spinner_text);
            label.setText(typeList[position]);

            switch (position){
                case 0:
                    label.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    break;
                case 1:
                    label.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_local_dining_black_24dp), null, null, null);
                    break;
                case 2:
                    label.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_attach_money_black_24dp), null, null, null);
                    break;
                case 3:
                    label.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_filter_vintage_black_24dp), null, null, null);
                    break;
            }
            return row;
        }
    }

    private class UPLOAD_TO_SERVER extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            String title = mTitle.getText().toString();
            String photoUrl = mServerUrl+"upload/"+title+unique+".jpg";
            String place = lng+" "+ltd;
            String end_date = (System.currentTimeMillis()+120000)+"";
            String description = mDescription.getText().toString();
            String user = mSettings.getString(APP_PREFERENCES_ID, "0");
            try {
                String post_url = mServerUrl + "service.php?action=insert&"
                        + "type=" + URLEncoder.encode(type.trim(), "UTF-8")
                        + "&title=" + URLEncoder.encode(title.trim(), "UTF-8")
                        + "&photo_url=" + URLEncoder.encode(photoUrl.trim(), "UTF-8")
                        + "&place=" + URLEncoder.encode(place.trim(), "UTF-8")
                        + "&description=" + URLEncoder.encode(description.trim(), "UTF-8")
                        + "&end_date=" + URLEncoder.encode(end_date.trim(), "UTF-8")
                        + "&user=" + URLEncoder.encode(user.trim(), "UTF-8");
                conn = (HttpURLConnection) new URL(post_url).openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.connect();
                res = conn.getResponseCode();
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
                setFrom();
                e.printStackTrace();
            } finally {
                setFrom();
                dialog.dismiss();
                conn.disconnect();
            }
            return res;
        }

        protected void onPostExecute(Integer result){
            dialog.dismiss();

        }
    }

    private class LOGGING extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddingNewActionActivity.this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.wait_loading));
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        protected Integer doInBackground(Void... params) {
            String loginOrEmail = mSettings.getString(APP_PREFERENCES_LOE, "");
            String password = mSettings.getString(APP_PREFERENCES_PASSWORD, "");
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
                String answer = stringBuilder.toString();
                answer = answer.substring(0, answer.indexOf("]") + 1);
                inputStream.close();
                reader.close();
                JSONArray jsonArray = new JSONArray(answer);
                JSONObject jsonObject;
                int code;
                jsonObject = jsonArray.getJSONObject(0);
                code = Integer.parseInt(jsonObject.getString("answer"));
                switch (code) {
                    case 0:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadImage();
                            }
                        });
                        break;
                    case 3:
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog();
                            }
                        });
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(":(");
        builder.setMessage(getResources().getString(R.string.account_banned));
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

