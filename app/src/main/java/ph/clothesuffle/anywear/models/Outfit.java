package ph.clothesuffle.anywear.models;

import java.util.ArrayList;

/**
 * Created by joeyramirez on 3/13/2016.
 */
public class Outfit {


    /*Fot thumbnail*/
    private ArrayList<String> photoPaths;
    private String folderName;
    private String folderPath;


    public Outfit(ArrayList<String> photoPaths, String folderName) {
        this.photoPaths = photoPaths;
        this.folderName = folderName;
    }


    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setFolderPath(String path) {
        folderPath = path;
    }

    public String getFolderPath() {

        return folderPath;
    }

    public Outfit() {

    }
}
