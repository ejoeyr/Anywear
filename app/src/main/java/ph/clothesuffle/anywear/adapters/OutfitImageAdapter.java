package ph.clothesuffle.anywear.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPagerActivity;
import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.models.Outfit;

/**
 * Created by joeyramirez on 3/13/2016.
 */
public class OutfitImageAdapter extends RecyclerView.Adapter<OutfitImageAdapter.OutfitViewHolder> {

    ArrayList<String> paths;
    Context context;

    public OutfitImageAdapter(Context mContext, ArrayList<String> paths) {
        this.paths = paths;
        this.context = mContext;
    }

    @Override
    public OutfitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit_thumbnail, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OutfitViewHolder holder, int position) {
        holder.bindData(paths.get(position));
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class OutfitViewHolder extends RecyclerView.ViewHolder implements RequestListener<String, GlideDrawable>{

        ImageView imageView_thumbnails;
        ProgressBar progressBar;

        public OutfitViewHolder(View itemView) {
            super(itemView);
            imageView_thumbnails = (ImageView) itemView.findViewById(R.id.imageView_thumbnail);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_imageLoading);

        }

        public void bindData(String path) {

            Glide.with(context).load(path).crossFade().listener(this).into(imageView_thumbnails);
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

            progressBar.setVisibility(View.GONE);
            return false;
        }


    }
}
