package scrappy.core.issuetypes;

import java.util.List;

public class ExecutionIssue extends Issue {
    public ExecutionIssue(String title, String summary, IssueState state, List<Issue> subIssues) {
        super(title, summary, state, subIssues);
    }
}
