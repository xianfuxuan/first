package gamegame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by Administrator on 2018-7-23.
 */
public class ImgUtil {

    public static Bitmap readBitMap(Context context, int id) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        opt.inPurgeable = true;
//        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(id);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
