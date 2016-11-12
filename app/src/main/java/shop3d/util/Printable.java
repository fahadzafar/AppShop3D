package shop3d.util;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Fahad on 11/11/2015.
 */
public class Printable {

    // ------------ Shipping

    public static String GetUsername() {
        return "User: " + SPManager.current_user.getUsername();
    }

    public static String GetShippingTitle(ParseObject obj) {
        return "Title: " + obj.getString("address_title");
    }

    public static String GetShippingAddressLine(ParseObject obj, int num) {
        if (num == 1)
            return obj.getString("address_line_1");
        else
            return obj.getString("address_line_2");
    }

    // -----------------
    public static String Separator = "\\*";

    public static String GetModifersWithoutTitle( ParseObject obj) {
        return GetModifersInt(obj) + "";
    }
    public static String GetModifers( ParseObject obj) {
        return "Changeable Colors: " + GetModifersInt(obj);
    }

    public static int GetModifersInt(ParseObject obj) {
        return obj.getInt("mtl_flat_count");
    }

    public static String GetPrice(ParseObject obj, int index) {
        String price = SplitAndGetIndex(obj.getString("sale_price"), index);
        return price + " $ (USD)";
    }

    public static String MakePrintablePrice(float val) {
        return Helper.FormatDouble(val, 2) + " $ (USD)";
    }

    public static float GetPriceInInt(ParseObject obj, int index) {
        String price = SplitAndGetIndex(obj.getString("sale_price"), index);
        return Float.parseFloat(price);
    }

    public static String SplitAndGetIndex(String input, int index) {
        String[] tokens = input.split(Separator);
        if (index >= tokens.length)
            return "-";
        else
            return tokens[index];
    }


    public static String GetCategory(ParseObject obj) {
        String catId = obj.getParseObject("category_id").getObjectId();

        String returnCatLabel = "X";
        for (int i = 0; i < SPManager.Categories.size(); i++) {
            if (SPManager.Categories.get(i).Id.equals(catId)) {
                returnCatLabel = SPManager.Categories.get(i).Name;
            }
        }

        return returnCatLabel;
    }

    public static float GetRatingsFloat(ParseObject obj) {
        double avg_rating = obj.getDouble("rating");
        if (avg_rating == 0)
            avg_rating = 4.8;

       // double avg_rating = obj.getDouble("average_rating");
        //double rating_count = obj.getInt("total_rating_count");

        double printRating = Helper.FormatDouble(avg_rating, 1);

        return (float) printRating;
    }

    public static String GetRatings(ParseObject obj) {
        String rating = "Rating: " + GetRatingsFloat(obj);
        return rating;
    }

    public static int GetScaleCount(ParseObject obj) {
        String[] tokens = obj.getString("scales").split("\\*");
        return tokens.length;
    }

    public static String GetTitle(ParseObject obj) {
        String title = obj.getString("title");
        String[] tokens = title.split(" ");
        StringBuilder retTitle = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].length() != 0) {
                String output = Character.toUpperCase(tokens[i].charAt(0)) + tokens[i].substring(1);
                retTitle.append(output);
                retTitle.append(" ");
            }
        }

        return retTitle.toString();
    }

    public static String GetDescription(ParseObject obj) {
        String descp = obj.getString("description");
        String output = Character.toUpperCase(descp.charAt(0)) + descp.substring(1);
        return output;
    }

    public static ArrayList<String> GetSizeArray(ParseObject obj) {
        String Xdims = obj.getString("dims");
        String[] dims = Xdims.split(Separator);
        return new ArrayList(Arrays.asList(dims));
    }

    public static ArrayList<String> GetScaleArray(ParseObject obj) {

        String[] dims = obj.getString("scales").split(Separator);
        return new ArrayList(Arrays.asList(dims));
    }


    public static String GetScale(ParseObject obj, int index) {

        String[] dims = obj.getString("scales").split(Separator);
        return dims[index];
    }

    public static String GetSize(ParseObject obj, int index) {
        String Xdims = obj.getString("dims");
        String[] dims = Xdims.split(Separator);
        return dims[index];
    }

    public static String GetTallSize(ParseObject obj) {
        int totalDims = GetScaleCount(obj);
        String allDims = obj.getString("dims");
        String minDim = SplitAndGetIndex(allDims, 0);
        String maxDim = SplitAndGetIndex(allDims, totalDims - 1);

        String[] minz = minDim.split("x");
        String[] maxz = maxDim.split("x");

        String returnTall = "" + minz[2] + " - " + maxz[2] + " inches";
        return returnTall;

    }
}
