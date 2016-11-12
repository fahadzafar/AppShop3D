package shop3d.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Fahad on 11/9/2015.
 */
public class ImageCache {/*
    // Holds the downloaded images for questions.
    public static ConcurrentHashMap<String, Bitmap> imageCache = new ConcurrentHashMap<String, Bitmap>();

    public static boolean IsPresent(String Id, String imageField) {
        return imageCache.containsKey(Id + "_" + imageField);
    }

    public static Bitmap Get(String Id, String imageField) {
        return (Bitmap) imageCache.get(Id + "_" + imageField);
    }

    public static void Put(String Id, String imageField, byte[] byteData) {
        try {
            Bitmap imageData = Helper.ConvertByteArrayToBitmap(Helper.decompress(byteData));
            imageData = GetProportionalBitmap(imageData, 480, "x");
            imageData = ConvertBitmap(imageData, Bitmap.Config.RGB_565);
           // System.gc();
            imageCache.putIfAbsent(Id + "_" + imageField, imageData);

        } catch (Exception er) {

        }
    }

    public static void RemoveAllExceptIcon0() {
        Iterator it = imageCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (pair.getKey().toString().contains("_1") ||
                    pair.getKey().toString().contains("_2") ||
                    pair.getKey().toString().contains("_3") ||
                    pair.getKey().toString().contains("_4")
                    ) {
                Bitmap bt = (Bitmap) pair.getValue();
                bt.recycle();
                bt = null;
                it.remove();

            }

        }
    }

    public static Bitmap ConvertBitmap(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }

    public static Bitmap GetProportionalBitmap(Bitmap bitmap,
                                               int newDimensionXorY,
                                               String XorY) {
        if (bitmap == null) {
            return null;
        }

        float xyRatio = 0;
        int newWidth = 0;
        int newHeight = 0;

        if (XorY.toLowerCase().equals("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        } else if (XorY.toLowerCase().equals("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }*/
}
