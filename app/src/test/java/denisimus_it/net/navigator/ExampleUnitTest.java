package denisimus_it.net.navigator;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import denisimus_it.net.navigator.client.JsonReader;

import static junit.framework.TestCase.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private JsonReader jsonReader = new JsonReader();

    private String key;
    private String uri;

    @Test
    public void testKey() throws Exception {
        String actual = "";
        //before
        key = "AIzaSyAup7ejWeQ8B9jjKIBD5ynAou3uYl7EqE4";
        uri = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&language=ru&mode=walking&origin=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%2C+%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D1%83%D0%BB%D0%B8%D1%86%D0%B0+%D0%9F%D0%BE%D0%BA%D0%BB%D0%BE%D0%BD%D0%BD%D0%B0%D1%8F%2C+12&destination=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%2C+%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D1%8F+%D0%BC%D0%B5%D1%82%D1%80%D0%BE+%D0%9F%D0%B0%D1%80%D0%BA+%D0%9F%D0%BE%D0%B1%D0%B5%D0%B4%D1%8B&key=" + key;


        final JSONObject response;
        try {
            response = JsonReader.read(uri);

            // как правило наиболее подходящий ответ первый и данные о координатах можно получить по пути
            // //results[0]/geometry/location/lng и //results[0]/geometry/location/lat
            JSONObject location = response.getJSONArray("routes").getJSONObject(0);
            //вношу данные для выдоча мршрута
            actual = location.toString();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //then
        String excepted = "{\n" +
                "   \"destination_addresses\" : [\n" +
                "      \"Парк Победы, Кутузовский просп., Москва, Россия, 121170\",\n" +
                "      \"Студгородок, Москва, Россия, 121165\"\n" +
                "   ],\n" +
                "   \"origin_addresses\" : [ \"Поклонная ул., 12, Москва, Россия, 121170\" ],\n" +
                "   \"rows\" : [\n" +
                "      {\n" +
                "         \"elements\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"0,8 км\",\n" +
                "                  \"value\" : 833\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"10 мин.\",\n" +
                "                  \"value\" : 623\n" +
                "               },\n" +
                "               \"status\" : \"OK\"\n" +
                "            },\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"1,1 км\",\n" +
                "                  \"value\" : 1056\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"14 мин.\",\n" +
                "                  \"value\" : 822\n" +
                "               },\n" +
                "               \"status\" : \"OK\"\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}\n";






        System.out.println(actual);

        assertEquals(excepted, actual);

    }
}