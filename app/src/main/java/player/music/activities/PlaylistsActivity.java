package player.music.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import player.music.R;
import player.music.managers.DatabaseManager;

public class PlaylistsActivity extends AppCompatActivity {



    private Button btnCreatePlaylist;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.playlists_layout);


        btnCreatePlaylist = findViewById(R.id.btn_create_playlist);


        DatabaseManager.openDataBase(this);
        DatabaseManager.createPlaylistsTable();




        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });




    }

}
