package shop3d.util;

import android.content.res.Resources;

import com.parse.ParseObject;
import com.stripe.model.Account;

import java.util.ArrayList;
import java.util.Arrays;

import activity.shop3d.org.shop3d.R;
import shop3d.parse.ParseOperation;

/**
 * Created by Fahad on 11/22/2015.
 */
public class Helper_Shipping {

    public static float GetTax(String countryName, Resources res) {
        // Check for european countries first
        String[] euCountries = res.getStringArray(R.array.iso_european_countries);
        ArrayList<String> euArr = new ArrayList<String>(Arrays.asList(euCountries));
        if (euArr.contains(countryName) ||
                countryName.equals("France,FR") ||
                countryName.equals("Switzerland,CH") ||
                countryName.equals("United Kingdom,GB")) {
            return 0.2f;
        } else
            return 0.0f;
    }

    public static ArrayList<ParseObject> ExtractAllCountryShippingOptionsParseObj(String countryName, Resources res) {
        // Now get the country specific shipping options. Fill shipping options if necessary
        if (SPManager.ShippingRates.size() == 0) {
            ParseOperation.FillShippingRates();
        }

        // Check for european countries first
        String[] euCountries = res.getStringArray(R.array.iso_european_countries);
        ArrayList<String> euArr = new ArrayList<String>(Arrays.asList(euCountries));
        if (euArr.contains(countryName)) {
            countryName = "Rest of Europe";
        }


        ArrayList<ParseObject> allCountryShipOptions = new ArrayList<ParseObject>();
        for (int i = 0; i < SPManager.ShippingRates.size(); i++) {
            String ParseDBCounty = SPManager.ShippingRates.get(i).getString("country");
            if (ParseDBCounty.equals(countryName)) {
                allCountryShipOptions.add(SPManager.ShippingRates.get(i));
            }
        }

        // Could be an internationl country with no special shipping
        if (allCountryShipOptions.size() == 0) {
            countryName = "Other";
            for (int i = 0; i < SPManager.ShippingRates.size(); i++) {
                String ParseDBCounty = SPManager.ShippingRates.get(i).getString("country");
                if (ParseDBCounty.equals(countryName)) {
                    allCountryShipOptions.add(SPManager.ShippingRates.get(i));
                }
            }
        }


        return allCountryShipOptions;

    }

    public static ArrayList<String> ExtractAllCountryShippingOptionsInStrings(ArrayList<ParseObject> input) {
        ArrayList<String> allCountryShipOptions = new ArrayList<String>();
        for (int i = 0; i < input.size(); i++) {
            allCountryShipOptions.add(input.get(i).getString("type"));
        }
        return allCountryShipOptions;
    }

}
