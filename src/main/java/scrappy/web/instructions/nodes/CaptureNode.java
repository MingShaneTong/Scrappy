package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.CaptureType;
import scrappy.web.instructions.parameters.Selector;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Captures the text content of selector
 * @param type Type of text to capture
 * @param selector Selector reference to the HTML element
 * @param file File to save content to
 */
public record CaptureNode(CaptureType type, Selector selector, String file) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        String folder = var.get("location");

        Locator locator = page.locator(selector.selector());
        try (FileWriter writer = new FileWriter(folder + file)) {
            switch (type) {
                case HTML:
                    writer.append(locator.innerHTML());
                    break;
                case TEXTCONTENT:
                    writer.append(locator.textContent());
                    break;
                default:
                    throw new RuntimeException("Capture type is not supported");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
