package pl.edu.pum.movie_downloader.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.fragments.ClipInformationFragment;

public class DownloadListRecyclerViewAdapter extends RecyclerView.Adapter<DownloadListRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity mContext;
    private List<Pair<String, String>> mClipInformationList;
    private List<Pair<Integer, String>> mInformationToDownloadList;

    public DownloadListRecyclerViewAdapter ( FragmentActivity context, List<Pair<String, String>> clipInformationList, List<Pair<Integer, String>> informationToDownloadList) {
        this.mContext = context;
        this.mClipInformationList = clipInformationList;
        this.mInformationToDownloadList = informationToDownloadList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadListRecyclerViewAdapter .ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.download_list_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String format = mClipInformationList.get(position).first;
        final String title = mClipInformationList.get(position).second;
        final String id = mInformationToDownloadList.get(position).second;
        holder.bind(format, title, id);
    }

    @Override
    public int getItemCount() {
        return mClipInformationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mThumbnail;
        private TextView mTitleTextView;
        private TextView mFormatTextView;
        private Button mDownloadButton;
        private Button mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail = itemView.findViewById(R.id.thumbnail_image_view);
            mTitleTextView = itemView.findViewById(R.id.title_text_view_recycle);
            mFormatTextView = itemView.findViewById(R.id.format_text_view_recycle);
            mDownloadButton = itemView.findViewById(R.id.download_button);
            mDeleteButton = itemView.findViewById(R.id.delete_from_list_button);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClipInformationList.remove(getAdapterPosition());
                    mInformationToDownloadList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            mDownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itag = mInformationToDownloadList.get(getAdapterPosition()).first;
                    ClipInformationFragment.mYouTubeDownloadURL.downloadVideo(itag);
                }
            });

        }

        public void bind(String format, String title, String id) {
            //Glide.with(mContext).load("http://img.youtube.com/vi/-OKrloDzGpU/mqdefault.jpg").into(mThumbnail);
            android.os.Handler handler = new Handler();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        String url = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Added new clip to list", Toast.LENGTH_LONG).show();
                                mThumbnail.setImageBitmap(bitmap);
                                mFormatTextView.setText(mFormatTextView.getText().toString() + " " +  format);
                                mTitleTextView.setText(mTitleTextView.getText().toString() + " " + title);
                            }
                        });

                    }catch (Exception e){
                        Toast.makeText(mContext, "Poszlo nie tak", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            };
            new Thread(task).start();
        }

        @Override
        public void onClick(View v) {


        }
    }
}
