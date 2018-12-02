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
        endLocationEditText = dataPanelView.findViewById(R.id.endLocationEditText);
        distanceTextView = dataPanelView.findViewById(R.id.distanceTextView);
        timeTextView = dataPanelView.findViewById(R.id.timeTextView);

        generateRoadButton.setOnClickListener(this);


        return dataPanelView;

    }


    @Override
    public void onClick(View v) {
        Log.d(MY_LOG, "onClick DataPanelFragmentActivity: ");
        thread = new Thread(this);
        distanceTextView.setText(getString(R.string.distance_text_view_tex));
        timeTextView.setText(getString(R.string.time_text_view_text));

        startPoint = startLocationEditText.getText().toString();
        entdPoint = endLocationEditText.getText().toString();


        Log.d(MY_LOG, "startPoint: " + startPoint + " entdPoint: " + entdPoint);
        //TODO lcok buton
        thread.start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Map answer = (Map) msg.obj;

            Log.d(MY_LOG, "answer: " + answer);


            String distance = String.valueOf(answer.get("distance"));
            String transitTime = String.valueOf(answer.get("transitTime"));
            String points = String.valueOf(answer.get("points"));


            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("distance", distance);
            intent.putExtra("transitTime", transitTime);
            intent.putExtra("points", points);
            intent.putExtra("startPoint", startPoint);
            intent.putExtra("entdPoint", entdPoint);
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
        JSONObject response = null;// делаем запрос к вебсервису и получаем от него ответ
        try {
            response = JsonReader.read(url);

            JSONObject routes = response.getJSONArray("routes").getJSONObject(0);
            Log.d(MY_LOG, "routes: " + routes);

            JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);

            final String distance = legs.getJSONObject("distance").getString("text");
            final String transitTime = legs.getJSONObject("duration").getString("text");

            final String points = routes.getJSONObject("overview_polyline").getString("points"); //TODO
            Log.d(MY_LOG, "points: " + points);


            Log.d(MY_LOG, "ComputationOfARoute distance: " + distance + "\n" + "transitTime: " + transitTime);

            Map<String, String> answer = new LinkedHashMap<>();
            answer.put("distance", distance);
            answer.put("transitTime", transitTime);
            answer.put("points", points);

            Message message = handler.obtainMessage(0, 0, 0, answer);
            handler.sendMessage(message);

            //TODO close trend
        } catch (IOException | JSONException e) {


            Log.e(MY_LOG, e.toString());
            e.printStackTrace();
        }
    }


}