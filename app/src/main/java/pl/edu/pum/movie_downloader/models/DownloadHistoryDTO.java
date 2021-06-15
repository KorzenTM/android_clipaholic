package pl.edu.pum.movie_downloader.models;

import java.util.HashMap;
import java.util.Map;

public class DownloadHistoryDTO {
    private String mId;
    private final String mTitle;
    private final String mDownloadDate;
    private final String mFormat;
    private final String mSource;

    public DownloadHistoryDTO(String title, String downloadDate, String format, String source) {
        this.mTitle = title;
        this.mDownloadDate = downloadDate;
        this.mFormat = format;
        this.mSource = source;
    }

    public void setID(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDownloadDate() {
        return mDownloadDate;
    }

    public String getFormat() {
        return mFormat;
    }

    public String getSource() {
        return mSource;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("title", mTitle);
        dataToSend.put("format", mFormat);
        dataToSend.put("source", mSource);
        dataToSend.put("download_date", mDownloadDate);
        return  dataToSend;
    }
}
