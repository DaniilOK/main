package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.Models.MyAdapter;
import bigbottleapps.fluffer.Models.RecyclerItem;
import bigbottleapps.fluffer.R;

public class MapsFragment extends Fragment{
    private String lng, ltd;
    OnMapReadyCallback onMapReadyCallback;
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
    GoogleMap map;
    String title;
    LatLng latlng, latlng2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        SharedPreferences mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        switch (mSettings.getInt("city", -1)){
            case -1:

                break;
            case 0:
                latlng2 = new LatLng(52.087175135821, 23.702393397688866);
                break;
            case 1:
                latlng2 = new LatLng(53.90420423549256, 27.562403306365013);
                break;
            case 2:
                latlng2 = new LatLng(55.75118973951429, 37.616174966096885);
                break;
            case 3:
                latlng2 = new LatLng(59.93298866532049, 30.332120396196842);
                break;
            case 4:
                latlng2 = new LatLng(52.2277059779058, 21.01638838648796);
                break;
        }
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng2, 13));
                new SELECT().execute();
            }
        };
        mapFragment.getMapAsync(onMapReadyCallback);

        return view;
    }

    private class SELECT extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "service.php?action=select&user_id="+user_id+"&city="+getActivity().getSharedPreferences("users", Context.MODE_PRIVATE).getInt("city", 0));
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
            } finally {
                conn.disconnect();
            }
            if (answer != null && !answer.trim().equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(answer);
                    JSONObject jsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        title = jsonObject.getString("title");
                        String place = jsonObject.getString("place");
                        String likes = jsonObject.getString("likes");
                        String dislikes = jsonObject.getString("dislikes");
                        long d_date = Long.parseLong(jsonObject.getString("ago"))/1000;
                        int h = (int)(d_date/3600);
                        int m = (int)(d_date%3600)/60;
                        String date = "";
                        if(h==0){
                            date = m+" "+getString(R.string.ago_m);
                        }else {
                            date = h+" "+getString(R.string.ago_h);
                        }
                        int l = Integer.parseInt(likes);
                        int d = Integer.parseInt(dislikes);
                        String geo[] = place.split(" ");
                        latlng = new LatLng(Double.parseDouble(geo[1]), Double.parseDouble(geo[0]));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MarkerOptions marker = new MarkerOptions().position(latlng).draggable(false).title(title);
                                map.addMarker(marker);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            adapter = new MyAdapter(listItems, getActivity());

            return res;
        }
    }

}
