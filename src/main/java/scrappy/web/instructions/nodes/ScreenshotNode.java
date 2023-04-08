package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

import java.nio.file.Paths;

public record ScreenshotNode(Selector selector, String file) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        String folder = var.get("location");
        page.locator(selector.selector())
            .screenshot(
                new Locator.ScreenshotOptions()
                    .setPath(Paths.get(folder + file))
            );
    }
}
