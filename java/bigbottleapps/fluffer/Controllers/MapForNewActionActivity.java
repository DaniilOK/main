package bigbottleapps.fluffer.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bigbottleapps.fluffer.R;

public class MapForNewActionActivity extends FragmentActivity {

    OnMapReadyCallback onMapReadyCallback;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_MAP = "map";
    public static final String APP_PREFERENCES_LNG = "lng";
    public static final String APP_PREFERENCES_LTD = "ltd";
    SharedPreferences mSettings;
    private String lng, ltd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_new_action);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_2);
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LatLng latlng2 = new LatLng(0, 0);
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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng2, 13));
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                      setMarker(googleMap, latLng);
                    }
                });
            }
        };
        Button add = (Button)findViewById(R.id.set_place);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettings.edit().putString(APP_PREFERENCES_MAP, "true").apply();
                mSettings.edit().putString(APP_PREFERENCES_LNG, lng).apply();
                mSettings.edit().putString(APP_PREFERENCES_LTD, ltd).apply();
                finish();
            }
        });
        mapFragment.getMapAsync(onMapReadyCallback);
    }

    public void setMarker(GoogleMap map, LatLng latLng){
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        ltd = latLng.latitude+"";
        lng = latLng.longitude+"";

    }



}
