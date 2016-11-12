package shop3d.util;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Fahad on 12/9/2015.
 */
public class HtmlInfoExtractor {

    public static String inProcess = "";

    public static boolean IsCancelled(String htmlDoc) {
        if (htmlDoc.contains("This order has been cancelled")) {
            return true;
        }
        return false;
    }

    public static ArrayList<String> GetUPSLinks(String htmlDoc) {
        ArrayList<String> upsFound = new ArrayList<String>();

        String token = "\"http://wwwapps.ups.com/WebTracking/";
        if (htmlDoc.contains(token)) {
            int indexOfMarker = htmlDoc.indexOf(token);
            String docSplit = htmlDoc.substring(indexOfMarker + 1, htmlDoc.indexOf("\"", indexOfMarker + 1));

            indexOfMarker = docSplit.indexOf("InquiryNumber1=");
            docSplit = docSplit.substring( docSplit.indexOf("=", indexOfMarker + 1) + 1, docSplit.indexOf("&", indexOfMarker + 1));


            // Now extract the number
            String preText = "https://wwwapps.ups.com/WebTracking/track?loc=en_US&track.x=Track&trackNums=";
            upsFound.add(preText + docSplit);

        }
        return upsFound;
    }

    public static ArrayList<String> GetStepsDone(String htmlDoc) {
        inProcess = "";
        ArrayList<String> stepsDone = new ArrayList<String>();

        String token = "class=\"step_done\"";
        if (htmlDoc.contains(token)) {
            String[] docSplit = htmlDoc.split(token);
            for (int i = 1; i < docSplit.length; i++) {
                String process = "";

                if (docSplit[i].contains("Queuing")) {
                    if (i != 1)
                        break;
                    else {
                        int startingI = docSplit[i].indexOf("=\"");
                        int endingI = docSplit[i].indexOf("\">");

                        process = docSplit[i].substring(startingI + 2, endingI);
                        stepsDone.add(process);
                    }
                } else if (docSplit[i].contains("step_in_progress")) {
                    // Get the stage that is in progress
                    String[] tokens = docSplit[i].split("step_in_progress");

                    if (tokens.length >= 2) {
                        int startingI = tokens[1].indexOf("=\"");
                        int endingI = tokens[1].indexOf("\">");
                        inProcess = tokens[1].substring(startingI + 2, endingI);
                        ;


                        // -------------- Also recover the stage that was in this string..
                        startingI = tokens[0].indexOf("=\"");
                        endingI = tokens[0].indexOf("\">");

                        process = tokens[0].substring(startingI + 2, endingI);
                        stepsDone.add(process);

                    }
                    // -----------------------------
                    break;
                }

                // To stop from repeating the queue, stage
                else if (docSplit[i].contains("step_in_progress") == false) {
                    int startingI = docSplit[i].indexOf("=\"");
                    int endingI = docSplit[i].indexOf("\">");

                    process = docSplit[i].substring(startingI + 2, endingI);
                    stepsDone.add(process);
                }


            }
        }
        return stepsDone;
    }

    public static ArrayList<String> GetLatestEDeliveryDates(String htmlDoc) {
        ArrayList<String> estDeliveries = new ArrayList<String>();

        String token = "<td title=\"Estimated Delivery\">";
        if (htmlDoc.contains(token)) {
            String[] docSplit = htmlDoc.split(token);
            for (int i = 1; i < docSplit.length; i++) {
                String date = docSplit[i].substring(0, docSplit[1].indexOf("</td>"));
                estDeliveries.add(date);
            }
        }
        return estDeliveries;
    }

    public static ArrayList<String> GetLatestSDeliveryDates(String htmlDoc) {
        // Elements els = doc.getElementsByClass("order_item").not(".pad_10").not(".pad_20");
        ArrayList<String> sDeliveries = new ArrayList<String>();

        String token = "<td title=\"Shipped\">";
        if (htmlDoc.contains(token)) {
            String[] docSplit = htmlDoc.split(token);
            for (int i = 1; i < docSplit.length; i++) {
                String date = docSplit[i].substring(0, docSplit[1].indexOf("</td>"));
                sDeliveries.add(date);
            }
        }
        return sDeliveries;
    }

    public static String GetOriginalEDeliveryDate(String htmlDoc) {
        /*
        <div class="light-frame fright">
        <label>Order received : &nbsp;Dec. 8, 2015</label>
        <br class="clr">
        <label>Original shipping estimate : &nbsp;Dec. 10, 2015</label>
        <br class="clr">
        <label>Original delivery estimate : &nbsp;Dec. 14, 2015</label>
        </div>
        */
        if (htmlDoc.contains("Original delivery estimate")) {
            String[] docSplit = htmlDoc.split("Original delivery estimate");
            String devDate = docSplit[1].substring(docSplit[1].indexOf(";") + 1, docSplit[1].indexOf("<"));
            return devDate;

        } else
            return "";
    }

    public static String GetOriginalEShippingDate(String htmlDoc) {
        /*
        <div class="light-frame fright">
        <label>Order received : &nbsp;Dec. 8, 2015</label>
        <br class="clr">
        <label>Original shipping estimate : &nbsp;Dec. 10, 2015</label>
        <br class="clr">
        <label>Original delivery estimate : &nbsp;Dec. 14, 2015</label>
        </div>
        */
        if (htmlDoc.contains("Original shipping estimate")) {
            String[] docSplit = htmlDoc.split("Original shipping estimate");
            String devDate = docSplit[1].substring(docSplit[1].indexOf(";") + 1, docSplit[1].indexOf("<"));
            return devDate;

        } else
            return "";
    }

    public static String GetOrderDate(String htmlDoc) {
        /*
        <div class="light-frame fright">
        <label>Order received : &nbsp;Dec. 8, 2015</label>
        <br class="clr">
        <label>Original shipping estimate : &nbsp;Dec. 10, 2015</label>
        <br class="clr">
        <label>Original delivery estimate : &nbsp;Dec. 14, 2015</label>
        </div>
        */
        if (htmlDoc.contains("Order received")) {
            String[] docSplit = htmlDoc.split("Original shipping estimate");
            String devDate = docSplit[1].substring(docSplit[1].indexOf(";") + 1, docSplit[1].indexOf("<"));
            return devDate;

        } else
            return "";
    }


}
