package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

public record CommentNode() implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {}
}
