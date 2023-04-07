package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;
import scrappy.web.instructions.parameters.Selector;

public class WaitForNode implements IInstructionNode {
    private Selector selector;

    public WaitForNode(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void apply(Page page, Variables var) {
        page.waitForSelector(selector.getSelector());
    }
}
