package player.music.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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


public class TracksAdapter extends RecyclerView.Adapter <TracksAdapter.MyViewHolder> {


    private Context context;
    public static List<Song> songs;
    private int lastPosition = -1;

    private static RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public TracksAdapter(/*List<Song> songs, */ RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {

   //     this.songs = songs;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public @NonNull TracksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //On creating view and returning view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_fragment_layout,
                parent, false);
        context = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TracksAdapter.MyViewHolder holder, int position) {

        //On binding the view to recycler view
        //Setting the single song details to recycler view
        holder.utvTrackTitle.setText(songs.get(position).getTitle());
        holder.utvTrackAlbum.setText(songs.get(position).getAlbumTitle());
        holder.utvTrackArtist.setText(songs.get(position).getArtistName());
        holder.utvTrackDuration.setText(Tools.getSongTime(songs.get(position).getDuration()));

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        Glide.with(context).load("file://" +
                 songs.get(position).getAlbumArt())
                .placeholder(R.drawable.album_icon)
                .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivAlbumImage);
    }


    @Override
    public int getItemCount() {

        if (songs != null){
            return songs.size();
        }
        return 0;
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull TracksAdapter.MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private UbuntuTextView utvTrackTitle;
        private UbuntuTextView utvTrackArtist;
        private UbuntuTextView utvTrackAlbum;
        private UbuntuTextView utvTrackDuration;
        private ImageView ivAlbumImage;
        private ImageView ivOverflow;

        MyViewHolder(final View view) {
            super(view);
            view.setOnClickListener(this);

            utvTrackTitle = view.findViewById(R.id.utv_track_title);
            utvTrackAlbum = view.findViewById(R.id.utv_album_name);
            utvTrackArtist = view.findViewById(R.id.utv_artist_name);
            ivAlbumImage = view.findViewById(R.id.iv_album_art);
            ivOverflow = view.findViewById(R.id.iv_overflow_menu);
            utvTrackDuration = view.findViewById(R.id.utv_track_duration);

            ivOverflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewOnItemClickListener.recyclerViewOverflowClicked(view, getLayoutPosition());
                }
            });
        }


        @Override
        public void onClick(View view) {
            recyclerViewOnItemClickListener.recyclerViewItemClicked(view, this.getLayoutPosition(),
                    songs.get(this.getAdapterPosition()).getId());
        }


    }
}
