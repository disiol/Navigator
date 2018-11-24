package denisimus_it.net.navigator;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import denisimus_it.net.navigator.client.JsonReader;

import static denisimus_it.net.navigator.client.AbstractSample.encodeParams;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Runnable {

    public static final String MY_LOG = "My_log";
    private GoogleMap mMap;
    private Thread thread;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        key = getString(R.string.google_maps_key);

        thread = new Thread(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        thread.start();

    }


    @Override
    public void run() {
        ComputationOfARoute("Россия, Москва, улица Дениса Давыдова, 7", "Россия, Москва, улица Кульнева 3", key);
    }

    public void ComputationOfARoute(String startPoint, String entdPoint, String key) {
        final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";// путь к Geocoding API по
        // HTTP
        final Map<String, String> params = Maps.newLinkedHashMap();
        params.put("sensor", "false");// указывает, исходит ли запрос на геокодирование от устройства с датчиком
        params.put("language", "ru");// язык данные на котором мы хочем получить
        params.put("mode", "walking");// способ перемещения, может быть driving, walking, bicycling
        params.put("origin", startPoint);// адрес или текстовое значение широты и
        // отправного пункта маршрута
        params.put("destination", entdPoint);// адрес или текстовое значение широты и долготы
        // долготы конечного пункта маршрута
        params.put("key", key);

        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами
        Log.d(MY_LOG, "ComputationOfARoute url: " + url); // Можем проверить что вернет этот путь в браузере
        final JSONObject response;// делаем запрос к вебсервису и получаем от него ответ
        try {
            response = JsonReader.read(url);
            // как правило наиболее подходящий ответ первый и данные о кординатах можно получить по пути
            // //results[0]/geometry/location/lng и //results[0]/geometry/location/lat
            JSONObject location = response.getJSONArray("routes").getJSONObject(0);
            location = location.getJSONArray("legs").getJSONObject(0);

            final String distance = location.getJSONObject("distance").getString("text");
            final String transitTime = location.getJSONObject("duration").getString("text");
            Log.d(MY_LOG, "ComputationOfARoute distance: " + distance + "\n" + "transitTime: " + transitTime);
            System.out.println(distance + "\n" + transitTime);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


    }
}
