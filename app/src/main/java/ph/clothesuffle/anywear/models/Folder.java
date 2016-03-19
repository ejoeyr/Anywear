package ph.clothesuffle.anywear.models;

import java.io.File;
import java.io.Serializable;

/**
 * Created by joeyramirez on 3/1/2016.
 */
public class Folder implements Serializable {

    private String mFullPath;
    private String mFileName;
    private String mFileSize;
    private int mFolderItems;
    private String folderDate;

    public Folder() {

    }

    public Folder(String folderPath, String folderName,String fileSize,int items,String fDate) {
        mFullPath = folderPath;
        mFileName = folderName;
        mFileSize = fileSize;
        mFolderItems = items;
        folderDate = fDate;

    }

    public String getFileName() {

        return mFileName;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public void setFullPath(String path) {

        mFullPath = path;
    }

    public void setFileName(String fileName) {

        mFileName = fileName;

    }

    public void setFileSize(String size) {

        mFileSize = size;

    }

    public void setFolderItems(int items) {

        mFolderItems = items;

    }

    public int getFolderItems() {

        return mFolderItems;
    }

    public String getFileSize() {


        return mFileSize;
    }


    public String getFolderDate() {
        return folderDate;
    }

    public void setFolderDate(String date)
    {


        folderDate = date;

    }
}
