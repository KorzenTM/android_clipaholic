package pl.edu.pum.movie_downloader.downloader.YouTubeURL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import pl.edu.pum.movie_downloader.downloader.Downloader;

public class YouTubeDownloadURL {
    private final String mLink;
    private final Context mContext;
    private SparseArray<YtFile> mYTFiles;
    private VideoMeta mMeta;
    private List<RadioButton> mRadioButtonsList = new ArrayList<RadioButton>();;

    public YouTubeDownloadURL(Context context, String link){
        this.mLink = link;
        this.mContext = context;
    }

    @SuppressLint("StaticFieldLeak")
    public void extract(YouTubeDownloadUrlState youTubeDownloadUrlState){
        mRadioButtonsList.clear();
        new YouTubeExtractor(mContext) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {
                    youTubeDownloadUrlState.isOperationSuccessfully("NO_SUCCESS");
                    return;
                }
                mYTFiles = ytFiles;
                mMeta = vMeta;
                int itag;
                RadioButton newButton;
                String materialExtension;
                int videoHeight, audioBitrate;
                for (int i = 0; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    Format format = ytFiles.get(ytFiles.keyAt(i)).getFormat();
                    materialExtension = format.getExt();
                    videoHeight = format.getHeight();
                    audioBitrate = format.getAudioBitrate();
                    newButton = new RadioButton(mContext);

                    if (videoHeight > 0) {
                        newButton.setText(("Video " + materialExtension + " " + videoHeight + "p").toUpperCase());
                    } else {
                        newButton.setText(("Audio " + materialExtension + " " + audioBitrate + "kb/s").toUpperCase());
                    }

                    newButton.setId(itag);
                    mRadioButtonsList.add(newButton);
                }

                if (mRadioButtonsList.size() > 0){
                    youTubeDownloadUrlState.isOperationSuccessfully("SUCCESS");
                }
            }}.extract(mLink, true, false);
    }

    public List<RadioButton> getRadioButtonsList() {return mRadioButtonsList;}

    public void downloadVideo(int iTag){
        YtFile ytFile = mYTFiles.get(iTag);
        String downloadURL = ytFile.getUrl();
        String videoTitle = mMeta.getTitle();
        String filename;
        if (videoTitle.length() > 55) {
            filename = videoTitle.substring(0, 55) + "." + ytFile.getFormat().getExt();
        } else {
            filename = videoTitle + "." + ytFile.getFormat().getExt();
        }
        Downloader downloader = new Downloader(mContext);
        downloader.downloadFromUrl(downloadURL, videoTitle, filename);
    }
}
