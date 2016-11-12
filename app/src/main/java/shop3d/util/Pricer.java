package shop3d.util;

import com.parse.ParseObject;

import java.util.ArrayList;

import shop3d.beans.SaleItem;

/**
 * Created by Fahad on 11/27/2015.
 */
public class Pricer {

    String shippingCost = "";
    String salePrice = "";
    String taxCost = "";
    String totalItemCount = "";
    String totalItemTypeCount = "";
    String status = "";
    String chargeAmount = "";

    public Pricer() {

    }

    public void Init(ParseObject obj) {
        // The cost of shipping.
        double dShipCost = obj.getDouble("shipping_cost");
        double dShipCCCost = obj.getDouble("shipping_cc_cost");
        double finalShipCost = dShipCost + dShipCCCost;
        shippingCost = Printable.MakePrintablePrice((float) finalShipCost);

        // Printing costs.
        double finalSalePrice = obj.getDouble("sale_price");
        salePrice = Printable.MakePrintablePrice((float) finalSalePrice);


        // Tax.


        // Amount charged to the credit card.
        double finalChargeAmount = obj.getDouble("charge_amount");
        chargeAmount = Printable.MakePrintablePrice((float) finalChargeAmount);


        // Total items.
        double itemCount = obj.getDouble("total_item_count");
        totalItemCount = itemCount + "";

        // Total types of items.
        double itemTypeCount = obj.getDouble("total_item_type_count");
        totalItemTypeCount = itemCount + "";

        // Status of the order.
        String oStatus = obj.getString("status");
        if (oStatus.equals("paid")) {
            status = "Order Processing";
        }
        if (oStatus.equals("posted")) {
            status = "Order is in print queue";
        }

    }


    public static double GetDouble(ParseObject item, int index, String fieldTitle) {
        String allPrices = item.getString(fieldTitle);
        double ans = Double.parseDouble(Printable.SplitAndGetIndex(allPrices, index));
        return ans;
    }

}
