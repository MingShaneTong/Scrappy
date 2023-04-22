package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

/**
 * Waits a for selector to become available
 * @param selector
 */
public record WaitForNode(Selector selector) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        page.waitForSelector(selector.selector());
    }
}
