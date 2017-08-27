package bigbottleapps.fluffer.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bigbottleapps.fluffer.Fragments.MainActivityFragments.NewActionFragment;
import bigbottleapps.fluffer.R;

public class MapForNewActionActivity extends FragmentActivity {

    OnMapReadyCallback onMapReadyCallback;
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public final static String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_ID = "id";
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
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                      setMarker(googleMap, latLng);
                    }
                });
            }
        };
        mapFragment.getMapAsync(onMapReadyCallback);
    }

    public void setMarker(GoogleMap map, LatLng latLng){
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        ltd = latLng.latitude+"";
        lng = latLng.longitude+"";

    }

}
