package myApp;

import javafx.scene.image.Image;

public class ImageHandler {

    static Image getImage(String id){
        String imageUrl = ClassLoader.getSystemResource(id).toExternalForm();
        return new Image(imageUrl);
    }
}
