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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Album;


public class AlbumsAdapter extends RecyclerView.Adapter <AlbumsAdapter.MyViewHolder> {


    private Context context;
    private static RecyclerViewOnItemClickListener rvOnItemClickListener;
    public static List<Album> albums;
    private int lastPosition = -1;



    public AlbumsAdapter( /*List<Album> albums,*/ RecyclerViewOnItemClickListener rvOnItemClickListener) {

    //    this.albums = albums;
        this.rvOnItemClickListener = rvOnItemClickListener;
    }

    @Override
    public @NonNull AlbumsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_row_fragment_layout,
                parent, false);
        context = parent.getContext();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumsAdapter.MyViewHolder holder, int position) {

        holder.mtvAlbumName.setText(albums.get(position).getAlbumTitle());
        holder.mtvArtistName.setText(albums.get(position).getArtistName());
        int numberOfSongs = albums.get(position).getNumberOfTracks();
        if ( numberOfSongs == 1 ) {
            holder.mtvNoOfTracks.setText(numberOfSongs + " " + context.getResources().getString(R.string.track) );
        } else {
            holder.mtvNoOfTracks.setText(numberOfSongs + " " +context.getResources().getString(R.string.tracks) );
        }

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        Glide.with(context)
                .load( "file://" + albums.get(position).getAlbumArt() )
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivAlbumImage);

      /*  try {
            Drawable drawable = Drawable.createFromPath("file://" + albums.get(position).getAlbumArt());
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            Palette.Builder palette = Palette.from(bitmap);

            palette.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {

                    String hexCode = Integer.toHexString(palette.getLightMutedColor(
                            context.getResources().getColor(R.color.colorPrimary)));

                    *//*hexCode.substring(0, 2), 16) << 24*//*

                    int colorCode = (Integer.parseInt("33", 16) << 24) +
                            Integer.parseInt(hexCode.substring(2), 16);


                    holder.llAlbum.setBackgroundColor(colorCode);
                }
            });
        } catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }*/

      /*  try {

        if ( albums.get(position).getAlbumArt() != null && !albums.get(position).getAlbumArt().isEmpty() ){

            Drawable drawable =  ((ImageView)holder.ivAlbumImage).getDrawable();
            Bitmap bitmap = ((GlideBitmapDrawable) drawable.getCurrent()).getBitmap();

                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {


                        holder.llAlbum.setBackgroundColor(palette.getVibrantColor(
                                context.getResources().getColor(R.color.colorBackgroundPrimary)));

                        holder.mtvAlbumName.setTextColor(palette.getLightMutedColor(
                                context.getResources().getColor(R.color.colorAccent)));

                        holder.mtvNoOfTracks.setTextColor(palette.getLightMutedColor(
                                context.getResources().getColor(R.color.colorAccent)));
                    }
                });

        }

        } catch (Exception e){

            Toast.makeText(context, "exception in albums fragment: " + e, Toast.LENGTH_SHORT).show();
        }
*/



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

        private ImageView ivOverflow;
        private ImageView ivAlbumImage;
        private UbuntuTextView mtvAlbumName, mtvArtistName, mtvNoOfTracks;
        private LinearLayout llAlbum;



        MyViewHolder(final View view) {
            super(view);

            ivAlbumImage = view.findViewById(R.id.iv_album);
            mtvAlbumName = view.findViewById(R.id.text_view_album_name_album_layout);
            mtvArtistName = view.findViewById(R.id.text_view_artist_name_album_layout);
            mtvNoOfTracks = view.findViewById(R.id.text_view_number_of_tracks_album_layout);
            ivOverflow = view.findViewById(R.id.iv_overflow_menu);
            llAlbum = view.findViewById(R.id.ll_album);


            ivOverflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rvOnItemClickListener.recyclerViewOverflowClicked(view, getLayoutPosition());
                }
            });

            view.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            rvOnItemClickListener.recyclerViewItemClicked(view, this.getLayoutPosition(),
                    albums.get(this.getAdapterPosition()).getAlbumId());
        }
    }

    
}
