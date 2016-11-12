package shop3d.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.FavoritesActivity;
import shop3d.activity.MainActivity;
import shop3d.activity.MyModelActivity;


/**
 * Created by Fahad on 11/6/2015.
 */
public class Helper {


    public static ParseObject GetRealObjectForFullScreen(String RequesterActivity, int SelectedIndex) {
        ParseObject actualObject = null;
        //  ParseObject realModel = null;
        if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_MAIN)) {
            actualObject = MainActivity.listOfModels.get(SelectedIndex);
        } else if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_FAVORITES))
            actualObject = FavoritesActivity.listOfModels.get(SelectedIndex);
        else if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_MYMODEL)) {
            actualObject = MyModelActivity.listOfModels.get(SelectedIndex);
        }

        // Only if its a favorite, return its real  object.
        if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_FAVORITES)) {
            if (actualObject.getParseObject("usermade_model_id") == null)
                actualObject = actualObject.getParseObject("model_id");

            // Could also be a usermade object.
        }
        return actualObject;

/*
        if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_MYMODEL)
                || RequesterActivity.equals(ATAGS.TAG_ACTIVITY_FAVORITES)) {
            realModel = actualObject.getParseObject("original_model_id");
        } else {
            // This code runs for  ATAGS.TAG_ACTIVITY_MAIN only
            realModel = actualObject;
        }
        return realModel;*/
    }

    public static byte[] convertToBytes(Object object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        } catch (Exception er) {
            return null;
        }
    }

    public static Bitmap AddWatermark(Context con, Bitmap screen, int borderLimit) {
        Bitmap waterMark = BitmapFactory.decodeResource(con.getResources(), R.drawable.share_logo);
        waterMark = Bitmap.createScaledBitmap(waterMark, 487, 171, true);
        Bitmap outBmp = screen.copy(screen.getConfig(), true);
        for (int y = screen.getHeight() - waterMark.getHeight(), i = 0; y < screen.getHeight(); y++, i++) {
            for (int x = screen.getWidth() - waterMark.getWidth(), j = 0; x < screen.getWidth(); x++, j++) {
                //Get Both Colours at the pixel point
                int col1 = screen.getPixel(x, y);
                int col2 = waterMark.getPixel(j, i);

                 if ((Color.alpha(col2) == 0))
                    continue;

                int r = 0, g = 0, b = 0;
                r =  Color.red(col2);
                g =  Color.green(col2);
                b =  Color.blue(col2);


                //r = Math.min((Color.red(col1) / 2 + Color.red(col2) / 2), 255);
                //g = Math.min((Color.green(col1) / 2 + Color.green(col2) / 2), 255);
                //b = Math.min((Color.blue(col1) / 2 + Color.blue(col2) / 2), 255);


                int newcol = Color.argb(255, r, g, b);
                outBmp.setPixel(x, y, newcol);


            }
        }


        // Add the border

        for (int y = 0; y < screen.getHeight(); y++) {
            for (int x = 0; x < screen.getWidth(); x++) {
                if (y < borderLimit || x < borderLimit ||
                        (screen.getHeight() - y - 1) < borderLimit || (screen.getWidth() - x - 1) < borderLimit) {
                    outBmp.setPixel(x, y, Color.BLACK);
                }

            }

        }

        return outBmp;
    }

    public static void ShowDialogue(String title, String data, Context cont) {
        try {
            Toast.makeText(cont, title + data, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object3D convertFromBytesToObject3D(byte[] bytes) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput out = new ObjectInputStream(bis);
            Object3D something = Object3D.mergeAll(Loader.loadSerializedObject(bis));

            //DeSerializer dez = new DeSerializer();

            return something;
        } catch (Exception er) {
            return null;
        }
    }

    public static ArrayList<String> SplitAndGetArrayList(String data) {
        String Separator = "\\*";
        String[] tokens = data.split(Separator);
        if (tokens.length > 0)
            return new ArrayList<String>(Arrays.asList(tokens));
        else
            return new ArrayList<String>();
    }

    public static int getRColor(int ARGB) {
        int r = ((ARGB >> 16) & 0xFF);
        return r;
    }

    public static int getGColor(int ARGB) {
        int g = ((ARGB >> 8) & 0xFF);
        return g;
    }

    public static int getBColor(int ARGB) {
        int b = (ARGB & 0xFF);
        return b;
    }

    public static ParseFile UploadParseFile(byte[] data, boolean compress) {
        try {
            if (compress) {
                data = Helper.compress(data);
            }
            ParseFile UplodedFile = new ParseFile("some_file", data);
            UplodedFile.save();
            return UplodedFile;
        } catch (Exception er) {
            System.out.println(" Parse file upload exception");
            return null;
        }
    }

    public static Bitmap GetColorBitmap(int argb, int width, int height) {
        int b = (argb) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int r = (argb >> 16) & 0xFF;

        Bitmap dumb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                dumb.setPixel(i, j, Color.argb(255, r, g, b));

        return dumb;
    }


    // Extract the country names out of the shippingRates array and return it.
    public static ArrayList<String> GetShipCountryArray() {
        ArrayList<String> allCountryNames = new ArrayList<String>();
        for (int i = 0; i < SPManager.ShippingRates.size(); i++) {
            if (allCountryNames.contains(SPManager.ShippingRates.get(i).getString("country")) == false)
                allCountryNames.add(SPManager.ShippingRates.get(i).getString("country"));
        }
        return allCountryNames;
    }

    public static Object convertFromBytes(byte[] bytes) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            Object abc = in.readObject();
            return abc;
        } catch (Exception er) {
            return null;
        }
    }

    public static int CountCharInString(String input, char x) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == x) {
                count++;
            }
        }
        return count;
    }

    public static void LaunchActivity(Context con, Class<?> obj) {
        Intent intent = new Intent();
        intent.setClass(con, obj);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        con.startActivity(intent);

    }


    public static Boolean AmIOnline(Context con) {
        final ConnectivityManager conMgr = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
            return true;
        } else {
            // notify user you are not online
            return false;
        }
    }

    public static Bitmap ConvertByteArrayToBitmap(byte[] data) {
        Bitmap bmp;
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }


    public static byte[] compress(byte[] data) {
        byte[] output = null;
        try {
            Deflater deflater = new Deflater();
            deflater.setInput(data);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer); // returns the generated code... index
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            output = outputStream.toByteArray();
        } catch (Exception er) {

        }
        // LOG.debug("Original: " + data.length / 1024 + " Kb");
        // LOG.debug("Compressed: " + output.length / 1024 + " Kb");
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        // LOG.debug("Original: " + data.length);
        // LOG.debug("Compressed: " + output.length);
        return output;
    }

    public static double FormatDouble(double input, int digits) {
        String start = ".";
        double answer = 5;
        try {

            for (int i = 0; i < digits; i++) {
                start += "#";
            }

            //"#.#", new DecimalFormatSymbols(Locale.US)
            DecimalFormat df = new DecimalFormat(start, new DecimalFormatSymbols(Locale.US));
            answer = Double.valueOf(df.format(input));

        }catch(Exception er) {

        }
        return answer;
    }

    public static boolean isEmpty(String value) {
        if (value.equals("")) {
            return true;
        }
        return false;
    }

}
