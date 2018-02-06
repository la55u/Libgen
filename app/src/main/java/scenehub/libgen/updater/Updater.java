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

    public static final int PERMISSION_WRITE_STORAGE = 11;


    public Updater(final Activity activity){
        this.activity = activity;
        if (isNetworkAvailable()) checkUpdate();
    }


    public void checkUpdate(){
        ApiClient.getInstance().getUpdateJSON().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.d(TAG, "response: "+response.message()+" code: "+response.code());
                try{
                    newVersionCode = response.body().getAsJsonObject("update").get("newVersion").getAsString();
                    currentVersionCode = String.valueOf(BuildConfig.VERSION_CODE);
                    updateURL = response.body().getAsJsonObject("update").get("url").getAsString();

                    Log.d(TAG, "currentVersion: " + currentVersionCode + " newVersion: " + newVersionCode);

                    if (Integer.valueOf(newVersionCode) > Integer.valueOf(currentVersionCode)) {

                        Log.d(TAG, "Updating from version " + currentVersionCode + " to " + newVersionCode);
                        Log.d(TAG, "URL: " + updateURL);


                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //not granted. Ask again
                            Log.d(TAG, "No permission to WRITE_EXTERNAL_STORAGE");
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                        } else {
                            Log.d(TAG, "Permission granted to WRITE_EXTERNAL_STORAGE");
                            updateApp();
                        }
                    } else {
                        Log.d(TAG, "No need to update from " + currentVersionCode + " to " + newVersionCode);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "Failed to get a valid response from server");
            }
        });
    }

    //If network is available, download new APK and pop installation request to user
    //After app is installed, delete downloaded apk
    public void updateApp() {

        if (isNetworkAvailable()) {
            String APK_FILENAME = "libgen-update.apk";
            Log.d(TAG, "new filename: "+APK_FILENAME );

            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_FILENAME);
            final Uri fileUri = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                    FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", apkFile) :
                    Uri.fromFile(apkFile);


            //Delete update file if exists
            if (apkFile.exists()){
                Log.d(TAG,"Deleting file:"+apkFile.getName());
                apkFile.delete();
            }else{
                Log.d(TAG, "No need to delete any file");
            }


            //set downloadManager
            Log.d(TAG, "URI scheme: "+Uri.parse(updateURL).getScheme());
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateURL))
                    .setTitle("Update for "+ activity.getResources().getString(R.string.app_name))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILENAME);


            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "Download completed");
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
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if( activeNetworkInfo != null && activeNetworkInfo.isConnected() ){
            Log.d(TAG, "Network OK");
            return true;
        }else{
            Log.d(TAG, "No network available");
            return false;
        }
    }



}