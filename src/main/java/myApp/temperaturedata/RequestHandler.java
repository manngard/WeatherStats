package myApp.temperaturedata;

import com.google.gson.*;
import kotlin.Triple;
import myApp.Pair;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class RequestHandler {
    private final OkHttpClient client = new OkHttpClient();
    private final Map<String, WeatherData> weatherLocations = new HashMap<>();
    private final Map<String, WeatherData> graphItems = new HashMap<>();
    private final Map<String, WeatherData> favorites = new HashMap<>();
    private final Map<String, String> aliasNames = new HashMap<>();
    private final Set<Triple<String, String, Number>> history = new HashSet<>();

    private String GET(String location) {
        String parsedResponse = null;

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
            parsedResponse = gson.toJson(dataResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedResponse;
    }

    private List<String> objectsToJson(List<Object> data) {
        Gson gson = new Gson();
        List<String> jsonArray = new ArrayList<>();
        for (Object object : data) {
            String json = gson.toJson(object);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    public void createWeatherDataObject(String location, boolean inGraph) throws IllegalArgumentException, IllegalStateException {
        Gson gson = new Gson();
        String city = aliasNames.getOrDefault(location, location);
        WeatherData data = fetchNewData(city);

        if (!inGraph) {
            weatherLocations.put(data.getCityName(), data);
        }

        if (!data.getCityName().equals(city)) {
            updateHistory(city, data.getCityName());
        } else {
            updateHistory(data.getCityName());
        }
        cacheDataObjects();
    }

    private WeatherData fetchNewData(String location) throws IllegalArgumentException {
        Gson gson = new Gson();
        WeatherData data;
        try {
            String response = GET(location);
            data = gson.fromJson(response, WeatherData.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException();
        }
        return data;
    }

    private void cacheDataObjects() {
        DataStorer.saveAsTextfile(objectsToJson(Arrays.asList(favorites.values().toArray())), "cache.txt");
        DataStorer.saveAsTextfile(objectsToJson(new ArrayList<>(history)), "history.txt");
    }

    public void loadDataObjects() {
        Gson gson = new Gson();

        for (String s : DataStorer.loadFromTextfile("cache.txt")) {
            WeatherData data = gson.fromJson(s, WeatherData.class);
            WeatherData updatedData = gson.fromJson(GET(data.getCityName()), WeatherData.class);
            weatherLocations.put(updatedData.getCityName(), updatedData);
            favorites.put(updatedData.getCityName(), updatedData);
        }
        for (String s2 : DataStorer.loadFromTextfile("history.txt")) {
            if (s2.isEmpty()) break;
            Triple<String, String, Number> data2 = gson.fromJson(s2, Triple.class);
            history.add(new Triple<>(data2.getFirst(), data2.getSecond(), data2.getThird()));
        }
    }

    public Collection<WeatherData> getWeatherLocations() {
        return weatherLocations.values();
    }

    public Collection<WeatherData> getFavorites() {
        return favorites.values();
    }

    public Collection<Pair<String, Number>> getHistory(String city) {
        List<Pair<String, Number>> cityHistory = new ArrayList<>();
        List<String> aliases = new ArrayList<>();
        aliases.add(city);
        aliases.add(aliasNames.get(city));

        for (Triple<String, String, Number> t : history) {
            if (aliases.contains(t.getFirst())) {
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


    private void updateHistory(String city) throws IllegalStateException {
        if (graphItems.containsKey(city)) {
            throw new IllegalStateException();
        }

        WeatherData data = fetchNewData(city);
        history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));

    }

    private void updateHistory(String city, String alias) throws IllegalArgumentException, IllegalStateException {
        if (graphItems.containsKey(city) || graphItems.containsKey(alias)) {
            throw new IllegalStateException();
        }

        WeatherData data = fetchNewData(city);
        WeatherData aliasdata = fetchNewData(city);

        if (data == null && aliasdata == null) {
            throw new IllegalArgumentException();
        } else {
            if (data != null) {
                history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));
            }
            if (aliasdata != null) {
                history.add(new Triple<>(city, aliasdata.getDate(), Integer.valueOf(aliasdata.getTemperature())));
            }
            aliasNames.put(alias, city);
        }
    }
}
