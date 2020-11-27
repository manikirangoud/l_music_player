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

import java.util.List;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Artist;

public class ArtistsAdapter extends RecyclerView.Adapter <ArtistsAdapter.MyViewHolder> {


    public static List<Artist> artists;
    public static RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    private int lastPosition = -1;
    private Context context;

    public ArtistsAdapter( List<Artist> artists) {

        this.artists = artists;
    }

    @Override
    public @NonNull ArtistsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_row_fragment_layout, parent, false);
        this.context = parent.getContext();
        return new ArtistsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.MyViewHolder holder, int position) {

        String albumsCount, tracksCount, artistName;
        int numberOfSongs, numberOfAlbums;

        artistName = artists.get(position).getArtistName();
        numberOfAlbums = artists.get(position).getNumberOfAlbums();
        numberOfSongs = artists.get(position).getNumberOfTracks();

        try {
            holder.utvArtistStartLetter.setText(artistName.substring(0, 2));
            holder.utvArtistName.setText(artistName);
        } catch (NullPointerException e) {
            Log.e("ArtistsAdapter", e.toString());
        }

        if( numberOfAlbums == 1 ) {
            albumsCount = numberOfAlbums + " Album";
        } else {
            albumsCount = numberOfAlbums + " Albums";
        }
        if( numberOfSongs == 1 ) {
            tracksCount = numberOfSongs + " Track";
        } else {
            tracksCount = numberOfSongs + " Tracks";
        }
        holder.utvAlbumsCount.setText(albumsCount);
        holder.utvTracksCount.setText(tracksCount);

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        try {
            return artists.size();
        }catch (NullPointerException e) {
            return 0;
        }catch (Exception e) {
            Log.e("ERROR_AAL_GetItemCount", e.toString());
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private UbuntuTextView utvArtistName;
        private UbuntuTextView utvAlbumsCount;
        private UbuntuTextView utvArtistStartLetter;
        private UbuntuTextView utvTracksCount;
        private ImageView ivOverflow;

        MyViewHolder(final View view) {
            super(view);


            utvArtistName = view.findViewById(R.id.utv_artist_name);
            utvAlbumsCount = view.findViewById(R.id.utv_album_count);
            utvTracksCount = view.findViewById(R.id.utv_track_count);
            utvArtistStartLetter = view.findViewById(R.id.mtv_artist_letters);
            ivOverflow = view.findViewById(R.id.iv_overflow_menu);

            ivOverflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recyclerViewOnItemClickListener.recyclerViewOverflowClicked(view, getLayoutPosition());
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewOnItemClickListener.recyclerViewItemClicked( view, getLayoutPosition(),
                    artists.get(getLayoutPosition()).getId() );
        }
    }
}
