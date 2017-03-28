package com.eaglesakura.android.util;

import com.eaglesakura.util.IOUtil;
import com.eaglesakura.util.MathUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class ImageUtil {

    /**
     * ある特定の色の補色を計算する
     * <br>
     * 計算はIllustrator方式で行う
     * <br>
     * http://appakumaturi.hatenablog.com/entry/20120121/1327143125
     */
    public static int getComplementaryColor(int xrgb) {
        int r = Color.red(xrgb);
        int g = Color.green(xrgb);
        int b = Color.blue(xrgb);

        int maxValue = Math.max(Math.max(r, g), b);
        int minValue = Math.min(Math.min(r, g), b);

        int sum = maxValue + minValue;

        return Color.rgb(MathUtil.minmax(0, 255, sum - r), MathUtil.minmax(0, 255, sum - g), MathUtil.minmax(0, 255, sum - b));
    }

    /**
     * 反転色を取得する
     */
    public static int getNegaColor(int xrgb) {
        int r = Color.red(xrgb);
        int g = Color.green(xrgb);
        int b = Color.blue(xrgb);

        return Color.rgb(255 - r, 255 - g, 255 - b);
    }

    /**
     * 画像からSHA1指紋を作成する。
     */
    public static String genSHA1(Bitmap bitmap) {
        final int[] pixels = new int[bitmap.getWidth()];
        final byte[] src = new byte[pixels.length * 4];
        final int height = bitmap.getHeight();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            for (int i = 0; i < height; ++i) {
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
                md.update(IOUtil.toByteArray(pixels, src));
            }

            byte[] digest = md.digest();

            StringBuffer sBuffer = new StringBuffer(digest.length * 2);
            for (byte b : digest) {
                String s = Integer.toHexString(((int) b) & 0xff);

                if (s.length() == 1) {
                    sBuffer.append('0');
                }
                sBuffer.append(s);
            }
            return sBuffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Bitmapからサムネイルを読み込む
     */
    public static Bitmap decodeThumbnail(String pathName) {
        try {
            ExifInterface exif = new ExifInterface(pathName);
            return decode(exif.getThumbnail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * image bufferからデコードする
     */
    public static Bitmap decode(byte[] imageFile) {
        if (imageFile == null) {
            return null;
        }

        try {
            return BitmapFactory.decodeByteArray(imageFile, 0, imageFile.length);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static Bitmap decode(Context context, int drawableId) {
        try {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decode(AssetManager assets, String path) {
        InputStream is = null;
        try {
            is = assets.open(path);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(is);
        }
        return null;
    }

    /**
     * 画像ファイルから直接デコードする
     */
    public static Bitmap decode(String pathName) {
        try {
            return BitmapFactory.decodeFile(pathName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * デコードする
     */
    public static Bitmap decode(InputStream stream) {
        try {
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * PNG画像にエンコードする
     */
    public static byte[] encodePng(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            bitmap.compress(CompressFormat.PNG, 100, os);
            return os.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Jpeg画像にエンコードする
     */
    public static byte[] encodeJpeg(Bitmap bitmap, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            bitmap.compress(CompressFormat.JPEG, quality, os);
            return os.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 指定した最大サイズに収まるようにスケーリングする
     * <br>
     * 元のsrcオブジェクトをそのまま返す場合があるので注意。
     */
    public static Bitmap toScaledImage(Bitmap src, int maxWidth, int maxHeight) {
        int originWidth = src.getWidth();
        int originHeight = src.getHeight();
        if (originWidth <= maxWidth && originHeight <= maxHeight) {
            return src;
        }

        double scaleWidth = (double) maxWidth / (double) originWidth;
        double scaleHeight = (double) maxHeight / (double) originHeight;

        int newWidth;
        int newHeight;
        if (scaleWidth < scaleHeight) {
            // 幅に合わせる
            newWidth = maxWidth;
            newHeight = (int) (scaleWidth * originHeight);
        } else {
            // 高さに合わせる
            newWidth = (int) (scaleHeight * originWidth);
            newHeight = maxHeight;
        }

        // スケーリングしたBitmapを返す
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
    }

    /**
     * アルファ値をブレンドした新たな画像を生成する
     */
    public static Bitmap blendAlpha(Bitmap src, Bitmap alpha) {
        // アルファ成分をコピーして作成
        Bitmap image = Bitmap.createScaledBitmap(alpha, src.getWidth(), src.getHeight(), true);
        Canvas canvas = new Canvas(image);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        // 転送モードを工夫してから
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(src, 0, 0, paint);

        // 生成した画像を返す
        return image;
    }

    /**
     * Drawableの内容をBitmapに焼きこんで返却する
     *
     * @param drawable もとのDrawable
     * @param pixel    長辺のピクセル数
     */
    @NonNull
    public static Bitmap toBitmap(@NonNull Drawable drawable, int pixel) {
        Rect rawBounds = new Rect(drawable.getBounds());

        int width = drawable.getMinimumWidth();
        int height = drawable.getMinimumHeight();
        {
            if (width > height) {
                // 横長
                float scale = (float) pixel / (float) width;
                width = pixel;
                height = (int) (scale * height);
            } else {
                // 縦長
                float scale = (float) pixel / (float) height;
                width = (int) (scale * width);
                height = pixel;
            }
        }
        Rect newBounds = new Rect(0, 0, width, height);

        // 描画用のメモリを用意して焼き込む
        Bitmap dst = Bitmap.createBitmap(newBounds.width(), newBounds.height(), Bitmap.Config.ARGB_8888);

        drawable.setBounds(newBounds);
        {
            Canvas canvas = new Canvas(dst);
            drawable.draw(canvas);
        }
        // 描画位置を元に戻す
        drawable.setBounds(rawBounds);
        return dst;
    }
}
