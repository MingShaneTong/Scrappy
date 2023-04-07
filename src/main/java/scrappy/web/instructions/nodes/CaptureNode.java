package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.CaptureType;
import scrappy.web.instructions.parameters.Selector;

import java.io.FileWriter;
import java.io.IOException;

public class CaptureNode implements IInstructionNode {
    private final CaptureType type;
    private final Selector selector;
    private final String file;

    public CaptureNode(CaptureType type, Selector selector, String file) {
        this.type = type;
        this.selector = selector;
        this.file = file;
    }

    @Override
    public void apply(Page page, Variables var) {
        Locator locator = page.locator(selector.getSelector());
        try (FileWriter writer = new FileWriter(file)) {
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
