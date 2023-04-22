package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

/**
 * Clicks on the element at selector
 * @param selector Selector reference to the HTML element
 */
public record ClickNode(Selector selector) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        page.click(selector.selector());
    }
}
