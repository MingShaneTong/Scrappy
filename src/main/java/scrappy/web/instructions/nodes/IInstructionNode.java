package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

/**
 * Interface for instructions
 */
public interface IInstructionNode {
    /**
     * @param page Page to perform instructions on
     * @param var Variables local to the current scope
     */
    void apply(Page page, Variables var);
}
