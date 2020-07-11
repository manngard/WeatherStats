package myApp;

import javafx.scene.image.Image;

/**
 *  The specification of a ImageHandler class
 */

public class ImageHandler {

    /**
     * @param id - The name of jpg to be loaded
     * @return - The resource Image associated with the given id
     */
    static Image getImage(String id){
        String imageUrl = ClassLoader.getSystemResource("myApp/"+id).toExternalForm();
        return new Image(imageUrl);
    }
}
