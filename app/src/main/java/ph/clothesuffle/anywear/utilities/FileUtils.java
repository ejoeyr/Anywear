package ph.clothesuffle.anywear.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ph.clothesuffle.anywear.models.Folder;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.models.Outfit;

/**
 * Created by joeyramirez on 2/29/2016.
 */
public class FileUtils {


    /*FUNCTIONS*/
//    createFolder
//    checkFolder
//    deleteFolder
//    getAllImagesIn
//    getMimeType

    public static boolean createFolder(String folderName) {

        /*USER MKDIRS TO CREATE DIRECTORIES NOT ONLY THE folderName*/
        File file = new File(Environment.getExternalStorageDirectory(), Constants.FOLDER_ROOT + folderName);

        if (file.exists()) {
            return false;
        } else {
            return file.mkdirs();
        }

    }

    public static boolean renameFolder(String old, String newName) {

        File sdcard = Environment.getExternalStorageDirectory();
        File from = new File(sdcard + "/" + Constants.FOLDER_ROOT, old);
        File to = new File(sdcard + "/" + Constants.FOLDER_ROOT, newName);

        return from.renameTo(to);


    }

    public static boolean checkFolder(String folderName) {

        return new File(folderName).exists();
    }

    public static boolean deleteFolder(String fileName) {
        File cur = new File(fileName);
        /*Get the files inside the dir*/
        if (!cur.getName().equals(Constants.FOLDER_OUTFITS)) {
            File[] files = cur.listFiles();
            /*Delete it's file first*/
            if (files != null && files.length > 0) {
                for (File file : files) {
                    file.delete();
                }
            }
            /*Delete dirs*/
            cur.delete();
        } else {

            return false;
        }

        return true;

    }

    public static ArrayList<Image> getAllImagesIn(String folder) {
        /*Instantiate to avoid null*/
        ArrayList<Image> imageArrayList = new ArrayList<>();
        File[] files = new File(Environment.getExternalStorageDirectory() + "/" + Constants.FOLDER_ROOT, folder).listFiles();

        if (files != null && files.length > 0) {
            for (File image : files) {
                if (isImage(image.getAbsolutePath())) {
                    imageArrayList.add(new Image(image.getAbsolutePath(), convertSize(image.length())));
                }
            }
        }
        return imageArrayList;
    }

    public static boolean isImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (options.outWidth != -1 && options.outHeight != -1) {
            return true;
        } else {
            return false;
        }
    }

    public static String convertSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static ArrayList<Folder> getFolders(String folder) {

        ArrayList<Folder> folders = new ArrayList<>();
        /*Get the path to the env dir then look for the folder*/
        File[] files = new File(Environment.getExternalStorageDirectory(), folder).listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {

                if (f.isDirectory()) {

                    folders.add(new Folder(f.getAbsolutePath(),
                            f.getName(), getFolderSize(f),
                            f.listFiles() != null ? f.listFiles().length : 0,
                            convertDate(f.lastModified())));
                }


            }
        }
        return folders;
    }

    private static String getFolderSize(File f) {
        long currentSize =  f.length();
        if (f.listFiles() != null) {
            for (File current : f.listFiles())
            {
                currentSize += current.length();
            }
        }
        return convertSize(currentSize);
    }

    private static String convertDate(long l) {
        /*MONTH DAY YEAR*/
        return new SimpleDateFormat("MM/dd/yyyy").format(l);
    }

    public static void copyFile(ArrayList<String> photos, String location) {

        for(int x = 0 ; x < photos.size(); x++)
        {

            try {
                copyNow(new File(photos.get(x)),createNewImage(location));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static File createNewImage(String actionBarSubtitle) {

       return new File(Environment.getExternalStorageDirectory() + "/" +
                        Constants.FOLDER_ROOT + actionBarSubtitle,
                "IMG-" + System.currentTimeMillis() + "-AW.jpg");
    }
    private static void copyNow(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    public static ArrayList<Outfit> getAllOutfits() {


        ArrayList<Outfit> outfitArrayList = new ArrayList<>();
        ArrayList<Folder> folders = getFolders(Constants.FOLDER_ROOT+Constants.FOLDER_OUTFITS);
        for(int x = 0 ; x < folders.size(); x++ )
        {
            /*Get the current folder*/
            Folder f = folders.get(x);
            /*Init a new outfit object*/
            Outfit outfit = new Outfit();
            outfit.setFolderName(f.getFileName());
            outfit.setFolderPath(f.getFullPath());
            ArrayList<String> pathStrings = new ArrayList<>();
            File [] fileList = new File(f.getFullPath()).listFiles();

            if(fileList != null)
            {
                for(File img:fileList)
                {
                    String path = img.getAbsolutePath();
                    if(isImage(path))
                    {
                        pathStrings.add(path);
                    }

                }

            }
            outfit.setPhotoPaths(pathStrings);
            outfitArrayList.add(outfit);
        }


        return outfitArrayList;
    }
}
