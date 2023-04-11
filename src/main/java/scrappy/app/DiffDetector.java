package scrappy.app;

import name.fraser.neil.plaintext.diff_match_patch;
import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DiffDetector {
    public void detectDifferences(Issue issue) {
        detectDifferences(issue, "");
    }

    public void detectDifferences(Issue issue, String location) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + issue.getKey() + "/";
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                detectDifferences(subIssue, nextLocation);
            }
        } else if (issue instanceof UrlIssue) {
            Path archive = Paths.get(AppLocations.ARCHIVE + nextLocation + AppLocations.DIFF_FILE);
            Path artifacts = Paths.get(AppLocations.ARTIFACTS + nextLocation + AppLocations.DIFF_FILE);
            Path diff = Paths.get(AppLocations.DIFF + nextLocation + AppLocations.DIFF_FILE);
            recordDifferences(archive, artifacts, diff);
        }
    }

    private void recordDifferences(Path archive, Path artifacts, Path diff) {
        try {
            String archiveStr = Files.exists(archive) ? Files.readString(archive) : "";
            String artifactsStr = Files.exists(artifacts) ? Files.readString(artifacts) : "";

            diff_match_patch diffLib = new diff_match_patch();
            LinkedList<diff_match_patch.Diff> diffs = diffLib.diff_main(archiveStr, artifactsStr);
            diffLib.diff_cleanupSemantic(diffs);
            String diffString = diffToString(diffs);
            System.out.println(diffLib.diff_prettyHtml(diffs));

            Files.createDirectories(diff.getParent());
            try (FileWriter writer = new FileWriter(String.valueOf(diff))) {
                writer.write(diffString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String diffToString(List<diff_match_patch.Diff> diffs) {
        StringBuilder sb = new StringBuilder();
        diff_match_patch.Diff previous = null;

        for (diff_match_patch.Diff current: diffs) {
            switch (current.operation) {
                case DELETE:
                    if (previous != null && previous.operation == diff_match_patch.Operation.EQUAL) {
                        sb.append(sampleEnd(previous.text));
                    }
                    sb.append("(")
                        .append(current.text.replace("\n", ")\n("))
                        .append(")");
                    break;

                case INSERT:
                    if (previous != null && previous.operation == diff_match_patch.Operation.EQUAL) {
                        sb.append(sampleEnd(previous.text));
                    }
                    sb.append("<")
                        .append(current.text.replace("\n", ">\n<"))
                        .append(">");
                    break;

                case EQUAL:
                    if (previous != null) {
                        sb.append(sampleStart(current.text));
                        sb.append("\n\n");
                    }
            }
            previous = current;
        }

        return sb.toString();
    }
}
