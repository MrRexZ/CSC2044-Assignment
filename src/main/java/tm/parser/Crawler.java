package tm.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony Tjuatja on 6/30/2017.
 */
public class Crawler {
    public static void main(String[] args) throws Exception {
        ArrayList<String> listOfPatentsURL = new ArrayList<>();
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());

        final String baseURL = "https://patents.google.com/";
        // Go to the Google Suggest home page
        driver.get(baseURL);

        // Enter the query string "Cheese"
        WebElement query = driver.findElement(By.name("q"));
        WebElement submitButton = driver.findElement(By.id("searchButton"));
        query.sendKeys("Google");
        submitButton.click();


        while (listOfPatentsURL.size() < 20) {
            // Sleep until the div we want is visible or 5 seconds is over
            long end = System.currentTimeMillis() + 200000;
            while (System.currentTimeMillis() < end) {
                ArrayList<WebElement> resultsDiv = (ArrayList<WebElement>) driver.findElements(By.tagName("search-result-item"));
                if (resultsDiv.size() > 0) {
                    break;
                }
            }
            List<WebElement> allResults = driver.findElements(By.xpath("//state-modifier[@class='result-title style-scope search-result-item']"));
            System.out.println(allResults.size());
            for (WebElement result : allResults) {
                listOfPatentsURL.add(baseURL + result.getAttribute("open-result"));
                System.out.println(result.getAttribute("open-result"));
            }

            WebElement nextButton = driver.findElement(By.xpath("//state-modifier[@page='next']"));
            nextButton.click();
        }
        driver.quit();
    }
}

