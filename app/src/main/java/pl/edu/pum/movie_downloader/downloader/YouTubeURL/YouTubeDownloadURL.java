package pl.edu.pum.movie_downloader.downloader.YouTubeURL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.downloader.Downloader;

public class YouTubeDownloadURL extends Downloader{
    private static String mLink;
    private final Context mContext;
    private static SparseArray<YtFile> mYTFiles;
    private static VideoMeta mMeta;
    private final static List<RadioButton> mRadioButtonsList = new ArrayList<>();

    public YouTubeDownloadURL(Context context, String link){
        super(context);
        mLink = link;
        this.mContext = context;
    }

    public static String getLink() {
        return mLink;
    }

    public static void extract(YouTubeDownloadUrlState youTubeDownloadUrlState){
        mRadioButtonsList.clear();
        YouTubeExtractor youTubeExtractor = new YouTubeExtractor(NavHostActivity.getContext()) {
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
                    System.out.println(format.toString());
                    materialExtension = format.getExt();
                    videoHeight = format.getHeight();
                    audioBitrate = format.getAudioBitrate();
                    newButton = new RadioButton(NavHostActivity.getContext());
                    newButton.setId(itag);

                    if (videoHeight > 0) {
                        newButton.setText(("Video " + materialExtension + " " + videoHeight + "p"));
                    } else {
                        newButton.setText(("Audio " + materialExtension + " " + audioBitrate + "kb/s"));
                    }
                    mRadioButtonsList.add(newButton);
                }
                youTubeDownloadUrlState.isOperationSuccessfully("SUCCESS");
            }
        };
        youTubeExtractor.extract(mLink, true, false);
    }

    public List<RadioButton> getRadioButtonsList() {return this.mRadioButtonsList;}

    public String getDownloadURL(int ITAG){
        return mYTFiles.get(ITAG).getUrl();
    }

    public String getExtension(int ITag){
        return  mYTFiles.get(ITag).getFormat().getExt();
    }

    public void downloadVideoFromITag(int iTag){
        YtFile ytFile = mYTFiles.get(iTag);
        String downloadURL = ytFile.getUrl();
        String videoTitle = mMeta.getTitle();
        String extension = ytFile.getFormat().getExt();
        String filename = createFilename(videoTitle, extension);
        downloadFromUrl(downloadURL, videoTitle, filename);
    }

    public void downloadVideoFromURL(String URL, String title, String extension){
        String filename = createFilename(title, extension);
        downloadFromUrl(URL, title, filename);
    }
}
