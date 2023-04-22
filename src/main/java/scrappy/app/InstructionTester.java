package scrappy.app;

import scrappy.web.instructions.InstructionParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Tests whether a set of instructions can compile
 */
public class InstructionTester {
    /**
     * @param args file containing instructions to test
     * @throws FileNotFoundException Throws exception if file does not exist
     */
    public static void main(String[] args) throws FileNotFoundException {
        String file = args[0];
        Scanner scanner = new Scanner(new File(file));
        InstructionParser.parseProgram(scanner);
    }
}
