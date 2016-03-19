package ph.clothesuffle.anywear.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by joeyramirez on 2/29/2016.
 */
public class Image implements Serializable {

    private String filePath;
    private String fileSize;
    private int position;

    public Image(String filePath, String fileSize) {
        this.filePath = filePath;
        this.fileSize = fileSize;

    }
    public Image() {
    }

    public Image setPosition(int pos)
    {

        this.position = pos;


        return this;
    }
    public int getPosition()
    {

        return  position;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


}
