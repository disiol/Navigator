package denisimus_it.net.navigator;

import android.annotation.SuppressLint;
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
    private EditText startLocationEditText, endtLocationEditText;
    private TextView distanceTextView, timeTextView;

    private String startPoint;
    private String entdPoint;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dataPanelView = inflater.inflate(R.layout.fragment_data_panel, container, false);

        key = getString(R.string.google_maps_key);
        thread = new Thread(this);

        generateRoadButton = dataPanelView.findViewById(R.id.generateRoadButton);
        startLocationEditText = dataPanelView.findViewById(R.id.startLocationEditText);
        endtLocationEditText = dataPanelView.findViewById(R.id.endtLocationEditText);
        distanceTextView = dataPanelView.findViewById(R.id.distanceTextView);
        timeTextView = dataPanelView.findViewById(R.id.timeTextView);

        generateRoadButton.setOnClickListener(this);


        return dataPanelView;

    }


    @Override
    public void onClick(View v) {
        startPoint = startLocationEditText.getText().toString();
        entdPoint = endtLocationEditText.getText().toString();
        //TODO dcok buton
        if (!thread.isAlive()) {
            thread.start();
        } else if (thread.isAlive()) {
            thread.interrupt();
        }


    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Map answer = (Map) msg.obj;

            Log.d(MY_LOG, "answer: " + answer);


            Object distance = answer.get("distance");
            Object transitTime = answer.get("transitTime");
            distanceTextView.setText(getString(R.string.distance_text_view_tex) + distance);
            timeTextView.setText(getString(R.string.time_text_view_text) + transitTime);


        }
    };

    @Override
    public void run() {

        ComputationOfARoute(startPoint, entdPoint, key);
    }

    public void ComputationOfARoute(String startPoint, String entdPoint, String key) {
        final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";

        final Map<String, String> params = setParams("false", "ru", "walking", startPoint, entdPoint, key);

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

            Map<String, String> answer = new LinkedHashMap<>();
            answer.put("distance", distance);
            answer.put("transitTime", transitTime);

            Message message = handler.obtainMessage(0, 0, 0, answer);
            handler.sendMessage(message);

            //TODO close trend
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


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


}