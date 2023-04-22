package scrappy.app;

import scrappy.web.instructions.InstructionParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Tests whether a set of instructions can compile
 */
public class InstructionTester {
    /**
     * @param args file containing instructions to test
     * @throws IOException Throws exception if file does not exist
     */
    public static void main(String[] args) throws IOException {
        String file = args[0];
        Path path = Paths.get(file);
        String str = Files.readString(path, StandardCharsets.UTF_8);
        InstructionParser.parse(str);
    }
}
