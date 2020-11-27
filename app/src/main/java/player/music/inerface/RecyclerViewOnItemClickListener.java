package player.music.inerface;


import android.view.View;

public interface RecyclerViewOnItemClickListener {

    void recyclerViewItemClicked(View view, int position, long itemId);
    void recyclerViewItemClicked(View view, int position);
    void recyclerViewOverflowClicked(View view, int position);
}
