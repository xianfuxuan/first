package gamegame.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import gamegame.twoballs.Ball;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class SnakeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;//绘图的画布
    private boolean drawing;
    private int TIME_IN_FRAME = 30;
    private int viewHeigth = 0;//1680;
    private int viewWidth = 0;//1060;
    private long GAME_TIME_START = System.currentTimeMillis();;
    private int GAME_BULLET_LIMIT = 8;
    private int GAME_FOOD_LIMIT = 20;
    private int SNAKE_SIZE_LIMIT = 30;
    private int SNAKE_TYPE_ONE = 1;
    private int SNAKE_TYPE_LONG = 9;
    private int snakeType = SNAKE_TYPE_ONE;
    private List<Ball> foodBalls = new ArrayList<>();
    private List<Ball> snakeBalls = new ArrayList<>();
    private Ball snakeBall;
    private int GAME_TIME_LIMIT = 30;
    private int GAME_STATUS_INIT = 0;
    private int GAME_STATUS_WIN = 1;
    private int GAME_STATUS_LOSE = 2;
    private int GAME_STATUS_TIMEUP = 3;
    private int win = GAME_STATUS_INIT;

    public SnakeView(Context context) {
        super(context);
        initView();
    }

    public SnakeView(Context context, int viewWidth, int viewHeigth) {
        super(context);
        this.viewWidth = viewWidth;
        this.viewHeigth = viewHeigth;
        initView();
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int REDBALLR = 100;
    private int REDBALLX = 400;
    private int REDBALLY = 300;
    private int BLUEBALLR = 60;
    private int BLUEBALLX = 300;
    private int BLUEBALLY = 900;
    private List<Ball> redBalls = new ArrayList<>(10);
    private List<Ball> blueBalls = new ArrayList<>(10);
    private Ball redBall = new Ball(REDBALLX, REDBALLY, REDBALLR, Color.RED);
    private Ball blueBall = new Ball(BLUEBALLX, BLUEBALLY, BLUEBALLR, Color.BLUE);

    private List<Ball> blueBulletBalls = new ArrayList<>();

    private List<Ball> ballSlices = new ArrayList<>();

    private Ball directionBall = new Ball(120, viewHeigth - 120, 120, Color.LTGRAY);
    private Ball directionMoveBall = new Ball(120, viewHeigth - 120, 60, Color.YELLOW);
    private Ball fightBackBall = new Ball(viewWidth - 100, viewHeigth - 120, 120, Color.RED);
    private Ball fightBall = new Ball(viewWidth - 100, viewHeigth - 120, 90, Color.BLUE);
    private Ball changeTypeBall = new Ball(viewWidth - 80, viewHeigth - 300, 60, Color.GREEN);

    private void initView() {
        surfaceHolder = getHolder();//获取SurfaceHolder对象
        surfaceHolder.addCallback(this);//注册SurfaceHolder的回调方法
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        snakeBall = new Ball(500, 500, 30, Color.RED);
        snakeBalls.add(snakeBall);

        directionBall = new Ball(120, viewHeigth - 400, 120, Color.LTGRAY);
        directionMoveBall = new Ball(120, viewHeigth - 400, 60, Color.YELLOW);
        fightBackBall = new Ball(viewWidth - 100, viewHeigth - 400, 120, Color.RED);
        fightBall = new Ball(viewWidth - 100, viewHeigth - 400, 90, Color.BLUE);
        changeTypeBall = new Ball(viewWidth - 80, viewHeigth - 600, 60, Color.GREEN);

    }

    public int getViewHeigth() {
        return viewHeigth;
    }

    public void setViewHeigth(int viewHeigth) {
        this.viewHeigth = viewHeigth;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    @Override
    public void run() {

        while (drawing) {

            long startTime = System.currentTimeMillis();

            synchronized (surfaceHolder) {
                draw();
            }

            fooding();

            snakeMove();

            long endTime = System.currentTimeMillis();
            int diffTime  = (int)(endTime - startTime);

            while(diffTime <= TIME_IN_FRAME) {
                diffTime = (int)(System.currentTimeMillis() - startTime);
                Thread.yield();
            }

        }
    }

    void fooding() {
        if(foodBalls.size() < GAME_FOOD_LIMIT) {
            int i = 1234;
            long timeStamp = System.currentTimeMillis() * i;
            int a = new Random(timeStamp).nextInt(9);
            if(a % 5 != 1) {
                return;
            }
            timeStamp = System.currentTimeMillis() * i++;
            x = new Random(timeStamp * i++).nextInt(8);
            x *= 100;
            x += new Random(timeStamp * i++).nextInt(50);
            if(x < 10) {
                x += 20;
            }
            int z = new Random(timeStamp * i++).nextInt(7);
            int r = z + 5;
            int color = Color.RED;
            if(z % 5 == 0) {
                color = Color.BLUE;
            } else if(z % 5 == 1) {
                color = Color.GREEN;
            } else if(z % 5 == 2) {
                color = Color.YELLOW;
            } else if(z % 5 == 3) {
                color = Color.CYAN;
            }
            int y = new Random(timeStamp * i++).nextInt(10);
            y *= 100;
            y += new Random(timeStamp * i++).nextInt(60);
            if(y < 10) {
                y += 50;
            }
            Ball foodBall = new Ball(x, y, r, color);
            foodBalls.add(foodBall);
        }
    }

    void snakeMove() {
        List<Ball> outSnakeBalls = new ArrayList<>();
        List<Ball> outBalls = new ArrayList<>();
        for(Ball snakeBall : snakeBalls) {
            for(Ball ball : foodBalls) {
                int distancer = snakeBall.getR();
                int distancex = Math.abs(ball.getX() - snakeBall.getX());
                int distancey = Math.abs(ball.getY() - snakeBall.getY());

                if(snakeBall.getR() < ball.getR()) {
                    distancer = ball.getR();
                }
                if(distancex < distancer
                        && distancey < distancer) {
                    if(snakeBall.getR() < ball.getR()) {
                        outSnakeBalls.add(snakeBall);
                    } else {
                        outBalls.add(ball);
                    }
                }
            }
        }
        if(outSnakeBalls.size() > 0 && snakeBalls.size() > 0) {
            for (Ball outBall : outSnakeBalls) {
                snakeBalls.remove(outBall);
            }
        }
        if(outBalls.size() > 0 && foodBalls.size() > 0) {
            for(Ball outBall : outBalls) {
                foodBalls.remove(outBall);
                if(snakeBalls.size() > 0) {
                    if(snakeType == SNAKE_TYPE_ONE) {
                        Ball ball = snakeBalls.get(0);
                        ball.bigger(outBall.getR());
                    } else {
                        Ball lastBall = snakeBalls.get(snakeBalls.size() - 1);
                        if (lastBall.getR() > 50) {
                            int movex = lastBall.getX() - outBall.getX();
                            if(movex < 0) {
                                movex -= (lastBall.getR() + outBall.getR());
                            } else {
                                movex += (lastBall.getR() + outBall.getR());
                            }
                            int movey = lastBall.getY() - outBall.getY();
                            if(movey < 0) {
                                movey -= (lastBall.getR() + outBall.getR());
                            } else {
                                movey += (lastBall.getR() + outBall.getR());
                            }
                            outBall.move(movex, movey);
                            snakeBalls.add(outBall);
                        } else {
                            lastBall.bigger(outBall.getR());
                        }
                    }
                }
            }
        }
        if(snakeBalls.size() > 0 && (movex != 0 || movey != 0)) {
            if(snakeBalls.size() == 1) {
                Ball ball = snakeBalls.get(0);
                ball.move(movex * 5, movey * 5);
            } else {
                for(int i = snakeBalls.size() - 1; i > 0; i--) {
                    Ball ball = snakeBalls.get(i);
                    Ball preBall = snakeBalls.get(i - 1);
                    ball.moveTo(preBall.getX(), preBall.getY());
                }

                Ball ball = snakeBalls.get(0);
                ball.moveTo(ball.getX() + movex * ball.getR() * 2, ball.getY() + movey * ball.getR() * 2);
            }
            movex = 0;
            movey = 0;
        }
    }

    private int x = 0;
    private int y = 0;
    private Paint paint = new Paint();
    private void draw() {
        try {
            canvas = surfaceHolder.lockCanvas();
//            x+=1;
//            y=(int)(100*Math.sin(x*2*Math.PI/180)+400);
//            path.lineTo(x,y);
            canvas.drawColor(Color.WHITE);

            int middlex = viewWidth / 2;
            int middley = viewHeigth / 2 - 200;
            if(win == GAME_STATUS_WIN) {
                paint.setTextSize(80);
                paint.setColor(Color.BLUE);
                canvas.drawText("你赢了，真棒！", 300, middley, paint);
            } else if(win == GAME_STATUS_TIMEUP) {
                paint.setTextSize(80);
                paint.setColor(Color.RED);
                canvas.drawText("时间到了，下次继续！", 300, middley, paint);
            } else if(win == GAME_STATUS_LOSE) {
                paint.setTextSize(80);
                paint.setColor(Color.RED);
                canvas.drawText("你输了，下次努力！", 300, middley, paint);
            } else {

                if(foodBalls.size() > 0) {
                    for (Ball ball : foodBalls) {
                        ball.draw(canvas, paint);
                    }
                }
                if(snakeBalls.size() > 0) {
                    for (Ball ball : snakeBalls) {
                        ball.draw(canvas, paint);
                    }
                }

                fightBackBall.draw(canvas, paint);
                fightBall.draw(canvas, paint);

                changeTypeBall.draw(canvas, paint);

                directionBall.draw(canvas, paint);
                directionMoveBall.draw(canvas, paint);
            }
        } catch (Exception e) {
        } finally {
            if (canvas != null)
                surfaceHolder.unlockCanvasAndPost(canvas);//保证每次都将绘图的内容提交
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(win != 0) {
            return true;
        }
        int pointCount = event.getPointerCount();
        for(int i = 0; i < pointCount; i++) {
            int x = (int) event.getX(i);
            int y = (int) event.getY(i);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(fighting(x, y)) {
                        fight();
                    } else if(changing(x, y)) {
                        snakeTypeChange();
                    } else if(moving(x, y)){
                        move(x, y);
                    } else {
                        touchMove(x, y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(!fighting(x, y) && !changing(x, y)) {
                        if(moving(x, y)) {
                            move(x, y);
                        } else {
                            touchMove(x, y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(fighting(x, y)) {
                        fightBall.setColor(Color.BLUE);
                    }
                    break;
            }
        }
        return true;
    }

    private void snakeTypeChange() {
        if(snakeType == SNAKE_TYPE_ONE) {
            snakeType = SNAKE_TYPE_LONG;
            if(snakeBalls.size() > 0) {
                Ball ball = snakeBalls.get(0);
                if(ball.getR() > SNAKE_SIZE_LIMIT) {
                    double count = ball.getR() / 10;
                    int r = (int) Math.sqrt(Math.pow(ball.getR(), 2) / count);
                    snakeBalls.remove(ball);
                    for(int i = 0; i <  count; i++) {
                        Ball newBall = new Ball(ball.getX() + (r * i) * 2, ball.getY() + i, r, ball.getColor(), ball.getType());
                        snakeBalls.add(newBall);
                    }
                }
            }
        } else {
            snakeType = SNAKE_TYPE_ONE;
            if(snakeBalls.size() > 1) {
                int x = 0;
                int y = 0;
                int type = Ball.TYPE_BALL;
                int color = Color.RED;
                double r = 0;
                for(Ball ball : snakeBalls) {
                    r += Math.pow(ball.getR(), 2);
                    if(x == 0) {
                        x = ball.getX();
                        y = ball.getY();
                        type = ball.getType();
                        color = ball.getColor();
                    }
                }
                r = Math.sqrt(r);
                Ball oneBall = new Ball(x, y, (int) r, color, type);
                snakeBalls = new ArrayList<>();
                snakeBalls.add(oneBall);
            }
        }
    }

    private void fight() {

    }

    int movex = 0;
    int movey = 0;
    private void move(int x, int y) {
        if(x < directionBall.getX()) {
            movex = -1;
            directionMoveBall.setX(directionBall.getX() - 30);
        } else if(x > directionBall.getX()) {
            movex = 1;
            directionMoveBall.setX(directionBall.getX() + 30);
        } else {
            directionMoveBall.setX(directionBall.getX());
        }
        if(y < directionBall.getY()) {
            movey = -1;
            directionMoveBall.setY(directionBall.getY() - 30);
        } else if(y > directionBall.getY()) {
            movey = 1;
            directionMoveBall.setY(directionBall.getY() + 30);
        } else {
            directionMoveBall.setY(directionBall.getY());
        }
//        if(snakeBalls.size() > 0) {
//            if(snakeBalls.size() == 1) {
//                Ball ball = snakeBalls.get(0);
//                ball.move(movex * 5, movey * 5);
//            } else {
//                for(int i = snakeBalls.size() - 1; i > 0; i--) {
//                    Ball ball = snakeBalls.get(i);
//                    Ball preBall = snakeBalls.get(i - 1);
//                    ball.moveTo(preBall.getX(), preBall.getY());
//                }
//
//                Ball ball = snakeBalls.get(0);
//                ball.move(movex * ball.getR() * 2, movey * ball.getR() * 2);
//            }
//        }
    }

    private void touchMove(int x, int y) {
        Ball fisrtBall = new Ball(0, 0, 5);
        if(snakeBalls.size() > 0) {
            fisrtBall = snakeBalls.get(0);
        }
//        int movex = 1;
//        int movey = 1;
        if(x < fisrtBall.getX()) {
            movex = -1;
        } else if(x > fisrtBall.getX()) {
            movex = 1;
        }
        if(y < fisrtBall.getY()) {
            movey = -1;
        } else if(y > fisrtBall.getY()) {
            movey = 1;
        }
//        if(snakeBalls.size() > 0) {
//            if(snakeBalls.size() == 1) {
//                Ball ball = snakeBalls.get(0);
//                ball.move(movex * 5, movey * 5);
//            } else {
//                for(int i = snakeBalls.size() - 1; i > 0; i--) {
//                    Ball ball = snakeBalls.get(i);
//                    Ball preBall = snakeBalls.get(i - 1);
//                    ball.moveTo(preBall.getX(), preBall.getY());
//                }
//
//                Ball ball = snakeBalls.get(0);
//                ball.moveTo(ball.getX() + movex * ball.getR() * 2, ball.getY() + movey * ball.getR() * 2);
//            }
//        }
    }

    private boolean moving(int x, int y) {
        boolean moving = false;
        if(x > directionBall.getX() - directionBall.getR()
                && x < directionBall.getX() + directionBall.getR()
                && y > directionBall.getY() - directionBall.getR()
                && y < directionBall.getY() + directionBall.getR()) {
            moving = true;
        }
        return moving;
    }

    private boolean fighting(int x, int y) {
        boolean fighting = false;
        if(x > fightBall.getX() - fightBall.getR()
                && x < fightBall.getX() + fightBall.getR()
                && y > fightBall.getY() - fightBall.getR()
                && y < fightBall.getY() + fightBall.getR()) {
            fighting = true;
        }
        return fighting;
    }

    private boolean changing(int x, int y) {
        boolean changing = false;
        if(x > changeTypeBall.getX() - changeTypeBall.getR()
                && x < changeTypeBall.getX() + changeTypeBall.getR()
                && y > changeTypeBall.getY() - changeTypeBall.getR()
                && y < changeTypeBall.getY() + changeTypeBall.getR()) {
            changing = true;
        }
        return changing;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawing = false;
    }
}
