package denisimus_it.net.navigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String MY_LOG = "My_log";

    private GoogleMap mMap;
    private float startPointLat;
    private float startPointLng;
    private float endPointLat;
    private float endPointLng;
    private List<LatLng> points = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            //TODO сохранить время и дистанцю
            endPointLat = Float.valueOf(intent.getStringExtra("endPointLat"));
            endPointLng = Float.valueOf(intent.getStringExtra("endPointLng"));
            points = PolyUtil.decode(intent.getStringExtra("points"));

            Log.d(MY_LOG, "points: " + points.toString());
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions line = new PolylineOptions();
        line.width(4f);
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                if (i == 0) {
                    MarkerOptions startMarkerOptions = new MarkerOptions()
                            .position(points.get(i));
                    mMap.addMarker(startMarkerOptions);
                } else if (i == points.size() - 1) {
                    MarkerOptions endMarkerOptions = new MarkerOptions()
                            .position(points.get(i));
                    mMap.addMarker(endMarkerOptions);
                }
                line.add(points.get(i));
                latLngBuilder.include(points.get(i));
            }
            mMap.addPolyline(line);
            int size = getResources().getDisplayMetrics().widthPixels;
            LatLngBounds latLngBounds = latLngBuilder.build();
            CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
            mMap.moveCamera(track);
        }
    }
}
