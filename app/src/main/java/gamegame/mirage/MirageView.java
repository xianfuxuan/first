package gamegame.mirage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import gamegame.R;
import gamegame.mirage.Mirage;
import gamegame.twoballs.Ball;
import gamegame.util.ImgUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MirageView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;//绘图的画布
    private boolean drawing;
    private int TIME_IN_FRAME = 30;
    private int viewHeigth = 0;//1680;
    private int viewWidth = 0;//1060;
    private int bgHeigth = 0;
    private int bgHeigth2 = 0;
    private long GAME_TIME_START = System.currentTimeMillis();;
    private int GAME_BULLET_LIMIT = 8;
    private int GAME_TIME_LIMIT = 300;
    private int GAME_STATUS_INIT = 0;
    private int GAME_STATUS_WIN = 1;
    private int GAME_STATUS_LOSE = 2;
    private int GAME_STATUS_TIMEUP = 3;
    private int win = GAME_STATUS_INIT;

    public MirageView(Context context) {
        super(context);
        initView();
    }

    public MirageView(Context context, int viewWidth, int viewHeigth) {
        super(context);
        this.viewWidth = viewWidth;
        this.viewHeigth = viewHeigth;
        initView();
    }

    public MirageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MirageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int REDBALLR = 40;
    private int REDBALLX = 400;
    private int REDBALLY = 300;
    private int BLUEBALLR = 30;
    private int BLUEBALLX = 300;
    private int BLUEBALLY = 900;
    private List<Mirage> redMirages = new ArrayList<>(10);
    private List<Mirage> blueMirages = new ArrayList<>(10);
    private Mirage redMirage;// = new Mirage(REDBALLX, REDBALLY, REDBALLR, Color.RED, Ball.TYPE_PLANE);
    private Mirage blueMirage;// = new Mirage(BLUEBALLX, BLUEBALLY, BLUEBALLR, Color.BLUE, Ball.TYPE_PLANE);

    private List<Mirage> blueBulletMirages = new ArrayList<>();

    private List<Mirage> mirageSlices = new ArrayList<>();

    private Mirage directionMirage;// = new Mirage(120, viewHeigth - 120, 120, Color.LTGRAY);
    private Mirage directionMoveMirage;// = new Mirage(120, viewHeigth - 120, 60, Color.YELLOW);
    private Mirage fightBackMirage;// = new Mirage(viewWidth - 100, viewHeigth - 120, 120, Color.RED);
    private Mirage fightMirage;// = new Mirage(viewWidth - 100, viewHeigth - 120, 90, Color.BLUE);
    private Mirage changeTypeMirage;// = new Mirage(viewWidth - 80, viewHeigth - 300, 60, Color.GREEN);

    Bitmap mirageBitmap;// = ImgUtil.readBitMap(this.getContext(), R.drawable.mirage);
    Bitmap planeBitmap;// = ImgUtil.readBitMap(this.getContext(), R.drawable.plan_0);
    Bitmap enemyBitmap;// = ImgUtil.readBitMap(this.getContext(), R.drawable.enemy_0);
    Bitmap bossBitmap;// = ImgUtil.readBitMap(this.getContext(), R.drawable.e1_0);
    Bitmap bulletBitmap;// = ImgUtil.readBitMap(this.getContext(), R.drawable.bullet_0);
    Bitmap bgBitmap;
    Bitmap bgBitmap2;

    private void initView() {
        surfaceHolder = getHolder();//获取SurfaceHolder对象
        surfaceHolder.addCallback(this);//注册SurfaceHolder的回调方法
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        redMirage= new Mirage(REDBALLX, REDBALLY, REDBALLR, Color.RED, Ball.TYPE_PLANE);
        blueMirage = new Mirage(BLUEBALLX, BLUEBALLY, BLUEBALLR, Color.BLUE, Ball.TYPE_PLANE);

        mirageBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.mirage);
        planeBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.plan_0);
        enemyBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.enemy_0);
        bossBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.e1_0);
        bulletBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.bullet_0);
        bgBitmap = ImgUtil.readBitMap(this.getContext(), R.drawable.map);
        bgBitmap2 = ImgUtil.readBitMap(this.getContext(), R.drawable.map);

        bgHeigth2 = viewHeigth - 5;

        directionMirage = new Mirage(120, viewHeigth - 400, 120, Color.LTGRAY);
        directionMoveMirage = new Mirage(120, viewHeigth - 400, 60, Color.YELLOW);
        fightBackMirage = new Mirage(viewWidth - 100, viewHeigth - 400, 120, Color.RED);
        fightMirage = new Mirage(viewWidth - 100, viewHeigth - 400, 90, Color.BLUE);
        changeTypeMirage = new Mirage(viewWidth - 80, viewHeigth - 600, 60, Color.GREEN);
        for(int i = 1; i <= 10; i++) {
            int x = 200 + 60 * i;
            long timeStamp = System.currentTimeMillis() * i;
            x += new Random(timeStamp).nextInt(300);
            if(x > 800) {
                x -= (i % 2 + 1) * 50;
            }
            int y = 300 + 60 * new Random(timeStamp).nextInt(6);
            redMirages.add(new Mirage(x, y, 50, Color.RED, Ball.TYPE_PLANE));
        }
        for(int i = 1; i <= 10; i++) {
            int x = 100 + 50 * i;
            long timeStamp = System.currentTimeMillis() * i;
            x += new Random(timeStamp).nextInt(300);
            if(x > 800) {
                x -= (i % 2 + 1) * 50;
            }
            int y = 800 + 50 * new Random(timeStamp).nextInt(8);
            blueMirages.add(new Mirage(x, y, 40, Color.BLUE, Ball.TYPE_PLANE));
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
            if(redMirages.size() == 0 && redMirage.getColor() == Color.BLUE) {
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

            bgMove();

            redMove();
//            blueMove();
            bigMove();

            blueBulletMove();

//            blueMirageSizeChange();

//            mirageSlicesMove();

            long endTime = System.currentTimeMillis();
            int diffTime  = (int)(endTime - startTime);

            while(diffTime <= TIME_IN_FRAME) {
                diffTime = (int)(System.currentTimeMillis() - startTime);
                Thread.yield();
            }

        }
    }

    private void bgMove() {
        bgHeigth -= 5;
        if(bgHeigth < 0 - viewHeigth) {
            bgHeigth = viewHeigth - 5;
        }
        bgHeigth2 -= 5;
        if(bgHeigth2 < 0 - viewHeigth) {
            bgHeigth2 = viewHeigth - 5;
        }
    }

    private void blueMirageTypeChange() {
        int type = blueMirage.getType();
        type += 1;
        type = type % 6;
        blueMirage.setType(type);
    }

    private void blueMirageSizeChange() {
        if(blueMirage.getR() < BLUEBALLR - 20) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(3);
            if(x % 3 == 0) {
                blueMirage.bigger(1);
            } else if(x % 3 == 1) {
                blueMirage.bigger(2);
            } else {
                blueMirage.bigger(3);
            }
        } else if(blueMirage.getR() < BLUEBALLR) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(6);
            if(x % 3 == 0) {
                blueMirage.bigger(2);
            }
        } else if(blueMirage.getR() < BLUEBALLR + 20) {
            long timeStamp = System.currentTimeMillis();
            int x = new Random(timeStamp).nextInt(9);
            if(x % 5 == 0) {
                blueMirage.bigger(1);
            }
        }
    }

    void mirageSlicesMove() {
        if(mirageSlices.size() > 0) {
            int i = 0;
            List<Mirage> outMirages = new ArrayList<>();
            for(Mirage mirage : mirageSlices) {
                if(mirage.getR() < 5) {
                    outMirages.add(mirage);
                } else {
                    int r = mirage.getR() * 3 / 4;
                    mirage.smaller(5);
                    mirage.move(0, -5);
                    if(i % 4 == 0) {
                        mirage.left(r);
                    } else if(i % 4 == 1) {
                        mirage.top(r);
                    } else if(i % 4 == 2) {
                        mirage.right(r);
                    }else {
                        mirage.down(r);
                    }
                }
            }
            if(outMirages.size() > 0) {
                for(Mirage outMirage : outMirages) {
                    mirageSlices.remove(outMirage);
                }
            }
        }
    }

    void blueBulletMove() {
        if(blueBulletMirages.size() > 0) {
            List<Mirage> outBullets = new ArrayList<>();
            List<Mirage> outMirages = new ArrayList<>();
            for(Mirage blueBulletMirage : blueBulletMirages) {
                for(Mirage mirage : redMirages) {
                    int distancer = mirage.getR() + blueBulletMirage.getR() - 5;
                    int distancex = Math.abs(blueBulletMirage.getX() - mirage.getX());
                    int distancey = Math.abs(blueBulletMirage.getY() - mirage.getY());

                    if(distancex < distancer
                            && distancey < distancer) {
                        outMirages.add(mirage);
                        outBullets.add(blueBulletMirage);
                    }
                }
//                if(redMirage.getColor() == Color.RED) {
                    int distancer = redMirage.getR();
                    int distancex = Math.abs(blueBulletMirage.getX() - redMirage.getX());
                    int distancey = Math.abs(blueBulletMirage.getY() - redMirage.getY());

                    if(distancex < distancer
                            && distancey < distancer) {
                        outBullets.add(blueBulletMirage);
//                        if(redMirage.getR() > 60) {
//                            redMirage.smaller(blueBulletMirage.getR() /  5);
//                        } else {
//                            redMirage.setColor(Color.BLUE);
//                            redMirage.setType(blueBulletMirage.getType());
//                        }
                    }
//                }
            }
            if(outMirages.size() > 0) {
                for(Mirage outMirage : outMirages) {
                    redMirages.remove(outMirage);
                    mirageSlices.addAll(outMirage.die());
                }
            }

            if(outBullets.size() > 0) {
                for(Mirage outBullet : outBullets) {
                    blueBulletMirages.remove(outBullet);
                    mirageSlices.addAll(outBullet.die());
                }
                outBullets = new ArrayList<>();
            }
            for(Mirage blueBulletMirage : blueBulletMirages) {
//                blueBulletMirage.setY(blueBulletMirage.getY() - 6);
                if(blueBulletMirage.getY() < 5) {
                    outBullets.add(blueBulletMirage);
                }
                blueBulletMirage.move();
            }
            if(outBullets.size() > 0) {
                for(Mirage outBullet : outBullets) {
                    blueBulletMirages.remove(outBullet);
                }
            }
        }
    }

    void redMove() {
        int i = 66;
        for(Mirage mirage : redMirages) {
            long timeStamp = System.currentTimeMillis() * i;
            int x = new Random(timeStamp).nextInt(9);
            if(mirage.getX() > (redMirage.getX() + redMirage.getR() + mirage.getR())) {
                x = 0 - x;
            } else {
                if(x % 3 == 0) {
                    x = 0 - x;
                } else {
                    continue;
                }
            }
            if(mirage.getX() < 100 && x < 0) {
                x = 0 - x;
            }
            if(mirage.getX() > 800 && x > 0) {
                x = 0 - x;
            }
            timeStamp = System.currentTimeMillis() * i++;
            int y = new Random(timeStamp).nextInt(6);
            if(mirage.getY() > (redMirage.getY() + redMirage.getR() + mirage.getR())) {
                y = 0 - y;
            } else {
                if(y % 4 == 0) {
                    y = 0 - y;
                }
            }

            if(mirage.getY() < 400 && y < 0) {
                y = 0 - y;
            }
            if(mirage.getY() > 900 && y > 0) {
                y = 0 - y;
            }
            mirage.move(x, y);
        }
    }

    void blueMove() {
        int i = 888;
        for(Mirage mirage : blueMirages) {
            long timeStamp = System.currentTimeMillis() * i;
            int x = new Random(timeStamp).nextInt(8);
            if(x % 3 == 0) {
                x = 0 - x;
            } else {
                continue;
            }
            if(mirage.getX() < 150 && x < 0) {
                x = 0 - x;
            }
            if(mirage.getX() > 900 && x > 0) {
                x = 0 - x;
            }
            timeStamp = System.currentTimeMillis() * i++;
            int y = new Random(timeStamp).nextInt(5);
            if(y % 3 != 0) {
                y = 0 - y;
            }
            if(mirage.getY() < 100 && y < 0) {
                y = 0 - y;
            }
            if(mirage.getY() > 600 && y > 0) {
                y = 0 - y;
            }
            mirage.move(x, y);

        }
    }

    void bigMove() {
        int i = 9901;
        long timeStamp = System.currentTimeMillis() * i;
        int x = new Random(timeStamp).nextInt(12);
        if(x % 5 == 1) {
            x = 0 - x;
        } else if(x % 5 == 2) {
            return;
        }
        if(redMirage.getX() < 200 && x < 0) {
            x = 0 - x;
        }
        if(redMirage.getX() > 600 && x > 0) {
            x = 0 - x;
        }
        timeStamp = System.currentTimeMillis() * i++;
        int y = new Random(timeStamp).nextInt(15);
        if(redMirage.getColor() == Color.BLUE) {
            y = 0 - y;
        }
        if(y % 10 == 0) {
            y = 0 - y;
        } else if(x % 5 == 2) {
            return;
        }
        if(redMirage.getY() < 300 && y < 0) {
            y = 0 - y;
        }
        if(redMirage.getY() > 800 && y > 0) {
            y = 0 - y;
        }
        redMirage.move(x, y);
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

            canvas.drawBitmap(bgBitmap, 0, bgHeigth, paint);
            canvas.drawBitmap(bgBitmap2, 0, bgHeigth2, paint);
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
                canvas.drawText(viewWidth+"*"+viewHeigth+":"+timeLeft, 200, viewHeigth - 300, paint);

                float t = 0f;
                float l = 0f;
                float r = viewWidth;
                float b = viewHeigth;
                paint.setColor(Color.RED);
                canvas.drawRect(t, l, r, b, paint);
                r = viewWidth - 100;
                b = viewHeigth - 100;
                paint.setColor(Color.BLUE);
                canvas.drawRect(t, l, r, b, paint);
                r = viewWidth - 200;
                b = viewHeigth - 200;
                paint.setColor(Color.GREEN);
                canvas.drawRect(t, l, r, b, paint);
                r = viewWidth - 300;
                b = viewHeigth - 300;
                paint.setColor(Color.GREEN);
                canvas.drawRect(t, l, r, b, paint);

                if(blueBulletMirages.size() > 0) {
                    for (Mirage blueBulletMirage : blueBulletMirages) {
                        blueBulletMirage.setBitmap(bulletBitmap);
                        blueBulletMirage.draw(canvas, paint);
                    }
                }

                for(Mirage mirage : mirageSlices) {
                    mirage.draw(canvas, paint);
                }
                for(Mirage mirage : redMirages) {
                    mirage.setBitmap(enemyBitmap);
                    mirage.draw(canvas, paint);
                }
                redMirage.setBitmap(bossBitmap);
                redMirage.draw(canvas, paint);
//                for(Mirage mirage : blueMirages) {
//                    mirage.setBitmap(planeBitmap);
//                    mirage.draw(canvas, paint);
//                }
//                blueMirage.setBitmap(mirageBitmap);
                blueMirage.setBitmap(planeBitmap);
                blueMirage.draw(canvas, paint);

                fightBackMirage.draw(canvas, paint);
                fightMirage.draw(canvas, paint);

                changeTypeMirage.draw(canvas, paint);

                directionMirage.draw(canvas, paint);
                directionMoveMirage.draw(canvas, paint);
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
                        blueMirageTypeChange();
                    } else if(moving(x, y)){
                        move(x, y);
                    } else {
                        blueMirage.touchMove(x, y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(!fighting(x, y) && !changing(x, y)) {
                        if(moving(x, y)) {
                            move(x, y);
                        } else {
                            blueMirage.touchMove(x, y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(fighting(x, y)) {
                        fightMirage.setColor(Color.BLUE);
                    }
                    break;
            }
        }
        return true;
    }

    private void fight() {
        fightMirage.setColor(Color.YELLOW);

        if(blueBulletMirages.size() < GAME_BULLET_LIMIT) {
            int blueBulletMirageR = 20;
            if(blueMirage.getR() > BLUEBALLR + 20) {
                blueBulletMirageR = 30;
            } else if(blueMirage.getR() < BLUEBALLR - 10) {
                blueBulletMirageR = 15;
            }
            Mirage blueBulletMirage = new Mirage(blueMirage.getX(),
                    blueMirage.getY() - blueMirage.getR(),
                    blueBulletMirageR, blueMirage.getColor(), blueMirage.getType());
            blueBulletMirages.add(blueBulletMirage);
            blueMirage.smaller(blueBulletMirageR/5);
        }
    }

    private void move(int x, int y) {
        int movex = 5;
        int movey = 5;
        if(x < directionMirage.getX()) {
            movex = -5;
            directionMoveMirage.setX(directionMirage.getX() - 30);
        } else if(x > directionMirage.getX()) {
            directionMoveMirage.setX(directionMirage.getX() + 30);
        } else {
            directionMoveMirage.setX(directionMirage.getX());
        }
        if(y < directionMirage.getY()) {
            movey = -5;
            directionMoveMirage.setY(directionMirage.getY() - 30);
        } else if(y > directionMirage.getY()) {
            directionMoveMirage.setY(directionMirage.getY() + 30);
        } else {
            directionMoveMirage.setY(directionMirage.getY());
        }
        blueMirage.move(movex, movey);
    }

    private boolean moving(int x, int y) {
        boolean moving = false;
        if(x > directionMirage.getX() - directionMirage.getR()
                && x < directionMirage.getX() + directionMirage.getR()
                && y > directionMirage.getY() - directionMirage.getR()
                && y < directionMirage.getY() + directionMirage.getR()) {
            moving = true;
        }
        return moving;
    }

    private boolean fighting(int x, int y) {
        boolean fighting = false;
        if(x > fightMirage.getX() - fightMirage.getR()
                && x < fightMirage.getX() + fightMirage.getR()
                && y > fightMirage.getY() - fightMirage.getR()
                && y < fightMirage.getY() + fightMirage.getR()) {
            fighting = true;
        }
        return fighting;
    }

    private boolean changing(int x, int y) {
        boolean changing = false;
        if(x > changeTypeMirage.getX() - changeTypeMirage.getR()
                && x < changeTypeMirage.getX() + changeTypeMirage.getR()
                && y > changeTypeMirage.getY() - changeTypeMirage.getR()
                && y < changeTypeMirage.getY() + changeTypeMirage.getR()) {
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
