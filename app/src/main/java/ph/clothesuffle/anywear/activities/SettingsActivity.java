package ph.clothesuffle.anywear.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.fragments.SettingsFragment;
import ph.clothesuffle.anywear.utilities.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by joeyramirez on 3/2/2016.
 */
public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setOverAllFonts() {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(Constants.FONT_QUESTRIAL)
                        .setFontAttrId(R.attr.fontPath)
                        .build());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverAllFonts();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        SettingsFragment.newInstance())
                .commit();
    }



}
