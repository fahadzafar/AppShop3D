package shop3d.util;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Fahad on 11/24/2015.
 */
public class ColorHelper {


    public static int StringToColorInt(String val) {
        int col = Color.BLACK;
        try {
            String[] RGB = val.split(":");
            col = Color.argb(0xff, (int) (Float.parseFloat(RGB[0]) * 255),
                    (int) (Float.parseFloat(RGB[1]) * 255), (int) (Float.parseFloat(RGB[2]) * 255));
        } catch (Exception er) {

        }
        return col;
    }

    public static String IntArrToString(ArrayList<Integer> arr) {
        String col = "";
        for (int i = 0; i < arr.size(); i++) {
            if (i >= 1)
                col += "*";

            int matCol = arr.get(i);
            col += Helper.FormatDouble(Helper.getRColor(matCol) / 255.0, 2) + ":" +
                    Helper.FormatDouble(Helper.getGColor(matCol) / 255.0, 2) + ":" +
                    Helper.FormatDouble(Helper.getBColor(matCol) / 255.0, 2);
        }
        return col;
    }
}
