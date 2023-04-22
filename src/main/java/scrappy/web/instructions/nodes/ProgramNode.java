package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

import java.util.List;

/**
 * Contains instructions to execute
 * @param instructions Instruction node to execute
 */
public record ProgramNode(List<IInstructionNode> instructions) implements IInstructionNode {
    @Override
    public void apply(Page page, Variables var) {
        for (IInstructionNode instruct: instructions) {
            instruct.apply(page, var);
        }
    }
}
