package shop3d.util;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

/**
 * Created by Fahad on 11/23/2015.
 */
public class MathString {

    public static String MatToString(Matrix mat) {
        return mat.toString();
    }

    public static Matrix StringToMat(String sMat) {
        Matrix mat = new Matrix();
        String[] rows = sMat.split("\n");

        for (int i = 1; i < rows.length - 1; i++) {
            String[] rowItems = rows[i].split("\t");
            for (int j = 1; j < rowItems.length; j++)
                mat.set(i - 1, j - 1, Float.parseFloat(rowItems[j]));

        }

        return mat;
    }

    public static String VecToString(SimpleVector sim) {
        return sim.toString();
    }

    public static SimpleVector StringToVec(String sim) {
        String remBrackets = sim.substring(1, sim.length() - 1);
        String[] rowItems = remBrackets.split(",");

        return new SimpleVector(Float.parseFloat(rowItems[0]),
                Float.parseFloat(rowItems[1]),
                Float.parseFloat(rowItems[2]));

    }


}
