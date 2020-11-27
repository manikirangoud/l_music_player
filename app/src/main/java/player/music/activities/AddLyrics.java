package player.music.activities;

import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import player.music.R;

public class AddLyrics extends AppCompatActivity {


    private Button buttonAddLyrics;
    private EditText editTextAddLyrics;
    private Bundle bundle;
    private String albumTitle, songTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.add_lyric_layout);


        editTextAddLyrics = findViewById(R.id.et_add_lyrics);
        buttonAddLyrics = findViewById(R.id.btn_add_lyrics);


        bundle = getIntent().getExtras();
        songTitle = bundle.getString("songTitle", "");
        albumTitle = bundle.getString("albumTitle", "");


        getUnsyncedLyrics();


        buttonAddLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lyrics = editTextAddLyrics.getText().toString();
                if (lyrics.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please add lyrics",
                            Toast.LENGTH_LONG).show();
                } else {


                    File root = Environment.getExternalStorageDirectory();
                    File path = new File(root.getAbsolutePath() + "/" + "Data Of Unsynced Lyrics"
                            + "/" + albumTitle );

                    File file = new File(path, songTitle + ".txt");

                    try{

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));

                        outputStreamWriter.write(lyrics);
                        outputStreamWriter.flush();
                        outputStreamWriter.close();

                        Toast.makeText(AddLyrics.this, "Lyrics saved successfully!", Toast.LENGTH_LONG).show();
                        setResult(1);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(AddLyrics.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }


    private void getUnsyncedLyrics() {

        File root = Environment.getExternalStorageDirectory();
        File path = new File(root.getAbsolutePath() + "/" + "Data Of Unsynced Lyrics"
                + "/" + albumTitle );

        File file = new File(path, songTitle + ".txt");

        List<String> lyrics = new ArrayList<>();

        try{
            if (file.exists()) {


                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = bufferedReader.readLine()) != null ) {

                    if ( !line.isEmpty() ) {
                        lyrics.add(line);
                    }
                }
                if (lyrics.size() > 0) {

                    for (String text : lyrics) {
                        editTextAddLyrics.append(text + "\n");
                    }
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
