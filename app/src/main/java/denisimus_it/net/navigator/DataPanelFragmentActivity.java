package denisimus_it.net.navigator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import denisimus_it.net.navigator.client.JsonReader;

import static denisimus_it.net.navigator.client.AbstractSample.encodeParams;


public class DataPanelFragmentActivity extends Fragment implements Runnable, View.OnClickListener {

    public static final String MY_LOG = "My_log";
    private Thread thread;
    private String key;
    private Button generateRoadButton;
    private EditText startLocationEditText, endLocationEditText;
    private TextView distanceTextView, timeTextView;

    private String startPoint;
    private String entdPoint;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dataPanelView = inflater.inflate(R.layout.fragment_data_panel, container, false);

        key = getString(R.string.google_maps_key);

        generateRoadButton = dataPanelView.findViewById(R.id.generateRoadButton);
        startLocationEditText = dataPanelView.findViewById(R.id.startLocationEditText);
        endLocationEditText = dataPanelView.findViewById(R.id.endtLocationEditText);
        distanceTextView = dataPanelView.findViewById(R.id.distanceTextView);
        timeTextView = dataPanelView.findViewById(R.id.timeTextView);

        generateRoadButton.setOnClickListener(this);


        return dataPanelView;

    }


    @Override
    public void onClick(View v) {

        distanceTextView.setText(getString(R.string.distance_text_view_tex));
        timeTextView.setText(getString(R.string.time_text_view_text));
        thread = new Thread(this);

        startPoint = startLocationEditText.getText().toString();
        entdPoint = endLocationEditText.getText().toString();
        //TODO lcok buton
        thread.start();

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Map answer = (Map) msg.obj;

            Log.d(MY_LOG, "answer: " + answer);


            Object distance = answer.get("distance");
            Object transitTime = answer.get("transitTime");

            String startPointLat = String.valueOf(answer.get("startPointLat"));
            String startPointLng = String.valueOf(answer.get("startPointLng"));
            String endPointLat = String.valueOf(answer.get("endPointLat"));
            String endPointLng = String.valueOf(answer.get("endPointLng"));


            distanceTextView.setText(getString(R.string.distance_text_view_tex) + distance);
            timeTextView.setText(getString(R.string.time_text_view_text) + transitTime);

            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("startPointLat",  startPointLat);
            intent.putExtra("startPointLng", startPointLng);
            intent.putExtra("endPointLat", endPointLat);
            intent.putExtra("endPointLng", endPointLng);
            getActivity().startActivity(intent);


        }
    };

    @Override
    public void run() {
        ComputationOfARoute(startPoint, entdPoint, key);

    }

    private void ComputationOfARoute(String startPoint, String entdPoint, String key) {
        final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";

        final Map<String, String> params = setParams("false", "ru", "walking", startPoint, entdPoint, key);
        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами

        Log.d(MY_LOG, "ComputationOfARoute url: " + url); // Можем проверить что вернет этот путь в браузере
        requestToWebServer(url);


    }

    @NonNull
    private Map<String, String> setParams(String sensorStatus, String language, String mode, String startPoint, String entdPoint, String key) {
        final Map<String, String> params = Maps.newLinkedHashMap();
        params.put("sensor", sensorStatus);// указывает, исходит ли запрос на геокодирование от устройства с датчиком
        params.put("language", language);// язык данные на котором мы хочем получить
        params.put("mode", mode);// способ перемещения, может быть driving, walking, bicycling
        params.put("origin", startPoint);// адрес или текстовое значение широты и
        params.put("destination", entdPoint);// адрес или текстовое значение широты и долготы долготы конечного пункта маршрута
        params.put("key", key); //ключ проекта
        return params;
    }

    private void requestToWebServer(String url) {
        final JSONObject response;// делаем запрос к вебсервису и получаем от него ответ
        try {
            response = JsonReader.read(url);
            JSONObject routes = response.getJSONArray("routes").getJSONObject(0);
            Log.d(MY_LOG, "routes: " +  routes);
            JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);

            final String distance = legs.getJSONObject("distance").getString("text");
            final String transitTime = legs.getJSONObject("duration").getString("text");

            final String startPointLat = legs.getJSONObject("start_location").getString("lat");
            final String startPointLng = legs.getJSONObject("start_location").getString("lng");

            final String endPointLat = legs.getJSONObject("end_location").getString("lat");
            final String endPointLng = legs.getJSONObject("end_location").getString("lng");


            Log.d(MY_LOG, "ComputationOfARoute distance: " + distance + "\n" + "transitTime: " + transitTime);

            Map<String, String> answer = new LinkedHashMap<>();
            answer.put("distance", distance);
            answer.put("transitTime", transitTime);
            answer.put("startPointLat", startPointLat);
            answer.put("startPointLng", startPointLng);
            answer.put("endPointLat", endPointLat);
            answer.put("endPointLng", endPointLng);

            Message message = handler.obtainMessage(0, 0, 0, answer);
            handler.sendMessage(message);

            //TODO close trend
        } catch (IOException | JSONException e) {
            Log.e(MY_LOG, e.toString());
            e.printStackTrace();
        }
    }


}