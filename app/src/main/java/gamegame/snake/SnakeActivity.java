package gamegame.snake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import gamegame.twoballs.TwoBallsView;

/**
 *
 */
public class SnakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        SnakeView snakeView = new SnakeView(this, displayMetrics.widthPixels, displayMetrics.heightPixels);
        setContentView(snakeView);
    }
}
