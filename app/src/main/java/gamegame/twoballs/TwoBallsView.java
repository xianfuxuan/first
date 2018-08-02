package gamegame.twoballs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.*;

/**
 *
 */
public class TwoBallsView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;//绘图的画布
    private boolean drawing;
    private int TIME_IN_FRAME = 30;
    private int viewHeigth = 0;//1680;
    private int viewWidth = 0;//1060;
    private long GAME_TIME_START = System.currentTimeMillis();;
    private int GAME_BULLET_LIMIT = 8;
    private int GAME_TIME_LIMIT = 300;
    private int GAME_STATUS_INIT = 0;
    private int GAME_STATUS_WIN = 1;
    private int GAME_STATUS_LOSE = 2;
    private int GAME_STATUS_TIMEUP = 3;
    private int win = GAME_STATUS_INIT;

    public TwoBallsView(Context context) {
        super(context);
        initView();
    }

    public TwoBallsView(Context context, int viewWidth, int viewHeigth) {
        super(context);
        this.viewWidth = viewWidth;
        this.viewHeigth = viewHeigth;
        initView();
    }

    public TwoBallsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TwoBallsView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        directionBall = new Ball(120, viewHeigth - 400, 120, Color.LTGRAY);
        directionMoveBall = new Ball(120, viewHeigth - 400, 60, Color.YELLOW);
        fightBackBall = new Ball(viewWidth - 100, viewHeigth - 400, 120, Color.RED);
        fightBall = new Ball(viewWidth - 100, viewHeigth - 400, 90, Color.BLUE);
        changeTypeBall = new Ball(viewWidth - 80, viewHeigth - 600, 60, Color.GREEN);
        for(int i = 1; i <= 10; i++) {
            int x = 200 + 60 * i;
            long timeStamp = System.currentTimeMillis() * i;
            x += new Random(timeStamp).nextInt(300);
            if(x > 800) {
                x -= (i % 2 + 1) * 50;
            }
            int y = 300 + 60 * new Random(timeStamp).nextInt(6);
            redBalls.add(new Ball(x, y, 50, Color.RED));
        }
        for(int i = 1; i <= 10; i++) {
            int x = 100 + 50 * i;
            long timeStamp = System.currentTimeMillis() * i;
            x += new Random(timeStamp).nextInt(300);
            if(x > 800) {
                x -= (i % 2 + 1) * 50;
            }
            int y = 800 + 50 * new Random(timeStamp).nextInt(8);
            blueBalls.add(new Ball(x, y, 40, Color.BLUE));
        }
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
            if(redBalls.size() == 0 && redBall.getColor() == Color.BLUE) {
                win = GAME_STATUS_WIN;
            } else {
                long timeLeft = GAME_TIME_LIMIT - (System.currentTimeMillis() / 1000 - GAME_TIME_START / 1000);

                if(timeLeft <= 0) {
                    win = GAME_STATUS_TIMEUP;
                }
            }
            long startTime = System.currentTimeMillis();

            synchronized (surfaceHolder) {
                draw();
            }

            redMove();
            blueMove();
            bigMove();

            blueBulletMove();

            blueBallSizeChange();

            ballSlicesMove();

            long endTime = System.currentTimeMillis();
            int diffTime  = (int)(endTime - startTime);

            while(diffTime <= TIME_IN_FRAME) {
                diffTime = (int)(System.currentTimeMillis() - startTime);
                Thread.yield();
            }

        }
    }

    private void blueBallTypeChange() {
        int type = blueBall.getType();
        type += 1;
        type = type % 6;
        blueBall.setType(type);
    }

    private void blueBallSizeChange() {
        if(blueBall.getR() < BLUEBALLR - 20) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(3);
            if(x % 3 == 0) {
                blueBall.bigger(1);
            } else if(x % 3 == 1) {
                blueBall.bigger(2);
            } else {
                blueBall.bigger(3);
            }
        } else if(blueBall.getR() < BLUEBALLR) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(6);
            if(x % 3 == 0) {
                blueBall.bigger(2);
            }
        } else if(blueBall.getR() < BLUEBALLR + 20) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(9);
            if(x % 5 == 0) {
                blueBall.bigger(1);
            }
        }
    }

    void ballSlicesMove() {
        if(ballSlices.size() > 0) {
            int i = 0;
            List<Ball> outBalls = new ArrayList<>();
            for(Ball ball : ballSlices) {
                if(ball.getR() < 5) {
                    outBalls.add(ball);
                } else {
                    int r = ball.getR() * 3 / 4;
                    ball.smaller(5);
                    ball.move(0, -5);
                    if(i % 4 == 0) {
                        ball.left(r);
                    } else if(i % 4 == 1) {
                        ball.top(r);
                    } else if(i % 4 == 2) {
                        ball.right(r);
                    }else {
                        ball.down(r);
                    }
                }
            }
            if(outBalls.size() > 0) {
                for(Ball outBall : outBalls) {
                    ballSlices.remove(outBall);
                }
            }
        }
    }

    void blueBulletMove() {
        if(blueBulletBalls.size() > 0) {
            List<Ball> outBullets = new ArrayList<>();
            List<Ball> outBalls = new ArrayList<>();
            for(Ball blueBulletBall : blueBulletBalls) {
                for(Ball ball : redBalls) {
                    int distancer = ball.getR() + blueBulletBall.getR() - 5;
                    int distancex = Math.abs(blueBulletBall.getX() - ball.getX());
                    int distancey = Math.abs(blueBulletBall.getY() - ball.getY());

                    if(distancex < distancer
                            && distancey < distancer) {
                        outBalls.add(ball);
                        outBullets.add(blueBulletBall);
                    }
                }
                if(redBall.getColor() == Color.RED) {
                    int distancer = redBall.getR();
                    int distancex = Math.abs(blueBulletBall.getX() - redBall.getX());
                    int distancey = Math.abs(blueBulletBall.getY() - redBall.getY());

                    if(distancex < distancer
                            && distancey < distancer) {
                        outBullets.add(blueBulletBall);
                        if(redBall.getR() > 60) {
                            redBall.smaller(blueBulletBall.getR() /  5);
                        } else {
                            redBall.setColor(Color.BLUE);
                            redBall.setType(blueBulletBall.getType());
                        }
                    }
                }
            }
            if(outBalls.size() > 0) {
                for(Ball outBall : outBalls) {
                    redBalls.remove(outBall);
                    ballSlices.addAll(outBall.die());
                }
            }

            if(outBullets.size() > 0) {
                for(Ball outBullet : outBullets) {
                    blueBulletBalls.remove(outBullet);
                    ballSlices.addAll(outBullet.die());
                }
                outBullets = new ArrayList<>();
            }
            for(Ball blueBulletBall : blueBulletBalls) {
//                blueBulletBall.setY(blueBulletBall.getY() - 6);
                if(blueBulletBall.getY() < 5) {
                    outBullets.add(blueBulletBall);
                }
                blueBulletBall.move();
            }
            if(outBullets.size() > 0) {
                for(Ball outBullet : outBullets) {
                    blueBulletBalls.remove(outBullet);
                }
            }
        }
    }

    void redMove() {
        int i = 66;
        for(Ball ball : redBalls) {
            long timeStamp = System.currentTimeMillis() * i;
            int x = new Random(timeStamp).nextInt(9);
            if(ball.getX() > (redBall.getX() + redBall.getR() + ball.getR())) {
                x = 0 - x;
            } else {
                if(x % 3 == 0) {
                    x = 0 - x;
                } else {
                    continue;
                }
            }
            if(ball.getX() < 100 && x < 0) {
                x = 0 - x;
            }
            if(ball.getX() > 800 && x > 0) {
                x = 0 - x;
            }
            timeStamp = System.currentTimeMillis() * i++;
            int y = new Random(timeStamp).nextInt(6);
            if(ball.getY() > (redBall.getY() + redBall.getR() + ball.getR())) {
                y = 0 - y;
            } else {
                if(y % 4 == 0) {
                    y = 0 - y;
                }
            }

            if(ball.getY() < 400 && y < 0) {
                y = 0 - y;
            }
            if(ball.getY() > 900 && y > 0) {
                y = 0 - y;
            }
            ball.move(x, y);
        }
    }

    void blueMove() {
        int i = 888;
        for(Ball ball : blueBalls) {
            long timeStamp = System.currentTimeMillis() * i;
            int x = new Random(timeStamp).nextInt(8);
            if(x % 3 == 0) {
                x = 0 - x;
            } else {
                continue;
            }
            if(ball.getX() < 150 && x < 0) {
                x = 0 - x;
            }
            if(ball.getX() > 900 && x > 0) {
                x = 0 - x;
            }
            timeStamp = System.currentTimeMillis() * i++;
            int y = new Random(timeStamp).nextInt(5);
            if(y % 3 != 0) {
                y = 0 - y;
            }
            if(ball.getY() < 100 && y < 0) {
                y = 0 - y;
            }
            if(ball.getY() > 600 && y > 0) {
                y = 0 - y;
            }
            ball.move(x, y);

        }
    }

    void bigMove() {
        List<Ball> outBalls = new ArrayList<>();
        for(Ball ball : redBalls) {
            int distancer = redBall.getR();
            int distancex = Math.abs(ball.getX() - redBall.getX());
            int distancey = Math.abs(ball.getY() - redBall.getY());

            if(distancex < distancer
                    && distancey < distancer) {
                if(redBall.getColor() != Color.RED) {
                    outBalls.add(ball);
                    redBall.setColor(Color.RED);
                }
            }
        }
        if(outBalls.size() > 0 && redBalls.size() > 0) {
            for(Ball outBall : outBalls) {
                redBalls.remove(outBall);
            }
        }

        List<Ball> outBlueBalls = new ArrayList<>();
        for(Ball ball : blueBalls) {
            int distancer = redBall.getR();
            int distancex = Math.abs(ball.getX() - redBall.getX());
            int distancey = Math.abs(ball.getY() - redBall.getY());

            if(distancex < distancer
                    && distancey < distancer) {
                if(redBall.getColor() == Color.RED) {
                    outBlueBalls.add(ball);
                    redBall.bigger(5);
                }
            }
        }
        if(outBlueBalls.size() > 0 && blueBalls.size() > 0) {
            for(Ball outBall : outBlueBalls) {
                blueBalls.remove(outBall);
            }
        }
        if(blueBalls.size() == 0) {
            win = GAME_STATUS_WIN;
            return;
        }


        int i = 9901;
        long timeStamp = System.currentTimeMillis() * i;
        int x = new Random(timeStamp).nextInt(12);
        if(x % 5 == 1) {
            x = 0 - x;
        } else if(x % 5 == 2) {
            return;
        }
        if(redBall.getX() < 200 && x < 0) {
            x = 0 - x;
        }
        if(redBall.getX() > 600 && x > 0) {
            x = 0 - x;
        }
        timeStamp = System.currentTimeMillis() * i++;
        int y = new Random(timeStamp).nextInt(15);
        if(redBall.getColor() == Color.BLUE) {
            y = 0 - y;
        }
        if(y % 10 == 0) {
            y = 0 - y;
        } else if(x % 5 == 2) {
            return;
        }
        if(redBall.getY() < 300 && y < 0) {
            y = 0 - y;
        }
        if(redBall.getY() > 800 && y > 0) {
            y = 0 - y;
        }
        redBall.move(x, y);
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
                long timeLeft = GAME_TIME_LIMIT - (System.currentTimeMillis() / 1000 - GAME_TIME_START / 1000);
                paint.setTextSize(80);
                paint.setColor(Color.RED);
                canvas.drawText("-"+timeLeft+"-", middlex, viewHeigth - 300, paint);

                if(blueBulletBalls.size() > 0) {
                    for (Ball blueBulletBall : blueBulletBalls) {
                        blueBulletBall.draw(canvas, paint);
                    }
                }

                for(Ball ball : ballSlices) {
                    ball.draw(canvas, paint);
                }
                for(Ball ball : redBalls) {
                    ball.draw(canvas, paint);
                }
                redBall.draw(canvas, paint);
                for(Ball ball : blueBalls) {
                    ball.draw(canvas, paint);
                }
                blueBall.draw(canvas, paint);

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
                        blueBallTypeChange();
                    } else if(moving(x, y)){
                        move(x, y);
                    } else {
                        blueBall.touchMove(x, y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(!fighting(x, y) && !changing(x, y)) {
                        if(moving(x, y)) {
                            move(x, y);
                        } else {
                            blueBall.touchMove(x, y);
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

    private void fight() {
        fightBall.setColor(Color.YELLOW);

        if(blueBulletBalls.size() < GAME_BULLET_LIMIT) {
            int blueBulletBallR = 20;
            if(blueBall.getR() > BLUEBALLR + 20) {
                blueBulletBallR = 30;
            } else if(blueBall.getR() < BLUEBALLR - 10) {
                blueBulletBallR = 15;
            }
            Ball blueBulletBall = new Ball(blueBall.getX(),
                    blueBall.getY() - blueBall.getR(),
                    blueBulletBallR, blueBall.getColor(), blueBall.getType());
            blueBulletBalls.add(blueBulletBall);
            blueBall.smaller(blueBulletBallR/5);
        }
    }

    private void move(int x, int y) {
        int movex = 5;
        int movey = 5;
        if(x < directionBall.getX()) {
            movex = -5;
            directionMoveBall.setX(directionBall.getX() - 30);
        } else if(x > directionBall.getX()) {
            directionMoveBall.setX(directionBall.getX() + 30);
        } else {
            directionMoveBall.setX(directionBall.getX());
        }
        if(y < directionBall.getY()) {
            movey = -5;
            directionMoveBall.setY(directionBall.getY() - 30);
        } else if(y > directionBall.getY()) {
            directionMoveBall.setY(directionBall.getY() + 30);
        } else {
            directionMoveBall.setY(directionBall.getY());
        }
        blueBall.move(movex, movey);
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
