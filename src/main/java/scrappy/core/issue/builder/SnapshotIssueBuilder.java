package scrappy.core.issue.builder;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SnapshotIssueBuilder {
    private static String formatString;

    static {
        try {
            Path path = Paths.get("template/SnapshotIssueApiTemplate");
            formatString = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String projectKey = "";
    private String summary = "";
    private String issueType = "";

    public SnapshotIssueBuilder setProject(String key) {
        this.projectKey = key;
        return this;
    }

    public SnapshotIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public SnapshotIssueBuilder setIssueType(String issueType) {
        this.issueType = issueType;
        return this;
    }

    @Override
    public String toString() {
        return String.format(formatString, projectKey, summary, issueType);
    }
}
