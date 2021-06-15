package pl.edu.pum.movie_downloader.data.youtube;

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

public class YouTubeFormatAPI extends Downloader{
    private final Context mContext;
    private String mLink;
    private SparseArray<YtFile> mYTFiles;
    private VideoMeta mMeta;
    private final List<RadioButton> mRadioButtonsList = new ArrayList<>();

    public YouTubeFormatAPI(Context context, String link){
        super(context);
        mContext = context;
        mLink = link;
    }

    public  String getLink() {
        return mLink;
    }

    public void extract(YouTubeAPIState youTubeAPIState){
        mRadioButtonsList.clear();
        @SuppressLint("StaticFieldLeak") YouTubeExtractor youTubeExtractor = new YouTubeExtractor(NavHostActivity.getContext()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {
                    youTubeAPIState.isOperationSuccessfully("NO_SUCCESS");
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

                    newButton = new RadioButton(NavHostActivity.getContext());
                    newButton.setId(itag);
                    if (videoHeight > 0) {
                        newButton.setText(("Video " + materialExtension + " " + videoHeight + "p"));
                    } else {
                        newButton.setText(("Audio " + materialExtension + " " + audioBitrate + "kb/s"));
                    }
                    mRadioButtonsList.add(newButton);
                }
                youTubeAPIState.isOperationSuccessfully("SUCCESS");
            }
        };
        youTubeExtractor.extract(mLink, true, false);
    }

    public List<RadioButton> getRadioButtonsList() {return mRadioButtonsList;}

    public String getDownloadURL(int ITAG){
        return mYTFiles.get(ITAG).getUrl();
    }

    public String getExtension(int ITag){
        return  mYTFiles.get(ITag).getFormat().getExt();
    }
}
