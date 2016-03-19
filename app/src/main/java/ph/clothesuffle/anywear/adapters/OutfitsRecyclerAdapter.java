package ph.clothesuffle.anywear.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.activities.ImageActivity;
import ph.clothesuffle.anywear.interfaces.AdapterInterface;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.models.Outfit;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/13/2016.
 */
public class OutfitsRecyclerAdapter extends RecyclerView.Adapter<OutfitsRecyclerAdapter.OutfitsViewHolder> {


    private ArrayList<Outfit> mOutfits = new ArrayList<>();
    private ArrayList<Outfit> mOutfitsCopy = new ArrayList<>();
    private Context mContext;
    private AdapterInterface adapterInterface;

    public OutfitsRecyclerAdapter(Context context, ArrayList<Outfit> outfits,AdapterInterface dapterInterface) {

        mContext = context;
        mOutfits.addAll(outfits);
        adapterInterface = dapterInterface;
        mOutfitsCopy.addAll(outfits);


    }

    @Override
    public OutfitsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);

        return new OutfitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OutfitsViewHolder holder, int position) {

        holder.bindItem(mOutfits.get(position));
    }

    @Override
    public int getItemCount() {
        return mOutfits.size();
    }

    public void filter(String newText) {

        newText = newText.toLowerCase();
        mOutfits.clear();
        //If the text length is 0 then place in the copy of it
        if (newText.length() == 0) {

           mOutfits.addAll(mOutfitsCopy);

        } else {
//            else search for something

            for (Outfit wp : mOutfitsCopy) {
                /*If the contact name has the character that is on the charText then add it up*/
                if (wp.getFolderName().toLowerCase().contains(newText)) {
                    mOutfits.add(wp);

                }

            }
        }
        notifyDataSetChanged();

    }

    public void setList(ArrayList<Outfit> outfits) {


        mOutfits.addAll(outfits);
        mOutfitsCopy.addAll(outfits);
        notifyDataSetChanged();
    }

    public class OutfitsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView_folder;
        private RecyclerView recyclerView_images;
        private ImageView imageView_delete;


        public OutfitsViewHolder(View itemView) {
            super(itemView);

            textView_folder = (TextView) itemView.findViewById(R.id.textView_folderName);
            recyclerView_images = (RecyclerView) itemView.findViewById(R.id.recyclerView_images);
            imageView_delete = (ImageView) itemView.findViewById(R.id.imageView_delete);
            imageView_delete.setOnClickListener(this);
            itemView.setOnClickListener(this);
            recyclerView_images.setHasFixedSize(true);
            recyclerView_images.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        }

        public void bindItem(Outfit outfit) {

            textView_folder.setText(outfit.getFolderName());
            recyclerView_images.setAdapter(new OutfitImageAdapter(mContext, outfit.getPhotoPaths()));


        }

        @Override
        public void onClick(View v) {
            int adapterPost = getAdapterPosition();
            if (v == imageView_delete) {

             delete(adapterPost);

            }
            else if (v == itemView)
            {
              startViewing(adapterPost);
            }
        }
    }

    public void startViewing(int adapterPost) {

        /*Conversion*/

        ArrayList<Image> images = new ArrayList<>();
        ArrayList<String> photoPath = mOutfits.get(adapterPost).getPhotoPaths();

        for (int x = 0; x < photoPath.size(); x++) {

            if (FileUtils.isImage(photoPath.get(x))) {
                Image image = new Image();
                image.setFilePath(photoPath.get(x));
                images.add(image);
            }

        }
        Intent intent = new Intent(mContext, ImageActivity.class);
        intent.putExtra(Constants.ACTIVITY_KEY_IMAGES, images);
        intent.putExtra(Constants.ACTIVITY_KEY_SHOW_BUTTON, false);
        intent.putExtra(Constants.ACTIVITY_KEY_SELECTED, 0);
        intent.putExtra(Constants.ACTIVITY_KEY_SHOW_LOADING,false);
        intent.putExtra(Constants.ACTIVITY_KEY_OUTFIT_NAME_OR_FOLDER, mOutfits.get(adapterPost).getFolderName());
        mContext.startActivity(intent);
    }

    private void delete(final int adapterPost) {

        new AlertDialog.Builder(mContext,R.style.AlertDialogStyle)
                .setMessage("Are you sure you want to delete this outfit?")
                .setTitle("Delete "+mOutfits.get(adapterPost).getFolderName()+" Outfit?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtils.deleteFolder(mOutfits.get(adapterPost).getFolderPath());
                        mOutfits.remove(adapterPost);
                        mOutfitsCopy.remove(adapterPost);
                        notifyItemRemoved(adapterPost);
                        adapterInterface.loadRename();
                    }
                }).create().show();

    }
}
