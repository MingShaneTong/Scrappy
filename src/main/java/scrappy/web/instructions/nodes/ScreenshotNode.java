package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

import java.nio.file.Paths;

public class ScreenshotNode implements IInstructionNode {
    private final Selector selector;
    private final String file;

    public ScreenshotNode(Selector selector, String file) {
        this.selector = selector;
        this.file = file;
    }

    @Override
    public void apply(Page page, Variables var) {
        String folder = var.get("location");
        page.locator(selector.getSelector())
            .screenshot(
                new Locator.ScreenshotOptions()
                    .setPath(Paths.get(folder + file))
            );
    }
}
