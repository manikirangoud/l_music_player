package player.music.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import player.music.R;


public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_activity_layout);

        toolbar = ( Toolbar ) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_toolbar_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.drawable.left_arrow);
        setSupportActionBar(toolbar);



//WHEN CLICKED ON NAVAIGATION ICON
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_action, R.anim.left_to_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_action, R.anim.left_to_right);
    }
}
