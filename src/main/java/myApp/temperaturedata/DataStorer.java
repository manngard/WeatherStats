package myApp.temperaturedata;

import myApp.App;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataStorer {

    static List <String> loadFromTextfile(String filename) {
        List <String> jsonArray = new ArrayList<>();
        try{
            Path path = Paths.get(App.class.getResource(filename).toURI());
            jsonArray = Files.readAllLines(path, Charset.defaultCharset());
        }
        catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }

        return jsonArray;
    }

    static void saveAsTextfile(List <String> jsonArray, String filename) {
        try {
            Path path = Paths.get(App.class.getResource(filename).toURI());
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path, jsonArray, Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
