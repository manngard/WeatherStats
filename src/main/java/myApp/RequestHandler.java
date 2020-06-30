package myApp;

import com.google.gson.*;
import kotlin.Triple;
import myApp.TemperatureData.WeatherData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RequestHandler {
    OkHttpClient client = new OkHttpClient();
    Map<String, WeatherData> weatherLocations = new HashMap<>();
    Map<String, WeatherData> graphItems = new HashMap<>();
    Map<String, WeatherData> favorites = new HashMap<>();
    Set<Triple<String, String, Number>> history = new HashSet<>();

    String GET(String location) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder();
        httpBuilder.addQueryParameter("q", location);
        httpBuilder.addQueryParameter("cnt", "1");
        httpBuilder.addQueryParameter("appid", "f28077bccf0b9836ababfa545de25285");

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        try (Response response = client.newCall(request).execute()) {
            JsonElement dataResponse = parser.parse(response.body().string());
            String parsedResponse = gson.toJson(dataResponse);
            return parsedResponse;
        } catch (IOException e) {
            return "Fault";
        }
    }

    List<String> objectsToJson(List<?> data) {
        Gson gson = new Gson();
        List<String> jsonArray = new ArrayList<>();
        for (Object object : data) {
            String json = gson.toJson(object);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    void createWeatherDataObject(String location) throws IllegalArgumentException {
        Gson gson = new Gson();
        WeatherData data;
        try {
            String response = GET(location);
            data = gson.fromJson(response, WeatherData.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException();
        }

        weatherLocations.put(data.getCityName(), data);
        updateHistory(data.getCityName());
        cacheDataObjects();
    }

    void cacheDataObjects() {
        List<String> jsonArray = objectsToJson(Arrays.asList(favorites.values().toArray()));
        List<String> jsonArray2 = objectsToJson(new ArrayList<>(history));
        try {
            Path path = Paths.get(App.class.getResource("cache.txt").toURI());
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path, jsonArray, Charset.defaultCharset());

            Path path2 = Paths.get(App.class.getResource("history.txt").toURI());
            Files.newBufferedWriter(path2, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path2, jsonArray2, Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void loadDataObjects() {
        Gson gson = new Gson();
        List<String> jsonArray;
        List<String> jsonArray2;
        try {
            Path path = Paths.get(App.class.getResource("cache.txt").toURI());
            jsonArray = Files.readAllLines(path, Charset.defaultCharset());
            Path path2 = Paths.get(App.class.getResource("history.txt").toURI());
            jsonArray2 = Files.readAllLines(path2, Charset.defaultCharset());
            for (String s : jsonArray) {
                WeatherData data = gson.fromJson(s, WeatherData.class);
                WeatherData updatedData = gson.fromJson(GET(data.getCityName()), WeatherData.class);
                weatherLocations.put(updatedData.getCityName(), updatedData);
                favorites.put(updatedData.getCityName(), updatedData);
            }
            for (String s2 : jsonArray2) {
                if (s2.isEmpty()) break;
                Triple<String, String, Number> data2 = gson.fromJson(s2, Triple.class);
                history.add(new Triple<>(data2.getFirst(), data2.getSecond(), data2.getThird()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Collection<WeatherData> getWeatherLocations() {
        return weatherLocations.values();
    }

    public Collection<WeatherData> getFavorites() {
        return favorites.values();
    }

    public Collection<myApp.Pair<String, Number>> getHistory(String city) {
        List<Pair<String, Number>> cityHistory = new ArrayList<>();
        for (Triple<String, String, Number> t : history) {
            if (city.equals(t.getFirst())) {
                cityHistory.add(new Pair<>(t.getSecond(), t.getThird()));
            }
        }
        return cityHistory;
    }

    public void removeWeatherDataObject(WeatherData data) {
        weatherLocations.remove(data.getCityName());
        removeFavorite(data.getCityName());
    }

    public void removeFavorite(String city) {
        favorites.remove(city);
        cacheDataObjects();
    }

    public void addFavorite(String city) {
        favorites.put(city, weatherLocations.get(city));
        cacheDataObjects();
    }

    public void addGraphItem(String city) {
        graphItems.put(city, weatherLocations.get(city));
    }

    public void cleargraphItems() {
        graphItems.clear();
    }


    public void updateHistory(String city) throws IllegalArgumentException,IllegalStateException {

        if (graphItems.containsKey(city)) {
            throw new IllegalStateException();
        }

        WeatherData data = weatherLocations.get(city);

        history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));
        cacheDataObjects();
    }
}
