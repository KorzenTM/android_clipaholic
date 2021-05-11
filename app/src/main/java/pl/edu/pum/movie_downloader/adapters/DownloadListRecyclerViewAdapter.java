package pl.edu.pum.movie_downloader.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.youtube.model.Thumbnail;

import java.util.List;

import pl.edu.pum.movie_downloader.R;

public class DownloadListRecyclerViewAdapter extends RecyclerView.Adapter<DownloadListRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity mContext;
    private final List<Pair<Thumbnail, String>>  mDownloadList;

    public DownloadListRecyclerViewAdapter ( FragmentActivity context, List<Pair<Thumbnail, String>>  downloadlist) {
        this.mContext = context;
        this.mDownloadList = downloadlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadListRecyclerViewAdapter .ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.download_list_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Thumbnail videoThumbnail = mDownloadList.get(position).first;
        final String title = mDownloadList.get(position).second;
        holder.bind(videoThumbnail, title);
    }

    @Override
    public int getItemCount() {
        return mDownloadList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mThumbnail;
        private final TextView mTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail = itemView.findViewById(R.id.thumbnail_image_view);
            mTitleTextView = itemView.findViewById(R.id.title_text_view_recycle);

        }

        public void bind(Thumbnail thumbnail, String title) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(thumbnail.toString(), MediaStore.Images.Thumbnails.MINI_KIND);
            mThumbnail.setImageBitmap(bitmap);
            mTitleTextView.setText(title);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
