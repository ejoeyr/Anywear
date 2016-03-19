package ph.clothesuffle.anywear.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.activities.ImageActivity;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 2/29/2016.
 */
public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ImagesViewHolder> {

    private Context mContext;
    private ArrayList<Image> mImages;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private ClickListener clickListener;
    private Vibrator vibrator;
    private SharedPreferences sharedPreferences;

    public ImagesRecyclerAdapter(Context mContext, ArrayList<Image> mImages, ClickListener clickListener) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.clickListener = clickListener;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        vibrator = (Vibrator)this.mContext.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_image, parent, false);

        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        holder.wrapData(mImages.get(position));

        holder.itemView.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void getList(String folder) {

        mImages = FileUtils.getAllImagesIn(folder);
        notifyDataSetChanged();

    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public ArrayList<Image> getSelectedItems() {
        ArrayList<Image> items = new ArrayList<Image>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            items.add(mImages.get(key).setPosition(key));
        }
        return items;
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RequestListener<String, GlideDrawable>, View.OnLongClickListener {


        private ImageView mImageView;
        private ProgressBar mProgressBar;


        public ImagesViewHolder(View itemView) {

            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView_image);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_loading);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }

        public void wrapData(Image image) {

            Glide.with(mContext)
                    .load(image.getFilePath())
                    .crossFade()
                    .listener(this)
                    .into(mImageView);

        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {


            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

            mProgressBar.setVisibility(View.INVISIBLE);
            return false;
        }


        @Override
        public void onClick(View v) {

            if (selectedItems.size() > 0)
            {
                clickListener.onItemClicked(getAdapterPosition());
            }else
            {
               startFullImageActivity(getAdapterPosition());
            }
        }
        @Override
        public boolean onLongClick(View v) {

            if(sharedPreferences.getBoolean(mContext.getString(R.string.pref_vibration), false))
            {
                vibrator.vibrate(Constants.ADAPTER_VIBRATION);
            }

            clickListener.onItemLongClicked(getAdapterPosition());
            return false;
        }
    }

    private void startFullImageActivity(int pos) {

        Intent intent = new Intent(mContext, ImageActivity.class);
        intent.putExtra(Constants.ACTIVITY_KEY_IMAGES,mImages);
        intent.putExtra(Constants.ACTIVITY_KEY_SHOW_BUTTON,false);
        intent.putExtra(Constants.ACTIVITY_KEY_SHOW_LOADING,false);
        intent.putExtra(Constants.ACTIVITY_KEY_OUTFIT_NAME_OR_FOLDER,
                        sharedPreferences.getString(Constants.ACTIVITY_KEY_CURRENT_FOLDER,
                        Constants.FOLDER_OUTFITS));
        intent.putExtra(Constants.ACTIVITY_KEY_SELECTED, pos);
        mContext.startActivity(intent);

    }

    public interface ClickListener {

        boolean onItemLongClicked(int position);

        void onItemClicked(int pos);

    }
}
