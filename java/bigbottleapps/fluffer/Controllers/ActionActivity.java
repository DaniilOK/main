package bigbottleapps.fluffer.Controllers;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import bigbottleapps.fluffer.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActionActivity extends AppCompatActivity {
    int id;
    TextView likes, dislikes, title;
    ProgressBar progressBar;
    CircleImageView iw;

    private HttpURLConnection conn;
    private int res;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_ID = "id";
    public static final String APP_PREFERENCES_FROM = "from";
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private String answer, user_id, l , d;
    private boolean lOrD;
    SharedPreferences mSettings;

    @Override
    protected void onResume(){
        super.onResume();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        user_id = mSettings.getString(APP_PREFERENCES_ID, "0");
        if((mSettings!=null)&&(mSettings.contains(APP_PREFERENCES_FROM)))
            if (mSettings.getString(APP_PREFERENCES_FROM, "action").equals("action")) {
                new SELECT().execute();
            }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        id = Integer.parseInt(getIntent().getStringExtra("id"));
        likes = (TextView)findViewById(R.id.text_likes);
        dislikes = (TextView)findViewById(R.id.text_dislikes);
        title = (TextView)findViewById(R.id.title);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iw = (CircleImageView)findViewById(R.id.image);
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesOrDislikesClick(true, getString(R.string.only_registered_like_posts));
            }
        });

        dislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesOrDislikesClick(false, getString(R.string.only_registered_dslike_posts));
            }
        });
        new SELECT().execute();
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
            new SELECT1().execute();
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

    private class SELECT extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "get_by_id.php?id="+id+"&user_id="+user_id);
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
                        title.setText(jsonObject.getString("title"));
                        setImage(iw, jsonObject.getString("photo_url"));
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

    private class SELECT1 extends AsyncTask<Void, Void, Integer> {
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
}