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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import player.music.R;
import player.music.extras.Tools;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Song;


public class ArtistSongsAdapter extends RecyclerView.Adapter <ArtistSongsAdapter.MyViewHolder> {


    private Context context;
    private List<Song> songs;
    private static RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    private int lastPosition = -1;

    public ArtistSongsAdapter(List<Song> songs , RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {


        this.songs = songs;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public @NonNull ArtistSongsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_fragment_layout, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSongsAdapter.MyViewHolder holder, int position) {


        try {
            holder.textViewSongTitle.setText(songs.get(position).getTitle());
            holder.textViewSongDetails.setText(songs.get(position).getAlbumTitle() + " | " +
                    songs.get(position).getArtistName());
            holder.textViewSongDuration.setText(Tools.getSongTime(songs.get(position).getDuration()));

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

            Glide.with(context).load("file://" +
                    songs.get(position).getAlbumArt())
                    .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewAlbumImage);

        } catch (Exception e) {
            Log.e("Bind", e.toString());
        }

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

        private TextView textViewSongTitle, textViewSongDetails, textViewSongDuration;
        private ImageView imageViewAlbumImage, imageViewOverflowMenu;

        MyViewHolder(final View view) {
            super(view);

            textViewSongTitle = view.findViewById(R.id.utv_track_title);
            textViewSongDetails = view.findViewById(R.id.utv_artist_name);
            imageViewAlbumImage = view.findViewById(R.id.iv_album_art);
            imageViewOverflowMenu = view.findViewById(R.id.iv_overflow_menu);
            textViewSongDuration = view.findViewById(R.id.utv_track_duration);


            view.setOnClickListener(this);

            imageViewOverflowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recyclerViewOnItemClickListener.recyclerViewOverflowClicked( view, getAdapterPosition() );
                }
            });
        }

        @Override
        public void onClick(View view) {
            recyclerViewOnItemClickListener.recyclerViewItemClicked( view, getAdapterPosition() );
        }
    }
}
