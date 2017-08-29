package bigbottleapps.fluffer.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bigbottleapps.fluffer.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActionActivity extends AppCompatActivity {
    int id;
    TextView likes, dislikes, title, description;
    ProgressBar progressBar;
    CircleImageView iw;

    private HttpURLConnection conn;
    private int res;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_ID = "id";
    public static final String APP_PREFERENCES_FROM = "from";
    public static final String APP_PREFERENCES_LOE = "loe";
    public final static String APP_PREFERENCES_PASSWORD = "password";
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private String answer, user_id, l , d, place;
    private boolean lOrD;
    ProgressDialog dialog;
    boolean flag;
    SharedPreferences mSettings;

    @Override
    protected void onResume(){
        super.onResume();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if((mSettings!=null)&&(mSettings.contains(APP_PREFERENCES_FROM)))
            if (mSettings.getString(APP_PREFERENCES_FROM, "action").equals("action")) {
                new GET_BY_ID().execute();
            }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Button button = (Button)findViewById(R.id.on_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapForActionActivity.class);
                intent.putExtra("place", place);
                startActivity(intent);
            }
        });
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        likes = (TextView)findViewById(R.id.text_likes);
        dislikes = (TextView)findViewById(R.id.text_dislikes);
        title = (TextView)findViewById(R.id.title);
        description = (TextView)findViewById(R.id.description);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iw = (CircleImageView)findViewById(R.id.image);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        user_id = mSettings.getString(APP_PREFERENCES_ID, "0");
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                new LOGGING().execute();
            }
        });

        dislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                new LOGGING().execute();
            }
        });
        new GET_BY_ID().execute();
    }

    private void setDialog(String text){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.registration));
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.registration), new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), RegisterOrLogInActivity.class);
                intent.putExtra("from", "action");
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void likesOrDislikesClick(boolean flag, final String text){
        lOrD = flag;
        if(!user_id.equals("0"))
            new SET_LIKES().execute();
        else
            setDialog(text);
    }

    private void setLikesAndDislikes(int ans) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Integer.parseInt(l)<10000)
                    likes.setText(l);
                if((Integer.parseInt(l)>=10000)&&(Integer.parseInt(l)<1000000))
                    likes.setText(Integer.parseInt(l)/1000+"k");
                if(Integer.parseInt(l)>=1000000)
                    likes.setText(Integer.parseInt(l)/1000000+"M");
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Integer.parseInt(d)<10000)
                    dislikes.setText(d);
                if((Integer.parseInt(d)>=10000)&&(Integer.parseInt(d)<1000000))
                    dislikes.setText(Integer.parseInt(d)/1000+"k");
                if(Integer.parseInt(d)>=1000000)
                    dislikes.setText(Integer.parseInt(d)/1000000+"M");
            }
        });
        progressBar.setProgress(calcProgress(Integer.parseInt(l), Integer.parseInt(d)));
        Log.d("user_check", ans+"");
        switch (ans) {
            case 0:
                setLikeImage(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                setDislikeImage(getResources().getDrawable(R.drawable.ic_thumb_down_blue_24dp));
                break;
            case 1:
                setLikeImage(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                setDislikeImage(getResources().getDrawable(R.drawable.ic_thumb_down_black_24dp));
                break;
            case 2:

                setLikeImage(getResources().getDrawable(R.drawable.ic_thumb_up_blue_24dp));
                setDislikeImage(getResources().getDrawable(R.drawable.ic_thumb_down_black_24dp));
                break;
        }
    }

    private void setLikeImage(final Drawable res){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                likes.setCompoundDrawablesWithIntrinsicBounds(null, res, null, null);
            }
        });

        }

    private void setDislikeImage(final Drawable res){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dislikes.setCompoundDrawablesWithIntrinsicBounds(null, res, null, null);
            }
        });

        }

    private int calcProgress(int likes, int dislikes){
        if((likes==0)&&(dislikes!=0))
            return 0;
        if((likes!=0)&&(dislikes==0))
            return 100;
        if ((likes!=0)&&(dislikes!=0))
            return likes*100/(likes+dislikes);
        return 0;
    }

    private class GET_BY_ID extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "get_by_id.php?id="+id+"&user_id="+user_id);
                Log.d("check1", url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setDoInput(true);
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            if (answer != null && !answer.trim().equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(answer);
                    JSONObject jsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        l = jsonObject.getString("likes");
                        d = jsonObject.getString("dislikes");
                        place = jsonObject.getString("place");
                        setTitle(jsonObject.getString("title"));
                        setImage(iw, jsonObject.getString("photo_url"));
                        setDescription(jsonObject.getString("description"));
                        int ans = Integer.parseInt(jsonObject.getString("this"));
                        setLikesAndDislikes(ans);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return res;
        }
    }

    private class SET_LIKES extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                String likeOrDislike = "0";
                if (lOrD)
                    likeOrDislike = "2";
                URL url = new URL(mServerUrl + "valuation_service.php?action=insert&action_id="+id+"&user_id="+user_id+
                        "&like_or_dislike="+likeOrDislike);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setDoInput(true);
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
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                l = jsonObject.getString("likes");
                d = jsonObject.getString("dislikes");
                int ans = Integer.parseInt(jsonObject.getString("this"));
                setLikesAndDislikes(ans);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }

    private void setImage(CircleImageView iw, String res){
        new DownloadImageTask(iw).execute(res);
    }

    private void setTitle(final String title_text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(title_text);
            }
        });
    }

    private void setDescription(final String desc_text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                description.setText(desc_text);
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView iw;

        DownloadImageTask(CircleImageView iw){
            this.iw = iw;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(urls[0]).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            this.iw.setImageBitmap(result);
        }
    }
    private class LOGGING extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionActivity.this); // this = YourActivity
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
                dialog.dismiss();
                switch (code) {
                    case 0:
                        likesOrDislikesClick(flag, getString(R.string.only_registered_like_posts));
                        break;
                    case 3:
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
