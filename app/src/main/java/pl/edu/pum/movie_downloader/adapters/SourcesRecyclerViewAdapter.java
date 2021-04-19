package pl.edu.pum.movie_downloader.adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.pum.movie_downloader.R;

public class SourcesRecyclerViewAdapter extends RecyclerView.Adapter<SourcesRecyclerViewAdapter.ViewHolder>
{
    private final FragmentActivity mContext;
    private List<Integer> mLogos;

    public SourcesRecyclerViewAdapter(FragmentActivity context, List<Integer> logos)
    {
        this.mContext = context;
        this.mLogos = logos;
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
        final int imageResID = mLogos.get(position);
        holder.bind(imageResID);

    }

    @Override
    public int getItemCount()
    {
        return mLogos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mLogoImageView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mLogoImageView = itemView.findViewById(R.id.source_icon_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int itemPosition = this.getAdapterPosition();
            Toast.makeText(mContext, "Clicked " + itemPosition, Toast.LENGTH_SHORT).show();

        }

        public void bind(int resID)
        {
            mLogoImageView.setImageResource(resID);
        }
    }
}
