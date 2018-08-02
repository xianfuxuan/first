package gamegame.twoballs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import gamegame.R;

/**
 *
 */
public class TwoBallsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        TwoBallsView twoBallsView = new TwoBallsView(this, displayMetrics.widthPixels, displayMetrics.heightPixels);
        setContentView(twoBallsView);
    }
}
