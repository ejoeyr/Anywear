package ph.clothesuffle.anywear.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.adapters.ImagesRecyclerAdapter;
import ph.clothesuffle.anywear.interfaces.FragmentInterface;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 2/29/2016.
 */
public class ImagesFragment extends Fragment implements ImagesRecyclerAdapter.ClickListener {

    /*Declare global variables so it's accessible through out the class*/
    private View view_RootView;
    private RecyclerView recyclerView_images;
    private LinearLayout linearLayout_empty;
    private ArrayList<Image> imageArrayList;
    private TextView textView_empty;
    private String currentFolder;
    private ImagesRecyclerAdapter imagesRecyclerAdapter;

    /*ActionMode*/
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private FragmentInterface anInterface;
    private TextView textView_add;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view_RootView = inflater.inflate(R.layout.fragment_fragments, container, false);
        setHasOptionsMenu(true);
        /*Casting*/
        setUpViews();
        /*Call this first */
        getImages();
        /*Then this so the data is already there*/
        setUpAdapter();


        return view_RootView;
    }

    private void setUpAdapter() {

        imagesRecyclerAdapter = new ImagesRecyclerAdapter(getActivity(), imageArrayList, this);
        ScaleInAnimationAdapter slideInBottomAnimationAdapter = new ScaleInAnimationAdapter((imagesRecyclerAdapter));
        slideInBottomAnimationAdapter.setFirstOnly(true);
        recyclerView_images.setAdapter(slideInBottomAnimationAdapter);
    }

    private void setUpViews() {

        recyclerView_images = (RecyclerView) view_RootView.findViewById(R.id.recyclerView_images);
     /*   recyclerView_images.setLayoutManager(new GridLayoutManager(getActivity(), 3));*/
        recyclerView_images.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView_images.setHasFixedSize(true);
        linearLayout_empty = (LinearLayout) view_RootView.findViewById(R.id.linearLayout_empty);
        textView_empty = (TextView) view_RootView.findViewById(R.id.textView_empty);
        textView_add = (TextView) view_RootView.findViewById(R.id.textView_apparel);

        currentFolder = getArguments().getString(Constants.FRAGMENT_KEY_FOLDER);
    }

    private void getImages() {

        imageArrayList = FileUtils.getAllImagesIn(currentFolder);
    }

    @Override
    public void onResume() {
        super.onResume();
        imagesRecyclerAdapter.getList(currentFolder);
        checkEmptyView();
    }

    private void checkEmptyView() {
        getImages();
        if (imageArrayList.size() < 1) {
            linearLayout_empty.setVisibility(View.VISIBLE);
            textView_empty.setText("NO \n" + new File(currentFolder).getName().toUpperCase() + "\nFOUND");
            textView_add.setText(String.format("(Tap the circle button to add %s)", currentFolder));
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                showBottomSheet();
                break;
            case R.id.action_delete:
                openCAB();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCAB() {

        if (imageArrayList.isEmpty()) {
            Toast.makeText(getActivity(), "No items to delete", Toast.LENGTH_SHORT).show();
        } else {
            if (actionMode == null) {
                actionMode = getActivity().startActionMode(actionModeCallback);
            }
            toggleSelection(0);
        }
    }

    private void showBottomSheet() {

        if (currentFolder.equals(Constants.FOLDER_OUTFITS)) {
            anInterface.IShowShuffleDialog();
        } else {
            anInterface.IAddPhotosBottomSheet();
        }

    }


    //Static method that returns new image fragment object
    public static ImagesFragment newInstance(String folder, FragmentInterface fragmentInterface) {

        /*Init a new bundle object*/
        Bundle bundle = new Bundle();
        /*Put the key and the value*/
        bundle.putString(Constants.FRAGMENT_KEY_FOLDER, folder);
        /*Create a new ImagesFragment*/
        ImagesFragment fragment = new ImagesFragment();
        fragment.setInterface(fragmentInterface);
        /*Set it's data to our created bundle*/
        fragment.setArguments(bundle);
        /*Return the fragment to whatever requested this method*/
        return fragment;


    }

    /*Interface from the adapter*/
    @Override
    public boolean onItemLongClicked(int position) {

        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    private void toggleSelection(int position) {
        imagesRecyclerAdapter.toggleSelection(position);
        int count = imagesRecyclerAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle("Items:" + count);
            actionMode.invalidate();
        }
    }

    public void setInterface(FragmentInterface anInterface) {
        this.anInterface = anInterface;
    }


    /*888*******************************************************************/
    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    deleteItems(mode);

                    break;
                case R.id.action_clear:

                    actionMode.finish();

                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            imagesRecyclerAdapter.clearSelections();
            actionMode = null;

        }
    }


    private void deleteItems(final ActionMode mode) {
        final ArrayList<Image> forDelete = imagesRecyclerAdapter.getSelectedItems();
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle("Delete?")
                .setMessage(forDelete.size() + " item/s will be deleted")
                .setNegativeButton(getString(R.string.string_cancel), null)
                .setPositiveButton(getString(R.string.string_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(forDelete.size());
                        deleteAsyncTask.execute(forDelete);
                    }
                }).create().show();

    }

    class DeleteAsyncTask extends AsyncTask<ArrayList<Image>, Integer, Void> {

        private ProgressDialog progressDialog;
        private int progress = 0;

        public DeleteAsyncTask(int progress) {
            this.progress = progress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Deleting images");
            progressDialog.setCancelable(false);
            progressDialog.setMax(progress);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            actionMode.finish();
            checkEmptyView();
            imagesRecyclerAdapter.getList(currentFolder);
        }

        @Override
        protected Void doInBackground(ArrayList<Image>... params) {

            ArrayList<Image> imagesList = params[0];
            for (int x = 0; x < imagesList.size(); x++) {

                Image current = imagesList.get(x);
                new File(current.getFilePath()).delete();
                publishProgress(x + 1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);

        }
    }


}

