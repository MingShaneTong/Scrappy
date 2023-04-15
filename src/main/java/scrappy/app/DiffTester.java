package scrappy.app;

import scrappy.app.steps.DiffDetector;
import scrappy.core.diff.DiffMatch;

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
        String str1 = Files.readString(path1, StandardCharsets.UTF_8);
        String str2 = Files.readString(path2, StandardCharsets.UTF_8);

        DiffMatch diffLib = new DiffMatch(DiffMatch.DiffSize.LINE);
        DiffDetector dd = new DiffDetector();
        List<DiffMatch.Diff> diffs = diffLib.findDiffs(str1, str2);
        String d = dd.diffToAdf(diffs);
        System.out.println(d);
    }
}
