package player.music.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import player.music.Constants;
import player.music.R;
import player.music.adapters.ThemeAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.ThemeSelection;

public class SettingsActivity extends AppCompatActivity implements ThemeSelection {


    private Toolbar toolbar;
    private UbuntuTextView mtvChangeBackground;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView rvChooseTheme;
    private GridLayoutManager gridLayoutManager;
    private ThemeAdapter themeAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);



        toolbar = findViewById(R.id.toolbar);
        mtvChangeBackground = findViewById(R.id.mtv_change_background);
        rvChooseTheme = findViewById(R.id.rv_choose_theme);

        sharedPreferences = getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);


        toolbar.setTitle(getResources().getString(R.string.settings));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
        toolbar.setTitleTextAppearance(this, R.style.MyToolbarTextView);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.left_arrow);




//Setting recycler view for bottom sheet.
        ThemeAdapter.themeSelection = this;
        themeAdapter = new ThemeAdapter( getResources().getIntArray(R.array.themeColors) );
        gridLayoutManager = new GridLayoutManager(this, 3);
        rvChooseTheme.setLayoutManager( gridLayoutManager );
        rvChooseTheme.setAdapter( themeAdapter );



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.no_action, R.anim.left_to_right);
            }
        });



        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });









    }



    @Override
    public void onThemeSelected(int position) {


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("background", position);
        editor.apply();

        sendBroadcast(new Intent(Constants.ACTIONS.CHANGE_BACKGROUND));
    }



}
