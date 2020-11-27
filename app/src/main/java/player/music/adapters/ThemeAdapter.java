package player.music.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import player.music.R;
import player.music.inerface.ThemeSelection;


public class ThemeAdapter  extends RecyclerView.Adapter <ThemeAdapter.MyViewHolder> {


    private Context context;
    public static ThemeSelection themeSelection;
    private int[] colorCodes;



    public ThemeAdapter( int[] colorCodes) {

        this.colorCodes = colorCodes;
    }



    @Override
    public @NonNull
    ThemeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_row_layout,
                parent, false);
        context = parent.getContext();

        return new ThemeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeAdapter.MyViewHolder holder, int position) {

        holder.cvTheme.setCardBackgroundColor( colorCodes[position] );


    }

    @Override
    public int getItemCount() {
        try {

            return colorCodes.length;

        } catch (NullPointerException e) {
            return 0;
        } catch (Exception e) {
            Log.e("ERROR_AAL_GetThemeCount", e.toString());
            return 0;
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CardView cvTheme;


        MyViewHolder(final View view) {
            super(view);

            cvTheme = view.findViewById(R.id.cv_theme);


            cvTheme.setOnClickListener( this);


        }


        @Override
        public void onClick(View view) {
            themeSelection.onThemeSelected(getLayoutPosition());
        }


    }


}
