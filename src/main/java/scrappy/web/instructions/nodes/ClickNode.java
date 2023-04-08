package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

public record ClickNode(Selector selector) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        page.click(selector.selector());
    }
}
