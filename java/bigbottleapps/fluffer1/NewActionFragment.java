package bigbottleapps.fluffer1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class NewActionFragment extends Fragment implements View.OnClickListener{
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_action, container, false);
        InitializeUI(view);
        Uri path = Uri.parse("content://com.android.providers.media.documents/document/image%3A101");
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseBn:
                selectImage();
                break;
            case R.id.uploadBn:
                uploadImage();
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
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private void uploadImage(){
        dialog = new ProgressDialog(getActivity()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

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
            Toast.makeText(getActivity(), "Some troubles with uploading... \nCheck your Internet connection", Toast.LENGTH_SHORT).show();
        }
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
            String user = "q.mis2013@gmail.com";

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
                conn.disconnect();
            }
            return res;
        }

        protected void onPostExecute(Integer result){

            if(result==200) {
                dialog.dismiss();
                ((MainActivity) getActivity()).setActionList();
            }
        }


    }
}
