package scrappy.app;

import name.fraser.neil.plaintext.diff_match_patch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class DiffTester {
    public static void main(String[] args) throws IOException {
        String file1 = args[0];
        String file2 = args[1];
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);
        String str1 = Files.readString(path1, StandardCharsets.UTF_8);
        String str2 = Files.readString(path2, StandardCharsets.UTF_8);

        diff_match_patch diffLib = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diffs = diffLib.diff_main(str1, str2);
        diffLib.diff_cleanupSemantic(diffs);

    }
}
