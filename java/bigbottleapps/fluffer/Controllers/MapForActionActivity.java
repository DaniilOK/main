package bigbottleapps.fluffer.Controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bigbottleapps.fluffer.R;

public class MapForActionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_action);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String geo[] = getIntent().getExtras().getString("place").split(" ");
        LatLng latlng = new LatLng(Double.parseDouble(geo[1]), Double.parseDouble(geo[0]));
        mMap.addMarker(new MarkerOptions().position(latlng).title(getIntent().getExtras().getString("title")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
    }
}
