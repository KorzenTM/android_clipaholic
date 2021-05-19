package pl.edu.pum.movie_downloader.adapters;

import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

import pl.edu.pum.movie_downloader.R;

public class AvailableSourcesRecyclerViewAdapter extends RecyclerView.Adapter<AvailableSourcesRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity mContext;
    private final List<Pair<Integer, String>>  mSources;

    public AvailableSourcesRecyclerViewAdapter(FragmentActivity context, List<Pair<Integer, String>>  sources) {
        this.mContext = context;
        this.mSources = sources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AvailableSourcesRecyclerViewAdapter.ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.sources_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int imageResID = mSources.get(position).first;
        final String title = mSources.get(position).second;
        holder.bind(imageResID, title);

    }

    @Override
    public int getItemCount() {
        return mSources.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mLogoImageView;
        private final TextView mTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLogoImageView = itemView.findViewById(R.id.source_icon_image_view);
            mTitleTextView = itemView.findViewById(R.id.source_title_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = this.getAdapterPosition();
            switch (itemPosition){
                case 0:
                    redirectionToSource( "https://www.youtube.com/",  "vnd.youtube");
                    break;
                case 1:
                    redirectionToSource( "https://www.facebook.com/", "fb://page/");
                    break;
                case 2:
                    redirectionToSource("http://player.vimeo.com/", "com.vimeo.android.videoapp");
            }
        }

        private void redirectionToSource(String homeLink, String appHome){
            Toast.makeText(mContext, "Redirect...", Toast.LENGTH_SHORT).show();
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appHome));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeLink));
            try {
                mContext.startActivity(appIntent);
            } catch (Exception e) {
                mContext.startActivity(webIntent);
            }
        }

        public void bind(int resID, String title) {
            mLogoImageView.setImageResource(resID);
            mTitleTextView.setText(title);
        }
    }
}
