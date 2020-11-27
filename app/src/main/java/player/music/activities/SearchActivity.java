package player.music.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import player.music.R;


public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextSearch;
    private ImageView imageViewCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_activity_layout);

        toolbar = ( Toolbar ) findViewById(R.id.toolbar_search);
        editTextSearch = ( EditText ) findViewById(R.id.edit_text_search_search_layout);
        imageViewCancel = ( ImageView ) findViewById(R.id.image_view_cancel_search_layout);



        toolbar.setNavigationIcon(R.drawable.left_arrow);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_action, R.anim.left_to_right);
            }
        });

        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearch.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_action, R.anim.left_to_right);
    }
}
