package shop3d.beans;

import com.parse.ParseObject;

/**
 * Created by Fahad on 11/10/2015.
 */
public class SaleItem {
    public ParseObject item;
    public int SizeIndex;
    public int Quantity;
    public double SalePrice;
    public double PrintPrice;
    public double ProfitPercentage;


    public SaleItem(ParseObject incObject, int scaleIndex, int iQuantity, double iPPrice, double iSPrice
    , double iProfitPercentage) {
        SizeIndex = scaleIndex;
        item = incObject;
        Quantity = iQuantity;
        SalePrice = iSPrice;
        PrintPrice = iPPrice;
        ProfitPercentage = iProfitPercentage;
    }


}
