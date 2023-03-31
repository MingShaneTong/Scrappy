package scrappy.core.issue.types;

import java.util.List;

public class UrlIssue extends Issue {
    private final String url;

    public UrlIssue(String key, String summary, IssueState state, String url) {
        super(key, summary, state, List.of());
        this.url = url;
    }

    public String getUrl() { return url; }
}
