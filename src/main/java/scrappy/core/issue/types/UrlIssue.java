package scrappy.core.issue.types;

import java.util.List;

/**
 * Contains Url to test
 */
public class UrlIssue extends Issue {
    private final String url;
    private final String instructions;

    public UrlIssue(String key, String summary, IssueState state, String instructions, String url) {
        super(key, summary, state, List.of());
        this.url = url;
        this.instructions = instructions;
    }

    public String getUrl() { return url; }

    public String getInstructions() {
        return instructions;
    }
}
