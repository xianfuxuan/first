package gamegame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import gamegame.mirage.MirageActivity;
import gamegame.plane.PlaneActivity;
import gamegame.snake.SnakeActivity;
import gamegame.twoballs.TwoBallsActivity;

public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Button buttonStart = (Button) findViewById(R.id.id_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"hello!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TwoBallsActivity.class);
                startActivity(intent);
            }
        });
        Button buttonSnake = (Button) findViewById(R.id.id_snake);
        buttonSnake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"hello, snake!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SnakeActivity.class);
                startActivity(intent);
            }
        });
        Button buttonPlane = (Button) findViewById(R.id.id_plane);
        buttonPlane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"hello, plane!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PlaneActivity.class);
                startActivity(intent);
            }
        });
        Button buttonMirage = (Button) findViewById(R.id.id_mirage);
        buttonMirage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"hello, mirage!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MirageActivity.class);
                startActivity(intent);
            }
        });
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        TwoBallsView twoBallsView = new TwoBallsView(this);
//        twoBallsView.setViewHeigth(displayMetrics.heightPixels);
//        twoBallsView.setViewWidth(displayMetrics.widthPixels);
//        setContentView(twoBallsView);
    }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Intent intent = new Intent(this, TwoBallsActivity.class);
//        startActivity(intent);
//        return super.onTouchEvent(event);
//    }
}
