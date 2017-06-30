package tm.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by antho on 6/7/2017.
 */
public class HTMLParser {

    public static final String MASTER_URL = "https://patents.google.com/?q=Google&assignee=Google+Inc.";
    public static String[] urls;

    public HTMLParser() throws IOException {

    }


    public static void execute(String[] patentsURL) {
        urls = patentsURL;
        try {
            Document mainDoc = Jsoup.connect(MASTER_URL).get();
            for (int i = 0; i < 20; i++) {
                System.out.println("Parsing document from : " + urls[i]);
                Document doc = Jsoup.connect(urls[i]).get();
                Element searchApp = doc.select("search-app").first();
                String textdesc = searchApp.select("section[itemprop=description]").first().select("[mxw-id]").first().text();
                try (PrintWriter out = new PrintWriter(String.format("src/main/resources/desc-%d.txt", i))) {
                    out.println(textdesc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
