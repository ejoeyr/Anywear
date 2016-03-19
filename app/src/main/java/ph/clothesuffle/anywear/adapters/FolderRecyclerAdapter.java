package ph.clothesuffle.anywear.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.custom.RenameFolderDialog;
import ph.clothesuffle.anywear.interfaces.AdapterInterface;
import ph.clothesuffle.anywear.interfaces.NavigationDrawerInterface;
import ph.clothesuffle.anywear.models.Folder;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/1/2016.
 */
public class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderRecyclerAdapter.FolderViewHolder>
        implements AdapterInterface {


    private ArrayList<Folder> mFolderArrayList;
    private Context mContext;
    private NavigationDrawerInterface mNavigationDrawerInterface;
    private Vibrator vibrator;


    public FolderRecyclerAdapter() {

    }

    public FolderRecyclerAdapter(ArrayList<Folder> mFolderArrayList, Context mContext, NavigationDrawerInterface mNavigationDrawerInterface) {


        this.mFolderArrayList = mFolderArrayList;
        this.mContext = mContext;
        this.mNavigationDrawerInterface = mNavigationDrawerInterface;
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.item_folder, parent, false);

        return new FolderViewHolder(v);

    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        if(position == 0)
        {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryDark);
        }
        holder.wrapData(mFolderArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFolderArrayList.size();
    }

    public void setList(ArrayList<Folder> folderArrayList) {

        mFolderArrayList = folderArrayList;
        notifyDataSetChanged();
    }

    @Override
    public void loadRename() {

        setList(FileUtils.getFolders(Constants.FOLDER_ROOT));
    }

    public class FolderViewHolder
            extends
            RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener {

        TextView textView_folderName;

        public FolderViewHolder(View itemView) {

            super(itemView);
            textView_folderName = (TextView) itemView.findViewById(R.id.textView_folderName);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void wrapData(Folder folder) {


            textView_folderName.setText(folder.getFileName());

        }

        @Override
        public void onClick(View v) {

            mNavigationDrawerInterface.closeDrawer(mFolderArrayList.get(getAdapterPosition()).getFileName());
        }

        @Override
        public boolean onLongClick(View v) {

            if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(mContext.getString(R.string.pref_vibration), false)) {
                vibrator.vibrate(Constants.ADAPTER_VIBRATION);
            }
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.setGravity(Gravity.END);
            popupMenu.getMenuInflater().inflate(R.menu.menu_folder_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int pos = getAdapterPosition();
                    Folder folder = mFolderArrayList.get(pos);

                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            showDeleteDialog(folder, pos);
                            break;
                        /*FEATURE STILL UNDECIDED BUT WORKING*/
                        /*case R.id.action_rename:
                            showRenameDialog(folder);
                            break;*/
                        case R.id.action_properties:
                            showPropertiesDialog(folder);
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
            return false;
        }
    }

    /*PROPERTIES
    * */
    private void showPropertiesDialog(Folder folder) {
        /*TODO*/
        String folderInfo = String.format(
                "SIZE: %s \nITEMS: %d \nDATE: %s",
                folder.getFileSize(),
                folder.getFolderItems(),
                folder.getFolderDate());
        AlertDialog builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                .setTitle(folder.getFileName() + " properties")
                .setPositiveButton("Ok", null)
                .setMessage(folderInfo).create();

        builder.show();

    }


    /*DELETE DIALOG*/
    private void showDeleteDialog(final Folder folder, final int pos) {
        if (!folder.getFileName().equals(Constants.FOLDER_OUTFITS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                    .setTitle(mContext.getString(R.string.string_delete) + folder.getFileName() + mContext.getString(R.string.string_folder))
                    .setMessage(R.string.string_delete_this_folder)
                    .setNegativeButton(mContext.getString(R.string.string_cancel), null);

            builder.setPositiveButton(mContext.getString(R.string.string_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    boolean deleted = FileUtils.deleteFolder(folder.getFullPath());

                    if (deleted) {

                        if (folder.getFileName().equals(PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.ACTIVITY_KEY_CURRENT_FOLDER, Constants.FOLDER_OUTFITS))) {
                            mNavigationDrawerInterface.changeFragment();
                        }
                        removeFolder(pos);
                    } else {
                        Toast.makeText(mContext, R.string.string_cannot_do_that, Toast.LENGTH_SHORT).show();
                    }
                }
            }).create().show();
        } else {
            Toast.makeText(mContext, "Can't delete presets", Toast.LENGTH_SHORT).show();
        }

    }


    private void removeFolder(int pos) {
        mFolderArrayList.remove(pos);
        notifyItemRemoved(pos);
    }


    /*
    *//*FEATURE STILL UNDECIDED*//*
    private void showRenameDialog(Folder folder) {
        if (!folder.getFileName().equals(Constants.FOLDER_OUTFITS)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.FRAGMENT_KEY_FOLDER, folder);
            RenameFolderDialog
                    .newInstance(this, bundle)
                    .show(((AppCompatActivity) mContext).getFragmentManager(), "");

        } else {
            Toast.makeText(mContext, "Can't rename preset folder", Toast.LENGTH_SHORT).show();
        }

    }*/
}
