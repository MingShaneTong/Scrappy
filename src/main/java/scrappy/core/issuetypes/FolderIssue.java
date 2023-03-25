package scrappy.core.issuetypes;

import java.util.List;

public class FolderIssue extends Issue {
    public FolderIssue(String key, String summary, IssueState state) {
        super(key, summary, state);
    }

    public FolderIssue(String key, String summary, IssueState state, List<Issue> subIssues) {
        super(key, summary, state, subIssues);
    }
}
