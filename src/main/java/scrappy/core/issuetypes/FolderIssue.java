package scrappy.core.issuetypes;

import java.util.List;

public class FolderIssue extends Issue {
    public FolderIssue(String title, IssueState state, List<Issue> subIssues) {
        super(title, state, subIssues);
    }
}
