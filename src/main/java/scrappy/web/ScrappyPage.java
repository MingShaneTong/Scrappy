package scrappy.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Scraps data from the webpage
 */
public class ScrappyPage {
    /**
     * Visits the url and captures screenshot, HTML and text content
     * and saves to location
     * @param url
     * @param location
     */
    public void VisitAndCaptureData(String url, String location) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate(url);

            CaptureScreenshot(location + "/screenshot.png", page);
            CaptureHTML(location + "/page.html", page);
            CaptureTextContent(location + "/content.txt", page);
        }
    }

    /**
     * Captures a screenshot of the page and saves to location
     * @param location
     * @param page
     */
    private void CaptureScreenshot(String location, Page page) {
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(location))
            .setFullPage(true));
    }

    /**
     * Captures HTML of the page and saves to location
     * @param location
     * @param page
     */
    private void CaptureHTML(String location, Page page) {
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(page.content());
        } catch (IOException e) {
            System.out.println("Capture HTML failed: " + page.url());
        }
    }

    /**
     * Captures text of the page and saves to location
     * @param location
     * @param page
     */
    private void CaptureTextContent(String location, Page page) {
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(page.textContent("body"));
        } catch (IOException e) {
            System.out.println("Capture HTML failed: " + page.url());
        }
    }
}
