package myApp.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 *  The specification for a APIHandler class,
 *  which is responsible for sending requests to the OpenWeatherMap API and providing the JSON-reply
 */

public class APIHandler {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     *
     * @param location - The city name to get weather forecast for
     * @return - The String API-response in JSON format describing WeatherData objects
     */

    private String GET(String location) {
        String parsedResponse = null;

        HttpUrl.Builder httpBuilder = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder();
        httpBuilder.addQueryParameter("q", location);
        httpBuilder.addQueryParameter("cnt", "1");
        httpBuilder.addQueryParameter("appid", "f28077bccf0b9836ababfa545de25285");

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        JsonParser parser = new JsonParser();

        try (Response response = client.newCall(request).execute()) {
            JsonElement dataResponse = parser.parse(response.body().string());
            parsedResponse = gson.toJson(dataResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedResponse;
    }

    /**
     *
     * @param location - The city name to fetch new WeatherData for
     * @return - The String API-response in JSON format describing WeatherData objects
     * @throws IllegalArgumentException if API could not find a city matching location
     */

    String fetchNewData(String location) throws IllegalArgumentException {
        String response;
        try {
            response = GET(location);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException();
        }
        return response;
    }
}
