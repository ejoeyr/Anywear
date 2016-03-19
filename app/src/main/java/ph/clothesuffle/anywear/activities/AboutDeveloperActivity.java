package ph.clothesuffle.anywear.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.utilities.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutDeveloperActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(Constants.FONT_QUESTRIAL)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_about_developer);
        statusBarHider();


    }

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void clickHandler(View view) {

        if (view == findViewById(R.id.button_linkedin)) {
            linkedInMe();
        } else if (view == findViewById(R.id.button_email)) {
            emailMe();
        } else if (view == findViewById(R.id.button_apps)) {
            goToApps();
        }

    }

    private void goToApps() {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=levrsoft&c=apps"));
        startActivity(Intent.createChooser(intent, "View page using..."));

    }


    private void emailMe() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "joeyramirez1000@gmail.com", null));
        startActivity(Intent.createChooser(emailIntent, "Send email using..."));
    }

    private void linkedInMe() {


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ph.linkedin.com/in/ramirezjoey"));
        startActivity(Intent.createChooser(intent, "View page using..."));

    }
}
