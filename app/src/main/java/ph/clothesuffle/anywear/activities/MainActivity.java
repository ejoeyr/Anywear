package ph.clothesuffle.anywear.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.adapters.FolderRecyclerAdapter;
import ph.clothesuffle.anywear.custom.AddFolderDialog;
import ph.clothesuffle.anywear.fragments.ImagesFragment;
import ph.clothesuffle.anywear.fragments.OutfitsFragment;
import ph.clothesuffle.anywear.interfaces.FragmentInterface;
import ph.clothesuffle.anywear.interfaces.NavigationDrawerInterface;
import ph.clothesuffle.anywear.models.Folder;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*Created by Joey Ramirez */

public class MainActivity extends AppCompatActivity implements NavigationDrawerInterface, FragmentInterface {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FolderRecyclerAdapter folderRecyclerAdapter;
    private ArrayList<Folder> folderArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOverAllFonts();
        start();

    }

    private void start() {


        setContentView(R.layout.activity_main);
        setToolBar();
        setDrawerLayout();
        setRecyclerViewNavigationDrawer();
    }


    private void renameActivitySubtitle(String fName) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(fName);
        }
    }

    private void setRecyclerViewNavigationDrawer() {
        getFolders();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_folders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        folderRecyclerAdapter = new FolderRecyclerAdapter(folderArrayList, this, this);
        recyclerView.setAdapter(folderRecyclerAdapter);

    }

    private void getFolders() {

        folderArrayList = FileUtils.getFolders(Constants.FOLDER_ROOT);

    }

    private void setDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close) {

                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);

                    }

                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);


                    }
                };

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void setToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void setOverAllFonts() {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(Constants.FONT_QUESTRIAL)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void closeDrawer(String folder) {
        /*Refresh fragment with the folder*/
        changeFragmentAndSubtitle(folder);
        drawerLayout.closeDrawers();
    }

    /* @UiThread*/
    @Override
    public void reloadAdapter() {

        getFolders();
        folderRecyclerAdapter.setList(folderArrayList);
    }

    @Override
    public void changeFragment() {

        changeFragmentAndSubtitle(Constants.FOLDER_OUTFITS);

    }


    private void changeFragmentAndSubtitle(String folder) {

        sharedPreferences.edit().putString(Constants.ACTIVITY_KEY_CURRENT_FOLDER, folder).apply();

        if (folder.equals(Constants.FOLDER_OUTFITS)) {
            changeFragmentNow(OutfitsFragment.newInstance());
        } else {
            changeFragmentNow(ImagesFragment.newInstance(folder, this));

        }


        renameActivitySubtitle(folder);
    }

    private void changeFragmentNow(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relative_main, fragment)
                .commit();
    }

    public void buttonHandler(View view) {

        int id = view.getId();
        if (id == R.id.fab) {
            addPhotoOrFavourites();
        } else if (id == R.id.button_add) {
            showFolderDialog();
        } else if (id == R.id.button_shuffle) {
            showShuffleDialog();
        } else if (id == R.id.button_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }


    }


    public void showShuffleDialog() {
        /*Get folders first*/
        getFolders();
        /*Create a List that will store the paths*/
        final ArrayList<String> strings = new ArrayList<>();

        for (int x = 0; x < folderArrayList.size(); x++) {


            strings.add(folderArrayList.get(x).getFileName());

        }
        strings.remove(0);

        final String[] data = new String[strings.size()];
        strings.toArray(data);
        strings.clear();
        /*final ArrayList<String> selected = new ArrayList<>(data.length);*/
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setMultiChoiceItems(data, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            strings.add(data[which]);
                        } else {
                            strings.remove((String) data[which]);
                        }
                    }

                })
                .setTitle(R.string.string_select_sources)
                .setNegativeButton(R.string.string_cancel, null)
                .setPositiveButton(R.string.string_shuffle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strings.trimToSize();
                        if (strings.size() < 1) {
                            makeText(getString(R.string.string_select_atleast_one));
                        } else {
                            startShuffleActivity(strings);
                        }
                    }
                }).create().show();
    }

    private void startShuffleActivity(ArrayList<String> paths) {

        new ShuffleTask().execute(paths);
    }

    private void makeText(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*ROOT FOLDER*/
        FileUtils.createFolder(Constants.FOLDER_OUTFITS);
        changeFragmentAndSubtitle(sharedPreferences.
                getString(Constants.ACTIVITY_KEY_CURRENT_FOLDER, Constants.FOLDER_OUTFITS));
        folderRecyclerAdapter.loadRename();

    }


    private void showFolderDialog() {

        AddFolderDialog.newInstance(this).show(getSupportFragmentManager(), "");

    }

    private void addPhotoOrFavourites() {

        /*Favourites folder*/
        if (getCurrentSubTitleFolder().equals(Constants.FOLDER_OUTFITS)) {
            showShuffleDialog();
        }
        /*Normal folder*/
        else {
            showAddPhotosBottomSheet();
        }
    }

    private void showAddPhotosBottomSheet() {

        new BottomSheet.Builder(this)
                .title("Select image from")
                .sheet(R.menu.menu_bottom_sheet)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_camera:
                                openCam();
                                break;
                            case R.id.action_gallery:
                                selectFromGallery();
                                break;
                        }
                    }
                }).show();

    }

    private void selectFromGallery() {
        PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
        intent.setPhotoCount(10);
        intent.setShowCamera(false);
        startActivityForResult(intent, Constants.INTENT_GALLERY_PICK);

    }

    private void openCam() {
        final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.createNewImage(getCurrentSubTitleFolder())));
        startActivityForResult(intent, Constants.INTENT_TAKE_PICTURE);
    }


    public String getCurrentSubTitleFolder() {
        if (getSupportActionBar() != null) {
            return getSupportActionBar().getSubtitle() + "";
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.INTENT_GALLERY_PICK) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                FileUtils.copyFile(photos, sharedPreferences.getString(Constants.ACTIVITY_KEY_CURRENT_FOLDER, ""));

            }

        }

    }

    @Override
    public void IAddPhotosBottomSheet() {
        addPhotoOrFavourites();
    }

    @Override
    public void IShowShuffleDialog() {
        showShuffleDialog();
    }


    private class ShuffleTask extends AsyncTask<ArrayList<String>, Integer, ArrayList<Image>> {

        @SafeVarargs
        @Override
        protected final ArrayList<Image> doInBackground(ArrayList<String>... params) {
            ArrayList<String> paths = params[0];
            ArrayList<Image> imageArrayList = new ArrayList<>();
            for (int x = 0; x < paths.size(); x++) {
                File[] files = new File(Environment.getExternalStorageDirectory(), Constants.FOLDER_ROOT + paths.get(x)).listFiles();
                if (files != null && files.length >= 1) {
                    Image image = new Image();
                    int randomNum = (int) (Math.random() * files.length);
                    image.setFilePath(files[randomNum].getAbsolutePath());
                    if (FileUtils.isImage(image.getFilePath())) {
                        imageArrayList.add(image);
                    }
                }
            }
            return imageArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> images) {
            super.onPostExecute(images);
            if (images.size() >= 1) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra(Constants.ACTIVITY_KEY_IMAGES, images);
                intent.putExtra(Constants.ACTIVITY_KEY_SHOW_BUTTON, true);
                intent.putExtra(Constants.ACTIVITY_KEY_SHOW_LOADING, true);
                MainActivity.this.startActivity(intent);
            } else {
                makeText(getString(R.string.string_empty_folders));
            }

        }
    }
}
