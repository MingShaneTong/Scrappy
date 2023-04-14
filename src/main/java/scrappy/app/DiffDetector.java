package scrappy.app;

import scrappy.core.diff.DiffMatch;
import scrappy.core.issue.builder.DescriptionBuilder;
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

            DiffMatch diffMatch = new DiffMatch(DiffMatch.DiffSize.WORD);
            List<DiffMatch.Diff> diffs = diffMatch.findDiffs(archiveStr, artifactsStr);
            String diffString = diffToAdf(diffs);

            Files.createDirectories(diff.getParent());
            try (FileWriter writer = new FileWriter(String.valueOf(diff))) {
                writer.write(diffString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String diffToAdf(List<DiffMatch.Diff> diffs) {
        List<List<DiffMatch.Diff>> changeGroups = new ArrayList<>();
        DiffMatch.Diff previous = null;
        List<DiffMatch.Diff> currentGroup = new ArrayList<>();

        // group diffs for differences
        for (DiffMatch.Diff current: diffs) {
            switch (current.operation()) {
                case DELETE:
                case INSERT:
                    // sample start
                    if (currentGroup.isEmpty() && previous != null && previous.operation() == DiffMatch.Operation.EQUAL) {
                        currentGroup.add(previous);
                    }
                    currentGroup.add(current);
                    break;
                case EQUAL:
                    if (previous != null) {
                        currentGroup.add(current);
                    }
                    if (current.text().contains("\n")){
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
                switch (diff.operation()) {
                    case DELETE:
                        deleteStream.add(deleteToAdf(diff.text()));
                        break;
                    case INSERT:
                        insertStream.add(insertToAdf(diff.text()));
                        break;
                    case EQUAL:
                        String adf;
                        if (diff == group.get(0)) {
                            adf = sampleEnd(diff.text());
                        } else if (diff == group.get(group.size() - 1)) {
                            adf = sampleStart(diff.text());
                        } else {
                            adf = plaintextToAdf(diff.text());
                        }
                        deleteStream.add(adf);
                        insertStream.add(adf);
                        break;
                }
            });

            // create table rows
            String deleteCell = DescriptionBuilder.createTableCellAdf(
                DescriptionBuilder.createParagraphAdf(
                    String.join(", ", deleteStream)
                )
            );
            String insertCell = DescriptionBuilder.createTableCellAdf(
                DescriptionBuilder.createParagraphAdf(
                    String.join(", ", insertStream)
                )
            );

            return DescriptionBuilder.createTableRowAdf(deleteCell + ", " + insertCell);
        }).collect(Collectors.joining(", "));

        return DescriptionBuilder.createTableAdf(tableRows);
    }

    private String deleteToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(DescriptionBuilder::createDeleteTextAdf)
            .collect(
                Collectors.joining(
                    ", " + DescriptionBuilder.createHardBreakAdf() + ", "
                )
            );
    }

    private String insertToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(DescriptionBuilder::createInsertTextAdf)
            .collect(
                Collectors.joining(
                    ", " + DescriptionBuilder.createHardBreakAdf() + ", "
                )
            );
    }

    private String plaintextToAdf(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
            .map(DescriptionBuilder::createPlainTextAdf)
            .collect(
                Collectors.joining(
                    ", " + DescriptionBuilder.createHardBreakAdf() + ", "
                )
            );
    }

    private String sampleStart(String text) {
        String[] lines = text.split("(\\r\\n|\\r|\\n)");
        String start = lines[0];
        return DescriptionBuilder.createPlainTextAdf(start);
    }

    private String sampleEnd(String text) {
        String[] lines = text.split("(\\r\\n|\\r|\\n)");
        String last = lines[lines.length - 1];
        return DescriptionBuilder.createPlainTextAdf(last);
    }
}
