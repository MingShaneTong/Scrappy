package scrappy.core.issue.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds a Snapshot issue Json to be in the body of the REST API Request
 */
public class SnapshotIssueBuilder {
    private static final String formatString;

    static {
        try {
            Path path = Paths.get("template/SnapshotIssueApiTemplate.json");
            formatString = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String projectKey = "";
    private String summary = "";
    private String issueLink = "";
    private String description = "";

    public SnapshotIssueBuilder setProject(String key) {
        this.projectKey = key;
        return this;
    }

    public SnapshotIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public SnapshotIssueBuilder setIssueLink(String issueLink) {
        this.issueLink = issueLink;
        return this;
    }

    public SnapshotIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return String.format(formatString, projectKey, summary, description, issueLink);
    }
}
