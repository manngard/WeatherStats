package myApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.util.Pair;
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
    Map<String,WeatherData> weatherLocations = new HashMap<>();
    Map<String,WeatherData> favorites = new HashMap<>();
    Set<Triple<String,String,Number>> history = new TreeSet<>(new Comparator<Triple>() {
        @Override
        public int compare(Triple t1, Triple t2) {
            System.out.println(t1.getFirst().toString().compareTo(t2.getFirst().toString()));
            return t1.getFirst().toString().compareTo(t2.getFirst().toString())
                    + t1.getSecond().toString().compareTo(t2.getSecond().toString());
        }
    });
    /*File cache = null;*/


    String GET(String url, String location){
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("q", location);
        httpBuilder.addQueryParameter("cnt", "1");
        httpBuilder.addQueryParameter("appid","f28077bccf0b9836ababfa545de25285");

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        try (Response response = client.newCall(request).execute()) {
            JsonElement dataResponse = parser.parse(response.body().string());
            String parsedResponse = gson.toJson(dataResponse);
            return parsedResponse;
        }
        catch (IOException e){
            return "Fault";
        }
    }

    List <String> objectsToJson(List <?> data){
        Gson gson = new Gson();
        List<String> jsonArray = new ArrayList<>();
        for (Object object : data){
            String json = gson.toJson(object);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    void createWeatherDataObject(String url, String location) throws Exception{
        Gson gson = new Gson();
        String response = GET(url,location);
        WeatherData data = gson.fromJson(response, WeatherData.class);
        if (weatherLocations.containsKey(data.getCityName())) {
            throw new Exception();
        }
        weatherLocations.put(data.getCityName(),data);
        updateHistory(data.getCityName());
        cacheDataObjects();
    }
    void cacheDataObjects() {
        List <String> jsonArray = objectsToJson(Arrays.asList(favorites.values().toArray()));
        List <String> jsonArray2 = objectsToJson(new ArrayList<>(history));
        try {
            Path path = Paths.get(App.class.getResource("cache.txt").toURI());
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path,jsonArray,Charset.defaultCharset());

            Path path2 = Paths.get(App.class.getResource("history.txt").toURI());
            Files.newBufferedWriter(path2, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path2,jsonArray2,Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void loadDataObjects() {
        Gson gson = new Gson();
        List <String> jsonArray;
        List <String> jsonArray2;
        try {
            Path path = Paths.get(App.class.getResource("cache.txt").toURI());
            jsonArray = Files.readAllLines(path,Charset.defaultCharset());
            Path path2 = Paths.get(App.class.getResource("history.txt").toURI());
            jsonArray2 = Files.readAllLines(path2,Charset.defaultCharset());
            for (String s : jsonArray){
                WeatherData data = gson.fromJson(s, WeatherData.class);
                weatherLocations.put(data.getCityName(),data);
                favorites.put(data.getCityName(),data);
            }
            for (String s2 : jsonArray2){
                if (s2.isEmpty()) break;
                Triple<String,String,Number> data2 = gson.fromJson(s2, Triple.class);
                history.add(new Triple<>(data2.getFirst(),data2.getSecond(),data2.getThird()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Collection <WeatherData> getWeatherLocations() {
        return weatherLocations.values();
    }

    public Collection<WeatherData> getFavorites() {
        return favorites.values();
    }

    public Collection<Pair<String,Number>> getHistory(String city) {
        List <Pair<String,Number>> cityHistory = new ArrayList<>();
        for (Triple <String,String,Number> t : history){
            if (city.equals(t.getFirst())){
                cityHistory.add(new Pair<>(t.getSecond(),t.getThird()));
            }
        }
        return cityHistory;
    }

    public void removeWeatherDataObject(WeatherData data){
        weatherLocations.remove(data.getCityName());
        removeFavorite(data.getCityName());
    }

    public void removeFavorite(String city){
        favorites.remove(city);
        cacheDataObjects();
    }

    public void addFavorite(String city){
        favorites.put(city,weatherLocations.get(city));
        cacheDataObjects();
    }

    public void updateHistory(String city){
        WeatherData data = weatherLocations.get(city);
        history.add(new Triple<>(city,data.getDate(),Integer.valueOf(data.getTemperature())));
        cacheDataObjects();
    }
}
