package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

public record VisitNode(String url) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        String baseUrl = var.get("url");
        page.navigate(baseUrl + url);
    }
}
