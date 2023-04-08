package scrappy.app;

import scrappy.core.util.Diff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DiffTester {
    public static void main(String[] args) throws IOException {
        String file1 = args[0];
        String file2 = args[1];
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);
        List<String> line1 = Files.readAllLines(path1, StandardCharsets.UTF_8);
        List<String> line2 = Files.readAllLines(path2, StandardCharsets.UTF_8);
        List<Diff.Change> diffs =  Diff.getDiff(line1, line2);
        System.out.println(diffs);
    }
}
