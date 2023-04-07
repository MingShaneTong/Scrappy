package scrappy.app;

import scrappy.web.instructions.InstructionParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InstructionTester {
    public static void main(String[] args) throws FileNotFoundException {
        String file = args[0];
        Scanner scanner = new Scanner(new File(file));
        InstructionParser.parseProgram(scanner);
    }
}
