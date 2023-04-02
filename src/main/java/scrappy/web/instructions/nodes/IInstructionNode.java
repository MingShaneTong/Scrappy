package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

public interface IInstructionNode {
    void apply(Page page, Variables var);
}
