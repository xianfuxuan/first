package gamegame.mirage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

/**
 *
 */
public class MirageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        MirageView mirageView = new MirageView(this, displayMetrics.widthPixels, displayMetrics.heightPixels);
        setContentView(mirageView);
    }
}
