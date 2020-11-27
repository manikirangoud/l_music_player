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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.ArtistDetailsAlbumClick;
import player.music.objects.Album;

public class ArtistAlbumAdapter extends RecyclerView.Adapter <ArtistAlbumAdapter.MyViewHolder> {


    private Context context;
    public static ArtistDetailsAlbumClick artistDetailsAlbumClick;
    private List<Album> albums;
    private int lastPosition = -1;

    public ArtistAlbumAdapter( List<Album> albums ) {

        this.albums = albums;
    }

    @Override
    public @NonNull
    ArtistAlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card_view,
                parent, false);
        context = parent.getContext();

        return new ArtistAlbumAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAlbumAdapter.MyViewHolder holder, int position) {

        try {

            holder.mtvAlbumName.setText(albums.get(position).getAlbumTitle());

            int numberOfTracks = albums.get(position).getNumberOfTracks() ;

            if ( numberOfTracks == 1 ) {
                holder.mtvNumberOfTracks.setText( numberOfTracks + " Track" );
            } else {
                holder.mtvNumberOfTracks.setText( numberOfTracks + " Tracks" );
            }

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

            Glide.with(context).load("file://" + albums.get(position).getAlbumArt())
                    .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewAlbumImage);
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        try {

            return albums.size();

        } catch (NullPointerException e) {
            return 0;
        } catch (Exception e) {
            Log.e("ERROR_AAL_GetItemCount", e.toString());
            return 0;
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageViewAlbumImage, imageViewOverflowMenu;
        private UbuntuTextView mtvAlbumName, mtvNumberOfTracks;

        MyViewHolder(final View view) {
            super(view);

            imageViewAlbumImage = view.findViewById(R.id.iv_album_art);
            mtvAlbumName = view.findViewById(R.id.mtv_album_title);
            mtvNumberOfTracks = view.findViewById(R.id.mtv_no_of_tracks);
            imageViewOverflowMenu = view.findViewById(R.id.iv_overflow_menu);

            view.setOnClickListener(this);
            imageViewOverflowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    artistDetailsAlbumClick.onAlbumMenuClick(view, getLayoutPosition());
                }
            });
        }


        @Override
        public void onClick(View view) {
            artistDetailsAlbumClick.onAlbumClick(view, this.getLayoutPosition());
        }
    }
}
