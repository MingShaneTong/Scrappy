package scrappy.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ScrappyPage {
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

    private void CaptureScreenshot(String location, Page page) {
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(location))
            .setFullPage(true));
    }

    private void CaptureHTML(String location, Page page) {
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(page.content());
        } catch (IOException e) {
            System.out.println("Capture HTML failed: " + page.url());
        }
    }

    private void CaptureTextContent(String location, Page page) {
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(page.textContent("body"));
        } catch (IOException e) {
            System.out.println("Capture HTML failed: " + page.url());
        }
    }
}
