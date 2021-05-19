package pl.edu.pum.movie_downloader.models;

public class YouTubeDownloadListInformation {
    private String mLink;
    private String mTitle;
    private String mFormat;
    private String mID;
    private String mDownloadURL;
    private String mExtension;
    private int mITag;

    public YouTubeDownloadListInformation(String Title, String Format, String ID, int ITAG, String URL, String ext, String link ) {
        this.mTitle = Title;
        this.mFormat = Format;
        this.mID = ID;
        this.mITag = ITAG;
        this.mDownloadURL = URL;
        this.mExtension = ext;
        this.mLink = link;
    }

    public String getLink() {return mLink;}

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