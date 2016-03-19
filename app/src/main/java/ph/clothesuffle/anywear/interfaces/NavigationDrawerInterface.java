package ph.clothesuffle.anywear.interfaces;

import android.view.View;

import ph.clothesuffle.anywear.models.Folder;

/**
 * Created by joeyramirez on 2/29/2016.
 */
public interface NavigationDrawerInterface {


    void closeDrawer(String folder);
    void reloadAdapter();
    void changeFragment();

}
