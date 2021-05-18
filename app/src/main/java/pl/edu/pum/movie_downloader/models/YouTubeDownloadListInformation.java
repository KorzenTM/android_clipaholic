package pl.edu.pum.movie_downloader.models;

public class YouTubeDownloadListInformation {
    private String mTitle;
    private String mFormat;
    private String mID;
    private String mDownloadURL;
    private String mExtension;
    private int mITag;

    public YouTubeDownloadListInformation(String Title, String Format, String ID, int ITAG, String URL, String ext ) {
        this.mTitle = Title;
        this.mFormat = Format;
        this.mID = ID;
        this.mITag = ITAG;
        this.mDownloadURL = URL;
        this.mExtension = ext;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getFormat() {
        return mFormat;
    }

    public String getID() {
        return mID;
    }

    public int getITag() {
        return mITag;
    }

    public String getExtension(){return mExtension;}

    public String getDownloadURL(){ return mDownloadURL; }
}
