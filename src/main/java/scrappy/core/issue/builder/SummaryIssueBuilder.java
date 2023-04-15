package scrappy.core.issue.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds a Snapshot issue Json to be in the body of the REST API Request
 */
public class SummaryIssueBuilder {
    private static final String formatString;

    static {
        try {
            Path path = Paths.get("template/SummaryIssueApiTemplate.json");
            formatString = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String projectKey = "";
    private String summary = "";
    private String issueLink = "";
    private String description = "";

    public SummaryIssueBuilder setProject(String key) {
        this.projectKey = key;
        return this;
    }

    public SummaryIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public SummaryIssueBuilder setIssueLink(String issueLink) {
        this.issueLink = issueLink;
        return this;
    }

    public SummaryIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return String.format(formatString, projectKey, summary, description, issueLink);
    }
}
