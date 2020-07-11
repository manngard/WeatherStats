package myApp.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class JSONParser <T> {
    private final Gson gson = new Gson();

    java.util.List<String> objectsToJson(java.util.List<Object> data) {
        List<String> jsonArray = new ArrayList<>();
        for (Object object : data) {
            String json = gson.toJson(object);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    T objectFromJson(String jsonObject, Class<T> objecttype){
        T object = gson.fromJson(jsonObject, objecttype);
        return object;
    }
}
