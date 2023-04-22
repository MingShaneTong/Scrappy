package scrappy.core.issue.types;

import java.util.List;

/**
 * To be executed by the application.
 */
public class ExecutionIssue extends Issue {
    /**
     * Creates an execution issue
     * @param key Issue key of Issue
     * @param summary Title of the Issue
     * @param state Whether the issue is in use
     * @param subIssues Issues contained within issue
     */
    public ExecutionIssue(String key, String summary, IssueState state, List<Issue> subIssues) {
        super(key, summary, state, subIssues);
    }
}
