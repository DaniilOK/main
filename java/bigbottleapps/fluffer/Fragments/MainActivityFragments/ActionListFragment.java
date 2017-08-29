package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.Models.MyAdapter;
import bigbottleapps.fluffer.R;
import bigbottleapps.fluffer.Models.RecyclerItem;

public class ActionListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private HttpURLConnection conn;
    private int res;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<RecyclerItem> listItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String answer, user_id;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_ID = "id";
    SharedPreferences mSettings;
    String typelist[], m_ago, h_ago;

    @Override
    public void onStart() {
        super.onStart();
        load();
    }

    @Override
    public void onRefresh() {
        load();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_action_list, container, false);
        Initialization(view);
        return view;
    }

    public void Initialization(View view) {
        typelist = getResources().getStringArray(R.array.typelist);
        m_ago = getString(R.string.ago_m);
        h_ago = getString(R.string.ago_h);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        listItems = new ArrayList<>();
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        user_id = mSettings.getString(APP_PREFERENCES_ID, "0");

    }

    Drawable getImageById(int id){
        return getActivity().getResources().getDrawable(id);
    }

    public void load(){
        listItems.clear();
        new SELECT().execute();
    }

    private class SELECT extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "service.php?action=select&user_id="+user_id+"&city="+mSettings.getInt("city", 0));
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new ActionListFragment.SELECT().execute();
                    }
                });
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
                        String action_id = jsonObject.getString("_id");
                        String title = jsonObject.getString("title");
                        int type_id = Integer.parseInt(jsonObject.getString("type"));

                        String photo_url = jsonObject.getString("photo_url");
                        String likes = jsonObject.getString("likes");
                        String dislikes = jsonObject.getString("dislikes");
                        long d_date = Long.parseLong(jsonObject.getString("ago"))/1000;
                        int h = (int)(d_date/3600);
                        int m = (int)(d_date%3600)/60;
                        String date = "";
                        if(h==0){
                            date = m+" "+m_ago;
                        }else {
                            date = h+" "+h_ago;
                        }
                        int l = Integer.parseInt(likes);
                        int d = Integer.parseInt(dislikes);
                        listItems.add(0, new RecyclerItem(title, l, d, photo_url, action_id, type_id, date));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            adapter = new MyAdapter(listItems, getActivity());

            return res;
        }

        protected void onPostExecute(Integer result) {
            if (result == 200) {
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}