package player.music.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Tools;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Song;

public class NowPlayingAdapter extends RecyclerView.Adapter <NowPlayingAdapter.MyViewHolder> {

    private Context context;
    public static List<Song> songs;
    private int lastPosition = -1;

    private static RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public NowPlayingAdapter(/*List<Song> songs, */ RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {

        //     this.songs = songs;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public @NonNull NowPlayingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                      int viewType) {

        //On creating view and returning view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_fragment_layout,
                parent, false);
        context = parent.getContext();
        return new NowPlayingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NowPlayingAdapter.MyViewHolder holder, int position) {

        long startTime = System.currentTimeMillis();

        //On binding the view to recycler view
        //Setting the single song details to recycler view
        holder.textViewSongTitle.setText(songs.get(position).getTitle());
        String subText = songs.get(position).getAlbumTitle() + " | " + songs.get(position).getArtistName();
        holder.textViewSongDetails.setText(subText);
        holder.textViewSongDuration.setText(Tools.getSongTime(songs.get(position).getDuration()));


        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        Glide.with(context).load("file://" +
                songs.get(position).getAlbumArt())
                .placeholder(R.drawable.album_icon)
                .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewAlbumImage);

        Log.e("Bind View Time: ", (System.currentTimeMillis() - startTime) + "");
    }

    @Override
    public int getItemCount() {
        try {
            return songs.size();
        } catch (NullPointerException e) {
            return 0;
        } catch (Exception e) {
            Log.e("ERROR_ASL_GetItemCount", e.toString());
            return 0;
        }

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private UbuntuTextView textViewSongTitle, textViewSongDetails, textViewSongDuration;
        private ImageView imageViewAlbumImage;

        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            textViewSongTitle = view.findViewById(R.id.utv_track_title);
            textViewSongDetails = view.findViewById(R.id.utv_artist_name);
            imageViewAlbumImage = view.findViewById(R.id.iv_album_art);
            textViewSongDuration = view.findViewById(R.id.utv_track_duration);

        }


        @Override
        public void onClick(View view) {

            recyclerViewOnItemClickListener.recyclerViewItemClicked(view, this.getLayoutPosition(),
                   songs.get(this.getAdapterPosition()).getId());
        }
    }
}
