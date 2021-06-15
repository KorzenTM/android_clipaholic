package pl.edu.pum.movie_downloader.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.models.DownloadHistoryDTO;

public class DownloadHistoryRecyclerViewAdapter extends RecyclerView.Adapter<DownloadHistoryRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity mContext;
    public final List<DownloadHistoryDTO> mDownloadHistoryList;

    public DownloadHistoryRecyclerViewAdapter ( FragmentActivity context, List<DownloadHistoryDTO> downloadHistoryList) {
        this.mContext = context;
        this.mDownloadHistoryList = downloadHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadHistoryRecyclerViewAdapter.ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.download_history_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadHistoryDTO obj = mDownloadHistoryList.get(position);
        holder.bind(obj.getTitle(), obj.getFormat(), obj.getDownloadDate(), obj.getSource());
    }

    @Override
    public int getItemCount() {
        return mDownloadHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDateTextView;
        private final TextView mTitleTextView;
        private final TextView mFormatTextView;
        private final ImageView mLogoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mDateTextView = itemView.findViewById(R.id.download_date_text_view);
            mTitleTextView = itemView.findViewById(R.id.history_title_text_view);
            mFormatTextView = itemView.findViewById(R.id.history_format_text_view);
            mLogoImageView = itemView.findViewById(R.id.logo_image_view);

        }

        public void bind(String title, String format, String date, String source) {
            mDateTextView.setText(date);
            mTitleTextView.setText(title);
            mFormatTextView.setText(format);
            if (source.equals("youtube")){
                mLogoImageView.setImageResource(R.mipmap.youtube_icon);

            }
            else if (source.equals("vimeo")){
                mLogoImageView.setImageResource(R.mipmap.vimeo_icon);
            }

        }

    }
}
