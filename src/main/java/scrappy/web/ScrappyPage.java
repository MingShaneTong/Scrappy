package scrappy.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import scrappy.web.instructions.InstructionParser;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.nodes.IInstructionNode;

/**
 * Scraps data from the webpage
 */
public class ScrappyPage {
    /**
     * Captures data based on instructions
     * @param url
     * @param instructions
     * @param location
     */
    public void capture(String url, String instructions, String location) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            Variables var = new Variables();
            var.put("url", url);
            var.put("location", location);

            IInstructionNode instruct = InstructionParser.parse(instructions);
            instruct.apply(page, var);
        }
    }
}
