package pl.edu.pum.movie_downloader.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Downloader {
    private final Context mContext;

    public Downloader(Context context){
        this.mContext = context;
    }

    public void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, "Download has been started.", Snackbar.LENGTH_SHORT).show();
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        manager.enqueue(request);
    }
}
