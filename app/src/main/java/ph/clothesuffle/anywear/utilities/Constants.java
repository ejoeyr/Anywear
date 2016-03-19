package ph.clothesuffle.anywear.utilities;

import android.os.Environment;

/**
 * Created by joeyramirez on 2/29/2016.
 */

public class Constants {

    /*FONTS*/
    public static final String FONT_ROOT = "fonts/";
    public static final String FONT_QUESTRIAL = FONT_ROOT+"questrial.ttf";
    /*KEYS*/
    public static final String FRAGMENT_KEY_FOLDER = "folder";
    /*ROOT FOLDER*/
    public static final String FOLDER_ROOT = ".Anywear/";
    /*SPECIAL FOLDER*/
    public static final String FOLDER_OUTFITS = "Outfits";
    /*DEFAULT FOLDERS*/
    public static final String FOLDER_DRESS = "Dresses";
    public static final String FOLDER_SHIRT= "Shirts";
    public static final String FOLDER_ACCESSORIES = "Accessories";
    public static final String FOLDER_TROUSERS = "Trousers";
    public static final String FOLDER_FOOTWEAR = "Footwears";
    /*Activity keys*/
    public static final String ACTIVITY_KEY_IMAGES = "Images" ;
    public static final String ACTIVITY_KEY_CURRENT_FOLDER = "folder";
    public static final String ACTIVITY_KEY_SHOW_LOADING = "load screen";
    public static final String ACTIVITY_KEY_OUTFIT_NAME_OR_FOLDER = "ootd name";
    public static final String ACTIVITY_KEY_SELECTED  = "Selected";
    public static final String ACTIVITY_KEY_SHOW_BUTTON = "Show button";
    /*Preference keys*/
    public static final String PREFS_FIRST_TIME ="first opened" ;
    /*Extras*/
    public static final int ADAPTER_VIBRATION = 300;
    public static final int INTENT_TAKE_PICTURE = 1;
    public static final int INTENT_GALLERY_PICK = 2;

}
