package ph.clothesuffle.anywear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.custom.AddOutfitDialog;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.utilities.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {


    private ArrayList<Image> mImagesArrayList = new ArrayList<>();
    private ViewPager viewPager_images;
    private Button button_saveToOutfits;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        statusBarHider();
        setOverAllFonts();
        intent = getIntent();

        if (intent.getBooleanExtra(Constants.ACTIVITY_KEY_SHOW_LOADING, false))
        {
            setContentView(R.layout.layout_shuffling);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    begin();


                }
            }, 1200);

        }
        else
        {
            begin();
        }



    }

    private void begin() {
        setContentView(R.layout.activity_image);

        mImagesArrayList = (ArrayList<Image>) intent.getSerializableExtra(Constants.ACTIVITY_KEY_IMAGES);
        viewPager_images = (ViewPager) findViewById(R.id.pager);
        viewPager_images.setAdapter(new SlidingImageAdapter(this));
        viewPager_images.setCurrentItem(intent.getIntExtra(Constants.ACTIVITY_KEY_SELECTED, 0));

        button_saveToOutfits = (Button) findViewById(R.id.button_saveToOutfits);

        if (intent.getBooleanExtra(Constants.ACTIVITY_KEY_SHOW_BUTTON, false)) {
            button_saveToOutfits.setOnClickListener(this);
        } else {
            button_saveToOutfits.setClickable(false);
            button_saveToOutfits.setText(getIntent().getStringExtra(Constants.ACTIVITY_KEY_OUTFIT_NAME_OR_FOLDER));
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setOverAllFonts() {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(Constants.FONT_QUESTRIAL)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public void loadImage(int pos, ImageView imageView, final ProgressBar progressBar) {
        Glide.with(ImageActivity.this)
                .load(mImagesArrayList.get(pos).getFilePath())
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {


                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void onClick(View v) {


        ArrayList<String> paths = new ArrayList<>();

        for (int x = 0; x < mImagesArrayList.size(); x++) {
            paths.add(mImagesArrayList.get(x).getFilePath());
        }


        AddOutfitDialog addOutfitDialog = new AddOutfitDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.ACTIVITY_KEY_IMAGES, paths);
        addOutfitDialog.setArguments(bundle);
        addOutfitDialog.show(getFragmentManager(), "");

    }


    public class SlidingImageAdapter extends PagerAdapter {


        private LayoutInflater inflater;


        public SlidingImageAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mImagesArrayList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_image_activity, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);
            final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progressBar_imageLoading);
            loadImage(position, imageView, progressBar);

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    /*Code to hide the status bar  from G*/
    private void statusBarHider() {

        final Handler hand = new Handler();

        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < 16) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                View decorView = getWindow().getDecorView();

                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);

                hand.postDelayed(this, 1000);

            }
        }, 1000);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
