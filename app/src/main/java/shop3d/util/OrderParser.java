package shop3d.util;


import android.content.Context;
import android.provider.DocumentsContract;
import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Fahad on 12/7/2015.
 */
public class OrderParser {

    public boolean IsCancelled = false;
    public ArrayList<String> EDeliveryDate = new ArrayList<String>();
    public ArrayList<String> EShipDate = new ArrayList<String>();
    public ArrayList<String> StepsDone = new ArrayList<String>();
    public ArrayList<String> UpsLinks = new ArrayList<String>();
    public String StepInProgress = "";
    public String StepCompleted = "";

    public OrderParser(){

    }
    public void LoadHtmlPage(String orderId) {

        try {
            Document doc = Jsoup.connect("http://www.sculpteo.com/en/shop/tracking/reference/"+orderId).get();

            String htmlData = doc.toString();

            EDeliveryDate = HtmlInfoExtractor.GetLatestEDeliveryDates(htmlData);

            // Always call i this order.
            StepsDone =  HtmlInfoExtractor.GetStepsDone(htmlData);
            StepInProgress = HtmlInfoExtractor.inProcess;

            for (int i = 0; i < StepsDone.size(); i++) {
                StepCompleted += StepsDone.get(i) + ", ";
            }
            EShipDate = HtmlInfoExtractor.GetLatestSDeliveryDates(htmlData);

            if (HtmlInfoExtractor.IsCancelled(htmlData)) {
                System.out.println("cancelled");
                IsCancelled = true;
            }

            UpsLinks = HtmlInfoExtractor.GetUPSLinks(htmlData);
/*
            Element table = doc.select("table[class=result light-frame]").first();// "table class=\"result light-frame\"");

            Elements allOrderItems = table.select("tr");

            for (Element element : allOrderItems) {
                Elements eachItem = element.select("td[class=first_td]");
                for (Element orderItem : eachItem) {
                    String myData = orderItem.text();
                    System.out.print(myData);

                }
            }
*/
        } catch (Exception er) {

            int yy = 0;
            System.out.println(er.getMessage());

        }
    }

}
