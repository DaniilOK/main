package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.R;
import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;

public class NewActionFragment extends Fragment implements View.OnClickListener {
    private Button mUploadBn;
    private EditText mTitle;
    private ImageView mImageView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private HttpURLConnection conn;
    private String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private Integer res;
    ProgressDialog dialog;
    private String unique = String.valueOf(System.currentTimeMillis());
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public final static String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_ID = "id";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_action_activity, container, false);
        InitializeUI(view);
        adding();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseBn:
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
        if(requestCode==IMG_REQUEST && resultCode == getActivity().RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                bitmap.setDensity(0);
                mImageView.setImageBitmap(bitmap);
                mImageView.setSystemUiVisibility(View.VISIBLE);
                mImageView.setVisibility(View.VISIBLE);
                mUploadBn.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void InitializeUI(View view){
        mUploadBn = (Button) view.findViewById(R.id.uploadBn);
        Button mChooseBn = (Button) view.findViewById(R.id.chooseBn);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mTitle = (EditText)view.findViewById(R.id.action_title);
        mChooseBn.setOnClickListener(this);
        mUploadBn.setOnClickListener(this);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void adding(){
        if (!mSettings.contains(APP_PREFERENCES_LOE)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Registration");
            builder.setMessage("Only registered users can add events");
            builder.setCancelable(false);
            builder.setPositiveButton("Registration", new DialogInterface.OnClickListener() { // Кнопка ОК
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getActivity().getApplicationContext(), RegisterOrLogInActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)getActivity()).navigation.setSelectedItemId(R.id.navigation_home);
                    ((MainActivity)getActivity()).setActionListFragment();
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, mUploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(getActivity(), Response, Toast.LENGTH_SHORT).show();
                            new INSERTtoChat().execute();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Some errors while loading... Please, try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(), "Some errors while loading... Please, try again", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", (mTitle.getText().toString()+unique).trim());
                params.put("image", imageToString(bitmap));
                return params;
            }
        };
        try {
            Volley.newRequestQueue(getActivity()).add(stringRequest);
        }catch (Exception e){
            dialog.dismiss();
            Toast.makeText(getActivity(), "Some troubles with uploading... \nCheck your Internet connection", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private class INSERTtoChat extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            String title = mTitle.getText().toString();
            String photoUrl = mServerUrl+"upload/"+title+unique+".jpg";
            String place = "Sovetskaya st 39-139";
            String end_date = (System.currentTimeMillis()+120000)+"";
            String type = "food";
            String description = "Уличные музыканты Dай Dарогу играют на советской";
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
                e.printStackTrace();
            } finally {
                dialog.dismiss();
                conn.disconnect();
            }
            return res;
        }

        protected void onPostExecute(Integer result){
            dialog.dismiss();
            ((MainActivity)getActivity()).setActionListFragment();
            ((MainActivity)getActivity()).navigation.setSelectedItemId(R.id.navigation_home);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadImage();
                            }
                        });
                        break;
                    case 3:
                        dialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(":(");
        builder.setMessage("Your account is banned");
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