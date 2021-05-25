package pl.edu.pum.movie_downloader.models;

public class DownloadListInformation {
    private final String mLink;
    private final String mTitle;
    private final String mFormat;
    private final String mID;
    private final String mDownloadURL;
    private final String mExtension;
    private final int mITag;

    public DownloadListInformation(String Title, String Format, String ID, int ITAG,
                                   String downloadURL, String ext, String link ) {
        this.mTitle = Title;
        this.mFormat = Format;
        this.mID = ID;
        this.mITag = ITAG;
        this.mDownloadURL = downloadURL;
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
