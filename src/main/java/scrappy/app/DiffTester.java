package scrappy.app;

import scrappy.core.diff.DiffMatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Tests the diff algorithm
 */
public class DiffTester {
    /**
     * @param args [file 1, file 2] to test the diff algorithm on
     * @throws FileNotFoundException Throws exception if file does not exist
     */
    public static void main(String[] args) throws IOException {
        String file1 = args[0];
        String file2 = args[1];
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);
        String str1 = Files.readString(path1, StandardCharsets.UTF_8);
        String str2 = Files.readString(path2, StandardCharsets.UTF_8);

        DiffMatch diffLib = new DiffMatch(DiffMatch.DiffDelimiter.LINE);
        List<DiffMatch.Diff> diffs = diffLib.findDiffs(str1, str2);
        System.out.println(diffs);
    }
}
