package pl.edu.pum.movie_downloader.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.downloader.Downloader;
import pl.edu.pum.movie_downloader.fragments.DownloadListFragment;
import pl.edu.pum.movie_downloader.models.DownloadListInformation;

public class DownloadListRecyclerViewAdapter extends RecyclerView.Adapter<DownloadListRecyclerViewAdapter.ViewHolder> {
    public interface OnButtonClickListener{
        void onItemCheck(DownloadListInformation downloadListInformation);
        void onItemUncheck(DownloadListInformation downloadListInformation);
    }

    private final FragmentActivity mContext;
    public final List<DownloadListInformation> mClipInformationList;
    public static boolean isSelectable = false;
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
        String title = information.getTitle();
        if (title.length() > 10){
            title = title.substring(0, 10) + "...";
        }
        final String id = information.getID();
        holder.bind(format, title, id);
    }

    @Override
    public int getItemCount() {
        return mClipInformationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final ImageView mThumbnail;
        private final TextView mTitleTextView;
        private final TextView mFormatTextView;
        public final CheckBox mDownloadCheckbox;
        public final ImageButton mDeleteButton;
        public final ImageButton mDownloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail = itemView.findViewById(R.id.thumbnail_image_view);
            mTitleTextView = itemView.findViewById(R.id.title_text_view_recycle);
            mFormatTextView = itemView.findViewById(R.id.format_text_view_recycle);
            mDeleteButton = itemView.findViewById(R.id.delete_from_list_button);
            mDownloadButton = itemView.findViewById(R.id.download_button);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

            mDownloadCheckbox = itemView.findViewById(R.id.download_checkbox);
            mDownloadCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                if (isChecked){
                    itemView.setBackgroundColor(Color.LTGRAY);
                    itemView.setBackgroundResource(R.drawable.selected_recycler_view_background);
                    mDeleteButton.setBackgroundColor(Color.rgb(211, 211, 211));
                    mDownloadButton.setBackgroundColor(Color.rgb(211, 211, 211));
                    onButtonClickListeners.onItemCheck(information);
                }else {
                    itemView.setBackgroundColor(Color.WHITE);
                    itemView.setBackgroundResource(R.drawable.recycler_view_background);
                    mDeleteButton.setBackgroundColor(Color.WHITE);
                    mDownloadButton.setBackgroundColor(Color.WHITE);
                    onButtonClickListeners.onItemUncheck(information);
                    mDownloadCheckbox.setVisibility(View.GONE);
                    setAccessibilityOfButtons(true);
                }
            });

            mDeleteButton.setOnClickListener(v -> {
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                mClipInformationList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), mClipInformationList.size() - 1);
                if (mDownloadCheckbox.isChecked()){
                    onButtonClickListeners.onItemUncheck(information);
                }
                int iTag = information.getITag();
                DownloadListFragment.dbHandler.deleteClipFromList(information.getTitle(),
                        iTag);
                NavHostActivity.mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                        setNumber(mClipInformationList.size());
            });

            mDownloadButton.setOnClickListener(v -> {
                DownloadListInformation information = mClipInformationList.get(getAdapterPosition());
                Downloader downloader = new Downloader(mContext);
                String downloadURL = information.getDownloadURL();
                System.out.println(downloadURL);
                String title = information.getTitle();
                downloader.downloadFromUrl(downloadURL, title);
            });
        }

        public void bind(String format, String title, String id) {
            if (id.equals("yt")){
                mThumbnail.setImageResource(R.mipmap.youtube_icon);

            }
            else if (id.equals("vimeo")){
                mThumbnail.setImageResource(R.mipmap.vimeo_icon);
            }

            mFormatTextView.setText(format);
            mTitleTextView.setText(title);
        }

        @Override
        public boolean onLongClick(View v) {
            mDownloadCheckbox.setVisibility(View.VISIBLE);
            setAccessibilityOfButtons(false);
            mDownloadCheckbox.setChecked(true);
            isSelectable = true;
            return true;
        }

        @Override
        public void onClick(View v) {
            if (isSelectable){
                mDownloadCheckbox.setVisibility(View.VISIBLE);
                setAccessibilityOfButtons(false);
                mDownloadCheckbox.setChecked(!mDownloadCheckbox.isChecked());
            }
        }

        private void setAccessibilityOfButtons(boolean isEnable){
            mDownloadButton.setEnabled(isEnable);
            mDeleteButton.setEnabled(isEnable);
        }
    }
}
