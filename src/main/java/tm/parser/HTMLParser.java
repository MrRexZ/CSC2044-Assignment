package tm.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by antho on 6/7/2017.
 */
public class HTMLParser {

    public static final String MASTER_URL = "https://patents.google.com/?q=Google&assignee=Google+Inc.";
    public static String[] urls = new String[]{
            "https://patents.google.com/patent/US7933897B2/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US20120066296A1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US20080301643A1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US8291492B2/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US7634463B1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US7685144B1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US8185830B2/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US7353114B1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US7978207B1/en?q=Google&assignee=Google+Inc.",
            "https://patents.google.com/patent/US20080183593A1/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US7933929B1/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8010407B1/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8010407B1/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8504437B1/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8060582B2/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8201081B2/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US7031961B2/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US7664734B2/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US8291341B2/en?q=Google&assignee=Google+Inc.&page=1",
            "https://patents.google.com/patent/US20080172373A1/en?q=Google&assignee=Google+Inc.&page=1"

    };

    public HTMLParser() throws IOException {

    }

    public static void main(String[] args) {

        try {
            Document mainDoc = Jsoup.connect(MASTER_URL).get();
            for (int i = 0; i < 20; i++) {
                System.out.println("Parsing : " + i);
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
