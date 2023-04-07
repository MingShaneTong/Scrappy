package scrappy.web.instructions.nodes;

import com.microsoft.playwright.Page;
import scrappy.web.instructions.Variables;

import java.util.List;

public class ProgramNode implements IInstructionNode {
    private List<IInstructionNode> instructions;

    public ProgramNode(List<IInstructionNode> instructions) {
        this.instructions = instructions;
    }

    @Override
    public void apply(Page page, Variables var) {
        for (IInstructionNode instruct: instructions) {
            instruct.apply(page, var);
        }
    }
}
