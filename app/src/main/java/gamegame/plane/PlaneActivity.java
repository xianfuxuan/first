package gamegame.plane;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import gamegame.R;

import java.io.InputStream;
import java.util.Random;

/**
 * Created by Administrator on 2018-7-19.
 */
public class PlaneActivity extends AppCompatActivity {
    AnimView mAnimView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏显示窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 获取屏幕宽高
        Display display = getWindowManager().getDefaultDisplay();

        // 显示自定义的游戏View
        mAnimView = new AnimView(this,display.getWidth(), display.getHeight());
        setContentView(mAnimView);
    }

    public boolean onTouchEvent(MotionEvent event) {
        // 获得触摸的坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            // 触摸屏幕时刻
            case MotionEvent.ACTION_DOWN:
                mAnimView.UpdateTouchEvent(x, y,true);
                break;
            // 触摸并移动时刻
            case MotionEvent.ACTION_MOVE:
                break;
            // 终止触摸时刻
            case MotionEvent.ACTION_UP:
                mAnimView.UpdateTouchEvent(x, y,false);
                break;
        }
        return false;
    }
    public class AnimView extends SurfaceView implements SurfaceHolder.Callback,Runnable{


        /**屏幕的宽高**/
        private int mScreenWidth = 0;
        private int mScreenHeight = 0;


        /**游戏主菜单状态**/
        private  static final int STATE_GAME = 0;

        /**游戏状态**/
        private int mState = STATE_GAME;

        Paint mPaint = null;


        /**游戏背景资源 两张图片进行切换让屏幕滚动起来**/
        private Bitmap mBitMenuBG0 = null;
        private Bitmap mBitMenuBG1 = null;

        /**记录两张背景图片时时更新的Y坐标**/
        private int mBitposY0 =0;
        private int mBitposY1 =0;


        /**飞机动画帧数**/
        final static int PLAN_ANIM_COUNT = 6;

        /**子弹动画帧数**/
        final static int BULLET_ANIM_COUNT = 4;

        /**子弹对象的数量**/
        final static int BULLET_POOL_COUNT = 15 ;

        /**飞机移动步长**/
        final static int PLAN_STEP = 10;

        /**没过500毫秒发射一颗子弹**/
        final static int PLAN_TIME = 500;

        /**子弹图片向上偏移量处理触屏**/
        final static int BULLET_UP_OFFSET = 40;
        /**子弹图片向左偏移量处理触屏**/
        final static int BULLET_LEFT_OFFSET = 5;

        /**敌人对象的数量**/
        final static int ENEMY_POOL_COUNT = 5 ;

        /**敌人行走动画帧数**/
        final static int ENEMY_ALIVE_COUNT = 1 ;
        /**敌人行死亡画帧数**/
        final static int ENEMY_DEATH_COUNT = 6 ;

        /**敌人飞机偏移量**/
        final static int ENEMY_POS_OFF = 65 ;


        /**游戏主线程**/
        private Thread mThread = null;
        /**线程循环标志**/
        private boolean mIsRunning = false;

        private SurfaceHolder mSurfaceHolder = null;
        private Canvas mCanvas = null;

        private Context mContext = null;


        /**飞机动画**/
        public Animation mAircraft =null;
        /**飞机在屏幕中的坐标**/
        public int mAirPosX = 0;
        public int mAirPosY = 0;

        /**敌人类**/
        Enemy mEnemy[] = null;



        /**子弹类**/
        Bullet mBuilet[] = null;
        Bitmap mBitbullet[] = null;


        /**初始化发射子弹ID升序**/
        public int mSendId = 0;


        /**上一颗子弹发射的时间**/
        public Long mSendTime = 0L;
        /**手指在屏幕触摸的坐标**/
        public int mTouchPosX = 0;
        public int mTouchPosY = 0;


        /**标志手指在屏幕触摸中**/
        public boolean mTouching = false;

        /**
         * 构造方法
         *
         * @param context
         */
        public AnimView(Context context,int screenWidth, int screenHeight) {
            super(context);
            mContext = context;
            mPaint = new Paint();
            mScreenWidth = 1000;//screenWidth;
            mScreenHeight = 1500;//screenHeight;
            /**获取mSurfaceHolder**/
            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
            setFocusable(true);
            init();
            setGameState(STATE_GAME);
        }

        protected void Draw() {
            switch (mState) {
                case STATE_GAME:
                    renderBg();
                    updateBg();
                    break;
            }
        }
        private void init() {
            /**游戏背景**/
            mBitMenuBG0 = ReadBitMap(mContext, R.drawable.map_0);
            mBitMenuBG1 = ReadBitMap(mContext, R.drawable.map_1);


            /**创建主角飞机动画对象**/
            mAircraft = new Animation(mContext,new int[] {R.drawable.plan_0,R.drawable.plan_1,R.drawable.plan_2,R.drawable.plan_3,R.drawable.plan_4,R.drawable.plan_5},true);

            /**第一张图片津贴在屏幕00点，第二张图片在第一张图片上方**/
            mBitposY0 = 0;
            mBitposY1 =-mScreenHeight;

            /**初始化飞机的坐标**/
            mAirPosX = 150;
            mAirPosY = 400;

            /**这里敌人行走动画就1帧**/
            Bitmap []bitmap0 = new Bitmap[ENEMY_ALIVE_COUNT];
            bitmap0[0] = ReadBitMap(mContext,R.drawable.e1_0);
            /**敌人死亡动画**/
            Bitmap []bitmap1 = new Bitmap[ENEMY_DEATH_COUNT];
            for(int i =0; i< ENEMY_DEATH_COUNT; i++) {
                bitmap1[i] = ReadBitMap(mContext,R.drawable.bomb_enemy_0 + i);
            }

            /**创建敌人对象**/
            mEnemy = new Enemy[ENEMY_POOL_COUNT];

            for(int i =0; i< ENEMY_POOL_COUNT; i++) {
                mEnemy[i] = new Enemy(mContext,bitmap0,bitmap1);
                mEnemy[i].init(i * ENEMY_POS_OFF, 0);
            }




            /**创建子弹类对象**/
            mBuilet = new Bullet[BULLET_POOL_COUNT];
            mBitbullet = new Bitmap[BULLET_ANIM_COUNT];
            for(int i=0; i<BULLET_ANIM_COUNT;i++) {
                mBitbullet[i] = ReadBitMap(mContext,i+R.drawable.bullet_0);
            }
            for (int i =0; i< BULLET_POOL_COUNT;i++) {
                mBuilet[i] = new Bullet(mContext,mBitbullet);
            }
            mSendTime = System.currentTimeMillis();
        }
        private void setGameState(int newState) {
            mState =  newState;
        }

        public void renderBg() {
            /** 绘制游戏地图 **/
            mCanvas.drawBitmap(mBitMenuBG0, 0, mBitposY0, mPaint);
            mCanvas.drawBitmap(mBitMenuBG1, 0, mBitposY1, mPaint);
            /**绘制飞机动画**/
            mAircraft.DrawAnimation(mCanvas, mPaint, mAirPosX, mAirPosY);



            /**绘制子弹动画*/
            for (int i =0; i < BULLET_POOL_COUNT; i++) {
                mBuilet[i].DrawBullet(mCanvas, mPaint);
            }

            /**绘制敌人动画**/
            for(int i =0; i< ENEMY_POOL_COUNT; i++) {
                mEnemy[i].DrawEnemy(mCanvas, mPaint);
            }


        }



        private void updateBg() {
            /** 更新游戏背景图片实现向下滚动效果 **/
            mBitposY0 += 10;
            mBitposY1 += 10;
            if (mBitposY0 == mScreenHeight) {
                mBitposY0 = -mScreenHeight;
            }
            if (mBitposY1 == mScreenHeight) {
                mBitposY1 = -mScreenHeight;
            }

            /** 手指触摸屏幕更新飞机坐标 **/
            if (mTouching) {

                if (mAirPosX < mTouchPosX) {
                    mAirPosX += PLAN_STEP;
                } else {
                    mAirPosX -= PLAN_STEP;
                }
                if (mAirPosY < mTouchPosY) {
                    mAirPosY += PLAN_STEP;
                } else {
                    mAirPosY -= PLAN_STEP;
                }

                if (Math.abs(mAirPosX - mTouchPosX) <= PLAN_STEP) {
                    mAirPosX = mTouchPosX;
                }
                if (Math.abs(mAirPosY - mTouchPosY) <= PLAN_STEP) {
                    mAirPosY = mTouchPosY;
                }
            }
            /** 更新子弹动画 **/
            for (int i = 0; i < BULLET_POOL_COUNT; i++) {
                /** 子弹出屏后重新赋值**/
                mBuilet[i].UpdateBullet();

            }
            /**绘制敌人动画**/
            for(int i =0; i< ENEMY_POOL_COUNT; i++) {
                mEnemy[i].UpdateEnemy();
                /**敌机死亡 或者 敌机超过屏幕还未死亡重置坐标**/
                if(mEnemy[i].mState == Enemy.ENEMY_DEATH_STATE || mEnemy[i].m_posY >=mScreenHeight) {
                    mEnemy[i].init(UtilRandom(0,ENEMY_POOL_COUNT) *ENEMY_POS_OFF, 0);
                }

            }



            /**根据时间初始化为发射的子弹**/
            if (mSendId < BULLET_POOL_COUNT) {
                long now = System.currentTimeMillis();
                if (now - mSendTime >= PLAN_TIME) {
                    mBuilet[mSendId].init(mAirPosX - BULLET_LEFT_OFFSET, mAirPosY - BULLET_UP_OFFSET);
                    mSendTime = now;
                    mSendId++;
                }
            }else {
                mSendId = 0;
            }

            //更新子弹与敌人的碰撞
            Collision();

        }

        public void Collision() {
            //更新子弹与敌人碰撞
            for (int i = 0; i < BULLET_POOL_COUNT; i++) {
                for (int j = 0; j < ENEMY_POOL_COUNT; j++) {
                    if(mBuilet[i].m_posX >= mEnemy[j].m_posX && mBuilet[i].m_posX<=mEnemy[j].m_posX + 20
                            &&mBuilet[i].m_posY >= mEnemy[j].m_posY && mBuilet[i].m_posY<=mEnemy[j].m_posY + 20

                            ) {
                        mEnemy[j].mAnimState = Enemy.ENEMY_DEATH_STATE;
                    }
                }

            }
        }


        public void UpdateTouchEvent(int x, int y, boolean touching) {
            // 在这里检测按钮按下播放不同的特效
            switch (mState) {
                case STATE_GAME:
                    mTouching = touching;
                    mTouchPosX = x;
                    mTouchPosY = y;
                    break;
            }
        }
        /**
         * 返回一个随机数
         * @param botton
         * @param top
         * @return
         */
        private int UtilRandom(int botton, int top) {
            return ((Math.abs(new Random().nextInt()) % (top - botton)) + botton);
        }
        /**
         * 读取本地资源的图片
         *
         * @param context
         * @param resId
         * @return
         */
        public Bitmap ReadBitMap(Context context, int resId) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            // 获取资源图片
            InputStream is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        }

        /**
         * 绘制画带阴影的文字
         * @param canvas
         * @param str
         * @param color
         * @param x
         * @param y
         */
        public final void drawRimString(Canvas canvas, String str, int color,int x, int y) {
            int backColor = mPaint.getColor();
            mPaint.setColor(~color);
            canvas.drawText(str, x + 1, y, mPaint);
            canvas.drawText(str, x, y + 1, mPaint);
            canvas.drawText(str, x - 1, y, mPaint);
            canvas.drawText(str, x, y - 1, mPaint);
            mPaint.setColor(color);
            canvas.drawText(str, x, y, mPaint);
            mPaint.setColor(backColor);
        }

        /**
         * 绘制一个矩形
         * @param canvas
         * @param color
         * @param x
         * @param y
         * @param w
         * @param h
         */
        public void drawFillRect(Canvas canvas, int color, int x, int y, int w, int h) {
            int backColor = mPaint.getColor();
            mPaint.setColor(color);
            canvas.drawRect(x, y, x + w, y + h, mPaint);
            mPaint.setColor(backColor);
        }

        /**
         * 绘制一个扇形
         * @param canvas
         * @param color
         * @param oval
         * @param startAngle
         * @param sweepAngle
         * @param useCenter
         */
        public void drawFillCircle(Canvas canvas, int color, RectF oval, int startAngle, int sweepAngle, boolean useCenter) {
            int backColor = mPaint.getColor();
            mPaint.setColor(color);
            canvas.drawArc(oval, startAngle, sweepAngle, useCenter, mPaint);
            mPaint.setColor(backColor);
        }

        /**
         * 程序切割图片
         * @param bitmap
         * @param x
         * @param y
         * @param w
         * @param h
         * @return
         */
        public Bitmap BitmapClipBitmap(Bitmap bitmap,int x, int y, int w, int h) {
            return  Bitmap.createBitmap(bitmap, x, y, w, h);
        }



        @Override
        public void run() {
            while (mIsRunning) {
                //在这里加上线程安全锁
                synchronized (mSurfaceHolder) {
                    /**拿到当前画布 然后锁定**/
                    mCanvas =mSurfaceHolder.lockCanvas();
                    Draw();
                    /**绘制结束后解锁显示在屏幕上**/
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                   int arg3) {
            // surfaceView的大小发生改变的时候

        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            /**启动游戏主线程**/
            mIsRunning = true;
            mThread = new Thread(this);
            mThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            // surfaceView销毁的时候
            mIsRunning = false;
        }
    }
}
