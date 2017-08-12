package bigbottleapps.fluffer1;

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


public class ActionListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private HttpURLConnection conn;
    private String answer;
    private int res;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<RecyclerItem> listItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onStart() {
        super.onStart();
        listItems.clear();
        new SELECT().execute();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        listItems = new ArrayList<>();
        new SELECT().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_action_list, container, false);
        UIInitialization(view);
        return view;
    }

    public void UIInitialization(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        listItems = new ArrayList<>();
    }

    private class SELECT extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "service.php?action=select");
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
                        new SELECT().execute();
                    }
                });
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }

            if (answer != null && !answer.trim().equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(answer);
                    JSONObject jsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        listItems.add(0, new RecyclerItem(jsonObject.getString("title"), 2, 2, jsonObject.getString("photo_url"),
                                jsonObject.getString("description")));
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
