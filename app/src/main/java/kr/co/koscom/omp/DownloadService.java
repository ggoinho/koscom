package kr.co.koscom.omp;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadService extends IntentService {

    private static final String DOWNLOAD_PATH = "Download_path";
    private static final String DESTINATION_PATH = "Destination_path";
    private static final String DESTINATION_FILENAME = "Destination_filename";

    public DownloadService() {
        super("DownloadSongService");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext,
                                            final @NonNull String downloadPath,
                                            final @NonNull String destinationPath,
                                            final @NonNull String fileName) {
        return new Intent(callingClassContext, DownloadService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(DESTINATION_FILENAME, fileName);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        String destinationFilename = intent.getStringExtra(DESTINATION_FILENAME);
        startDownload(downloadPath, destinationPath, destinationFilename);
    }

    private void startDownload(String downloadPath, String destinationPath, String destinatioinFilename) {
        Log.d(DownloadService.class.getSimpleName(), "startDownload(" + downloadPath + ", " + destinationPath + ", " + destinatioinFilename + ")");

        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(destinatioinFilename); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, destinatioinFilename);  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
    }
}
