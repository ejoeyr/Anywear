package ph.clothesuffle.anywear.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.activities.AboutDeveloperActivity;
import ph.clothesuffle.anywear.application.AnywearApplication;
import ph.clothesuffle.anywear.receivers.ShuffleBroadcastReceiver;
import ph.clothesuffle.anywear.util.IabHelper;
import ph.clothesuffle.anywear.util.IabResult;
import ph.clothesuffle.anywear.util.Inventory;
import ph.clothesuffle.anywear.util.Purchase;
import ph.clothesuffle.anywear.utilities.GeneralUtils;

/**
 * Created by joeyramirez on 3/2/2016.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    //private CheckBoxPreference checkBoxPreference_password;
    private CheckBoxPreference checkBoxPreference_ootd;
    private IabHelper mHelper;
    private String DONATE_SKU = "123";
    private boolean hasDonated = false;
    private SharedPreferences.Editor editor;
    private static String TAG = "BILLING";


    public SettingsFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setUpBilling();
        addPreferencesFromResource(R.xml.settings);
        traverseXmlFileAndPutListeners();
        configSpecialPreferences();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void configSpecialPreferences() {

        editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
       /* checkBoxPreference_password = (CheckBoxPreference)
                findPreference(getString(R.string.pref_app_lock));*/
        checkBoxPreference_ootd = (CheckBoxPreference)
                findPreference(getString(R.string.pref_ootd_noti));
  /*      editTextPreference_password = (EditTextPreference)
                findPreference(getString(R.string.string_app_lock));*/

    }

    private void traverseXmlFileAndPutListeners() {

        for (int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++) {
            /*Root prefs*/
            PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().getPreference(x);
            for (int y = 0; y < category.getPreferenceCount(); y++) {
                /*Current item of the root pref*/
                Preference pref = category.getPreference(y);
                /*Put listeners*/
                pref.setOnPreferenceClickListener(this);
            }
        }
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        setPrefsEnabled();
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_about))) {
            about();
        } else if (key.equals(getString(R.string.pref_share))) {
            share();
        } else if (key.equals(getString(R.string.pref_donate))) {
            donate();
        } else if (key.equals(getString(R.string.pref_rate))) {
            rate();
        } else if (key.equals(getString(R.string.pref_bug))) {
            bug();
        } else if (key.equals(getString(R.string.pref_clear_cache))) {
            clearCache();
        } else if (key.equals(getString(R.string.pref_defaults))) {
            restoreDefaults();
        }

        return false;
    }

    private void about() {

        startActivity(new Intent(getActivity(), AboutDeveloperActivity.class));

    }


    private void share() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Don't know what's your OOTD for today? Shuffle it now using #Anywear app Link: https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Where to share?"));
    }

    /*Invoked when feedback button is tapped*/
    private void bug() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "joeyramirez1000@yahoo.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Anywear app");
        startActivity(Intent.createChooser(emailIntent, "Report bug using..."));

    }

    /*Invoked when rate button is tapped*/
    private void rate() {

        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    private void restoreDefaults()
    {

        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(getString(R.string.string_clear_data))
                .setMessage(getString(R.string.string_clear_data_message))
                .setPositiveButton(getString(R.string.string_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startTransaction();
                    }
                }).setNegativeButton(getString(R.string.string_cancel), null).create().show();

    }
    private void startTransaction() {


        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().apply();
        AnywearApplication.createFolders();
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void clearCache() {

        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setMessage(getString(R.string.string_delete_cached_data))
                .setTitle(getString(R.string.string_clear_cache_title))
                .setPositiveButton(getString(R.string.string_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), getString(R.string.string_cache_cleared), Toast.LENGTH_SHORT).show();

                        GeneralUtils.trimCache(getActivity());

                    }
                }).setNegativeButton(getString(R.string.string_cancel), null).create().show();

    }

    @Override
    public void onResume() {
        super.onResume();
        setPrefsEnabled();
        setUpBilling();
    }

    private void setPrefsEnabled() {

       // findPreference(getString(R.string.pref_set_app_lock)).setEnabled(checkBoxPreference_password.isChecked());
        findPreference(getString(R.string.pref_set_time)).setEnabled(checkBoxPreference_ootd.isChecked());
    }



    /*

    ____ ____ ___  ____    ____ ____ ____    ___  ____ _  _ ____ ___ _ ____ _  _
    |    |  | |  \ |___    |___ |  | |__/    |  \ |  | |\ | |__|  |  | |  | |\ |
    |___ |__| |__/ |___    |    |__| |  \    |__/ |__| | \| |  |  |  | |__| | \|

                                                                             */


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(DONATE_SKU)) {
                Toast.makeText(getActivity(), "donate", Toast.LENGTH_SHORT).show();
                mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        if (result.isSuccess()) {

                            sayThanks(true);
                        }
                    }
                });
            }

        }
    };

    private void setUpBilling() {


        mHelper = new IabHelper(getActivity(), getString(R.string.google_billing_key));
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        setAlarm();


    }

    private void setAlarm() {

        Intent intent = new Intent(getActivity(), ShuffleBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if(checkBoxPreference_ootd.isChecked())
        {
            long _alarm = 0;
            Calendar now = Calendar.getInstance();
            Calendar alarm = Calendar.getInstance();
            alarm.set(Calendar.HOUR_OF_DAY, getListPreferenceValue());
            alarm.set(Calendar.MINUTE,0);

            if(alarm.getTimeInMillis() <= now.getTimeInMillis())
                _alarm = alarm.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
            else
                _alarm = alarm.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, _alarm, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else
        {
            alarmManager.cancel(pendingIntent);
        }


    }


    private boolean donate() {
        if (alreadyPurchased()) {

            sayThanks(true);

        } else {


            if (!mHelper.isAsyncOperation()) {

                mHelper.launchPurchaseFlow(getActivity(), DONATE_SKU, 10001,
                        mPurchaseFinishedListener);
            } else
            {
                mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), DONATE_SKU, 10001,
                        mPurchaseFinishedListener);
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean alreadyPurchased() {

        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isSuccess()) {
                    hasDonated = inv.hasPurchase(DONATE_SKU);
                } else {
                    Toast.makeText(getActivity(), "Failed getting data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return hasDonated;
    }

    private void sayThanks(boolean b) {
        String message = "";

        if (b) {
            message = getString(R.string.string_thanks_already);
        } else {
            message = getString(R.string.string_thanks_for_helpinh);
        }
        new android.app.AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle)
                .setTitle(getString(R.string.string_donation_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.string_no_problem), null)
                .create()
                .show();
    }

    public int getListPreferenceValue() {
        return
                Integer.parseInt(((ListPreference) findPreference(getString(R.string.pref_set_time))).getValue());
    }
}
