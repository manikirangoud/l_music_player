package player.music.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import player.music.R;
import player.music.objects.Song;

public class ViewPagerAdapter extends PagerAdapter {

    public static List<Song> songs;
    public Context context;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {

        if (songs != null){
            return songs.size();
        }

        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.image_view, container, false);

        ImageView imageView = viewGroup.findViewById(R.id.iv_vp_now_playing);

        Glide.with(context).load("file://" +
                songs.get(position).getAlbumArt())
                .placeholder(R.drawable.album_icon)
                .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


        container.addView(viewGroup);

        return viewGroup;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {


        return view == object;
    }
}
