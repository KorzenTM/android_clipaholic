package pl.edu.pum.movie_downloader.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.fragments.DownloadListFragment;
import pl.edu.pum.movie_downloader.models.YouTubeDownloadListInformation;

public class DownloadListRecyclerViewAdapter extends RecyclerView.Adapter<DownloadListRecyclerViewAdapter.ViewHolder> {
    public interface OnButtonClickListener{
        void onItemCheck(YouTubeDownloadListInformation youTubeDownloadListInformation);
        void onItemUncheck(YouTubeDownloadListInformation youTubeDownloadListInformation);
    }

    private final FragmentActivity mContext;
    public final List<Object> mClipInformationList;
    OnButtonClickListener onButtonClickListeners;

    public DownloadListRecyclerViewAdapter ( FragmentActivity context, List<Object> clipInformationList) {
        this.mContext = context;
        this.mClipInformationList = clipInformationList;
    }

    public void setOnButtonClickListeners(OnButtonClickListener listener){
        this.onButtonClickListeners = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadListRecyclerViewAdapter.ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.download_list_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object obj = mClipInformationList.get(position);
        if (obj.getClass() == YouTubeDownloadListInformation.class){
            YouTubeDownloadListInformation information = (YouTubeDownloadListInformation) obj;
            final String format = information.getFormat();
            final String title = information.getTitle();
            final String id = information.getID();
            holder.bind(format, title, id);
        }

    }

    @Override
    public int getItemCount() {
        return mClipInformationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mThumbnail;
        private final TextView mTitleTextView;
        private final TextView mFormatTextView;
        public final CheckBox mDownloadCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail = itemView.findViewById(R.id.thumbnail_image_view);
            mTitleTextView = itemView.findViewById(R.id.title_text_view_recycle);
            mFormatTextView = itemView.findViewById(R.id.format_text_view_recycle);
            Button mDownloadButton = itemView.findViewById(R.id.download_button);
            Button mDeleteButton = itemView.findViewById(R.id.delete_from_list_button);
            mDownloadCheckbox = itemView.findViewById(R.id.download_checkbox);
            mDownloadCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Object obj = mClipInformationList.get(getAdapterPosition());
                if (obj.getClass() == YouTubeDownloadListInformation.class) {
                    YouTubeDownloadListInformation information = (YouTubeDownloadListInformation) obj;
                    if (isChecked){
                        onButtonClickListeners.onItemCheck(information);
                    }else {
                        onButtonClickListeners.onItemUncheck(information);
                    }
                }
            });

            mDeleteButton.setOnClickListener(v -> {
                Object obj = mClipInformationList.get(getAdapterPosition());
                if (obj.getClass() == YouTubeDownloadListInformation.class){
                    YouTubeDownloadListInformation information = (YouTubeDownloadListInformation) obj;
                    int iTag = information.getITag();
                    DownloadListFragment.dbHandler.deleteYouTubeClip(mTitleTextView.getText().toString(),
                            iTag);
                    DownloadListFragment.getDownloadList();
                    notifyDataSetChanged();
                    NavHostActivity.mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                            setNumber(mClipInformationList.size());
                }
            });

            mDownloadButton.setOnClickListener(v -> {
                Object obj = mClipInformationList.get(getAdapterPosition());
                if (obj.getClass() == YouTubeDownloadListInformation.class){
                    YouTubeDownloadListInformation information = (YouTubeDownloadListInformation) obj;
                    String link = information.getDownloadURL();
                    String title = information.getTitle();
                    String ext = information.getExtension();

                    YouTubeDownloadURL youTubeDownloadURL = new YouTubeDownloadURL(mContext, link);
                    youTubeDownloadURL.downloadVideoFromURL(link, title, ext);
                }
            });
        }

        public void bind(String format, String title, String id) {
            android.os.Handler handler = new Handler();
            Runnable task = () -> {
                try
                {
                    String url = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    handler.post(() -> {
                        mThumbnail.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128, 64, false));
                        mFormatTextView.setText(format);
                        mTitleTextView.setText(title);
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            new Thread(task).start();
        }

        @Override
        public void onClick(View v) {
        }
    }
}
