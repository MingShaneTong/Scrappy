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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DiffDetector {
    private static final String deleteAdf;
    private static final String insertAdf;
    private static final String plaintextAdf;
    private static final String hardbreakAdf;
    private static final String paragraphAdf;
    private static final String tableCellAdf;
    private static final String tableRowAdf;
    private static final String tableAdf;

    static {
        try {
            Path deletePath = Paths.get("template/document/text/deletetext.json");
            Path insertPath = Paths.get("template/document/text/inserttext.json");
            Path plaintextPath = Paths.get("template/document/text/plaintext.json");
            Path hardbreakPath = Paths.get("template/document/text/hardbreak.json");
            Path paragraphPath = Paths.get("template/document/text/paragraph.json");
            Path tableCellPath = Paths.get("template/document/table/tableCell.json");
            Path tableRowPath = Paths.get("template/document/table/tableRow.json");
            Path tablePath = Paths.get("template/document/table/table.json");

            deleteAdf = new String(Files.readAllBytes(deletePath));
            insertAdf = new String(Files.readAllBytes(insertPath));
            plaintextAdf = new String(Files.readAllBytes(plaintextPath));
            hardbreakAdf = new String(Files.readAllBytes(hardbreakPath));
            paragraphAdf = new String(Files.readAllBytes(paragraphPath));
            tableCellAdf = new String(Files.readAllBytes(tableCellPath));
            tableRowAdf = new String(Files.readAllBytes(tableRowPath));
            tableAdf = new String(Files.readAllBytes(tablePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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
            Path archive = Paths.get(AppLocations.ARCHIVE + nextLocation + AppLocations.MAIN_FILE);
            Path artifacts = Paths.get(AppLocations.ARTIFACTS + nextLocation + AppLocations.MAIN_FILE);
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
            String diffString = diffToAdf(diffs);

            Files.createDirectories(diff.getParent());
            try (FileWriter writer = new FileWriter(String.valueOf(diff))) {
                writer.write(diffString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String diffToAdf(List<diff_match_patch.Diff> diffs) {
        List<List<diff_match_patch.Diff>> changeGroups = new ArrayList<>();
        diff_match_patch.Diff previous = null;
        List<diff_match_patch.Diff> currentGroup = new ArrayList<>();

        // group diffs for differences
        for (diff_match_patch.Diff current: diffs) {
            switch (current.operation) {
                case DELETE:
                case INSERT:
                    // sample start
                    if (currentGroup.isEmpty() && previous != null && previous.operation == diff_match_patch.Operation.EQUAL) {
                        currentGroup.add(previous);
                    }
                    currentGroup.add(current);
                    break;
                case EQUAL:
                    if (previous != null) {
                        currentGroup.add(current);
                    }
                    if (current.text.contains("\n")){
                        changeGroups.add(currentGroup);
                        currentGroup = new ArrayList<>();
                    }
                    break;
            }
            previous = current;
        }
        if (!currentGroup.isEmpty()){
            changeGroups.add(currentGroup);
        }

        if (changeGroups.isEmpty()) {
            return "";
        }

        // to strings
        String tableRows = changeGroups.stream().map(group -> {
            List<String> deleteStream = new ArrayList<>();
            List<String> insertStream = new ArrayList<>();

            // create delete and insert stream
            group.stream().forEach(diff -> {
                switch (diff.operation) {
                    case DELETE:
                        deleteStream.add(deleteToAdf(diff.text));
                        break;
                    case INSERT:
                        insertStream.add(insertToAdf(diff.text));
                        break;
                    case EQUAL:
                        String adf;
                        if (diff == group.get(0)) {
                            adf = sampleEnd(diff.text);
                        } else if (diff == group.get(group.size() - 1)) {
                            adf = sampleStart(diff.text);
                        } else {
                            adf = plaintextToAdf(diff.text);
                        }
                        deleteStream.add(adf);
                        insertStream.add(adf);
                        break;
                }
            });

            // create table rows
            String deleteCell = String.format(tableCellAdf,
                String.format(paragraphAdf,
                    String.join(", ", deleteStream))
            );
            String insertCell = String.format(tableCellAdf,
                String.format(paragraphAdf,
                    String.join(", ", insertStream))
            );

            return String.format(tableRowAdf, deleteCell + ", " + insertCell);
        }).collect(Collectors.joining(", "));

        return String.format(tableAdf, tableRows);
    }

    private String deleteToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(d -> String.format(deleteAdf, d))
            .collect(Collectors.joining(", " + hardbreakAdf + ", "));
    }

    private String insertToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(d -> String.format(insertAdf, d))
            .collect(Collectors.joining("," + hardbreakAdf + ","));
    }

    private String plaintextToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(d -> String.format(plaintextAdf, d))
            .collect(Collectors.joining("," + hardbreakAdf + ","));
    }

    private String sampleStart(String text) {
        String[] lines = text.split("(\\r\\n|\\r|\\n)");
        String last = lines[0];
        return String.format(plaintextAdf, last);
    }

    private String sampleEnd(String text) {
        String[] lines = text.split("(\\r\\n|\\r|\\n)");
        String last = lines[lines.length - 1];
        return String.format(plaintextAdf, last);
    }
}
