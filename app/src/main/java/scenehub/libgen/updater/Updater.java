package scenehub.libgen.updater;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import scenehub.libgen.BuildConfig;
import scenehub.libgen.R;
import scenehub.libgen.api.ApiClient;

public class Updater {

    private static final String TAG = Updater.class.getSimpleName();
    private Activity activity;

    private String newVersionCode;
    private String currentVersionCode;
    private String updateURL ;


    public Updater(final Activity activity){
        this.activity = activity;

        ///If WRITE_EXTERNAL_STORAGE permission was not granted, request this permission
        //Only when app has this permission, then it can download new apk to Downloads folder
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
        }

        //If network available, then we make a Retrofit GET request to check current app version
        if (isNetworkAvailable()) {

            Log.d(TAG, "Network is available. Checking for updates...");

            ApiClient.getInstance().getUpdateJSON().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    newVersionCode = response.body().getAsJsonObject("update").get("newVersion").getAsString();
                    currentVersionCode = String.valueOf(BuildConfig.VERSION_CODE);
                    updateURL = response.body().getAsJsonObject("update").get("url").getAsString();

                    Log.d(TAG, "currentVersion: " + currentVersionCode + " newVersion: " + newVersionCode);

                    if (Integer.valueOf(newVersionCode) > Integer.valueOf(currentVersionCode)) {

                        Log.d(TAG, "Updating from version" + currentVersionCode + " to " + newVersionCode);
                        Log.d(TAG, "URL: " + updateURL);


                        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //not granted. Ask again
                            Log.d(TAG, "No permission to WRITE_EXTERNAL_STORAGE");
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
                        } else {
                            Log.d(TAG, "Permission granted to WRITE_EXTERNAL_STORAGE");
                            updateApp();
                        }
                    } else {
                        Log.d(TAG, "No need to update from " + currentVersionCode + " to " + newVersionCode);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } else {
            Log.d(TAG, "Network is unavailable. Not checking for updates.");
        }
    }



    //If network is available, download new APK and pop installation request to user
    //After app is installed, delete downloaded apk
    public void updateApp() {

        if (isNetworkAvailable()) {
            String APK_FILENAME = "update-" + Calendar.getInstance().getTime() +".apk";

            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_FILENAME);
            final Uri fileUri = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                    FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", apkFile) :
                    Uri.fromFile(apkFile);


            //Delete update file if exists
            if (apkFile.exists())
                apkFile.delete();


            //set downloadManager
            Log.d(TAG, "URI scheme: "+Uri.parse(updateURL).getScheme());
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateURL))
                    .setTitle("Updater for "+ activity.getResources().getString(R.string.app_name))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILENAME);


            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setDataAndType(fileUri,manager.getMimeTypeForDownloadedFile(downloadId));

                    activity.startActivity(install);
                    activity.unregisterReceiver(this);
                    activity.finish();
                }
            };
            //register receiver for when .apk download is compete
            activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            Log.d(TAG, "Network is unavailable. Not checking for updates.");
        }
    }


    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}