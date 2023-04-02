package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

public class VisitNode implements IInstructionNode {
    private final String url;

    public VisitNode(String url) {
        this.url = url;
    }

    @Override
    public void apply(Page page, Variables var) {
        page.navigate(url);
    }
}
