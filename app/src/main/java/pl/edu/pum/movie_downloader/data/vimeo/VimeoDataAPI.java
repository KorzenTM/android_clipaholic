package pl.edu.pum.movie_downloader.data.vimeo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.edu.pum.movie_downloader.downloader.Downloader;
import vimeoextractor.OnVimeoExtractionListener;
import vimeoextractor.VimeoExtractor;
import vimeoextractor.VimeoVideo;

public class VimeoDataAPI extends Downloader {
    private final Context mContext;
    private String mLink;
    private String mTitle;
    private String mAuthor;
    private String mURLToPlay;
    private final List<RadioButton> mRadioButtonsList = new ArrayList<>();
    private Map<String, String> mStreamsMap = new TreeMap<>();

    public VimeoDataAPI(Context context, String link){
        super(context);
        this.mLink = link;
        this.mContext = context;
    }

    public void extract(VimeoDataAPIState vimeoDataAPIState){
        mRadioButtonsList.clear();
        VimeoExtractor.getInstance().fetchVideoWithURL(mLink, null, new OnVimeoExtractionListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VimeoVideo video) {
                mStreamsMap = video.getStreams();
                System.out.println(mStreamsMap);
                mTitle = video.getTitle();
                mAuthor = video.getVideoUser().getName();
                RadioButton newButton;
                for (Map.Entry<String, String> entry : mStreamsMap.entrySet()){
                    String quality = entry.getKey();
                    newButton = new RadioButton(mContext);
                    newButton.setId(Integer.parseInt(quality.replace("p", "")));
                    newButton.setText("Video " + quality);
                    mRadioButtonsList.add(newButton);
                    mURLToPlay = entry.getValue();
                }
                vimeoDataAPIState.isOperationSuccessfully("SUCCESS");
            }

            @Override
            public void onFailure(Throwable throwable) {
                vimeoDataAPIState.isOperationSuccessfully("NO_SUCCESS");
                Log.d("VIMEO", "VIMEO_STATUS:", throwable);
            }
        });
    }

    public void download(String URL){
        String filename = createFilename(mTitle, "mp4");
        downloadFromUrl(URL, mTitle, filename);
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getAuthorName(){
        return this.mAuthor;
    }

    public List<RadioButton> getRadioButtonsList(){
        return this.mRadioButtonsList;
    }

    public Map<String, String> getStreamsMap(){
        return this.mStreamsMap;
    }

    public String getURLToPlay(){
        return this.mURLToPlay;
    }
}
