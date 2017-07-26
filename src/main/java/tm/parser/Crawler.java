package tm.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Anthony Tjuatja on 6/30/2017.
 */
public class Crawler {
    public static void main(String[] args) throws Exception {
        ArrayList<String> listOfPatentsURL = new ArrayList<>();
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());

        final String baseURL = "https://patents.google.com/";
        driver.get(baseURL);

        WebElement query = driver.findElement(By.name("q"));
        WebElement submitButton = driver.findElement(By.id("searchButton"));
        query.sendKeys("Google");
        submitButton.click();


        while (listOfPatentsURL.size() < 20) {
            //System.out.println(driver.getTitle());
            long end = System.currentTimeMillis() + 20000;
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

            WebElement nextButton = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath("//paper-icon-button[@icon='chevron-right']")));
            nextButton.click();

            //Codes below Attempt to get the page header number to identify that the next page has finished loading
            // List<WebElement> pageCount =  driver.findElements(By.xpath("//div[@class='count style-scope search-results']//span[@class='style-scope search-results']"));
            // System.out.println(pageCount.getText());
            // for (WebElement pageHeaderElement : pageCount) {
            //System.out.println(pageHeaderElement.getText());
            // }


            //Current workaround to ensure the AJAX call to ensure correctness of fetching new patents is to put timeout of 5 seconds
            Thread.sleep(5000);

            //The code below does not provide positive for this context since nextButton.click() is already a blocking call,
            // but may when the features are extended
            while (!waitForJStoLoad(driver)) {
            }
        }
         driver.quit();
        startHTMLParser(listOfPatentsURL);
    }

    public static void startHTMLParser(ArrayList<String> urls) {
        HTMLParser.execute( urls.toArray(new String[urls.size()]));
    }

    public static boolean waitForJStoLoad(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, 30);
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .toString().equals("complete");
            }
        };

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }
}

