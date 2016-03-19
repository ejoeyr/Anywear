package ph.clothesuffle.anywear.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/1/2016.
 */
public class AnywearApplication extends Application{

    /*This class is called first whenever the app starts*/
    @Override
    public void onCreate() {
        super.onCreate();

        /*Create folders at application level*/
        FileUtils.createFolder(Constants.FOLDER_OUTFITS);
        SharedPreferences sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getInt(Constants.PREFS_FIRST_TIME, 0) == 0)
        {
           createFolders();
        }
        sharedPreferences.edit().putInt(Constants.PREFS_FIRST_TIME,1).apply();
    }

    public static void createFolders() {
        /*DEFAULT FOLDERS*/
        FileUtils.createFolder(Constants.FOLDER_SHIRT);
        FileUtils.createFolder(Constants.FOLDER_TROUSERS);
        FileUtils.createFolder(Constants.FOLDER_DRESS);
        FileUtils.createFolder(Constants.FOLDER_ACCESSORIES);
        FileUtils.createFolder(Constants.FOLDER_FOOTWEAR);
    }
}
