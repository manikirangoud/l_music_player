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

import java.util.List;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Tools;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Song;

public class AlbumSongsAdapter extends RecyclerView.Adapter <AlbumSongsAdapter.MyViewHolder> {

    private Context context;
    private static List<Song> songs;
    private static RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    private int lastPosition = -1;

    public AlbumSongsAdapter(List<Song> songs, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener){

        this.songs = songs;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @NonNull
    @Override
    public AlbumSongsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_row_album_details,
                viewGroup, false);
        context = viewGroup.getContext();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongsAdapter.MyViewHolder viewHolder, int position) {

        viewHolder.textViewSongTitle.setText(songs.get(position).getTitle());
        viewHolder.textViewArtistName.setText(songs.get(position).getArtistName());
        String duration = Tools.getSongTime( songs.get(position).getDuration() ) + "";
        viewHolder.textViewSongDuration.setText( duration );

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {

        return songs.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private UbuntuTextView textViewSongTitle, textViewArtistName, textViewSongDuration;
        private ImageView imageViewOverflowMenu;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            textViewSongTitle = itemView.findViewById(R.id.utv_track_title);
            textViewArtistName = itemView.findViewById(R.id.utv_artist_name);
            textViewSongDuration = itemView.findViewById(R.id.utv_track_duration);
            imageViewOverflowMenu = itemView.findViewById(R.id.iv_overflow_menu);


            imageViewOverflowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recyclerViewOnItemClickListener.recyclerViewOverflowClicked(view, getLayoutPosition());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            recyclerViewOnItemClickListener.recyclerViewItemClicked(view, this.getLayoutPosition(),
                    songs.get(this.getAdapterPosition()).getId());
        }
    }
}
