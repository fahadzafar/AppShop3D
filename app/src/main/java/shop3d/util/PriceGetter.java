package shop3d.util;

import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shop3d.request.Req_Base;
import shop3d.request.Req_GetPrice;


/**
 * Created by Fahad on 12/14/2015.
 */
public class PriceGetter {

    ExecutorService executor;
    String ans = "Fetching ...";
    Req_GetPrice priceMeModel;

    public PriceGetter() {
        priceMeModel = new Req_GetPrice(Req_Base.RunMode.PRODUCTION);

    }


    public double GetSinglePrice(final ParseObject model, final int scaleIndex) {
        ArrayList<String> allScales = Printable.GetScaleArray(model);
        try {
            //   for (int i = 0; i < allScales.size(); i++) {
            JSONObject priceResult = priceMeModel.SendPostRequest(model.getString("uuid"), allScales.get(scaleIndex));
            double sPricePerScale = priceResult.getJSONObject("body").getJSONObject("price").getDouble("unit_price_raw");
            sPricePerScale = Helper.FormatDouble(sPricePerScale, 2);
            return sPricePerScale;
        } catch (Exception er) {
            int y = 0;
        }
        return 0;
    }

    public static double MakeSalePrice(double printPrice, double profitPercentage) {
        double profit = printPrice * profitPercentage;
        double finalPrice = profit + printPrice;
        finalPrice = finalPrice + finalPrice * 3.0 / 100.0 + 0.30;
        return Helper.FormatDouble(finalPrice, 2);
    }


    public static String GetDisplayItemEstimatePrice(ParseObject fillModel){
        double printPrice =  fillModel.getDouble("smallest_size_price");
        double displayPrice = Helper.FormatDouble(printPrice + fillModel.getDouble("percentage_profit") * printPrice, 0);
        String pPrice = Printable.MakePrintablePrice((float)displayPrice);
        return "" + pPrice;
    }

}
