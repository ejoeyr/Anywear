package ph.clothesuffle.anywear.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.activities.ImageActivity;
import ph.clothesuffle.anywear.models.Image;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

public class ShuffleBroadcastReceiver extends BroadcastReceiver {
    public ShuffleBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        File file = new File(Environment.getExternalStorageDirectory(), Constants.FOLDER_ROOT + Constants.FOLDER_OUTFITS);
        File[] fileList = file.listFiles();
        String outFitName = "";
        ArrayList<Image> images = new ArrayList<>();
        if (fileList != null && fileList.length >= 1) {
            int random = (int) Math.random() * fileList.length;
            File[] fileRandom = fileList[random].listFiles();
            outFitName = fileList[random].getName();
            if (fileRandom != null && fileRandom.length >= 1) {

                for (File f : fileRandom) {
                    images.add(new Image(f.getAbsolutePath(), FileUtils.convertSize(f.length())));
                }

            }

        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotification;
        if (images.size() >= 1) {
            Intent myIntent = new Intent(context, ImageActivity.class);
            myIntent.putExtra(Constants.ACTIVITY_KEY_IMAGES, images);
            myIntent.putExtra(Constants.ACTIVITY_KEY_SHOW_BUTTON, false);
            intent.putExtra(Constants.ACTIVITY_KEY_SHOW_LOADING,true);
            myIntent.putExtra(Constants.ACTIVITY_KEY_OUTFIT_NAME_OR_FOLDER, outFitName);
            myIntent.putExtra(Constants.ACTIVITY_KEY_SELECTED, 0);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            myNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("Your OOTD is here")
                    .setContentText("Tap this notification to view")
                    .setTicker("Anywear...")
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();


        } else {
            myNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("We can't get you dressed!")
                    .setContentText("You have no outfits on your wardrobe!")
                    .setTicker("Anywear...")
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

        }


        manager.notify(1, myNotification);


    }
}
