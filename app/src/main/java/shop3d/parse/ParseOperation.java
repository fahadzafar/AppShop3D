package shop3d.parse;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import shop3d.util.SPManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fahad on 11/6/2015.
 */
public class ParseOperation {

    // ---------------------- Get all items per order
    public static ArrayList<ParseObject> GetItemsPerOrder(ParseObject order) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Order_Item");
        query.whereEqualTo("order_id", order);
        query.include("model_id");
        query.include("usermade_model_id");

        query.orderByAscending("createdAt");
        try {
            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        } catch (Exception er) {
        }
        return new ArrayList<ParseObject>();
    }


    // -------------------------------- Get ALL My Models
    public static ArrayList<ParseObject> GetAllMyModels(int currentPage) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Model_UserMade");
            query.include("original_model_id");
            query.whereEqualTo("user_id", SPManager.current_user);
            query.whereEqualTo("visible",true );
            query.orderByDescending("createdAt");
            query.setLimit((SPManager.ListPageSize));
            query.setSkip(SPManager.ListPageSize * currentPage);

            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }

        } catch (
                Exception er
                )

        {
            int y = 0;

        }

        return new ArrayList<ParseObject>();
    }


    // ------------------------------------------------------------

    public static void VizFalseParseObject(ParseObject delId) {
        try {
            delId.put("visible", false);
            delId.save();
        } catch (Exception er) {
            int y = 0;
        }
    }

    public static void RemoveParseObject(ParseObject delId) {
        try {
            delId.delete();
        } catch (Exception er) {
            int y = 0;
        }
    }

    public static ArrayList<ParseObject> GetAllAuthorNotes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("AuthorNotes");
        query.orderByDescending("createdAt");
        try {
            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        } catch (Exception er) {
        }
        return new ArrayList<ParseObject>();
    }

    public static ArrayList<ParseObject> GetAllShippingAddress() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Shipping_Address");
        query.whereEqualTo("user_id", SPManager.current_user);
        query.setLimit(SPManager.ShippingAddressLimit);
        query.orderByDescending("createdAt");
        try {
            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        } catch (Exception er) {
        }
        return new ArrayList<ParseObject>();
    }

    public static void FillShippingRates() {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Shipping_Rates");
            query.orderByAscending("order");

            List<ParseObject> ans = query.find();
            if (ans != null) {
                SPManager.ShippingRates = new ArrayList<ParseObject>(ans);
            }

        } catch (Exception er) {
        }
    }


    public static ArrayList<ParseObject> GetAllOrders(int currentPage) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
            query.whereEqualTo("user_id", SPManager.current_user);
            query.addDescendingOrder("createdAt");

            query.setLimit((SPManager.ListPageSize));
            query.setSkip(SPManager.ListPageSize * currentPage);

            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        } catch (Exception er) {
            int y = 0;
        }
        return new ArrayList<ParseObject>();
    }


    public static ArrayList<ParseObject> GetAllFavoriteModels(int currentPage) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
            query.include("model_id");
            query.include("usermade_model_id");
            query.whereEqualTo("user_id", SPManager.current_user);

            query.setLimit((SPManager.ListPageSize));
            query.setSkip(SPManager.ListPageSize * currentPage);

            List<ParseObject> ans = query.find();
            if (ans != null) {
                return new ArrayList<ParseObject>(ans);
            }
        } catch (Exception er) {
            int y = 0;
        }
        return new ArrayList<ParseObject>();
    }


    // -----------------------------------------------------------------------------

    public static void LogOutCurrentUser(Context con) {
        SPManager.current_user.logOut();
        ParseUser.logOut();
        SPManager.current_user = null;
        SPManager.clear(con);
    }


    public static ArrayList<ParseObject> GetAllCategories() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.orderByDescending("name");

        List<ParseObject> ans = null;
        try {
            ans = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (ans == null) {
            return new ArrayList<ParseObject>();
        } else
            return new ArrayList<ParseObject>(ans);
    }
}
