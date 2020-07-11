package myApp.model;

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

/**
 * The specification for a DataStorer class responsible for reading and writing JSON to and from .txt files
 */

class DataStorer {

    /**
     *
     * @param filename - The name of the .txt file to read
     * @return - The contents of the file as a JSONarray
     */
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

    /**
     *
     * @param jsonArray - The JSONarray to be saved
     * @param filename - The .txt file the JSONarray will be saved in
     */

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
