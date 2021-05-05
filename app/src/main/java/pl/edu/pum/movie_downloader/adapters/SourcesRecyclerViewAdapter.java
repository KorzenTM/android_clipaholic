package pl.edu.pum.movie_downloader.adapters;

import android.media.Image;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import pl.edu.pum.movie_downloader.R;

public class SourcesRecyclerViewAdapter extends RecyclerView.Adapter<SourcesRecyclerViewAdapter.ViewHolder>
{
    private final View mView;
    private final FragmentActivity mContext;
    private final List<Pair<Integer, String>>  mSources;

    public SourcesRecyclerViewAdapter(View view,FragmentActivity context, List<Pair<Integer, String>>  sources)
    {
        this.mView = view;
        this.mContext = context;
        this.mSources = sources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new SourcesRecyclerViewAdapter.ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.sources_recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final int imageResID = mSources.get(position).first;
        final String title = mSources.get(position).second;
        holder.bind(imageResID, title);

    }

    @Override
    public int getItemCount()
    {
        return mSources.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView mLogoImageView;
        private final TextView mTitleTextView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mLogoImageView = itemView.findViewById(R.id.source_icon_image_view);
            mTitleTextView = itemView.findViewById(R.id.source_title_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int itemPosition = this.getAdapterPosition();
            Navigation.findNavController(mView).navigate(R.id.action_home_fragment_to_clip_information_fragment);

        }

        public void bind(int resID, String title)
        {
            mLogoImageView.setImageResource(resID);
            mTitleTextView.setText(title);
        }
    }
}
