package pl.edu.pum.movie_downloader.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import pl.edu.pum.movie_downloader.downloader.Downloader;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.fragments.DownloadListFragment;
import pl.edu.pum.movie_downloader.models.DownloadListInformation;

public class DownloadListRecyclerViewAdapter extends RecyclerView.Adapter<DownloadListRecyclerViewAdapter.ViewHolder> {
    public interface OnButtonClickListener{
        void onItemCheck(DownloadListInformation downloadListInformation);
        void onItemUncheck(DownloadListInformation downloadListInformation);
    }

    private final FragmentActivity mContext;
    public final List<DownloadListInformation> mClipInformationList;
    OnButtonClickListener onButtonClickListeners;

    public DownloadListRecyclerViewAdapter ( FragmentActivity context, List<DownloadListInformation> clipInformationList) {
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
        DownloadListInformation information = mClipInformationList.get(position);
        final String format = information.getFormat();
        final String title = information.getTitle();
        final String id = information.getID();
        holder.bind(format, title, id);
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
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                if (isChecked){
                    itemView.setBackgroundColor(Color.LTGRAY);
                    onButtonClickListeners.onItemCheck(information);
                }else {
                    itemView.setBackgroundColor(Color.WHITE);
                    onButtonClickListeners.onItemUncheck(information);
                }
            });

            mDeleteButton.setOnClickListener(v -> {
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                if (mDownloadCheckbox.isChecked()){
                    onButtonClickListeners.onItemUncheck(information);
                }
                int iTag = information.getITag();
                DownloadListFragment.dbHandler.deleteYouTubeClip(mTitleTextView.getText().toString(),
                        iTag);
                DownloadListFragment.getDownloadList();
                notifyDataSetChanged();
                NavHostActivity.mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                        setNumber(mClipInformationList.size());
            });

            mDownloadButton.setOnClickListener(v -> {
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                Downloader downloader = new Downloader(mContext);
                String link = information.getDownloadURL();
                String title = information.getTitle();
                String ext = information.getExtension();
                String filename = downloader.createFilename(title, ext);
                downloader.downloadFromUrl(link, title, filename);
            });
        }

        public void bind(String format, String title, String id) {
            if (!id.isEmpty()){
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
            else{
                mThumbnail.setImageResource(R.mipmap.vimeo_icon);
                mFormatTextView.setText(format);
                mTitleTextView.setText(title);
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
