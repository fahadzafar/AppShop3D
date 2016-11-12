package shop3d.parse;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import shop3d.util.Helper;
import shop3d.util.SPManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fahad on 11/13/2015.
 */
public class ParseOperation_Dashboard {


    // Take care of the changable items inside a query -----------------------------------------
    // -----------------------------------------------------------------------------------------
    public static ParseQuery<ParseObject> ApplyCategoryIndexConstrain(ParseQuery<ParseObject> query, int categoryIndex) {
        // Take care of category index.
        if (categoryIndex != 0) {
            ParseObject category = SPManager.Categories.get(categoryIndex).PCatObject;
            query.whereEqualTo("category_id", category);

        }
        return query;
    }

    public static ParseQuery<ParseObject> ApplyVisibilityConstraint(ParseQuery<ParseObject> query){

        if (SPManager.IsPowerUser()) {
           ;
        } else {
            query.whereEqualTo("visible", true);
        }
        return query;
    }
    public static ParseQuery<ParseObject> ApplyPopularityIndexConstrain(ParseQuery<ParseObject> query, int popularityIndex) {
        switch (popularityIndex) {
            // Latest First
            case 0: {
                query.addDescendingOrder("priority");
                query.addDescendingOrder("createdAt");

                break;
            }
            // Oldest First
            case 1: {
                query.addDescendingOrder("priority");
                query.addAscendingOrder("createdAt");
                break;
            }
            // Highest Price
            case 2: {
                query.addDescendingOrder("priority");
                query.addAscendingOrder("smallest_size_price");
                break;
            }
            // Lowest Price
            case 3: {
                query.addDescendingOrder("priority");
                query.addDescendingOrder("smallest_size_price");
                break;
            }
        }


        return query;
    }


    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------
    // Get all Models
    public static ArrayList<ParseObject> GetAllModels(String searchTerm, int categoryIndex, int popularityIndex, int currentPage) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Model");



            query = ApplyCategoryIndexConstrain(query, categoryIndex);
            query = ApplyPopularityIndexConstrain(query, popularityIndex);
            query = ApplyVisibilityConstraint(query);


            if (searchTerm.equals("") == false) {
                searchTerm = searchTerm.toLowerCase();
                // ------

                query.whereContains("search_text", searchTerm);
            }
            //------
            query.setLimit((SPManager.ListPageSize));
            query.setSkip(SPManager.ListPageSize * currentPage);

            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        }catch(Exception er) {
            System.out.println(er.getMessage());
        }
        return new ArrayList<ParseObject>();
    }


}
