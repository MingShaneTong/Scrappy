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
    private static final IInstructionNode defaultInstruction;

    static {
        defaultInstruction = InstructionParser.parseFile("default.instruction");
    }

    /**
     * Captures data based on instructions
     * @param url Base url of the page
     * @param instructions instructions to parse and follow
     * @param location file location to save artifacts
     */
    public void capture(String url, String instructions, String location) {
        try (Playwright playwright = Playwright.create()) {
            try (Browser browser = playwright.chromium().launch()) {
                Page page = browser.newPage();

                Variables var = new Variables();
                var.put("url", url);
                var.put("location", location);

                IInstructionNode instruct;
                if (instructions == null || instructions.isBlank()) {
                    instruct = defaultInstruction;
                } else {
                    instruct = InstructionParser.parse(instructions);
                }
                instruct.apply(page, var);
            }
        }
    }
}
