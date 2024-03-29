package pl.edu.pum.movie_downloader.database.local.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import pl.edu.pum.movie_downloader.models.DownloadListInformationDTO;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "downloadListDB_JAVA.db";

    public static final String TABLE_DOWNLOAD_LIST = "download_list";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "clip_title";
    public static final String COLUMN_CLIP_ID = "clip_id";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_YT_ITAG = "yt_iTag"; //for youtube clips
    public static final String COLUMN_DOWNLOAD_LINK = "download_url";
    public static final String COLUMN_CLIP_EXTENSION = "clip_extension";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_SOURCE = "source";


    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DOWNLOAD_LIST_TABLE = "CREATE TABLE " +
                TABLE_DOWNLOAD_LIST +
                "(" +
                COLUMN_ID + " " +
                "INTEGER PRIMARY KEY," +
                COLUMN_TITLE +
                " TEXT," +
                COLUMN_FORMAT +
                " TEXT," +
                COLUMN_CLIP_ID +
                " TEXT," +
                COLUMN_YT_ITAG +
                " INTEGER," +
                COLUMN_DOWNLOAD_LINK +
                " TEXT," +
                COLUMN_CLIP_EXTENSION +
                " TEXT," +
                COLUMN_LINK +
                " TEXT," +
                COLUMN_SOURCE +
                " TEXT" +
                ")";
        db.execSQL(CREATE_DOWNLOAD_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD_LIST);
        onCreate(db);
    }

    public Cursor getDownloadList(){
        String query = "SELECT * FROM " + TABLE_DOWNLOAD_LIST;
        SQLiteDatabase db =this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public void addNewClipToList(DownloadListInformationDTO information){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, information.getTitle());
        values.put(COLUMN_CLIP_ID, information.getID());
        values.put(COLUMN_FORMAT, information.getFormat());
        values.put(COLUMN_YT_ITAG, information.getITag());
        values.put(COLUMN_DOWNLOAD_LINK, information.getDownloadURL());
        values.put(COLUMN_CLIP_EXTENSION, information.getExtension());
        values.put(COLUMN_LINK, information.getLink());
        values.put(COLUMN_SOURCE, information.getSource());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DOWNLOAD_LIST, null, values);
        db.close();
    }

    public void deleteClipFromList(String title, int itag){
        String query = "Select * FROM " +
                TABLE_DOWNLOAD_LIST +
                " WHERE " +
                COLUMN_TITLE +
                "= \"" +
                title +
                "\"" +
                " AND " +
                COLUMN_YT_ITAG +
                "= " +
                itag;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            int currentID = Integer.parseInt(cursor.getString(0));
            db.delete(TABLE_DOWNLOAD_LIST, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(currentID)});
        }
        db.close();
        cursor.close();
    }
}
