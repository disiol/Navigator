package denisimus_it.net.navigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float startPointLat;
    private float startPointLng;
    private float endPointLat;
    private float endPointLng;

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
            //TODO
            startPointLat = Float.valueOf(intent.getStringExtra("startPointLat"));
            startPointLng = Float.valueOf(intent.getStringExtra("startPointLng"));
            endPointLat = Float.valueOf(intent.getStringExtra("endPointLat"));
            endPointLng = Float.valueOf(intent.getStringExtra("endPointLng"));
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng startPoint = new LatLng(startPointLat, startPointLng);
        LatLng endPoint = new LatLng(endPointLat, endPointLng);

        mMap.addMarker(new MarkerOptions().position(startPoint).title("Start point"));
        mMap.addMarker(new MarkerOptions().position(endPoint).title("End point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
    }
}
