package scrappy.core.issuetypes;

import java.util.ArrayList;
import java.util.List;

public class UrlIssue extends Issue {
    private final String url;

    public UrlIssue(String title, IssueState state, String url) {
        super(title, state, List.of());
        this.url = url;
    }

    public String getUrl() { return url; }
}
