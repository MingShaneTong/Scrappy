package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

/**
 * Allows users to write comments in instructions without effecting functionality
 */
public record CommentNode() implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {}
}
