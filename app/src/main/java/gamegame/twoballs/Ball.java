package gamegame.twoballs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import gamegame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class Ball {
    public final static int TYPE_BALL = 1;
    public final static int TYPE_CIRCL = 2;
    public final static int TYPE_CUBE = 6;
    public final static int TYPE_SQUARE = 4;
    public final static int TYPE_TRIANGLE = 3;
    public final static int TYPE_HEART = 5;
    public final static int TYPE_PLANE = 50;

    public final static int TYPE_SPEED_DEAFAULT = 6;
    public final static int TYPE_SPEED_SLOWLY = 3;
    public final static int TYPE_SPEED_QUICKLY = 8;
    public final static int TYPE_SPEED_SPEEDLY = 10;

    private String id;
    private int x;
    private int y;
    private int r;
    private int color;
    private int type = TYPE_CIRCL;
    private int speed = TYPE_SPEED_DEAFAULT;
    private int blood = 10;
    private Bitmap bitmap;

    public Ball() {
        this.id = UUID.randomUUID().toString();
    }

    public Ball(int x, int y, Bitmap bitmap) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
    }

    public Ball(int x, int y, int r) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Ball(int x, int y, int r, int color) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
    }

    public Ball(int x, int y, int r, int color, int type) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
        this.type = type;
    }

    public Ball(String id, int x, int y, int r, int color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
    }

    public void touchMove(int x, int y) {
        if(x < this.x) {
            this.x -= 5;
        } else {
            this.x += 5;
        }
        if(y < this.y) {
            this.y -= 5;
        } else {
            this.y += 5;
        }
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void left(int x) {
        this.x -= x;
    }

    public void right(int x) {
        this.x += x;
    }

    public void top(int y) {
        this.y -= y;
    }

    public void down(int y) {
        this.y += y;
    }

    public void move() {
       this.x = x;
       this.y -= speed;
    }

    public void smaller(int smaller) {
        if(smaller < 4) {
            this.r -= smaller;
        } else {
            this.r -= Math.sqrt(smaller);
        }
    }

    public void bigger(int bigger) {
        if(bigger < 4) {
            this.r += bigger;
        } else {
            this.r += Math.sqrt(bigger);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        switch (type) {
            case TYPE_BALL:
                canvas.drawCircle(x, y, r, paint);
                break;
            case TYPE_CUBE:
                canvas.drawRect(x - r, y - r, x + r, y + r, paint);
                canvas.drawRect(x - r + 5, y - r + 5, x + r - 5 , y + r - 5, paint);
                break;
            case TYPE_SQUARE:
                canvas.drawRect(x - r, y - r, x + r, y + r, paint);
                break;
            case TYPE_TRIANGLE:
                Path trianglePath = new Path();
                trianglePath.moveTo(x - r, y + r);
                trianglePath.lineTo(x, y - r);
                trianglePath.lineTo(x + r, y + r);
                trianglePath.lineTo(x - r, y + r);
                canvas.drawPath(trianglePath, paint);
                break;
            case TYPE_HEART:
                canvas.drawRect(x - r, y - r, x + r, y + r, paint);
                break;
            case TYPE_PLANE:
                canvas.drawBitmap(bitmap, x, y, paint);
                break;
            default:
                canvas.drawCircle(x, y, r, paint);
        }
    }

    public List<Ball> die() {
        List<Ball> balls = new ArrayList<>(4);
        Ball ballRight = new Ball(this.x - this.r / 2, y, this.r / 2, this.color, this.type);
        Ball ballTop = new Ball(this.x, y - this.r / 2, this.r / 2, this.color, this.type);
        Ball ballLeft = new Ball(this.x + this.r / 2, y, this.r / 2, this.color, this.type);
        Ball ballDown = new Ball(this.x, y + this.r / 2, this.r / 2, this.color, this.type);
        balls.add(ballRight);
        balls.add(ballTop);
        balls.add(ballLeft);
        balls.add(ballDown);
        return balls;
    }
}
